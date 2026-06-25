package com.milktea.dto;

public class ProductSalesDTO {

    private String productName;

    private Long totalSold;

    public ProductSalesDTO(
            String productName,
            Long totalSold) {

        this.productName = productName;
        this.totalSold = totalSold;
    }

    public String getProductName() {
        return productName;
    }

    public Long getTotalSold() {
        return totalSold;
    }

}