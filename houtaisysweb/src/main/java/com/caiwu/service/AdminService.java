package com.caiwu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.caiwu.entity.Admin;

public interface AdminService extends IService<Admin> {

    Admin login(String username, String password);

    Page<Admin> listPage(Integer page, Integer size, String username);
}
