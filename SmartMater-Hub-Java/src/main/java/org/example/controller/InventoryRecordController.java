package org.example.controller;

import org.example.common.Result;
import org.example.entity.InventoryRecord;
import org.example.service.InventoryRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory-record")
public class InventoryRecordController {

    @Autowired
    private InventoryRecordService inventoryRecordService;

    @GetMapping("/{id}")
    public Result<InventoryRecord> getById(@PathVariable Integer id) {
        InventoryRecord record = inventoryRecordService.getById(id);
        return Result.success(record);
    }

    @GetMapping("/list")
    public Result<List<InventoryRecord>> getList(
            @RequestParam(required = false) String recordNo,
            @RequestParam(required = false) Integer materialId,
            @RequestParam(required = false) Integer recordType) {
        List<InventoryRecord> list = inventoryRecordService.getList(recordNo, materialId, recordType);
        return Result.success(list);
    }

    @PostMapping("/add")
    public Result<String> add(@RequestBody InventoryRecord record) {
        inventoryRecordService.add(record);
        return Result.success("添加成功");
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Integer id) {
        inventoryRecordService.delete(id);
        return Result.success("删除成功");
    }
}
