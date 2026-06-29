package com.milktea.config;

import com.milktea.entity.*;
import com.milktea.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

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

        // 3. Seed Customer with ID 1 for hardcoded reference
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

        // 4. Seed Table Cafe
        if (tableCafeRepository.count() == 0) {
            TableCafe table1 = new TableCafe();
            table1.setTableNumber("Bàn 01");
            table1.setStatus("TRONG");
            table1.setQrCode("qr_table_1");
            tableCafeRepository.save(table1);

            TableCafe table2 = new TableCafe();
            table2.setTableNumber("Bàn 02");
            table2.setStatus("TRONG");
            table2.setQrCode("qr_table_2");
            tableCafeRepository.save(table2);

            TableCafe table3 = new TableCafe();
            table3.setTableNumber("Bàn 03");
            table3.setStatus("TRONG");
            table3.setQrCode("qr_table_3");
            tableCafeRepository.save(table3);

            TableCafe table4 = new TableCafe();
            table4.setTableNumber("Bàn 04");
            table4.setStatus("TRONG");
            table4.setQrCode("qr_table_4");
            tableCafeRepository.save(table4);
        }

        // 5. Seed Category
        if (categoryRepository.count() == 0) {
            Category milkTea = new Category();
            milkTea.setCategoryName("Trà Sữa");
            milkTea.setDescription("Các loại trà sữa thơm ngon béo ngậy");
            categoryRepository.save(milkTea);

            Category fruitTea = new Category();
            fruitTea.setCategoryName("Trà Trái Cây");
            fruitTea.setDescription("Trà hoa quả thanh mát giải nhiệt");
            categoryRepository.save(fruitTea);

            Category coffee = new Category();
            coffee.setCategoryName("Cà Phê");
            coffee.setDescription("Cà phê rang xay chuẩn vị Việt");
            categoryRepository.save(coffee);

            Category topping = new Category();
            topping.setCategoryName("Toppings");
            topping.setDescription("Các loại topping thêm kèm phong phú");
            categoryRepository.save(topping);
        }

        // 6. Seed Product
        if (productRepository.count() == 0) {
            Category milkTea = categoryRepository.findAll().stream()
                    .filter(c -> "Trà Sữa".equals(c.getCategoryName()))
                    .findFirst().orElse(null);

            Category fruitTea = categoryRepository.findAll().stream()
                    .filter(c -> "Trà Trái Cây".equals(c.getCategoryName()))
                    .findFirst().orElse(null);

            Category coffee = categoryRepository.findAll().stream()
                    .filter(c -> "Cà Phê".equals(c.getCategoryName()))
                    .findFirst().orElse(null);

            Category topping = categoryRepository.findAll().stream()
                    .filter(c -> "Toppings".equals(c.getCategoryName()))
                    .findFirst().orElse(null);

            if (milkTea != null) {
                Product p1 = new Product();
                p1.setProductName("Trà sữa truyền thống");
                p1.setPrice(29000.0);
                p1.setImage("https://images.unsplash.com/photo-1541658016709-82535e94bc69?q=80&w=600&auto=format&fit=crop");
                p1.setDescription("Trà sữa vị truyền thống ngọt ngào đậm vị trà");
                p1.setStatus("ACTIVE");
                p1.setCategory(milkTea);
                productRepository.save(p1);

                Product p2 = new Product();
                p2.setProductName("Trà sữa trân châu đường đen");
                p2.setPrice(35000.0);
                p2.setImage("https://images.unsplash.com/photo-1507133750040-4a8f57021571?q=80&w=600&auto=format&fit=crop");
                p2.setDescription("Sự kết hợp hoàn hảo giữa sữa tươi và trân châu đường đen");
                p2.setStatus("ACTIVE");
                p2.setCategory(milkTea);
                productRepository.save(p2);
            }

            if (fruitTea != null) {
                Product p3 = new Product();
                p3.setProductName("Hồng trà tắc");
                p3.setPrice(25000.0);
                p3.setImage("https://images.unsplash.com/photo-1556679343-c7306c1976bc?q=80&w=600&auto=format&fit=crop");
                p3.setDescription("Hồng trà kết hợp quả tắc tươi mát chua ngọt");
                p3.setStatus("ACTIVE");
                p3.setCategory(fruitTea);
                productRepository.save(p3);

                Product p4 = new Product();
                p4.setProductName("Trà đào cam sả");
                p4.setPrice(32000.0);
                p4.setImage("https://images.unsplash.com/photo-1513558161293-cdaf765ed2fd?q=80&w=600&auto=format&fit=crop");
                p4.setDescription("Trà đào thơm lừng hương sả và nước cam tươi mát");
                p4.setStatus("ACTIVE");
                p4.setCategory(fruitTea);
                productRepository.save(p4);
            }

            if (coffee != null) {
                Product p5 = new Product();
                p5.setProductName("Bạc xỉu đá");
                p5.setPrice(25000.0);
                p5.setImage("https://images.unsplash.com/photo-1578314675249-a6910f80cc4e?q=80&w=600&auto=format&fit=crop");
                p5.setDescription("Cà phê thơm nồng quyện với vị sữa béo ngậy");
                p5.setStatus("ACTIVE");
                p5.setCategory(coffee);
                productRepository.save(p5);

                Product p6 = new Product();
                p6.setProductName("Cà phê đen đá");
                p6.setPrice(20000.0);
                p6.setImage("https://images.unsplash.com/photo-1509042239860-f550ce710b93?q=80&w=600&auto=format&fit=crop");
                p6.setDescription("Cà phê đen nguyên chất pha phin truyền thống");
                p6.setStatus("ACTIVE");
                p6.setCategory(coffee);
                productRepository.save(p6);
            }

            if (topping != null) {
                Product p7 = new Product();
                p7.setProductName("Trân châu đen");
                p7.setPrice(5000.0);
                p7.setImage("https://images.unsplash.com/photo-1598214886806-c87b80b7078b?q=80&w=600&auto=format&fit=crop");
                p7.setDescription("Trân châu đen giòn dai sần sật");
                p7.setStatus("ACTIVE");
                p7.setCategory(topping);
                productRepository.save(p7);

                Product p8 = new Product();
                p8.setProductName("Thạch nha đam");
                p8.setPrice(5000.0);
                p8.setImage("https://images.unsplash.com/photo-1563245372-f21724e3856d?q=80&w=600&auto=format&fit=crop");
                p8.setDescription("Thạch nha đam giòn giòn thanh mát");
                p8.setStatus("ACTIVE");
                p8.setCategory(topping);
                productRepository.save(p8);
            }
        }

        // 7. Seed Ingredients
        if (ingredientRepository.count() == 0) {
            Ingredient i1 = new Ingredient();
            i1.setIngredientName("Sữa tươi");
            i1.setUnit("Lít");
            i1.setQuantity(50);
            ingredientRepository.save(i1);

            Ingredient i2 = new Ingredient();
            i2.setIngredientName("Trà đen");
            i2.setUnit("Kg");
            i2.setQuantity(15);
            ingredientRepository.save(i2);

            Ingredient i3 = new Ingredient();
            i3.setIngredientName("Trân châu đen");
            i3.setUnit("Kg");
            i3.setQuantity(8); // This triggers "Sắp hết" since <= 20
            ingredientRepository.save(i3);

            Ingredient i4 = new Ingredient();
            i4.setIngredientName("Đường cát");
            i4.setUnit("Kg");
            i4.setQuantity(30);
            ingredientRepository.save(i4);

            Ingredient i5 = new Ingredient();
            i5.setIngredientName("Đá bi");
            i5.setUnit("Bao");
            i5.setQuantity(12); // This triggers "Sắp hết"
            ingredientRepository.save(i5);
        }

        // 8. Seed Vouchers
        if (voucherRepository.count() == 0) {
            Voucher v1 = new Voucher();
            v1.setVoucherCode("CHAOCUAHANG");
            v1.setDiscountPercent(10);
            v1.setStartDate(LocalDate.now());
            v1.setEndDate(LocalDate.now().plusMonths(3));
            voucherRepository.save(v1);

            Voucher v2 = new Voucher();
            v2.setVoucherCode("GIAMGIA20");
            v2.setDiscountPercent(20);
            v2.setStartDate(LocalDate.now());
            v2.setEndDate(LocalDate.now().plusMonths(3));
            voucherRepository.save(v2);

            Voucher v3 = new Voucher();
            v3.setVoucherCode("TRIANVIP");
            v3.setDiscountPercent(30);
            v3.setStartDate(LocalDate.now());
            v3.setEndDate(LocalDate.now().plusMonths(3));
            voucherRepository.save(v3);
        }
    }
}
