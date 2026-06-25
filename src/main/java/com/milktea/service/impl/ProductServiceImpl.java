package com.milktea.service.impl;

import com.milktea.entity.Product;
import com.milktea.repository.ProductRepository;
import com.milktea.service.ProductService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("productService")
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product getProductById(Integer id) {
        return productRepository.findById(id).orElse(null);
    }

    @Override
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public void deleteProduct(Integer id) {
        productRepository.deleteById(id);
    }

    @Override
    public List<Product> searchProducts(String keyword) {

        return productRepository
                .findByProductNameContaining(keyword);

    }
    @Override
    public List<Product> getProductsByCategory(Integer categoryId) {

        return productRepository
                .findByCategory_CategoryId(categoryId);

    }
}