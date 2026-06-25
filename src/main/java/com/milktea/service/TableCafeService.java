package com.milktea.service;

import com.milktea.entity.TableCafe;
import java.util.List;

public interface TableCafeService {

    List<TableCafe> getAllTables();

    TableCafe getTableById(Integer id);

    TableCafe saveTable(TableCafe table);

    void deleteTable(Integer id);
}