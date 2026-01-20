package org.example.service;

import org.example.entity.InventoryRecord;

import java.util.List;

public interface InventoryRecordService {

    InventoryRecord getById(Integer id);

    List<InventoryRecord> getList(String recordNo, Integer materialId, Integer recordType);

    int add(InventoryRecord record);

    int delete(Integer id);
}
