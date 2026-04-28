package com.caiwu.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caiwu.entity.Admin;
import com.caiwu.mapper.AdminMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;

@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {

    @Resource
    private AdminMapper adminMapper;

    @Override
    public Admin login(String username, String password) {
        String md5Password = DigestUtils.md5DigestAsHex(password.getBytes());
        LambdaQueryWrapper<Admin> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Admin::getUsername, username)
                .eq(Admin::getPassword, md5Password)
                .eq(Admin::getStatus, 1);
        return adminMapper.selectOne(queryWrapper);
    }

    @Override
    public Page<Admin> listPage(Integer page, Integer size, String username) {
        Page<Admin> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Admin> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.like(Admin::getUsername, username);
        }
        queryWrapper.orderByDesc(Admin::getCreateTime);
        return adminMapper.selectPage(pageParam, queryWrapper);
    }
}
