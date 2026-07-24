package com.milktea.service;

import com.milktea.entity.BankSetting;

public interface BankSettingService {

    BankSetting getBankSetting();

    BankSetting saveBankSetting(BankSetting bankSetting);

    String generateVietQrUrl(Double amount, String note);
}
