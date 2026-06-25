package com.milktea.service;

import com.milktea.entity.Product;
import java.util.List;

public interface ProductService {
    List<Product> searchProducts(String keyword);

    List<Product> getAllProducts();

    Product getProductById(Integer id);

    Product saveProduct(Product product);

    void deleteProduct(Integer id);

    List<Product> getProductsByCategory(Integer categoryId);
}