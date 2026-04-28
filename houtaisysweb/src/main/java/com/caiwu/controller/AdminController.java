package com.caiwu.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caiwu.common.Result;
import com.caiwu.entity.Admin;
import com.caiwu.service.AdminService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

@Controller
public class AdminController {

    private static final String PHONE_REGEX = "^1[3-9]\\d{9}$";
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final int PASSWORD_MIN_LENGTH = 6;

    @Resource
    private AdminService adminService;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/admin/login")
    @ResponseBody
    public Result<Admin> login(@RequestParam String username,
                                 @RequestParam String password,
                                 HttpSession session) {
        if (StrUtil.isBlank(username) || StrUtil.isBlank(password)) {
            return Result.error("用户名或密码不能为空");
        }
        Admin admin = adminService.login(username, password);
        if (admin != null) {
            admin.setPassword(null);
            session.setAttribute("admin", admin);
            return Result.success("登录成功", admin);
        }
        return Result.error("用户名或密码错误");
    }

    @GetMapping("/admin/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/")
    public String index(Model model, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("admin");
        model.addAttribute("admin", admin);
        return "index";
    }

    @GetMapping("/admin/list")
    public String adminPage() {
        return "admin/list";
    }

    @GetMapping("/admin/data")
    @ResponseBody
    public Result<Page<Admin>> data(@RequestParam(defaultValue = "1") Integer page,
                                      @RequestParam(defaultValue = "10") Integer size,
                                      @RequestParam(required = false) String username) {
        Page<Admin> result = adminService.listPage(page, size, username);
        return Result.success(result);
    }

    @GetMapping("/admin/get/{id}")
    @ResponseBody
    public Result<Admin> get(@PathVariable Long id) {
        Admin admin = adminService.getById(id);
        if (admin != null) {
            admin.setPassword(null);
        }
        return Result.success(admin);
    }

    @PostMapping("/admin/add")
    @ResponseBody
    public Result<Boolean> add(@RequestBody Admin admin) {
        if (StrUtil.isBlank(admin.getUsername())) {
            return Result.error("用户名不能为空");
        }
        if (admin.getUsername().length() < 2 || admin.getUsername().length() > 20) {
            return Result.error("用户名长度必须在2-20个字符之间");
        }
        
        if (StrUtil.isNotBlank(admin.getPassword())) {
            if (admin.getPassword().length() < PASSWORD_MIN_LENGTH) {
                return Result.error("密码长度不能少于" + PASSWORD_MIN_LENGTH + "位");
            }
        }
        
        if (StrUtil.isNotBlank(admin.getPhone())) {
            if (!admin.getPhone().matches(PHONE_REGEX)) {
                return Result.error("手机号格式不正确");
            }
        }
        
        if (StrUtil.isNotBlank(admin.getEmail())) {
            if (!admin.getEmail().matches(EMAIL_REGEX)) {
                return Result.error("邮箱格式不正确");
            }
        }
        
        LambdaQueryWrapper<Admin> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Admin::getUsername, admin.getUsername());
        if (adminService.count(queryWrapper) > 0) {
            return Result.error("用户名已存在");
        }
        if (StrUtil.isNotBlank(admin.getPassword())) {
            admin.setPassword(DigestUtils.md5DigestAsHex(admin.getPassword().getBytes()));
        } else {
            admin.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        }
        if (admin.getStatus() == null) {
            admin.setStatus(1);
        }
        boolean result = adminService.save(admin);
        return result ? Result.success("添加成功", true) : Result.error("添加失败");
    }

    @GetMapping("/admin/edit/{id}")
    public String editPage(@PathVariable Long id, Model model) {
        Admin admin = adminService.getById(id);
        model.addAttribute("admin", admin);
        return "admin/edit";
    }

    @PostMapping("/admin/update")
    @ResponseBody
    public Result<Boolean> update(@RequestBody Admin admin) {
        if (StrUtil.isBlank(admin.getUsername())) {
            return Result.error("用户名不能为空");
        }
        if (admin.getUsername().length() < 2 || admin.getUsername().length() > 20) {
            return Result.error("用户名长度必须在2-20个字符之间");
        }
        
        if (StrUtil.isNotBlank(admin.getPassword())) {
            if (admin.getPassword().length() < PASSWORD_MIN_LENGTH) {
                return Result.error("密码长度不能少于" + PASSWORD_MIN_LENGTH + "位");
            }
        }
        
        if (StrUtil.isNotBlank(admin.getPhone())) {
            if (!admin.getPhone().matches(PHONE_REGEX)) {
                return Result.error("手机号格式不正确");
            }
        }
        
        if (StrUtil.isNotBlank(admin.getEmail())) {
            if (!admin.getEmail().matches(EMAIL_REGEX)) {
                return Result.error("邮箱格式不正确");
            }
        }
        
        Admin existAdmin = adminService.getById(admin.getId());
        if (existAdmin == null) {
            return Result.error("管理员不存在");
        }
        
        if (!existAdmin.getUsername().equals(admin.getUsername())) {
            LambdaQueryWrapper<Admin> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Admin::getUsername, admin.getUsername());
            if (adminService.count(queryWrapper) > 0) {
                return Result.error("用户名已存在");
            }
        }
        
        if (StrUtil.isNotBlank(admin.getPassword())) {
            admin.setPassword(DigestUtils.md5DigestAsHex(admin.getPassword().getBytes()));
        } else {
            admin.setPassword(null);
        }
        admin.setUpdateTime(LocalDateTime.now());
        boolean result = adminService.updateById(admin);
        return result ? Result.success("修改成功", true) : Result.error("修改失败");
    }

    @PostMapping("/admin/delete/{id}")
    @ResponseBody
    public Result<Boolean> delete(@PathVariable Long id) {
        boolean result = adminService.removeById(id);
        return result ? Result.success("删除成功", true) : Result.error("删除失败");
    }

    @PostMapping("/admin/batchDelete")
    @ResponseBody
    public Result<Boolean> batchDelete(@RequestBody Long[] ids) {
        boolean result = false;
        for (Long id : ids) {
            result = adminService.removeById(id);
            if (!result) {
                return Result.error("删除失败");
            }
        }
        return Result.success("批量删除成功", true);
    }
}
