package org.example.controller;

import org.example.common.Result;
import org.example.dto.LoginRequest;
import org.example.dto.LoginResponse;
import org.example.dto.RegisterRequest;
import org.example.entity.User;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest request) {
        User user = userService.getByUsername(request.getUsername());
        
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        if (!user.getPassword().equals(request.getPassword())) {
            return Result.error("密码错误");
        }
        
        if (user.getStatus() == 0) {
            return Result.error("账号已被禁用");
        }
        
        LoginResponse response = new LoginResponse();
        response.setToken(generateToken(user.getId()));
        
        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setRealName(user.getRealName());
        userInfo.setRoleId(user.getRoleId());
        userInfo.setPhone(user.getPhone());
        userInfo.setEmail(user.getEmail());
        response.setUserInfo(userInfo);
        
        return Result.success(response);
    }
    
    @PostMapping("/register")
    public Result<String> register(@RequestBody RegisterRequest request) {
        User existUser = userService.getByUsername(request.getUsername());
        if (existUser != null) {
            return Result.error("用户名已存在");
        }
        
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setRealName(request.getRealName());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setRoleId(3);
        user.setStatus(1);
        
        userService.add(user);
        
        return Result.success("注册成功");
    }
    
    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success("登出成功");
    }
    
    private String generateToken(Integer userId) {
        return "token-" + userId + "-" + UUID.randomUUID().toString();
    }
}
