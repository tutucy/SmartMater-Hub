package org.example.controller;

import org.example.common.Result;
import org.example.entity.Inventory;
import org.example.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @GetMapping("/{id}")
    public Result<Inventory> getById(@PathVariable Integer id) {
        Inventory inventory = inventoryService.getById(id);
        return Result.success(inventory);
    }

    @GetMapping("/list")
    public Result<List<Inventory>> getList(
            @RequestParam(required = false) Integer materialId,
            @RequestParam(required = false) String warehouse) {
        List<Inventory> list = inventoryService.getList(materialId, warehouse);
        return Result.success(list);
    }

    @GetMapping("/warning")
    public Result<List<Inventory>> getWarningList() {
        List<Inventory> list = inventoryService.getWarningList();
        return Result.success(list);
    }

    @PostMapping("/add")
    public Result<String> add(@RequestBody Inventory inventory) {
        inventoryService.add(inventory);
        return Result.success("添加成功");
    }

    @PutMapping("/update")
    public Result<String> update(@RequestBody Inventory inventory) {
        inventoryService.update(inventory);
        return Result.success("更新成功");
    }

    @PutMapping("/quantity")
    public Result<String> updateQuantity(
            @RequestParam Integer materialId,
            @RequestParam Integer quantity) {
        inventoryService.updateQuantity(materialId, quantity);
        return Result.success("更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Integer id) {
        inventoryService.delete(id);
        return Result.success("删除成功");
    }
}
