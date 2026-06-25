package com.milktea.service;

import com.milktea.entity.Voucher;
import java.util.List;

public interface VoucherService {

    List<Voucher> getAllVouchers();

    Voucher getVoucherById(Integer id);

    Voucher saveVoucher(Voucher voucher);

    void deleteVoucher(Integer id);
}