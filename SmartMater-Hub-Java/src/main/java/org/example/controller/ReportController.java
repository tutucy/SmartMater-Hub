package org.example.controller;

import org.example.common.Result;
import org.example.entity.Inventory;
import org.example.entity.InventoryRecord;
import org.example.entity.PurchaseOrder;
import org.example.mapper.InventoryMapper;
import org.example.mapper.InventoryRecordMapper;
import org.example.mapper.PurchaseOrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/report")
public class ReportController {

    @Autowired
    private InventoryMapper inventoryMapper;

    @Autowired
    private InventoryRecordMapper inventoryRecordMapper;

    @Autowired
    private PurchaseOrderMapper purchaseOrderMapper;

    @GetMapping("/inventory")
    public Result<Map<String, Object>> getInventoryReport(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Integer categoryId) {
        Map<String, Object> result = new HashMap<>();
        
        // 获取库存汇总
        List<Inventory> inventoryList = inventoryMapper.selectList(null, null);
        int totalQuantity = inventoryList.stream().mapToInt(Inventory::getQuantity).sum();
        int warningCount = inventoryMapper.selectWarningList().size();
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalQuantity", totalQuantity);
        summary.put("warningCount", warningCount);
        
        result.put("summary", summary);
        result.put("details", inventoryList);
        
        return Result.success(result);
    }

    @GetMapping("/purchase")
    public Result<Map<String, Object>> getPurchaseReport(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Integer supplierId) {
        Map<String, Object> result = new HashMap<>();
        
        // 获取采购订单汇总
        List<PurchaseOrder> orderList = purchaseOrderMapper.selectList(null, supplierId, null);
        double totalAmount = orderList.stream().mapToDouble(order -> order.getTotalAmount().doubleValue()).sum();
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalOrders", orderList.size());
        summary.put("totalAmount", totalAmount);
        
        result.put("summary", summary);
        result.put("details", orderList);
        
        return Result.success(result);
    }

    @GetMapping("/usage")
    public Result<Map<String, Object>> getUsageReport(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String department) {
        Map<String, Object> result = new HashMap<>();
        
        // 获取出库记录汇总
        List<InventoryRecord> recordList = inventoryRecordMapper.selectList(null, null, 2);
        int totalOutQuantity = recordList.stream().mapToInt(InventoryRecord::getQuantity).sum();
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalOutQuantity", totalOutQuantity);
        
        result.put("summary", summary);
        result.put("details", recordList);
        
        return Result.success(result);
    }
}
