package com.caiwu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.caiwu.entity.Category;

import java.util.List;

public interface CategoryService extends IService<Category> {

    Page<Category> listPage(Integer page, Integer size, String name, Integer type);

    List<Category> listByType(Integer type);
}
