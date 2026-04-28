package com.caiwusys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caiwusys.entity.Category;
import com.caiwusys.mapper.CategoryMapper;
import com.caiwusys.service.CategoryService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Override
    public List<Category> getByType(Long userId, Integer type) {
        return list(new LambdaQueryWrapper<Category>()
                .eq(Category::getType, type)
                .and(wrapper -> wrapper.eq(Category::getUserId, 0).or().eq(Category::getUserId, userId))
                .orderByAsc(Category::getSort));
    }

    @Override
    public boolean addCategory(Category category) {
        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());
        return save(category);
    }

    @Override
    public boolean updateCategory(Category category) {
        category.setUpdateTime(LocalDateTime.now());
        return updateById(category);
    }

    @Override
    public boolean deleteCategory(Long id, Long userId) {
        Category category = getById(id);
        if (category == null) {
            return false;
        }
        if (category.getUserId() == 0) {
            throw new RuntimeException("系统默认类别不能删除");
        }
        if (!category.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除此类别");
        }
        return removeById(id);
    }
}
