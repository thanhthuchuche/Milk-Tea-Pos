package com.milktea.service.impl;

import com.milktea.entity.InventoryTransaction;
import com.milktea.repository.InventoryTransactionRepository;
import com.milktea.service.InventoryTransactionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryTransactionServiceImpl
        implements InventoryTransactionService {

    private final InventoryTransactionRepository repository;

    public InventoryTransactionServiceImpl(
            InventoryTransactionRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<InventoryTransaction> getAllTransactions() {
        return repository.findAll();
    }

    @Override
    public InventoryTransaction getTransactionById(Integer id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public void saveTransaction(InventoryTransaction transaction) {
        repository.save(transaction);
    }

    @Override
    public void deleteTransaction(Integer id) {
        repository.deleteById(id);
    }
}