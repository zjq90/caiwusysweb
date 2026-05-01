package com.caiwu.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caiwu.entity.User;
import com.caiwu.mapper.UserMapper;
import com.caiwu.utils.PasswordUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private UserMapper userMapper;

    @Override
    public Page<User> listPage(Integer page, Integer size, String username, String realName, Integer status) {
        Page<User> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.like(User::getUsername, username);
        }
        if (StringUtils.isNotBlank(realName)) {
            queryWrapper.like(User::getRealName, realName);
        }
        if (status != null) {
            queryWrapper.eq(User::getStatus, status);
        }
        queryWrapper.orderByDesc(User::getCreateTime);
        return userMapper.selectPage(pageParam, queryWrapper);
    }

    @Override
    public boolean checkUsernameExists(String username, Long excludeId) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username.trim());
        if (excludeId != null) {
            queryWrapper.ne(User::getId, excludeId);
        }
        return userMapper.selectCount(queryWrapper) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addUser(User user) {
        if (StringUtils.isNotBlank(user.getPassword())) {
            user.setPassword(PasswordUtils.encode(user.getPassword()));
        } else {
            user.setPassword(PasswordUtils.encode(PasswordUtils.getDefaultPassword()));
        }
        if (user.getStatus() == null) {
            user.setStatus(1);
        }
        return this.save(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUser(User user) {
        if (StringUtils.isNotBlank(user.getPassword())) {
            user.setPassword(PasswordUtils.encode(user.getPassword()));
        } else {
            user.setPassword(null);
        }
        return this.updateById(user);
    }
}
