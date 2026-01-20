package org.example.controller;

import org.example.common.Result;
import org.example.entity.Supplier;
import org.example.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/supplier")
public class SupplierController {

    @Autowired
    private SupplierService supplierService;

    @GetMapping("/{id}")
    public Result<Supplier> getById(@PathVariable Integer id) {
        Supplier supplier = supplierService.getById(id);
        return Result.success(supplier);
    }

    @GetMapping("/list")
    public Result<List<Supplier>> getList(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String contact,
            @RequestParam(required = false) Integer status) {
        List<Supplier> list = supplierService.getList(name, contact, status);
        return Result.success(list);
    }

    @PostMapping("/add")
    public Result<String> add(@RequestBody Supplier supplier) {
        supplierService.add(supplier);
        return Result.success("添加成功");
    }

    @PutMapping("/update")
    public Result<String> update(@RequestBody Supplier supplier) {
        supplierService.update(supplier);
        return Result.success("更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Integer id) {
        supplierService.delete(id);
        return Result.success("删除成功");
    }
}
