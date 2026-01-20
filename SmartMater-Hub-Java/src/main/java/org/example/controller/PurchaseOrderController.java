package org.example.controller;

import org.example.common.Result;
import org.example.entity.PurchaseOrder;
import org.example.service.PurchaseOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchase-order")
public class PurchaseOrderController {

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @GetMapping("/{id}")
    public Result<PurchaseOrder> getById(@PathVariable Integer id) {
        PurchaseOrder order = purchaseOrderService.getById(id);
        return Result.success(order);
    }

    @GetMapping("/list")
    public Result<List<PurchaseOrder>> getList(
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) Integer supplierId,
            @RequestParam(required = false) Integer status) {
        List<PurchaseOrder> list = purchaseOrderService.getList(orderNo, supplierId, status);
        return Result.success(list);
    }

    @PostMapping("/add")
    public Result<String> add(@RequestBody PurchaseOrder order) {
        purchaseOrderService.add(order);
        return Result.success("添加成功");
    }

    @PutMapping("/update")
    public Result<String> update(@RequestBody PurchaseOrder order) {
        purchaseOrderService.update(order);
        return Result.success("更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Integer id) {
        purchaseOrderService.delete(id);
        return Result.success("删除成功");
    }
}
