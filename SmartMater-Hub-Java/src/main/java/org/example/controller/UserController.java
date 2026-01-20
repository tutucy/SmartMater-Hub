package org.example.controller;

import org.example.common.Result;
import org.example.entity.User;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public Result<User> getById(@PathVariable Integer id) {
        User user = userService.getById(id);
        return Result.success(user);
    }

    @GetMapping("/list")
    public Result<List<User>> getList(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String realName,
            @RequestParam(required = false) Integer roleId,
            @RequestParam(required = false) Integer status) {
        List<User> list = userService.getList(username, realName, roleId, status);
        return Result.success(list);
    }

    @PostMapping("/add")
    public Result<String> add(@RequestBody User user) {
        userService.add(user);
        return Result.success("添加成功");
    }

    @PutMapping("/update")
    public Result<String> update(@RequestBody User user) {
        userService.update(user);
        return Result.success("更新成功");
    }

    @PutMapping("/{id}/password")
    public Result<String> resetPassword(@PathVariable Integer id, @RequestBody Map<String, String> params) {
        String password = params.get("password");
        String oldPassword = params.get("oldPassword");
        userService.resetPassword(id, password, oldPassword);
        return Result.success("密码重置成功");
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Integer id) {
        userService.delete(id);
        return Result.success("删除成功");
    }
}
