package com.caiwu.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caiwu.common.Result;
import com.caiwu.entity.Category;
import com.caiwu.service.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/category")
public class CategoryController {

    @Resource
    private CategoryService categoryService;

    @GetMapping("/list")
    public String listPage() {
        return "category/list";
    }

    @GetMapping("/data")
    @ResponseBody
    public Result<Page<Category>> list(@RequestParam(defaultValue = "1") Integer page,
                                         @RequestParam(defaultValue = "10") Integer size,
                                         @RequestParam(required = false) String name,
                                         @RequestParam(required = false) Integer type) {
        Page<Category> result = categoryService.listPage(page, size, name, type);
        return Result.success(result);
    }

    @GetMapping("/listByType/{type}")
    @ResponseBody
    public Result<List<Category>> listByType(@PathVariable Integer type) {
        List<Category> result = categoryService.listByType(type);
        return Result.success(result);
    }

    @GetMapping("/add")
    public String addPage() {
        return "category/add";
    }

    @PostMapping("/add")
    @ResponseBody
    public Result<Boolean> add(@RequestBody Category category) {
        if (StrUtil.isBlank(category.getName())) {
            return Result.error("类别名称不能为空");
        }
        if (category.getType() == null) {
            return Result.error("请选择类别类型");
        }
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getName, category.getName())
                .eq(Category::getType, category.getType());
        if (categoryService.count(queryWrapper) > 0) {
            return Result.error("该类别名称已存在");
        }
        if (category.getSort() == null) {
            category.setSort(0);
        }
        if (category.getStatus() == null) {
            category.setStatus(1);
        }
        boolean result = categoryService.save(category);
        return result ? Result.success("添加成功", true) : Result.error("添加失败");
    }

    @GetMapping("/edit/{id}")
    public String editPage(@PathVariable Long id, Model model) {
        Category category = categoryService.getById(id);
        model.addAttribute("category", category);
        return "category/edit";
    }

    @GetMapping("/get/{id}")
    @ResponseBody
    public Result<Category> get(@PathVariable Long id) {
        Category category = categoryService.getById(id);
        return Result.success(category);
    }

    @PostMapping("/update")
    @ResponseBody
    public Result<Boolean> update(@RequestBody Category category) {
        if (StrUtil.isBlank(category.getName())) {
            return Result.error("类别名称不能为空");
        }
        if (category.getType() == null) {
            return Result.error("请选择类别类型");
        }
        Category existCategory = categoryService.getById(category.getId());
        if (existCategory == null) {
            return Result.error("类别不存在");
        }
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getName, category.getName())
                .eq(Category::getType, category.getType())
                .ne(Category::getId, category.getId());
        if (categoryService.count(queryWrapper) > 0) {
            return Result.error("该类别名称已存在");
        }
        category.setUpdateTime(LocalDateTime.now());
        boolean result = categoryService.updateById(category);
        return result ? Result.success("修改成功", true) : Result.error("修改失败");
    }

    @PostMapping("/delete/{id}")
    @ResponseBody
    public Result<Boolean> delete(@PathVariable Long id) {
        boolean result = categoryService.removeById(id);
        return result ? Result.success("删除成功", true) : Result.error("删除失败");
    }

    @PostMapping("/batchDelete")
    @ResponseBody
    public Result<Boolean> batchDelete(@RequestBody Long[] ids) {
        for (Long id : ids) {
            boolean result = categoryService.removeById(id);
            if (!result) {
                return Result.error("删除失败");
            }
        }
        return Result.success("批量删除成功", true);
    }
}
