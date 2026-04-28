package com.caiwu.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caiwu.common.Result;
import com.caiwu.entity.User;
import com.caiwu.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @GetMapping("/list")
    public String listPage() {
        return "user/list";
    }

    @GetMapping("/data")
    @ResponseBody
    public Result<Page<User>> list(@RequestParam(defaultValue = "1") Integer page,
                                     @RequestParam(defaultValue = "10") Integer size,
                                     @RequestParam(required = false) String username,
                                     @RequestParam(required = false) String realName,
                                     @RequestParam(required = false) Integer status) {
        Page<User> result = userService.listPage(page, size, username, realName, status);
        return Result.success(result);
    }

    @GetMapping("/add")
    public String addPage() {
        return "user/add";
    }

    @PostMapping("/add")
    @ResponseBody
    public Result<Boolean> add(@RequestBody User user) {
        if (StrUtil.isBlank(user.getUsername())) {
            return Result.error("用户名不能为空");
        }
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, user.getUsername());
        if (userService.count(queryWrapper) > 0) {
            return Result.error("用户名已存在");
        }
        if (StrUtil.isNotBlank(user.getPassword())) {
            user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
        } else {
            user.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        }
        if (user.getStatus() == null) {
            user.setStatus(1);
        }
        boolean result = userService.save(user);
        return result ? Result.success("添加成功", true) : Result.error("添加失败");
    }

    @GetMapping("/edit/{id}")
    public String editPage(@PathVariable Long id, Model model) {
        User user = userService.getById(id);
        model.addAttribute("user", user);
        return "user/edit";
    }

    @GetMapping("/get/{id}")
    @ResponseBody
    public Result<User> get(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user != null) {
            user.setPassword(null);
        }
        return Result.success(user);
    }

    @PostMapping("/update")
    @ResponseBody
    public Result<Boolean> update(@RequestBody User user) {
        if (StrUtil.isBlank(user.getUsername())) {
            return Result.error("用户名不能为空");
        }
        User existUser = userService.getById(user.getId());
        if (existUser == null) {
            return Result.error("用户不存在");
        }
        if (StrUtil.isNotBlank(user.getPassword())) {
            user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
        } else {
            user.setPassword(null);
        }
        user.setUpdateTime(LocalDateTime.now());
        boolean result = userService.updateById(user);
        return result ? Result.success("修改成功", true) : Result.error("修改失败");
    }

    @PostMapping("/delete/{id}")
    @ResponseBody
    public Result<Boolean> delete(@PathVariable Long id) {
        boolean result = userService.removeById(id);
        return result ? Result.success("删除成功", true) : Result.error("删除失败");
    }

    @PostMapping("/batchDelete")
    @ResponseBody
    public Result<Boolean> batchDelete(@RequestBody Long[] ids) {
        for (Long id : ids) {
            boolean result = userService.removeById(id);
            if (!result) {
                return Result.error("删除失败");
            }
        }
        return Result.success("批量删除成功", true);
    }
}
