package com.milktea.repository;

import com.milktea.entity.TableCafe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface TableCafeRepository
        extends JpaRepository<TableCafe, Integer> {

    // khi thanh toán xong
    @Modifying
    @Transactional
    @Query("""
            UPDATE TableCafe t
            SET t.status='DA_THANH_TOAN'
            WHERE t.tableId=:tableId
            """)
    void updatePaid(Integer tableId);


    // khi bắt đầu gọi món
    @Modifying
    @Transactional
    @Query("""
            UPDATE TableCafe t
            SET t.status='CHUA_THANH_TOAN'
            WHERE t.tableId=:tableId
            """)
    void updateUnpaid(Integer tableId);

}