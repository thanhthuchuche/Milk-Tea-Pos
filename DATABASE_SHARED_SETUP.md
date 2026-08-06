# Dùng chung CSDL MySQL giữa hai máy

Ứng dụng hiện chạy mặc định bằng profile `mysql`. Cấu hình kết nối đọc từ biến môi trường, nên không lưu mật khẩu database trong Git.

## Máy đang chạy MySQL

- MySQL: `192.168.1.23:3306`
- Database: `milktea_pos`
- User ứng dụng: `milktea_app`
- MySQL đã mở truy cập LAN và schema được cập nhật tự động bởi Hibernate (`ddl-auto=update`).

## Chạy ứng dụng trên mỗi máy Windows

Mở PowerShell tại thư mục dự án và thay `<MAT_KHAU_DB>` bằng mật khẩu của user `milktea_app`:

```powershell
$env:DB_HOST="192.168.1.23"
$env:DB_PORT="3306"
$env:DB_NAME="milktea_pos"
$env:DB_USERNAME="milktea_app"
$env:DB_PASSWORD="<MAT_KHAU_DB>"
.\mvnw.cmd spring-boot:run
```

Các biến môi trường chỉ có hiệu lực trong cửa sổ PowerShell hiện tại. Muốn lưu lâu dài, dùng `setx`, sau đó mở PowerShell mới:

```powershell
setx DB_HOST "192.168.1.23"
setx DB_PORT "3306"
setx DB_NAME "milktea_pos"
setx DB_USERNAME "milktea_app"
setx DB_PASSWORD "<MAT_KHAU_DB>"
```

## Sau khi pull code mới

```powershell
git pull origin main
.\mvnw.cmd spring-boot:run
```

Không cần chạy file SQL để thêm bảng/cột mới. Khi ứng dụng khởi động, Hibernate tự cập nhật schema và DatabaseSeeder chỉ bổ sung dữ liệu mẫu khi cần.

## Kiểm tra lỗi kết nối

- Máy bên kia phải cùng mạng LAN với máy `192.168.1.23`.
- Kiểm tra cổng MySQL:

```powershell
Test-NetConnection 192.168.1.23 -Port 3306
```

- Nếu `TcpTestSucceeded` là `False`, kiểm tra MySQL service và Windows Firewall trên máy đang chạy MySQL.
- Nếu IP máy chủ thay đổi, cập nhật lại `DB_HOST` trên máy khách.

## Chạy H2 tạm thời để test riêng

```powershell
$env:SPRING_PROFILES_ACTIVE="h2"
.\mvnw.cmd spring-boot:run
```
