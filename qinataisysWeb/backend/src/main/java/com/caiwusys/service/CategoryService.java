package com.caiwusys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.caiwusys.entity.Category;

import java.util.List;

public interface CategoryService extends IService<Category> {

    List<Category> getByType(Long userId, Integer type);

    boolean addCategory(Category category);

    boolean updateCategory(Category category);

    boolean deleteCategory(Long id, Long userId);
}
