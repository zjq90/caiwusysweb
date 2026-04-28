package com.caiwu.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caiwu.entity.Category;
import com.caiwu.mapper.CategoryMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Resource
    private CategoryMapper categoryMapper;

    @Override
    public Page<Category> listPage(Integer page, Integer size, String name, Integer type) {
        Page<Category> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(name)) {
            queryWrapper.like(Category::getName, name);
        }
        if (type != null) {
            queryWrapper.eq(Category::getType, type);
        }
        queryWrapper.orderByAsc(Category::getSort)
                .orderByDesc(Category::getCreateTime);
        return categoryMapper.selectPage(pageParam, queryWrapper);
    }

    @Override
    public List<Category> listByType(Integer type) {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getType, type)
                .eq(Category::getStatus, 1)
                .orderByAsc(Category::getSort);
        return categoryMapper.selectList(queryWrapper);
    }
}
