package com.milktea.service.impl;

import com.milktea.entity.TableCafe;
import com.milktea.repository.TableCafeRepository;
import com.milktea.service.TableCafeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TableCafeServiceImpl implements TableCafeService {

    private final TableCafeRepository tableCafeRepository;

    public TableCafeServiceImpl(
            TableCafeRepository tableCafeRepository) {
        this.tableCafeRepository = tableCafeRepository;
    }

    @Override
    public List<TableCafe> getAllTables() {
        return tableCafeRepository.findAll();
    }

    @Override
    public TableCafe getTableById(Integer id) {
        return tableCafeRepository.findById(id).orElse(null);
    }

    @Override
    public TableCafe saveTable(TableCafe table) {
        return tableCafeRepository.save(table);
    }

    @Override
    public void deleteTable(Integer id) {
        tableCafeRepository.deleteById(id);
    }
}