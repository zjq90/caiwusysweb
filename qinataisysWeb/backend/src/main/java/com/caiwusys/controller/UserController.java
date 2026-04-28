package com.caiwusys.controller;

import com.caiwusys.common.Result;
import com.caiwusys.dto.LoginDTO;
import com.caiwusys.dto.RegisterDTO;
import com.caiwusys.dto.UpdatePasswordDTO;
import com.caiwusys.entity.User;
import com.caiwusys.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Api(tags = "用户模块")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    @ApiOperation("用户登录")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginDTO loginDTO) {
        try {
            Map<String, Object> result = userService.login(loginDTO);
            return Result.success("登录成功", result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/register")
    @ApiOperation("用户注册")
    public Result<Boolean> register(@Valid @RequestBody RegisterDTO registerDTO) {
        try {
            boolean result = userService.register(registerDTO);
            return Result.success("注册成功", result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/logout")
    @ApiOperation("用户注销")
    public Result<Boolean> logout(HttpServletRequest request) {
        return Result.success("注销成功", true);
    }

    @GetMapping("/info")
    @ApiOperation("获取用户信息")
    public Result<User> getUserInfo(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        User user = userService.getById(userId);
        if (user != null) {
            user.setPassword(null);
        }
        return Result.success(user);
    }

    @PutMapping("/info")
    @ApiOperation("修改用户信息")
    public Result<Boolean> updateUserInfo(@RequestBody User user, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        user.setId(userId);
        boolean result = userService.updateUserInfo(user);
        return Result.success("修改成功", result);
    }

    @PutMapping("/password")
    @ApiOperation("修改密码")
    public Result<Boolean> updatePassword(@Valid @RequestBody UpdatePasswordDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        try {
            boolean result = userService.updatePassword(userId, dto);
            return Result.success("密码修改成功", result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
