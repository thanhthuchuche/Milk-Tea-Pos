package com.milktea.controller;

import com.milktea.entity.Invoice;
import com.milktea.entity.Orders;
import com.milktea.entity.Product;
import com.milktea.service.InvoiceService;
import com.milktea.service.OrdersService;
import com.milktea.service.ProductService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.List;

@Controller
public class ReportExportController {

    private final InvoiceService invoiceService;
    private final OrdersService ordersService;
    private final ProductService productService;

    public ReportExportController(InvoiceService invoiceService, OrdersService ordersService, ProductService productService) {
        this.invoiceService = invoiceService;
        this.ordersService = ordersService;
        this.productService = productService;
    }

    @GetMapping("/reports/export-invoices-csv")
    public void exportInvoicesCsv(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"Bao_Cao_Hoa_Don_Co_Dao_Quan.csv\"");

        PrintWriter writer = response.getWriter();
        // UTF-8 BOM for Microsoft Excel auto-encoding detection
        writer.write('\uFEFF');

        writer.println("Mã Hóa Đơn,Mã Đơn Hàng,Ngày Xuất,Tổng Tiền (VNĐ)");

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        List<Invoice> invoices = invoiceService.getAllInvoices();
        for (Invoice inv : invoices) {
            String invId = inv.getInvoiceId() != null ? inv.getInvoiceId().toString() : "";
            String orderId = inv.getOrders() != null ? inv.getOrders().getOrderId().toString() : "";
            String dateStr = inv.getInvoiceDate() != null ? sdf.format(inv.getInvoiceDate()) : "";
            String amount = inv.getTotalAmount() != null ? String.format("%.0f", inv.getTotalAmount()) : "0";

            writer.println(String.format("%s,%s,%s,%s", invId, orderId, dateStr, amount));
        }
        writer.flush();
    }

    @GetMapping("/reports/export-orders-csv")
    public void exportOrdersCsv(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"Bao_Cao_Don_Hang_Co_Dao_Quan.csv\"");

        PrintWriter writer = response.getWriter();
        writer.write('\uFEFF');

        writer.println("Mã Đơn,Khách Hàng,Bàn,Nhân Viên,Ngày Tạo,Tổng Tiền (VNĐ),Trạng Thái");

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        List<Orders> orders = ordersService.getAllOrders();
        for (Orders o : orders) {
            String id = o.getOrderId() != null ? o.getOrderId().toString() : "";
            String cust = o.getCustomer() != null ? o.getCustomer().getFullName() : "Khách lẻ";
            String table = o.getTableCafe() != null ? o.getTableCafe().getTableNumber() : "Mang về";
            String staff = o.getUser() != null ? o.getUser().getFullName() : "Khách tự gọi";
            String dateStr = o.getOrderDate() != null ? sdf.format(o.getOrderDate()) : "";
            String amount = o.getTotalAmount() != null ? String.format("%.0f", o.getTotalAmount()) : "0";
            String status = o.getStatus() != null ? o.getStatus() : "";

            writer.println(String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",%s,\"%s\"",
                    id, cust, table, staff, dateStr, amount, status));
        }
        writer.flush();
    }

    @GetMapping("/reports/export-products-csv")
    public void exportProductsCsv(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"Danh_Sach_San_Pham_Co_Dao_Quan.csv\"");

        PrintWriter writer = response.getWriter();
        writer.write('\uFEFF');

        writer.println("Mã SP,Tên Sản Phẩm,Danh Mục,Giá Bán (VNĐ),Trạng Thái");

        List<Product> products = productService.getAllProducts();
        for (Product p : products) {
            String id = p.getProductId() != null ? p.getProductId().toString() : "";
            String name = p.getProductName() != null ? p.getProductName() : "";
            String cat = p.getCategory() != null ? p.getCategory().getCategoryName() : "Khác";
            String price = p.getPrice() != null ? String.format("%.0f", p.getPrice()) : "0";
            String status = p.getStatus() != null ? p.getStatus() : "";

            writer.println(String.format("\"%s\",\"%s\",\"%s\",%s,\"%s\"",
                    id, name, cat, price, status));
        }
        writer.flush();
    }
}
