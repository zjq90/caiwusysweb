package com.caiwu.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caiwu.common.PageResult;
import com.caiwu.common.Result;
import com.caiwu.entity.User;
import com.caiwu.service.UserService;
import com.caiwu.utils.ValidateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @GetMapping("/list")
    public Result<PageResult<User>> list(@RequestParam(defaultValue = "1") Integer page,
                                           @RequestParam(defaultValue = "10") Integer size,
                                           @RequestParam(required = false) String username,
                                           @RequestParam(required = false) String realName,
                                           @RequestParam(required = false) Integer status) {
        Page<User> pageResult = userService.listPage(page, size, username, realName, status);

        PageResult<User> result = new PageResult<>();
        result.setRecords(pageResult.getRecords());
        result.setTotal(pageResult.getTotal());
        result.setSize(pageResult.getSize());
        result.setCurrent(pageResult.getCurrent());
        result.setPages(pageResult.getPages());

        return Result.success(result);
    }

    @GetMapping("/get/{id}")
    public Result<User> getById(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user != null) {
            user.setPassword(null);
        }
        return Result.success(user);
    }

    @PostMapping("/add")
    public Result<Boolean> add(@RequestBody User user) {
        log.info("添加用户: username={}", user.getUsername());

        if (StrUtil.isBlank(user.getUsername())) {
            return Result.error("用户名不能为空");
        }

        if (userService.checkUsernameExists(user.getUsername(), null)) {
            return Result.error("用户名已存在");
        }

        String passwordError = ValidateUtils.validatePassword(user.getPassword(), false);
        if (passwordError != null) {
            return Result.error(passwordError);
        }

        boolean result = userService.addUser(user);
        return result ? Result.success("添加成功", true) : Result.error("添加失败");
    }

    @PostMapping("/update")
    public Result<Boolean> update(@RequestBody User user) {
        log.info("更新用户: id={}, username={}", user.getId(), user.getUsername());

        if (user.getId() == null) {
            return Result.error("ID不能为空");
        }

        if (StrUtil.isBlank(user.getUsername())) {
            return Result.error("用户名不能为空");
        }

        User existUser = userService.getById(user.getId());
        if (existUser == null) {
            return Result.error("用户不存在");
        }

        if (!existUser.getUsername().equals(user.getUsername())) {
            if (userService.checkUsernameExists(user.getUsername(), user.getId())) {
                return Result.error("用户名已存在");
            }
        }

        String passwordError = ValidateUtils.validatePassword(user.getPassword(), false);
        if (passwordError != null) {
            return Result.error(passwordError);
        }

        boolean result = userService.updateUser(user);
        return result ? Result.success("修改成功", true) : Result.error("修改失败");
    }

    @PostMapping("/delete/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        log.info("删除用户: id={}", id);
        boolean result = userService.removeById(id);
        return result ? Result.success("删除成功", true) : Result.error("删除失败");
    }

    @PostMapping("/batchDelete")
    public Result<Boolean> batchDelete(@RequestBody Long[] ids) {
        log.info("批量删除用户: count={}", ids.length);
        for (Long id : ids) {
            boolean result = userService.removeById(id);
            if (!result) {
                log.warn("批量删除失败: id={}", id);
                return Result.error("删除失败");
            }
        }
        return Result.success("批量删除成功", true);
    }
}
