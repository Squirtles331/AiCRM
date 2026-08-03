# 开发环境 PostgreSQL 本地免密配置（仅本机调试用，勿用于生产）
$ErrorActionPreference = 'Stop'
$pgHba = "C:\Program Files\PostgreSQL\16\data\pg_hba.conf"
$content = Get-Content $pgHba -Raw
$new = $content -replace 'scram-sha-256', 'trust'
Set-Content -Path $pgHba -Value $new -NoNewline -Encoding UTF8
Restart-Service postgresql-x64-16 -Force
Write-Output "PG_HBA_UPDATED_AND_SERVICE_RESTARTED"
