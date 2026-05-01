package com.caiwu.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caiwu.entity.Admin;
import com.caiwu.mapper.AdminMapper;
import com.caiwu.utils.PasswordUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Slf4j
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {

    @Resource
    private AdminMapper adminMapper;

    @Override
    public Admin login(String username, String password) {
        String encodedPassword = PasswordUtils.encode(password);
        LambdaQueryWrapper<Admin> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Admin::getUsername, username)
                .eq(Admin::getPassword, encodedPassword)
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

    @Override
    public boolean checkUsernameExists(String username, Long excludeId) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        LambdaQueryWrapper<Admin> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Admin::getUsername, username.trim());
        if (excludeId != null) {
            queryWrapper.ne(Admin::getId, excludeId);
        }
        return adminMapper.selectCount(queryWrapper) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addAdmin(Admin admin) {
        if (StringUtils.isNotBlank(admin.getPassword())) {
            admin.setPassword(PasswordUtils.encode(admin.getPassword()));
        } else {
            admin.setPassword(PasswordUtils.encode(PasswordUtils.getDefaultPassword()));
        }
        if (admin.getStatus() == null) {
            admin.setStatus(1);
        }
        return this.save(admin);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateAdmin(Admin admin) {
        if (StringUtils.isNotBlank(admin.getPassword())) {
            admin.setPassword(PasswordUtils.encode(admin.getPassword()));
        } else {
            admin.setPassword(null);
        }
        return this.updateById(admin);
    }
}
