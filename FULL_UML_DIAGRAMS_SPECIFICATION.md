# TỔNG HỢP 10 SƠ ĐỒ UML & THIẾT KẾ HỆ THỐNG CHI TIẾT - CÔ ĐÀO QUÁN POS

Tài liệu này bao gồm **10 sơ đồ UML chuẩn hóa** (Use Case, ERD, Class, Sequence, Activity, State Machine) có kèm mã **Mermaid Diagram** trực quan, giúp bạn sao chép trực tiếp vào báo cáo đồ án, luận văn hoặc công cụ vẽ sơ đồ (Draw.io, StarUML, Mermaid Live Editor).

---

## 📋 DANH SÁCH 10 SƠ ĐỒ TRONG TÀI LIỆU

1. **Sơ đồ 1**: Sơ đồ Use Case Tổng Thể (Overall Use Case Diagram)
2. **Sơ đồ 2**: Sơ đồ Thực Thể Mối Quan Hệ ERD (Full ERD Diagram - 17 Entities)
3. **Sơ đồ 3**: Sơ đồ Lớp Kiến Trúc 3 Tầng (Class Diagram - 3-Tier Architecture)
4. **Sơ đồ 4**: Sơ đồ Tuần Tự - Luồng Bán Hàng Tại Quầy POS (Sequence Diagram - POS Counter Checkout)
5. **Sơ đồ 5**: Sơ đồ Tuần Tự - Luồng Đặt Món Online & Admin Duyệt (Sequence Diagram - Online Order)
6. **Sơ đồ 6**: Sơ đồ Tuần Tự - Luồng Thanh Toán Mã VietQR & In Hóa Đơn Nhiệt 80mm (Sequence Diagram - VietQR & Print)
7. **Sơ đồ 7**: Sơ đồ Hoạt Động - Luồng Xử Lý Bán Hàng POS Thu Ngân (Activity Diagram - POS Order Flow)
8. **Sơ đồ 8**: Sơ đồ Hoạt Động - Luồng Tự Động Trừ Kho Nguyên Liệu & Tích Điểm (Activity Diagram - Auto Recipe Stock & Loyalty Points)
9. **Sơ đồ 9**: Sơ đồ Trạng Thái Vòng Đời Đơn Hàng (State Machine Diagram - Order Status Lifecycle)
10. **Sơ đồ 10**: Sơ đồ Trạng Thái Quản Lý Bàn Ăn (State Machine Diagram - Table Cafe Status Lifecycle)

---

## 📌 CHI TIẾT VẼ & MÃ MERMAID CHO TỪNG SƠ ĐỒ

---

### 1️⃣ SƠ ĐỒ USE CASE TỔNG THỂ (OVERALL USE CASE DIAGRAM)

* **Tác nhân (Actors)**:
  - `Admin (Quản lý)`: Quyền quản trị cao nhất.
  - `Staff (Nhân viên / Thu ngân)`: Thực hiện bán hàng tại quầy và quản lý đơn.
  - `Customer (Khách hàng)`: Đặt món online, quét QR tại bàn, xem lịch sử đơn & tích điểm.

#### Mã sơ đồ Mermaid:
```mermaid
graph LR
    classDef actorStyle fill:#2C1A14,color:#ffffff,stroke:#E6B87D,stroke-width:2px;
    classDef ucStyle fill:#ffffff,color:#2C1A14,stroke:#8B5E3C,stroke-width:1.5px;

    subgraph System["☕ HỆ THỐNG CÔ ĐÀO QUÁN POS"]
        UC1["UC-01: Đăng nhập / Đăng xuất"]:::ucStyle
        UC2["UC-02: Bán hàng tại quầy POS 1-Click"]:::ucStyle
        UC3["UC-03: Thanh toán & In hóa đơn nhiệt 80mm"]:::ucStyle
        UC4["UC-04: Quản lý trạng thái bàn ăn & Mã QR"]:::ucStyle
        UC5["UC-05: Duyệt / Hủy đơn online"]:::ucStyle
        UC6["UC-06: Xem thực đơn & Đặt món online"]:::ucStyle
        UC7["UC-07: Quét mã QR đặt món tại bàn"]:::ucStyle
        UC8["UC-08: Theo dõi đơn hàng & Tích điểm"]:::ucStyle
        UC9["UC-09: Quản lý Sản phẩm & Danh mục"]:::ucStyle
        UC10["UC-10: Quản lý Nguyên liệu & Xuất/Nhập Kho"]:::ucStyle
        UC11["UC-11: Quản lý Nhân viên & Phân quyền"]:::ucStyle
        UC12["UC-12: Quản lý Khách hàng thân thiết"]:::ucStyle
        UC13["UC-13: Quản lý Voucher khuyến mãi"]:::ucStyle
        UC14["UC-14: Cấu hình Ngân hàng VietQR"]:::ucStyle
        UC15["UC-15: Báo cáo Doanh thu & Xuất Excel"]:::ucStyle
    end

    Customer(("🥤 Khách Hàng")):::actorStyle
    Staff(("👔 Nhân Viên")):::actorStyle
    Admin(("👑 Quản Lý")):::actorStyle

    Customer --> UC6
    Customer --> UC7
    Customer --> UC8

    Staff --> UC1
    Staff --> UC2
    Staff --> UC3
    Staff --> UC4
    Staff --> UC5

    Admin --> UC1
    Admin --> UC2
    Admin --> UC3
    Admin --> UC4
    Admin --> UC5
    Admin --> UC9
    Admin --> UC10
    Admin --> UC11
    Admin --> UC12
    Admin --> UC13
    Admin --> UC14
    Admin --> UC15
```

---

### 2️⃣ SƠ ĐỒ THỰC THỂ MỐI QUAN HỆ ERD (FULL ERD DIAGRAM)

Mô tả 17 thực thể CSDL với các khóa chính, khóa ngoại và quan hệ bản thể (Cardinality).

#### Mã sơ đồ Mermaid:
```mermaid
erDiagram
    ROLE ||--o{ USERS : "has"
    CATEGORY ||--o{ PRODUCT : "contains"
    PRODUCT ||--o{ PRODUCT_INGREDIENT : "requires"
    INGREDIENT ||--o{ PRODUCT_INGREDIENT : "used_in"
    INGREDIENT ||--o{ INVENTORY_TRANSACTION : "logs"
    
    CUSTOMER ||--o{ ORDERS : "places"
    CUSTOMER ||--o{ CUSTOMER_ORDER : "places_online"
    TABLE_CAFE ||--o{ ORDERS : "assigned_to"
    VOUCHER ||--o{ ORDERS : "applied_on"
    USERS ||--o{ ORDERS : "created_by"
    
    ORDERS ||--o{ ORDER_DETAIL : "contains"
    PRODUCT ||--o{ ORDER_DETAIL : "ordered_in"
    
    ORDERS ||--|| INVOICE : "generates"
    INVOICE ||--|| PAYMENT : "settled_by"
    
    CUSTOMER_ORDER ||--o{ CUSTOMER_ORDER_DETAIL : "contains"
    PRODUCT ||--o{ CUSTOMER_ORDER_DETAIL : "ordered_online"

    USERS {
        int user_id PK
        string username
        string password
        string full_name
        int role_id FK
    }
    PRODUCT {
        int product_id PK
        string product_name
        double price
        string image
        int category_id FK
    }
    INGREDIENT {
        int ingredient_id PK
        string ingredient_name
        int quantity
        string unit
    }
    ORDERS {
        int order_id PK
        date order_date
        double total_amount
        string status
        int customer_id FK
        int table_id FK
        int voucher_id FK
        int user_id FK
    }
    INVOICE {
        int invoice_id PK
        date invoice_date
        double total_amount
        int order_id FK
    }
    PAYMENT {
        int payment_id PK
        string payment_method
        string payment_status
        int invoice_id FK
    }
    BANK_SETTING {
        int id PK
        string bank_id
        string account_no
        string account_name
    }
```

---

### 3️⃣ SƠ ĐỒ LỚP KIẾN TRÚC 3 TẦNG (CLASS DIAGRAM - 3-TIER ARCHITECTURE)

Mô tả sự kết nối giữa các lớp Controller, Service, Repository và Entity trong Spring Boot.

#### Mã sơ đồ Mermaid:
```mermaid
classDiagram
    class OrdersController {
        -OrdersService ordersService
        -OrderDetailService orderDetailService
        -InvoiceService invoiceService
        -PaymentService paymentService
        +addOrder(Model model) String
        +posCheckout(...) String
    }

    class OrdersService {
        <<interface>>
        +getAllOrders() List~Orders~
        +saveOrder(Orders order) Orders
        +getOrderById(Integer id) Orders
    }

    class OrdersServiceImpl {
        -OrdersRepository ordersRepository
        +getAllOrders() List~Orders~
        +saveOrder(Orders order) Orders
    }

    class OrdersRepository {
        <<interface>>
        +findByStatus(String status) List~Orders~
    }

    class Orders {
        -Integer orderId
        -Date orderDate
        -Double totalAmount
        -String status
        -Customer customer
        -TableCafe tableCafe
        -Voucher voucher
        -User user
    }

    OrdersController --> OrdersService
    OrdersServiceImpl ..|> OrdersService
    OrdersServiceImpl --> OrdersRepository
    OrdersRepository --> Orders
```

---

### 4️⃣ SƠ ĐỒ TUẦN TỰ - LUỒNG BÁN HÀNG TẠI QUẦY POS (POS COUNTER CHECKOUT)

Mô tả chi tiết tương tác giữa các đối tượng khi thu ngân tạo đơn và bấm **"Thanh Toán & In Hóa Đơn POS"**.

#### Mã sơ đồ Mermaid:
```mermaid
sequenceDiagram
    autonumber
    actor Staff as 👔 Thu Ngân
    participant UI as 🖥️ Màn Hình POS (/orders/add)
    participant Controller as ⚙️ OrdersController
    participant OrderSvc as 📦 OrdersService
    participant RecipeRepo as 🧪 ProductIngredientRepo
    participant StockRepo as 🧱 IngredientRepo
    participant CustRepo as 👤 CustomerRepo
    participant InvoiceSvc as 🧾 InvoiceService
    participant PrintUI as 🖨️ Thermal Receipt (/invoice/{id})

    Staff->>UI: Chọn món uống, số bàn & bấm "Thanh Toán POS"
    UI->>Controller: POST /orders/pos-checkout (productIds, quantities, customerId, tableId)
    Controller->>OrderSvc: saveOrder(order)
    OrderSvc-->>Controller: Return Order object (ID: #1)
    
    loop Cho từng món trong đơn
        Controller->>RecipeRepo: findByProductProductId(productId)
        RecipeRepo-->>Controller: Danh sách công thức nguyên liệu
        Controller->>StockRepo: Trừ số lượng tồn kho (Ingredient.quantity - neededQty)
        Controller->>StockRepo: Lưu Nhật ký xuất kho (EXPORTS)
    end

    opt Nếu chọn Khách Hàng Thân Thiết
        Controller->>CustRepo: Tự động cộng điểm thưởng (10,000 VNĐ = +1 điểm)
    end

    Controller->>InvoiceSvc: saveInvoice(invoice)
    InvoiceSvc-->>Controller: Return Invoice object (ID: #1)
    Controller-->>UI: Redirect 302 /invoice/1
    UI->>PrintUI: GET /invoice/1
    PrintUI-->>Staff: Hiển thị phiếu in nhiệt 80mm & Mã VietQR tự động
```

---

### 5️⃣ SƠ ĐỒ TUẦN TỰ - LUỒNG ĐẶT MÓN ONLINE & ADMIN DUYỆT (ONLINE ORDER)

#### Mã sơ đồ Mermaid:
```mermaid
sequenceDiagram
    autonumber
    actor Customer as 🥤 Khách Hàng
    participant CustomerUI as 📱 Customer Menu (/customer/menu)
    participant CartCtrl as 🛒 CustomerCartController
    actor Admin as 👑 Admin / Thu Ngân
    participant AdminUI as 🖥️ Đơn Online (/admin/customer-orders)
    participant AdminCtrl as ⚙️ AdminCustomerOrderController

    Customer->>CustomerUI: Bấm "Thêm vào giỏ hàng"
    CustomerUI->>CartCtrl: GET /customer-cart/add/{customerId}/{productId}
    CartCtrl-->>CustomerUI: Cập nhật giỏ hàng CART
    Customer->>CustomerUI: Bấm "Đặt hàng Online"
    CustomerUI->>CartCtrl: GET /customer-order/checkout
    CartCtrl-->>CustomerUI: Đơn chuyển trạng thái PENDING

    Admin->>AdminUI: Xem danh sách đơn online chờ duyệt
    AdminUI->>AdminCtrl: GET /admin/customer-orders/approve/{id}
    AdminCtrl->>AdminCtrl: Tự động chuyển status COMPLETED, trừ kho & tích điểm
    AdminCtrl-->>AdminUI: Cập nhật danh sách đơn hoàn tất
```

---

### 6️⃣ SƠ ĐỒ TUẦN TỰ - LUỒNG VIETQR & IN HÓA ĐƠN 80MM

#### Mã sơ đồ Mermaid:
```mermaid
sequenceDiagram
    autonumber
    actor User as 👤 Người Dùng / Khách Hàng
    participant InvoiceCtrl as ⚙️ InvoiceController
    participant BankSvc as 🏛️ BankSettingService
    participant VietQR as 🌐 VietQR API (img.vietqr.io)
    participant ThermalPage as 🖨️ Trang In Hóa Đơn 80mm

    User->>InvoiceCtrl: GET /invoice/{id}
    InvoiceCtrl->>BankSvc: getBankSetting()
    BankSvc-->>InvoiceCtrl: Trả về thông tin STK, Tên Ngân Hàng, Chủ TK
    InvoiceCtrl->>VietQR: Tạo URL mã QR động theo Số Tiền + Mã Hóa Đơn
    InvoiceCtrl-->>ThermalPage: Trả về Model (invoice, orderDetails, bankSetting)
    ThermalPage-->>User: Hiển thị Hóa Đơn Nhiệt + Mã VietQR tự động
    User->>ThermalPage: Bấm nút "In Hóa Đơn (80mm)" (window.print)
```

---

### 7️⃣ SƠ ĐỒ HOẠT ĐỘNG - LUỒNG XỬ LÝ BÁN HÀNG POS THU NGÂN (ACTIVITY DIAGRAM - POS)

#### Mã sơ đồ Mermaid:
```mermaid
stateDiagram-v2
    [*] --> MoManHinhPOS: Thu ngân mở màn hình /orders/add
    MoManHinhPOS --> ChonMonUong: Chọn sản phẩm từ danh mục / Tìm kiếm
    ChonMonUong --> KiemTraGio: Kiểm tra danh sách món đặt
    
    state KiemTraGio {
        [*] --> TangGiamSoLuong
        TangGiamSoLuong --> ChonGiamGiaVoucher: Chọn Voucher (nếu có)
    }
    
    KiemTraGio --> ChonKhachHang: Chọn Khách lẻ hoặc Khách thành viên
    ChonKhachHang --> ChonViTriBan: Chọn Mang đi hoặc Số bàn ngồi
    ChonViTriBan --> ChonPhuongThuc: Chọn Tiền mặt hoặc VietQR
    ChonPhuongThuc --> BamThanhToan: Bấm "Thanh Toán POS"
    
    state KiemTraDieuKien <<choice>>
    BamThanhToan --> KiemTraDieuKien
    
    KiemTraDieuKien --> BaoLoi: Danh sách món rỗng (0 món)
    BaoLoi --> ChonMonUong: Yêu cầu chọn ít nhất 1 món
    
    KiemTraDieuKien --> TaoDonHang: Danh sách món hợp lệ
    TaoDonHang --> TuDongTruKho: Trừ nguyên liệu tồn kho theo công thức
    TuDongTruKho --> TuDongTichDiem: Cộng điểm tích lũy Khách hàng (10k = 1 điểm)
    TuDongTichDiem --> TaoHoaDonPayment: Tạo Hóa đơn & Giao dịch Payment
    TaoHoaDonPayment --> ChuyenTrangIn: Chuyển thẳng sang trang In Hóa Đơn Nhiệt 80mm
    ChuyenTrangIn --> [*]
```

---

### 8️⃣ SƠ ĐỒ HOẠT ĐỘNG - LUỒNG TỰ ĐỘNG TRỪ KHO NGUYÊN LIỆU & TÍCH ĐIỂM

#### Mã sơ đồ Mermaid:
```mermaid
flowchart TD
    Start([Bắt đầu Thanh toán Đơn hàng]) --> GetOrderDetails[Lấy danh sách món trong đơn]
    GetOrderDetails --> LoopProducts{Còn món trong đơn?}
    
    LoopProducts -- Có --> FetchRecipe[Tra cứu công thức ProductIngredient]
    FetchRecipe --> LoopRecipe{Còn nguyên liệu trong món?}
    
    LoopRecipe -- Có --> CalcQty[Tính lượng dùng: NeededQty = QuantityUsed * Quantity]
    CalcQty --> UpdateStock[Cập nhật Tồn kho: Ingredient.quantity -= NeededQty]
    UpdateStock --> LogTransaction[Lưu nhật ký InventoryTransaction EXPORT]
    LogTransaction --> LoopRecipe
    
    LoopRecipe -- Hết --> LoopProducts
    
    LoopProducts -- Hết --> CheckCustomer{Có chọn Khách hàng thành viên?}
    CheckCustomer -- Có --> CalcPoints[EarnedPoints = TotalAmount / 10000]
    CalcPoints --> AddPoints[Cập nhật Customer.point += EarnedPoints]
    AddPoints --> SaveCustomer[Lưu thông tin Khách hàng]
    CheckCustomer -- Không --> End([Hoàn tất xử lý tự động])
    SaveCustomer --> End
```

---

### 9️⃣ SƠ ĐỒ TRẠNG THÁI VÒNG ĐỜI ĐƠN HÀNG (STATE MACHINE DIAGRAM - ORDER)

Vòng đời chuyển trạng thái của một đơn hàng trong hệ thống.

#### Mã sơ đồ Mermaid:
```mermaid
stateDiagram-v2
    [*] --> CART: Khách hàng chọn món (Thêm vào giỏ)
    CART --> PENDING: Khách bấm "Đặt hàng Online"
    
    PENDING --> COMPLETED: Admin / Thu Ngân bấm "Approve (Duyệt đơn)"
    PENDING --> CANCELLED: Admin / Thu Ngân bấm "Cancel (Hủy đơn)"
    
    [*] --> CREATED: Thu ngân tạo đơn tại quầy POS (/orders/add)
    CREATED --> COMPLETED: Thu ngân bấm "Thanh Toán POS"
    
    COMPLETED --> PAID: Hệ thống tự động tạo Hóa đơn & Payment COMPLETED
    PAID --> [*]
    CANCELLED --> [*]
```

---

### 🔟 SƠ ĐỒ TRẠNG THÁI QUẢN LÝ BÀN ĂN (STATE MACHINE DIAGRAM - TABLE CAFE)

Vòng đời chuyển trạng thái của các bàn trong quán trà sữa (`TableCafe`).

#### Mã sơ đồ Mermaid:
```mermaid
stateDiagram-v2
    [*] --> EMPTY: Khởi tạo bàn mới (Bàn trống)
    
    EMPTY --> OCCUPIED: Khách đặt quầy gán số bàn / Khách quét mã QR tại bàn
    OCCUPIED --> OCCUPIED: Phục vụ món & Khách thưởng thức tại bàn
    
    OCCUPIED --> EMPTY: Thu ngân thanh toán hóa đơn (/table-payment/{id}) & Giải phóng bàn
    
    EMPTY --> OUT_OF_SERVICE: Bảo trì / Tạm ngưng phục vụ bàn
    OUT_OF_SERVICE --> EMPTY: Bật lại hoạt động bàn
```
