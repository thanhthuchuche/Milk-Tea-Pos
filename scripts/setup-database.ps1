param(
    [string]$DbHost = "localhost",
    [int]$DbPort = 3306,
    [string]$DbName = "milktea_pos",
    [switch]$FreshInstall
)

$ErrorActionPreference = "Stop"

if (-not (Test-Path (Join-Path $PSScriptRoot "..\mvnw.cmd"))) {
    throw "Không tìm thấy mvnw.cmd. Hãy chạy script từ project đã pull đầy đủ."
}

$dbUserInput = Read-Host "MySQL username [root]"
$dbUser = if ([string]::IsNullOrWhiteSpace($dbUserInput)) { "root" } else { $dbUserInput.Trim() }
$securePassword = Read-Host "MySQL password (Enter nếu tài khoản không có mật khẩu)" -AsSecureString
$passwordPointer = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($securePassword)
try {
    $dbPassword = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($passwordPointer)
}
finally {
    [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($passwordPointer)
}

if ($FreshInstall) {
    Write-Warning "FreshInstall sẽ tạo lại schema và dữ liệu mẫu trong database '$DbName'. Không dùng tùy chọn này với database đang có dữ liệu cần giữ."
    $confirmation = Read-Host "Nhập RESET để xác nhận"
    if ($confirmation -cne "RESET") {
        Write-Host "Đã hủy, không thay đổi database."
        exit 1
    }
    $env:SPRING_JPA_HIBERNATE_DDL_AUTO = "create"
}
else {
    $env:SPRING_JPA_HIBERNATE_DDL_AUTO = "update"
}

$env:SPRING_PROFILES_ACTIVE = "mysql"
$env:DB_HOST = $DbHost
$env:DB_PORT = "$DbPort"
$env:DB_NAME = $DbName
$env:DB_USERNAME = $dbUser
$env:DB_PASSWORD = $dbPassword

Write-Host "Database: $DbHost`:$DbPort/$DbName" -ForegroundColor Cyan
Write-Host "User: $dbUser" -ForegroundColor Cyan
Write-Host "Ứng dụng sẽ tự tạo/cập nhật bảng và nạp dữ liệu mẫu." -ForegroundColor Green

$mavenWrapper = Join-Path $PSScriptRoot "..\mvnw.cmd"
& $mavenWrapper spring-boot:run
exit $LASTEXITCODE
