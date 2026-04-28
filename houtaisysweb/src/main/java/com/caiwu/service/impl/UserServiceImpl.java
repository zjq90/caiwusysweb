package com.caiwu.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caiwu.entity.User;
import com.caiwu.mapper.UserMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

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
}
