package com.milktea.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "customer_voucher", uniqueConstraints = @UniqueConstraint(columnNames = {"customer_id", "voucher_id"}))
public class CustomerVoucher {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer customerVoucherId;
    @ManyToOne @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    @ManyToOne @JoinColumn(name = "voucher_id", nullable = false)
    private Voucher voucher;
    @Temporal(TemporalType.TIMESTAMP) private Date redeemedAt;
    private String status;
    public Integer getCustomerVoucherId() { return customerVoucherId; }
    public void setCustomerVoucherId(Integer customerVoucherId) { this.customerVoucherId = customerVoucherId; }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public Voucher getVoucher() { return voucher; }
    public void setVoucher(Voucher voucher) { this.voucher = voucher; }
    public Date getRedeemedAt() { return redeemedAt; }
    public void setRedeemedAt(Date redeemedAt) { this.redeemedAt = redeemedAt; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
