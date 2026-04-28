package com.caiwu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.caiwu.entity.User;

public interface UserService extends IService<User> {

    Page<User> listPage(Integer page, Integer size, String username, String realName, Integer status);
}
