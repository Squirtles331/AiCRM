# 提权启动 PostgreSQL 与 RabbitMQ 服务（配合 Start-Process -Verb RunAs 调用）
$ErrorActionPreference = 'Continue'
Start-Service postgresql-x64-16
Start-Service RabbitMQ
Start-Sleep -Seconds 3
Get-Service postgresql-x64-16, RabbitMQ | Format-Table -AutoSize
