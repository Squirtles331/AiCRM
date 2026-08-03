# 将 Erlang OTP 从 29 降级到 27.3.4.15（需提权运行，请确认 UAC）
$out = "D:\Project\AI销售线索系统\AiCRM\scripts\erlang-swap-out.txt"
$ErrorActionPreference = 'Continue'
$log = @()

$log += "=== 1. 停止 RabbitMQ ==="
Stop-Service RabbitMQ -Force -ErrorAction SilentlyContinue
Start-Sleep -Seconds 3

$log += "=== 2. 卸载 Erlang OTP 29 ==="
$uninstaller = "C:\Program Files\Erlang OTP\Uninstall.exe"
if (Test-Path $uninstaller) {
    $p = Start-Process -FilePath $uninstaller -ArgumentList '/S' -PassThru -Wait
    $log += ("Uninstaller exit: " + $p.ExitCode)
} else {
    $log += "卸载程序不存在: $uninstaller"
}
Start-Sleep -Seconds 5
$log += ("Erlang OTP 目录仍存在: " + (Test-Path "C:\Program Files\Erlang OTP"))

$log += "=== 3. 安装 Erlang OTP 27.3.4.15 ==="
$installer = "$env:TEMP\otp_win64_27.3.4.15.exe"
if (Test-Path $installer) {
    $p2 = Start-Process -FilePath $installer -ArgumentList '/S' -PassThru -Wait
    $log += ("Installer exit: " + $p2.ExitCode)
} else {
    $log += "安装包不存在: $installer"
}
Start-Sleep -Seconds 5

$log += "=== 4. 验证安装 ==="
$erlBin = "C:\Program Files\Erlang OTP\bin\erl.exe"
$log += ("erl.exe 存在: " + (Test-Path $erlBin))
if (Test-Path $erlBin) {
    $ver = & "C:\Program Files\Erlang OTP\bin\erl.exe" -noshell -eval 'io:format("~s~n",[erlang:system_info(otp_release)]), halt().' 2>&1
    $log += ("OTP 版本: " + ($ver -join ''))
}

$log += "=== 5. 启动 RabbitMQ ==="
Start-Service RabbitMQ
Start-Sleep -Seconds 10
$svc = Get-Service RabbitMQ
$log += ("RabbitMQ Status: " + $svc.Status)

$log | Set-Content -Path $out -Encoding UTF8
