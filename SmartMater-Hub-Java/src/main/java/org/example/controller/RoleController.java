package org.example.controller;

import org.example.common.Result;
import org.example.entity.Role;
import org.example.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping("/{id}")
    public Result<Role> getById(@PathVariable Integer id) {
        Role role = roleService.getById(id);
        return Result.success(role);
    }

    @GetMapping("/list")
    public Result<List<Role>> getList(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer status) {
        List<Role> list = roleService.getList(name, status);
        return Result.success(list);
    }

    @PostMapping("/add")
    public Result<String> add(@RequestBody Role role) {
        roleService.add(role);
        return Result.success("添加成功");
    }

    @PutMapping("/update")
    public Result<String> update(@RequestBody Role role) {
        roleService.update(role);
        return Result.success("更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Integer id) {
        roleService.delete(id);
        return Result.success("删除成功");
    }
}
