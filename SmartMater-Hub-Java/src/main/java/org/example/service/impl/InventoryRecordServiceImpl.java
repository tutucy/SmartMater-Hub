package org.example.service.impl;

import org.example.entity.InventoryRecord;
import org.example.mapper.InventoryRecordMapper;
import org.example.service.InventoryRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryRecordServiceImpl implements InventoryRecordService {

    @Autowired
    private InventoryRecordMapper inventoryRecordMapper;

    @Override
    public InventoryRecord getById(Integer id) {
        return inventoryRecordMapper.selectById(id);
    }

    @Override
    public List<InventoryRecord> getList(String recordNo, Integer materialId, Integer recordType) {
        return inventoryRecordMapper.selectList(recordNo, materialId, recordType);
    }

    @Override
    public int add(InventoryRecord record) {
        return inventoryRecordMapper.insert(record);
    }

    @Override
    public int delete(Integer id) {
        return inventoryRecordMapper.deleteById(id);
    }
}
