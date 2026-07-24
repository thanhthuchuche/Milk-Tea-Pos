package com.milktea.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "bank_settings")
public class BankSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String bankId;       // Short code e.g. MB, VCB, TCB, ACB, BIDV
    private String bankName;     // Full name e.g. Ngân hàng Quân Đội (MBBank)
    private String accountNo;    // Account Number e.g. 0912345678
    private String accountName;  // Owner Name e.g. NGUYEN THAI THANH THU

    public BankSetting() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }
}
