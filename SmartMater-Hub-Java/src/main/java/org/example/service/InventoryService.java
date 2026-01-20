package org.example.service;

import org.example.entity.Inventory;

import java.util.List;

public interface InventoryService {

    Inventory getById(Integer id);

    Inventory getByMaterialId(Integer materialId);

    List<Inventory> getList(Integer materialId, String warehouse);

    List<Inventory> getWarningList();

    int add(Inventory inventory);

    int update(Inventory inventory);

    int updateQuantity(Integer materialId, Integer quantity);

    int delete(Integer id);
}
