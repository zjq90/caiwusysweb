package com.caiwu.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caiwu.common.PageResult;
import com.caiwu.common.Result;
import com.caiwu.entity.Admin;
import com.caiwu.service.AdminService;
import com.caiwu.utils.JwtUtils;
import com.caiwu.utils.ValidateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Resource
    private AdminService adminService;

    @Resource
    private JwtUtils jwtUtils;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestParam String username,
                                               @RequestParam String password) {
        log.info("管理员登录请求: username={}", username);

        if (StrUtil.isBlank(username) || StrUtil.isBlank(password)) {
            return Result.error("用户名或密码不能为空");
        }

        Admin admin = adminService.login(username, password);
        if (admin != null) {
            String token = jwtUtils.generateToken(admin.getId(), admin.getUsername());
            admin.setPassword(null);

            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            result.put("admin", admin);

            log.info("管理员登录成功: username={}", username);
            return Result.success("登录成功", result);
        }

        log.warn("管理员登录失败: username={}", username);
        return Result.error("用户名或密码错误");
    }

    @GetMapping("/info")
    public Result<Admin> getCurrentAdmin(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String username = (String) request.getAttribute("username");
        log.info("获取当前管理员信息: userId={}, username={}", userId, username);

        Admin admin = adminService.getById(userId);
        if (admin != null) {
            admin.setPassword(null);
            return Result.success(admin);
        }
        return Result.error("管理员不存在");
    }

    @GetMapping("/list")
    public Result<PageResult<Admin>> list(@RequestParam(defaultValue = "1") Integer page,
                                             @RequestParam(defaultValue = "10") Integer size,
                                             @RequestParam(required = false) String username) {
        Page<Admin> pageResult = adminService.listPage(page, size, username);

        PageResult<Admin> result = new PageResult<>();
        result.setRecords(pageResult.getRecords());
        result.setTotal(pageResult.getTotal());
        result.setSize(pageResult.getSize());
        result.setCurrent(pageResult.getCurrent());
        result.setPages(pageResult.getPages());

        return Result.success(result);
    }

    @GetMapping("/get/{id}")
    public Result<Admin> getById(@PathVariable Long id) {
        Admin admin = adminService.getById(id);
        if (admin != null) {
            admin.setPassword(null);
        }
        return Result.success(admin);
    }

    @PostMapping("/add")
    public Result<Boolean> add(@RequestBody Admin admin) {
        log.info("添加管理员: username={}", admin.getUsername());

        String usernameError = ValidateUtils.validateUsername(admin.getUsername());
        if (usernameError != null) {
            return Result.error(usernameError);
        }

        String passwordError = ValidateUtils.validatePassword(admin.getPassword(), false);
        if (passwordError != null) {
            return Result.error(passwordError);
        }

        String phoneError = ValidateUtils.validatePhone(admin.getPhone());
        if (phoneError != null) {
            return Result.error(phoneError);
        }

        String emailError = ValidateUtils.validateEmail(admin.getEmail());
        if (emailError != null) {
            return Result.error(emailError);
        }

        if (adminService.checkUsernameExists(admin.getUsername(), null)) {
            return Result.error("用户名已存在");
        }

        boolean result = adminService.addAdmin(admin);
        return result ? Result.success("添加成功", true) : Result.error("添加失败");
    }

    @PostMapping("/update")
    public Result<Boolean> update(@RequestBody Admin admin) {
        log.info("更新管理员: id={}, username={}", admin.getId(), admin.getUsername());

        if (admin.getId() == null) {
            return Result.error("ID不能为空");
        }

        String usernameError = ValidateUtils.validateUsername(admin.getUsername());
        if (usernameError != null) {
            return Result.error(usernameError);
        }

        String passwordError = ValidateUtils.validatePassword(admin.getPassword(), false);
        if (passwordError != null) {
            return Result.error(passwordError);
        }

        String phoneError = ValidateUtils.validatePhone(admin.getPhone());
        if (phoneError != null) {
            return Result.error(phoneError);
        }

        String emailError = ValidateUtils.validateEmail(admin.getEmail());
        if (emailError != null) {
            return Result.error(emailError);
        }

        Admin existAdmin = adminService.getById(admin.getId());
        if (existAdmin == null) {
            return Result.error("管理员不存在");
        }

        if (!existAdmin.getUsername().equals(admin.getUsername())) {
            if (adminService.checkUsernameExists(admin.getUsername(), admin.getId())) {
                return Result.error("用户名已存在");
            }
        }

        boolean result = adminService.updateAdmin(admin);
        return result ? Result.success("修改成功", true) : Result.error("修改失败");
    }

    @PostMapping("/delete/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        log.info("删除管理员: id={}", id);
        boolean result = adminService.removeById(id);
        return result ? Result.success("删除成功", true) : Result.error("删除失败");
    }

    @PostMapping("/batchDelete")
    public Result<Boolean> batchDelete(@RequestBody Long[] ids) {
        log.info("批量删除管理员: count={}", ids.length);
        for (Long id : ids) {
            boolean result = adminService.removeById(id);
            if (!result) {
                log.warn("批量删除失败: id={}", id);
                return Result.error("删除失败");
            }
        }
        return Result.success("批量删除成功", true);
    }
}
