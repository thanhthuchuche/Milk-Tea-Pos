package com.milktea.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "customer_notification")
public class CustomerNotification {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer notificationId;
    @ManyToOne @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    private String title;
    @Column(length = 1000) private String message;
    private Integer orderId;
    @Temporal(TemporalType.TIMESTAMP) private Date createdAt;
    private boolean read;
    public Integer getNotificationId() { return notificationId; }
    public void setNotificationId(Integer notificationId) { this.notificationId = notificationId; }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Integer getOrderId() { return orderId; }
    public void setOrderId(Integer orderId) { this.orderId = orderId; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }
}
