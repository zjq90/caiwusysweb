package com.caiwusys.controller;

import com.caiwusys.common.Result;
import com.caiwusys.entity.Category;
import com.caiwusys.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
@Api(tags = "收支类别模块")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/income")
    @ApiOperation("获取收入类别列表")
    public Result<List<Category>> getIncomeCategories(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<Category> categories = categoryService.getByType(userId, 1);
        return Result.success(categories);
    }

    @GetMapping("/expense")
    @ApiOperation("获取支出类别列表")
    public Result<List<Category>> getExpenseCategories(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<Category> categories = categoryService.getByType(userId, 2);
        return Result.success(categories);
    }

    @GetMapping("/type/{type}")
    @ApiOperation("根据类型获取类别列表")
    public Result<List<Category>> getByType(@PathVariable Integer type, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<Category> categories = categoryService.getByType(userId, type);
        return Result.success(categories);
    }

    @PostMapping
    @ApiOperation("添加类别")
    public Result<Boolean> addCategory(@RequestBody Category category, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        category.setUserId(userId);
        boolean result = categoryService.addCategory(category);
        return Result.success("添加成功", result);
    }

    @PutMapping("/{id}")
    @ApiOperation("修改类别")
    public Result<Boolean> updateCategory(@PathVariable Long id, @RequestBody Category category, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        category.setId(id);
        category.setUserId(userId);
        boolean result = categoryService.updateCategory(category);
        return Result.success("修改成功", result);
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除类别")
    public Result<Boolean> deleteCategory(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        try {
            boolean result = categoryService.deleteCategory(id, userId);
            return Result.success("删除成功", result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @ApiOperation("获取类别详情")
    public Result<Category> getCategory(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Category category = categoryService.getById(id);
        if (category != null && (category.getUserId() == 0 || category.getUserId().equals(userId))) {
            return Result.success(category);
        }
        return Result.error("类别不存在");
    }
}
