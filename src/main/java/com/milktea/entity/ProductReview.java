package com.milktea.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "product_review", uniqueConstraints = @UniqueConstraint(columnNames = {"customer_id", "product_id"}))
public class ProductReview {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer reviewId;
    @ManyToOne @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    @ManyToOne @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    private Integer rating;
    @Column(length = 1000) private String comment;
    @Temporal(TemporalType.TIMESTAMP) private Date createdAt;
    private String status;
    public Integer getReviewId() { return reviewId; }
    public void setReviewId(Integer reviewId) { this.reviewId = reviewId; }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
