package com.milktea.service;

import com.milktea.entity.InventoryTransaction;
import java.util.List;

public interface InventoryTransactionService {

    List<InventoryTransaction> getAllTransactions();

    InventoryTransaction getTransactionById(Integer id);

    void saveTransaction(InventoryTransaction transaction);

    void deleteTransaction(Integer id);
}