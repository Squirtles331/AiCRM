# 诊断并启动 PostgreSQL（输出写入文件）
$out = "D:\Project\AI销售线索系统\AiCRM\scripts\pg-diagnose-out.txt"
$ErrorActionPreference = 'Continue'
$log = @()
$log += "=== pg_hba.conf 认证方式检查 ==="
$pgHba = "C:\Program Files\PostgreSQL\16\data\pg_hba.conf"
if (Test-Path $pgHba) {
    $log += (Select-String -Path $pgHba -Pattern '^(host|local)' | Select-Object -First 10 | ForEach-Object { $_.Line })
} else {
    $log += "pg_hba.conf 不存在: $pgHba"
}
$log += "=== 尝试启动服务 ==="
Start-Service postgresql-x64-16
Start-Sleep -Seconds 5
$svc = Get-Service postgresql-x64-16
$log += ("Service Status: " + $svc.Status)
if ($svc.Status -ne 'Running') {
    $log += "=== 最近日志 ==="
    $logDir = "C:\Program Files\PostgreSQL\16\data\log"
    if (Test-Path $logDir) {
        $latest = Get-ChildItem $logDir -Filter *.log | Sort-Object LastWriteTime -Descending | Select-Object -First 1
        if ($latest) { $log += (Get-Content $latest.FullName -Tail 30) }
    } else {
        $log += "日志目录不存在: $logDir"
    }
    $log += "=== 尝试 net start ==="
    $log += (net start postgresql-x64-16 2>&1)
    Start-Sleep -Seconds 5
    $svc2 = Get-Service postgresql-x64-16
    $log += ("Final Status: " + $svc2.Status)
}
$log | Set-Content -Path $out -Encoding UTF8
