package com.milktea.service.impl;

import com.milktea.entity.BankSetting;
import com.milktea.repository.BankSettingRepository;
import com.milktea.service.BankSettingService;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class BankSettingServiceImpl implements BankSettingService {

    private final BankSettingRepository bankSettingRepository;

    public BankSettingServiceImpl(BankSettingRepository bankSettingRepository) {
        this.bankSettingRepository = bankSettingRepository;
    }

    @Override
    public BankSetting getBankSetting() {
        List<BankSetting> list = bankSettingRepository.findAll();
        if (!list.isEmpty()) {
            return list.get(0);
        }
        // Default Bank Setting if none exists
        BankSetting defaultSetting = new BankSetting();
        defaultSetting.setBankId("MB");
        defaultSetting.setBankName("Ngân hàng Quân Đội (MBBank)");
        defaultSetting.setAccountNo("0912345678");
        defaultSetting.setAccountName("CO DAO QUAN");
        return bankSettingRepository.save(defaultSetting);
    }

    @Override
    public BankSetting saveBankSetting(BankSetting bankSetting) {
        BankSetting existing = getBankSetting();
        existing.setBankId(bankSetting.getBankId());
        existing.setBankName(bankSetting.getBankName());
        existing.setAccountNo(bankSetting.getAccountNo());
        existing.setAccountName(bankSetting.getAccountName());
        return bankSettingRepository.save(existing);
    }

    @Override
    public String generateVietQrUrl(Double amount, String note) {
        BankSetting setting = getBankSetting();
        long amt = amount != null ? amount.longValue() : 0L;
        String encodedNote = note != null ? URLEncoder.encode(note, StandardCharsets.UTF_8) : "";
        String encodedName = setting.getAccountName() != null ? URLEncoder.encode(setting.getAccountName(), StandardCharsets.UTF_8) : "";
        
        // VietQR Image API format: https://img.vietqr.io/image/<BANK_ID>-<ACCOUNT_NO>-compact2.png?amount=<AMOUNT>&addInfo=<NOTE>&accountName=<NAME>
        return String.format("https://img.vietqr.io/image/%s-%s-compact2.png?amount=%d&addInfo=%s&accountName=%s",
                setting.getBankId(), setting.getAccountNo(), amt, encodedNote, encodedName);
    }
}
