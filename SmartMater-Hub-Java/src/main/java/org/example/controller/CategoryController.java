package org.example.controller;

import org.example.common.Result;
import org.example.entity.Category;
import org.example.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/{id}")
    public Result<Category> getById(@PathVariable Integer id) {
        Category category = categoryService.getById(id);
        return Result.success(category);
    }

    @GetMapping("/list")
    public Result<List<Category>> getList(
            @RequestParam(required = false) Integer parentId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer status) {
        List<Category> list = categoryService.getList(parentId, name, status);
        return Result.success(list);
    }

    @GetMapping("/tree")
    public Result<List<Category>> getTree() {
        List<Category> tree = categoryService.getTree();
        return Result.success(tree);
    }

    @PostMapping("/add")
    public Result<String> add(@RequestBody Category category) {
        categoryService.add(category);
        return Result.success("添加成功");
    }

    @PutMapping("/update")
    public Result<String> update(@RequestBody Category category) {
        categoryService.update(category);
        return Result.success("更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Integer id) {
        categoryService.delete(id);
        return Result.success("删除成功");
    }
}
