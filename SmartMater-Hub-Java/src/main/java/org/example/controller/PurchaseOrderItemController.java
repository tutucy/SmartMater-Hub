package org.example.controller;

import org.example.common.Result;
import org.example.entity.PurchaseOrderItem;
import org.example.service.PurchaseOrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchase-order-item")
public class PurchaseOrderItemController {

    @Autowired
    private PurchaseOrderItemService purchaseOrderItemService;

    @GetMapping("/list")
    public Result<List<PurchaseOrderItem>> getList(
            @RequestParam(required = false) Integer orderId) {
        List<PurchaseOrderItem> list = purchaseOrderItemService.getList(orderId);
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result<PurchaseOrderItem> getById(@PathVariable Integer id) {
        PurchaseOrderItem item = purchaseOrderItemService.getById(id);
        return Result.success(item);
    }

    @PostMapping("/add")
    public Result<String> add(@RequestBody PurchaseOrderItem item) {
        purchaseOrderItemService.add(item);
        return Result.success("添加成功");
    }

    @PutMapping("/update")
    public Result<String> update(@RequestBody PurchaseOrderItem item) {
        purchaseOrderItemService.update(item);
        return Result.success("更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Integer id) {
        purchaseOrderItemService.delete(id);
        return Result.success("删除成功");
    }
}
