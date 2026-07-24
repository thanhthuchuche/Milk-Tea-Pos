# 🧋 Cô Đào Quán POS - Hệ Thống Quản Lý & Đặt Món Trà Sữa

Hệ thống quản lý bán hàng (Point of Sale - POS) và đặt món tại bàn/online dành cho **Cô Đào Quán**. Được phát triển trên nền tảng **Spring Boot 3.x**, **Spring Security**, **H2 In-Memory DB (Zero-Config)**, **Thymeleaf**, **Bootstrap 5.3** và **Chart.js** với giao diện POS SaaS vuông vức, sắc nét và hiện đại.

---

## 🚀 Công Nghệ Sử Dụng

*   **Backend**: Java 17, Spring Boot 3.x, Spring Data JPA, Spring Security (Role-based Authorization & Authentication).
*   **Database**: Embedded H2 Database (`jdbc:h2:mem:milktea_pos`) - Tự động tạo bảng & nạp sẵn dữ liệu kiểm thử (Database Seeder) 100% tự động.
*   **Frontend**: HTML5, Vanilla CSS3 (SaaS POS Design Tokens), Thymeleaf Template Engine, Bootstrap 5.3.3, FontAwesome 6.5.
*   **Charts & Visuals**: Chart.js (Biểu đồ doanh thu hệ thống, top sản phẩm bán chạy, doanh thu tháng và tồn kho nguyên liệu).

---

## ✨ Các Tính Năng Nổi Bật

1.  **Giao Diện POS SaaS Vuông Vức & Sang Trọng**:
    *   Phông chữ chuẩn quốc tế **Plus Jakarta Sans**, phông nền màu kem cà phê ấm áp kết hợp thanh header đen espresso.
    *   Thẻ thông tin số liệu KPI sắc nét, viền 1.5px chuẩn mực, bảng biểu hiển thị rõ ràng với badge trạng thái màu sắc nổi bật.
2.  **Màn Hình Đăng Nhập Glassmorphism Xịn Xò (`login.html`)**:
    *   Hình ảnh minh họa trà sữa độ phân giải cao, hiệu ứng nền lưới chuyển động (Animated Mesh Background).
    *   Tích hợp sẵn các nút điền nhanh tài khoản kiểm thử (`Admin`, `Nhân viên`, `Khách hàng`).
3.  **Thực Đơn Đặt Món Khách Hàng Phong Phú (`customer-menu.html`)**:
    *   18 món uống & bánh ngọt phân chia 6 danh mục (*Trà Sữa Signature, Trà Trái Cây Tươi, Cà Phê & Macchiato, Đá Xay & Smoothies, Toppings, Bánh Ngọt*).
    *   Thanh tìm kiếm realtime không lag, thanh danh mục dính (`Sticky Category Bar`).
    *   Gắn nhãn **`BEST SELLER`**, **`HOT`**, đánh giá **⭐ 4.9** và **Nút Giỏ Hàng Nổi (`Floating Cart Button`)** cố định góc phải.
4.  **Tạo & In Mã QR Đặt Món Tại Bàn (`table-list.html`)**:
    *   Modal tự động tạo mã QR Code chuẩn theo URL bàn (`http://localhost:8082/menu/{tableId}`).
    *   Tích hợp nút **"In Mã QR"** hỗ trợ xem trước và in ấn nhanh chóng cho chủ quán.
5.  **In Hóa Đơn Nhiệt POS 80mm (`invoice-detail.html`)**:
    *   Định dạng phiếu tính tiền chuẩn 80mm dành cho máy in nhiệt tại quầy thu ngân.
6.  **Hệ Thống Phân Quyền Nhanh**:
    *   **ADMIN**: Toàn quyền quản lý doanh thu, nhân viên, sản phẩm, nguyên liệu kho, danh mục, hóa đơn và voucher.
    *   **STAFF**: Truy cập Dashboard, quản lý bán hàng tại bàn, xuất hóa đơn và kiểm kê nguyên liệu.
    *   **CUSTOMER**: Tự động chuyển hướng về trang đặt món sau khi đăng nhập thành công.

---

## 📦 Hướng Dẫn Kéo Code & Khởi Chạy Trên Máy Khác (Zero Config)

Dự án đã tích hợp **Maven Wrapper** và **H2 Database chạy ngầm trên RAM**, bất kỳ máy nào pull code về cũng có thể chạy ngay mà **không cần cài đặt MySQL hay Maven thủ công**!

### Bước 1: Pull mã nguồn về máy
```bash
git clone https://github.com/.../Milk-Tea-Pos.git
cd Milk-Tea-Pos
```

### Bước 2: Chạy dự án bằng lệnh Maven Wrapper

*   **Trên Windows (PowerShell / Command Prompt)**:
    ```powershell
    .\mvnw.cmd spring-boot:run
    ```

*   **Trên macOS / Linux**:
    ```bash
    chmod +x mvnw
    ./mvnw spring-boot:run
    ```

Dự án sẽ tự động tải các dependency Maven, khởi tạo các bảng dữ liệu, tự động seed 18 sản phẩm mẫu và khởi chạy tại cổng **8082**.

### Bước 3: Mở trình duyệt trải nghiệm
Truy cập hệ thống: **[http://localhost:8082](http://localhost:8082)**

---

## 🔑 Tài Khoản Thử Nghiệm Nạp Sẵn (Seeded Accounts)

| Vai trò | Tên đăng nhập | Mật khẩu | Chuyển hướng sau đăng nhập |
| :--- | :--- | :--- | :--- |
| **Quản trị viên (ADMIN)** | `admin` | `123456` | Dashboard POS Hệ Thống (`/`) |
| **Nhân viên (STAFF)** | `staff` | `123456` | Dashboard POS Hệ Thống (`/`) |
| **Khách hàng (CUSTOMER)** | `customer` | `123456` | Thực Đơn Đặt Món Khách Hàng (`/customer/menu`) |

---

## 🛡️ Tự Động Xử Lý Lỗi Bảo Mật
*   Tự động chuyển hướng khách truy cập chưa đăng nhập tại trang chủ `/` về màn hình Đăng nhập `/login` thay vì lỗi 403 Forbidden.
*   Chống lỗi đúp định tuyến (`Ambiguous mapping`) giữa Admin & Customer Menu Controllers.
*   Tự động tính toán lại tổng tiền giỏ hàng (`recalculateCartTotal`) khi tăng/giảm/xóa món.
