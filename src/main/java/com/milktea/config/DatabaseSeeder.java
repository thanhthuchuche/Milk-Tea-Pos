package com.milktea.config;

import com.milktea.entity.*;
import com.milktea.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final TableCafeRepository tableCafeRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final IngredientRepository ingredientRepository;
    private final VoucherRepository voucherRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseSeeder(
            RoleRepository roleRepository,
            UserRepository userRepository,
            CustomerRepository customerRepository,
            TableCafeRepository tableCafeRepository,
            CategoryRepository categoryRepository,
            ProductRepository productRepository,
            IngredientRepository ingredientRepository,
            VoucherRepository voucherRepository,
            PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.tableCafeRepository = tableCafeRepository;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.ingredientRepository = ingredientRepository;
        this.voucherRepository = voucherRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // 1. Seed Roles
        if (roleRepository.count() == 0) {
            Role adminRole = new Role();
            adminRole.setRoleName("ADMIN");
            adminRole.setDescription("Quản lý hệ thống");
            roleRepository.save(adminRole);

            Role staffRole = new Role();
            staffRole.setRoleName("STAFF");
            staffRole.setDescription("Nhân viên bán hàng");
            roleRepository.save(staffRole);

            Role customerRole = new Role();
            customerRole.setRoleName("CUSTOMER");
            customerRole.setDescription("Khách hàng");
            roleRepository.save(customerRole);
        }

        // 2. Seed Users
        if (userRepository.count() == 0) {
            Role adminRole = roleRepository.findAll().stream()
                    .filter(r -> "ADMIN".equals(r.getRoleName()))
                    .findFirst().orElse(null);

            Role staffRole = roleRepository.findAll().stream()
                    .filter(r -> "STAFF".equals(r.getRoleName()))
                    .findFirst().orElse(null);

            Role customerRole = roleRepository.findAll().stream()
                    .filter(r -> "CUSTOMER".equals(r.getRoleName()))
                    .findFirst().orElse(null);

            if (adminRole != null) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("123456"));
                admin.setFullName("Quản lý Cô Đào");
                admin.setPhone("0912345678");
                admin.setEmail("admin@codaoquan.com");
                admin.setStatus("ACTIVE");
                admin.setRole(adminRole);
                userRepository.save(admin);
            }

            if (staffRole != null) {
                User staff = new User();
                staff.setUsername("staff");
                staff.setPassword(passwordEncoder.encode("123456"));
                staff.setFullName("Nhân viên Cô Đào");
                staff.setPhone("0987654321");
                staff.setEmail("staff@codaoquan.com");
                staff.setStatus("ACTIVE");
                staff.setRole(staffRole);
                userRepository.save(staff);
            }

            if (customerRole != null) {
                User customerUser = new User();
                customerUser.setUsername("customer");
                customerUser.setPassword(passwordEncoder.encode("123456"));
                customerUser.setFullName("Khách Thân Thiết");
                customerUser.setPhone("0909090909");
                customerUser.setEmail("customer@gmail.com");
                customerUser.setStatus("ACTIVE");
                customerUser.setRole(customerRole);
                userRepository.save(customerUser);
            }
        }

        // 3. Seed Customer
        if (customerRepository.count() == 0) {
            Customer defaultCustomer = new Customer();
            defaultCustomer.setFullName("Khách Hàng Mẫu");
            defaultCustomer.setPhone("0999999999");
            defaultCustomer.setEmail("khach_mau@codaoquan.com");
            defaultCustomer.setPoint(100);
            customerRepository.save(defaultCustomer);

            Customer vipCustomer = new Customer();
            vipCustomer.setFullName("Nguyễn Văn A");
            vipCustomer.setPhone("0911223344");
            vipCustomer.setEmail("anguyen@gmail.com");
            vipCustomer.setPoint(250);
            customerRepository.save(vipCustomer);
        }

        // 4. Seed Tables
        if (tableCafeRepository.count() == 0) {
            for (int i = 1; i <= 6; i++) {
                TableCafe t = new TableCafe();
                t.setTableNumber("Bàn 0" + i);
                t.setStatus("TRONG");
                t.setQrCode("qr_table_" + i);
                tableCafeRepository.save(t);
            }
        }

        // 5. Seed Category & Products (Enriched Menu)
        if (productRepository.count() < 10) {
            productRepository.deleteAll();
            categoryRepository.deleteAll();

            // Categories
            Category cMilkTea = createCategory("Trà Sữa Signature", "Các loại trà sữa hảo hạng thơm béo chuẩn vị Cô Đào");
            Category cFruitTea = createCategory("Trà Trái Cây Tươi", "Trà hoa quả 100% trái cây tươi giải nhiệt thanh mát");
            Category cCoffee = createCategory("Cà Phê & Macchiato", "Cà phê rang xay đậm đà kết hợp lớp váng sữa béo ngậy");
            Category cSmoothies = createCategory("Đá Xay & Smoothies", "Đồ uống đá xay mát lạnh sảng khoái ngày hè");
            Category cToppings = createCategory("Toppings Cao Cấp", "Toppings giòn dai siêu cuốn cho ly nước thêm tròn vị");
            Category cBakery = createCategory("Bánh Ngọt & Snack", "Bánh tươi nướng trong ngày dùng kèm trà thơm");

            // 1. Trà Sữa Signature
            createProduct("Trà Sữa Trân Châu Hoàng Gia", 39000.0, "https://images.unsplash.com/photo-1541658016709-82535e94bc69?q=80&w=600&auto=format&fit=crop", "Vị trà đen nồng nàn hòa quyện sữa tươi thanh béo và trân châu dẻo thơm", cMilkTea);
            createProduct("Trà Sữa Ô Long Nướng Kem Cheese", 45000.0, "https://images.unsplash.com/photo-1576092768241-dec231879fc3?q=80&w=600&auto=format&fit=crop", "Trà Ô long nướng thơm lừng phủ lớp kem phô mai béo mặn đượm vị", cMilkTea);
            createProduct("Trà Sữa Matcha Trân Châu Trắng", 42000.0, "https://images.unsplash.com/photo-1536256263959-770b48d82b0a?q=80&w=600&auto=format&fit=crop", "Matcha Uji Nhật Bản nguyên chất ngậy béo cùng trân châu giòn sần sật", cMilkTea);
            createProduct("Trà Sữa Đường Đen Trứng Nướng", 49000.0, "https://images.unsplash.com/photo-1507133750040-4a8f57021571?q=80&w=600&auto=format&fit=crop", "Lớp kem trứng nướng cháy xèo quyện trân châu đường đen nóng dẻo", cMilkTea);

            // 2. Trà Trái Cây Tươi
            createProduct("Trà Đào Cam Sả Tươi", 38000.0, "https://images.unsplash.com/photo-1513558161293-cdaf765ed2fd?q=80&w=600&auto=format&fit=crop", "Trà đào mọng nước sảng khoái với mứt cam tươi và sả đập dập thơm nồng", cFruitTea);
            createProduct("Trà Dâu Tằm Pha Lê", 42000.0, "https://images.unsplash.com/photo-1556679343-c7306c1976bc?q=80&w=600&auto=format&fit=crop", "Dâu tằm tươi mọng ngọt thanh mát kết hợp thạch pha lê giòn", cFruitTea);
            createProduct("Hồng Trà Tắc Mật Ong", 32000.0, "https://images.unsplash.com/photo-1595981267035-7b04ca84a82d?q=80&w=600&auto=format&fit=crop", "Hồng trà ủ đậm đà hòa mật ong hoa rừng chua ngọt dịu mát", cFruitTea);
            createProduct("Trà Vải Lục Trà Hoa Lài", 39000.0, "https://images.unsplash.com/photo-1544787219-7f47ccb76574?q=80&w=600&auto=format&fit=crop", "Trà nhài thơm ngát cùng những trái vải ngâm đường giòn ngọt", cFruitTea);

            // 3. Cà Phê & Macchiato
            createProduct("Cà Phê Muối Kem Béo Cô Đào", 35000.0, "https://images.unsplash.com/photo-1578314675249-a6910f80cc4e?q=80&w=600&auto=format&fit=crop", "Cà phê Robusta đậm đặc lớp kem muối sánh mịn đắng ngọt mặn béo", cCoffee);
            createProduct("Bạc Xỉu Sữa Dừa Đá", 35000.0, "https://images.unsplash.com/photo-1514432324607-a09d9b4aefdd?q=80&w=600&auto=format&fit=crop", "Cà phê phin truyền thống hòa nước cốt dừa thơm phức ngậy béo", cCoffee);
            createProduct("Cà Phê Đen Đá Phin", 25000.0, "https://images.unsplash.com/photo-1509042239860-f550ce710b93?q=80&w=600&auto=format&fit=crop", "Cà phê Robusta rang mộc chuẩn vị đắng nồng sảng khoái tỉnh táo", cCoffee);

            // 4. Đá Xay & Smoothies
            createProduct("Matcha Cookie Freeze", 48000.0, "https://images.unsplash.com/photo-1572490122747-3968b75cc699?q=80&w=600&auto=format&fit=crop", "Matcha đá xay vụn bánh Oreo và xốt sô-cô-la phủ kem tươi ngậy", cSmoothies);
            createProduct("Sinh Tố Bơ Kem Dừa", 45000.0, "https://images.unsplash.com/photo-1553530666-ba11a7da3888?q=80&w=600&auto=format&fit=crop", "Bơ sáp Đắc Lắk xay nhuyễn dừa nạo và sữa đặc ngậy lừng", cSmoothies);

            // 5. Toppings Cao Cấp
            createProduct("Trân Châu Hoàng Gia", 8000.0, "https://images.unsplash.com/photo-1598214886806-c87b80b7078b?q=80&w=600&auto=format&fit=crop", "Trân châu đen nấu mật ong dẻo giòn đậm vị ngọt thanh", cToppings);
            createProduct("Kem Cheese Trứng Nướng", 10000.0, "https://images.unsplash.com/photo-1563245372-f21724e3856d?q=80&w=600&auto=format&fit=crop", "Lớp kem phô mai đánh bông béo mặn sánh mịn quyến rũ", cToppings);
            createProduct("Thạch Khúc Bạch Phô Mai", 10000.0, "https://images.unsplash.com/photo-1587314168485-3236d6710814?q=80&w=600&auto=format&fit=crop", "Thạch khúc bạch làm từ sữa tươi và phô mai mềm mịn tan chảy", cToppings);

            // 6. Bánh Ngọt & Snack
            createProduct("Bánh Croissant Bơ Pháp", 35000.0, "https://images.unsplash.com/photo-1555507036-ab1f4038808a?q=80&w=600&auto=format&fit=crop", "Bánh sừng bò nướng nóng giòn xốp nhiều lớp béo ngậy bơ Pháp", cBakery);
            createProduct("Tiramisu Kakao Matcha", 42000.0, "https://images.unsplash.com/photo-1571877227200-a0d98ea607e9?q=80&w=600&auto=format&fit=crop", "Bánh Tiramisu Ý mềm mịn đắng cacao nồng nàn vị kem Mascarpone", cBakery);
        }

        // 6. Seed Ingredients
        if (ingredientRepository.count() == 0) {
            createIngredient("Sữa tươi Thanh Trùng", 50, "Lít");
            createIngredient("Trà Đen Cốt Ô Long", 15, "Kg");
            createIngredient("Trân Châu Hoàng Gia", 8, "Kg");
            createIngredient("Mứt Đào Tươi", 12, "Hũ");
            createIngredient("Cà Phê Robusta Hạt", 25, "Kg");
            createIngredient("Bơ Sáp Đắc Lắk", 18, "Kg");
        }

        // 7. Seed Vouchers
        if (voucherRepository.count() == 0) {
            createVoucher("CHAOCUAHANG", 10);
            createVoucher("CODAOQUAN20", 20);
            createVoucher("TRIANVIP30", 30);
        } else {
            voucherRepository.findAll().stream()
                    .filter(voucher -> voucher.getRequiredPoints() == null)
                    .forEach(voucher -> {
                        voucher.setRequiredPoints(Math.max(50, (voucher.getDiscountPercent() == null ? 10 : voucher.getDiscountPercent()) * 5));
                        voucher.setRewardDescription("Voucher giảm " + voucher.getDiscountPercent() + "% cho thành viên.");
                        voucherRepository.save(voucher);
                    });
        }
    }

    private Category createCategory(String name, String desc) {
        Category c = new Category();
        c.setCategoryName(name);
        c.setDescription(desc);
        return categoryRepository.save(c);
    }

    private void createProduct(String name, Double price, String img, String desc, Category category) {
        Product p = new Product();
        p.setProductName(name);
        p.setPrice(price);
        p.setImage(img);
        p.setDescription(desc);
        p.setStatus("ACTIVE");
        p.setCategory(category);
        productRepository.save(p);
    }

    private void createIngredient(String name, int qty, String unit) {
        Ingredient i = new Ingredient();
        i.setIngredientName(name);
        i.setQuantity(qty);
        i.setUnit(unit);
        ingredientRepository.save(i);
    }

    private void createVoucher(String code, int discount) {
        Voucher v = new Voucher();
        v.setVoucherCode(code);
        v.setDiscountPercent(discount);
        v.setRequiredPoints(discount * 5);
        v.setRewardDescription("Voucher giảm " + discount + "% cho thành viên.");
        v.setStartDate(LocalDate.now());
        v.setEndDate(LocalDate.now().plusMonths(3));
        voucherRepository.save(v);
    }
}
