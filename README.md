# ☕ Cô Đào Quán POS - Hệ Thống Quản Lý & Đặt Món Trà Sữa

Dự án đồ án cơ sở xây dựng hệ thống quản lý bán hàng (Point of Sale - POS) và đặt món trực tuyến tại bàn dành cho **Cô Đào Quán**. Được phát triển trên nền tảng **Spring Boot**, **MySQL**, **Thymeleaf**, **Bootstrap 5** và **Chart.js** với giao diện hiện đại, chuyên nghiệp.

---

## 🚀 Công Nghệ Sử Dụng

*   **Backend**: Java 17, Spring Boot 3.x, Spring Data JPA, Spring Security (Quản lý phân quyền & xác thực).
*   **Database**: MySQL 8.x (Tự động sinh cấu trúc bảng & nạp dữ liệu mẫu).
*   **Frontend**: HTML5, Vanilla CSS3 (Custom Theme HSL), Thymeleaf (Template Engine), Bootstrap 5.3.3.
*   **Charting**: Chart.js (Biểu đồ thống kê doanh thu, kho và sản phẩm bán chạy).

---

## ✨ Các Tính Năng Nổi Bật

1.  **Dashboard Thống Kê SaaS Cao Cấp**:
    *   Thống kê doanh thu, đơn hàng, hóa đơn, khách hàng và trạng thái kho.
    *   Biểu đồ đường trực quan thể hiện doanh thu theo thời gian thực (được vẽ mượt với gradient fills).
    *   Biểu đồ cột ngang thống kê top sản phẩm bán chạy.
    *   Biểu đồ cột đứng thể hiện lượng tồn kho nguyên liệu hiện tại.
2.  **Đặt Món Tại Bàn Qua Mã QR (`qr-menu.html`)**:
    *   Khách hàng quét mã QR tại bàn để xem thực đơn riêng biệt của bàn đó.
    *   Tăng/giảm số lượng trực tiếp bằng các nút bấm cộng trừ tròn mượt mà dạng POS.
    *   Thêm ghi chú cụ thể cho từng món uống (ví dụ: ít đá, 50% đường).
    *   Gửi yêu cầu thanh toán trực tiếp về quầy.
3.  **Đặt Món Online Cho Khách Hàng (`customer-menu.html`)**:
    *   Thực đơn phân chia danh mục khoa học, thanh tìm kiếm nhanh không lag.
    *   Giỏ hàng (`customer-cart.html`) hiển thị chia cột: danh sách món bên trái, tóm tắt thanh toán bên phải.
    *   Thông báo thêm món bằng **Toast thông báo trượt** ở góc phải màn hình, không làm lệch giao diện.
4.  **Hệ Thống Phân Quyền Thông Minh**:
    *   **ADMIN**: Toàn quyền hệ thống, quản lý nhân viên, phân quyền và thiết lập voucher khuyến mãi.
    *   **STAFF**: Truy cập trang chủ, quản lý sản phẩm, danh mục, khách hàng, bàn ăn, hóa đơn và nhập kho nguyên liệu.
    *   **CUSTOMER**: Tự động chuyển hướng về trang đặt món cá nhân sau khi đăng nhập thành công.
5.  **Giao Diện Cozy Premium**:
    *   Tone màu chủ đạo lấy cảm hứng từ các quán cà phê ấm cúng (nâu trà sữa, kem bơ mềm mại, cam đất).
    *   Thiết kế thanh điều hướng dạng hiệu ứng kính mờ (Glassmorphism).
    *   Định dạng tiền tệ VNĐ chuyên nghiệp (`2.500.000 đ` thay vì `2500000.0`).

---

## 🛠️ Yêu Cầu Hệ Thống

Để khởi chạy dự án, máy của bạn cần cài đặt sẵn:
1.  **Java Development Kit (JDK)**: Phiên bản **17 trở lên**.
2.  **MySQL Server**: Đang hoạt động trên cổng mặc định `3306`.

---

## 📦 Hướng Dẫn Cài Đặt & Chạy Dự Án

### Bước 1: Pull mã nguồn về máy
Sau khi nhân bản hoặc tải zip mã nguồn về, mở thư mục dự án bằng IDE của bạn (IntelliJ IDEA, Eclipse hoặc VS Code).

### Bước 2: Cấu hình tài khoản MySQL Local
Mở tệp tin cấu hình cơ sở dữ liệu tại đường dẫn:
`src/main/resources/application.properties`

Thay đổi tên đăng nhập và mật khẩu MySQL tương thích với máy của bạn:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/milktea_pos?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=NHẬP_MẬT_KHẨU_MYSQL_CỦA_BẠN_VÀO_ĐÂY
```
> 💡 **Lưu ý**: Nhờ cấu hình `createDatabaseIfNotExist=true`, bạn **không cần tạo trước cơ sở dữ liệu** trong MySQL. Hệ thống sẽ tự động tạo database `milktea_pos` khi bắt đầu chạy!

### Bước 3: Chạy ứng dụng
Mở Terminal/Command Prompt tại thư mục gốc của dự án và chạy các lệnh tương ứng:

*   **Trên hệ điều hành Windows**:
    ```powershell
    .\mvnw.cmd spring-boot:run
    ```
*   **Trên hệ điều hành macOS / Linux**:
    ```bash
    ./mvnw spring-boot:run
    ```

Dự án sẽ tự động tải các thư viện Maven cần thiết, khởi tạo các bảng trong cơ sở dữ liệu, nạp dữ liệu mẫu và chạy tại cổng **8081**.

Truy cập hệ thống qua trình duyệt: **[http://localhost:8081](http://localhost:8081)**

---

## 🔑 Tài Khoản Thử Nghiệm Hệ Thống

Hệ thống đã tự động nạp sẵn (seed) các tài khoản kiểm thử sau đây để bạn dễ dàng chấm bài và đánh giá chức năng:

| Vai trò | Tên đăng nhập | Mật khẩu | Trang chuyển hướng sau đăng nhập |
| :--- | :--- | :--- | :--- |
| **Quản trị viên (ADMIN)** | `admin` | `123456` | Dashboard Hệ Thống (`/`) |
| **Nhân viên (STAFF)** | `staff` | `123456` | Dashboard Hệ Thống (`/`) |
| **Khách hàng thân thiết (CUSTOMER)** | `customer` | `123456` | Trang Đặt Món Khách Hàng (`/customer/menu`) |

---

## ⚙️ Cấu Hình Bảo Mật (Spring Security)
*   **Tắt CSRF**: Chế độ bảo vệ CSRF được cấu hình tắt (`csrf().disable()`) để đảm bảo các yêu cầu gửi đơn hàng, sửa sản phẩm và thanh toán qua QR hoạt động ổn định trên môi trường kiểm thử cục bộ.
*   **Xác thực mật khẩu**: Sử dụng thuật toán băm mật khẩu `BCryptPasswordEncoder` để mã hóa thông tin an toàn.
