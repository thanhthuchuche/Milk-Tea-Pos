package com.milktea.repository;

import com.milktea.entity.BankSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankSettingRepository extends JpaRepository<BankSetting, Integer> {
}
