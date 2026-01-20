package org.example.controller;

import org.example.common.Result;
import org.example.entity.Material;
import org.example.service.MaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/material")
public class MaterialController {

    @Autowired
    private MaterialService materialService;

    @GetMapping("/{id}")
    public Result<Material> getById(@PathVariable Integer id) {
        Material material = materialService.getById(id);
        return Result.success(material);
    }

    @GetMapping("/list")
    public Result<List<Material>> getList(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) Integer status) {
        List<Material> list = materialService.getList(code, name, categoryId, status);
        return Result.success(list);
    }

    @PostMapping("/add")
    public Result<String> add(@RequestBody Material material) {
        materialService.add(material);
        return Result.success("添加成功");
    }

    @PutMapping("/update")
    public Result<String> update(@RequestBody Material material) {
        materialService.update(material);
        return Result.success("更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Integer id) {
        materialService.delete(id);
        return Result.success("删除成功");
    }
}
