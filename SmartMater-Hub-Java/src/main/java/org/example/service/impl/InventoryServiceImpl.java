package org.example.service.impl;

import org.example.entity.Inventory;
import org.example.mapper.InventoryMapper;
import org.example.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryServiceImpl implements InventoryService {

    @Autowired
    private InventoryMapper inventoryMapper;

    @Override
    public Inventory getById(Integer id) {
        return inventoryMapper.selectById(id);
    }

    @Override
    public Inventory getByMaterialId(Integer materialId) {
        return inventoryMapper.selectByMaterialId(materialId);
    }

    @Override
    public List<Inventory> getList(Integer materialId, String warehouse) {
        return inventoryMapper.selectList(materialId, warehouse);
    }

    @Override
    public List<Inventory> getWarningList() {
        return inventoryMapper.selectWarningList();
    }

    @Override
    public int add(Inventory inventory) {
        return inventoryMapper.insert(inventory);
    }

    @Override
    public int update(Inventory inventory) {
        return inventoryMapper.update(inventory);
    }

    @Override
    public int updateQuantity(Integer materialId, Integer quantity) {
        return inventoryMapper.updateQuantity(materialId, quantity);
    }

    @Override
    public int delete(Integer id) {
        return inventoryMapper.deleteById(id);
    }
}
