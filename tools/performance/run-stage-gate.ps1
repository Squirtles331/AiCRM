[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)]
    [ValidateNotNullOrEmpty()]
    [string]$BaseUrl,

    [Parameter(Mandatory = $true)]
    [ValidateNotNullOrEmpty()]
    [string]$BearerToken,

    [Parameter(Mandatory = $true)]
    [ValidatePattern('^[0-9]+$')]
    [string]$FollowUpLeadId,

    [Parameter(Mandatory = $true)]
    [ValidateScript({ Test-Path $_ -PathType Leaf })]
    [string]$DataProfilePath,

    [ValidateRange(1, 1000)]
    [int]$ListVus = 20,

    [ValidatePattern('^[1-9][0-9]*[smh]$')]
    [string]$ListDuration = '2m',

    [ValidateRange(1, 10000)]
    [int]$CommandIterations = 100,

    [string]$ResultsDirectory = (Join-Path $PSScriptRoot (Join-Path 'results' (Get-Date -Format 'yyyyMMdd-HHmmss')))
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

function Require-Command([string]$Name) {
    if (-not (Get-Command $Name -ErrorAction SilentlyContinue)) {
        throw "Required command '$Name' was not found. Install Docker Desktop or provide Docker on PATH."
    }
}

function Get-DockerResources {
    $raw = docker info --format '{{.NCPU}} {{.MemTotal}}'
    if ($LASTEXITCODE -ne 0) {
        throw 'Unable to inspect the Docker engine. Ensure Docker is running.'
    }
    $parts = $raw.Trim() -split '\s+'
    if ($parts.Count -ne 2 -or $parts[0] -notmatch '^\d+$' -or $parts[1] -notmatch '^\d+$') {
        throw "Unexpected Docker resource report: $raw"
    }
    return [PSCustomObject]@{ Cpu = [int]$parts[0]; MemoryBytes = [int64]$parts[1] }
}

function Convert-BaseUrlForContainer([string]$Url) {
    $uri = [Uri]$Url
    if ($uri.Host -notin @('localhost', '127.0.0.1', '::1')) {
        return $Url.TrimEnd('/')
    }
    $builder = [UriBuilder]$uri
    $builder.Host = 'host.docker.internal'
    return $builder.Uri.AbsoluteUri.TrimEnd('/')
}

function Read-DataProfile([string]$Path) {
    try {
        $profile = Get-Content -Raw -Path $Path | ConvertFrom-Json
    } catch {
        throw "DataProfilePath must contain valid JSON: $($_.Exception.Message)"
    }
    if ($null -eq $profile.tenantCount -or $null -eq $profile.salesRecordCount -or [string]::IsNullOrWhiteSpace($profile.source)) {
        throw 'The data profile must contain tenantCount, salesRecordCount and source.'
    }
    if ([int64]$profile.tenantCount -ne 10) {
        throw "The performance gate requires exactly 10 tenants; data profile reports $($profile.tenantCount)."
    }
    if ([int64]$profile.salesRecordCount -lt 1000000) {
        throw "The performance gate requires at least 1,000,000 sales records; data profile reports $($profile.salesRecordCount)."
    }
    return $profile
}

Require-Command docker
$resources = Get-DockerResources
$minimumMemory = 8GB
if ($resources.Cpu -lt 4 -or $resources.MemoryBytes -lt $minimumMemory) {
    $memoryGiB = [Math]::Round($resources.MemoryBytes / 1GB, 2)
    throw "Performance stage gate requires at least 4 CPU cores and 8 GiB available to Docker; detected $($resources.Cpu) cores and $memoryGiB GiB."
}

$target = [Uri]$BaseUrl
if ($target.Scheme -notin @('http', 'https')) {
    throw 'BaseUrl must use http or https.'
}
$dataProfile = Read-DataProfile $DataProfilePath
$dataProfileHash = (Get-FileHash -Algorithm SHA256 -Path $DataProfilePath).Hash

New-Item -ItemType Directory -Force -Path $ResultsDirectory | Out-Null
$containerBaseUrl = Convert-BaseUrlForContainer $BaseUrl
$environment = [ordered]@{
    executedAtUtc = (Get-Date).ToUniversalTime().ToString('o')
    baseUrl = $BaseUrl.TrimEnd('/')
    dockerCpu = $resources.Cpu
    dockerMemoryBytes = $resources.MemoryBytes
    listVus = $ListVus
    listDuration = $ListDuration
    commandIterations = $CommandIterations
    followUpLeadId = $FollowUpLeadId
    dataProfileSha256 = $dataProfileHash
    dataProfileSource = $dataProfile.source
    k6Image = 'grafana/k6:0.54.0'
}
$environment | ConvertTo-Json | Set-Content -Encoding utf8 (Join-Path $ResultsDirectory 'environment.json')
Copy-Item -LiteralPath $DataProfilePath -Destination (Join-Path $ResultsDirectory 'data-profile.json')

$scriptMount = "$($PSScriptRoot):/scripts:ro"
$resultMount = "$(Resolve-Path $ResultsDirectory):/results"
$dockerArguments = @(
    'run', '--rm',
    '--add-host', 'host.docker.internal:host-gateway',
    '-v', $scriptMount,
    '-v', $resultMount,
    '-e', "PERF_BASE_URL=$containerBaseUrl",
    '-e', "PERF_BEARER_TOKEN=$BearerToken",
    '-e', "PERF_FOLLOW_UP_LEAD_ID=$FollowUpLeadId",
    '-e', "PERF_LIST_VUS=$ListVus",
    '-e', "PERF_LIST_DURATION=$ListDuration",
    '-e', "PERF_COMMAND_ITERATIONS=$CommandIterations",
    'grafana/k6:0.54.0', 'run', '--summary-export=/results/summary.json', '/scripts/sales-stage-gate.js'
)

Write-Host "Running performance gate against $($environment.baseUrl). Results: $ResultsDirectory"
& docker @dockerArguments
if ($LASTEXITCODE -ne 0) {
    throw "k6 reported a failed threshold or runtime error. Inspect $(Join-Path $ResultsDirectory 'summary.json')."
}
Write-Host "Performance stage gate passed. Summary: $(Join-Path $ResultsDirectory 'summary.json')"
