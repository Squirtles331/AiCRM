# 修复 pg_hba.conf BOM 问题并启动 PostgreSQL（需提权运行，请确认 UAC 弹窗）
$out = "D:\Project\AI销售线索系统\AiCRM\scripts\pg-fix-out.txt"
$ErrorActionPreference = 'Continue'
$log = @()
$pgHba = "C:\Program Files\PostgreSQL\16\data\pg_hba.conf"

$log += "=== 修复前第1行 ==="
$raw = [System.IO.File]::ReadAllBytes($pgHba)
$log += ("前3字节: " + ($raw[0..2] -join ','))
$content = [System.IO.File]::ReadAllText($pgHba)

# 去掉 BOM
$bom = [char]0xFEFF
$content = $content.TrimStart($bom)
# 确保本地连接为 trust（若之前替换未生效则再次替换）
$content = $content -replace 'scram-sha-256', 'trust'

# 无 BOM UTF8 写入
$utf8NoBom = New-Object System.Text.UTF8Encoding($false)
[System.IO.File]::WriteAllText($pgHba, $content, $utf8NoBom)

$log += "=== 修复后第1行 ==="
$raw2 = [System.IO.File]::ReadAllBytes($pgHba)
$log += ("前3字节: " + ($raw2[0..2] -join ','))
$firstLine = [System.IO.File]::ReadAllLines($pgHba)[0]
$log += ("第1行内容: " + $firstLine)
$log += ("host 行: " + ((Select-String -Path $pgHba -Pattern '^host' | Select-Object -First 3 | ForEach-Object { $_.Line }) -join " | "))

# 启动服务
$log += "=== 启动服务 ==="
Start-Service postgresql-x64-16
Start-Sleep -Seconds 5
$svc = Get-Service postgresql-x64-16
$log += ("Service Status: " + $svc.Status)
$log += ("端口5432: " + ((Test-NetConnection -ComputerName localhost -Port 5432 -WarningAction SilentlyContinue).TcpTestSucceeded))

$log | Set-Content -Path $out -Encoding UTF8
