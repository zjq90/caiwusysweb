package com.caiwu.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caiwu.common.PageResult;
import com.caiwu.common.Result;
import com.caiwu.entity.Category;
import com.caiwu.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Resource
    private CategoryService categoryService;

    @GetMapping("/list")
    public Result<PageResult<Category>> list(@RequestParam(defaultValue = "1") Integer page,
                                               @RequestParam(defaultValue = "10") Integer size,
                                               @RequestParam(required = false) String name,
                                               @RequestParam(required = false) Integer type) {
        Page<Category> pageResult = categoryService.listPage(page, size, name, type);

        PageResult<Category> result = new PageResult<>();
        result.setRecords(pageResult.getRecords());
        result.setTotal(pageResult.getTotal());
        result.setSize(pageResult.getSize());
        result.setCurrent(pageResult.getCurrent());
        result.setPages(pageResult.getPages());

        return Result.success(result);
    }

    @GetMapping("/listByType/{type}")
    public Result<List<Category>> listByType(@PathVariable Integer type) {
        List<Category> result = categoryService.listByType(type);
        return Result.success(result);
    }

    @GetMapping("/get/{id}")
    public Result<Category> getById(@PathVariable Long id) {
        Category category = categoryService.getById(id);
        return Result.success(category);
    }

    @PostMapping("/add")
    public Result<Boolean> add(@RequestBody Category category) {
        log.info("添加收支类别: name={}, type={}", category.getName(), category.getType());

        if (StrUtil.isBlank(category.getName())) {
            return Result.error("类别名称不能为空");
        }

        if (category.getType() == null) {
            return Result.error("请选择类别类型");
        }

        if (categoryService.checkNameExists(category.getName(), category.getType(), null)) {
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

    @PostMapping("/update")
    public Result<Boolean> update(@RequestBody Category category) {
        log.info("更新收支类别: id={}, name={}", category.getId(), category.getName());

        if (category.getId() == null) {
            return Result.error("ID不能为空");
        }

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

        if (!existCategory.getName().equals(category.getName())
                || !existCategory.getType().equals(category.getType())) {
            if (categoryService.checkNameExists(category.getName(), category.getType(), category.getId())) {
                return Result.error("该类别名称已存在");
            }
        }

        boolean result = categoryService.updateById(category);
        return result ? Result.success("修改成功", true) : Result.error("修改失败");
    }

    @PostMapping("/delete/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        log.info("删除收支类别: id={}", id);
        boolean result = categoryService.removeById(id);
        return result ? Result.success("删除成功", true) : Result.error("删除失败");
    }

    @PostMapping("/batchDelete")
    public Result<Boolean> batchDelete(@RequestBody Long[] ids) {
        log.info("批量删除收支类别: count={}", ids.length);
        for (Long id : ids) {
            boolean result = categoryService.removeById(id);
            if (!result) {
                log.warn("批量删除失败: id={}", id);
                return Result.error("删除失败");
            }
        }
        return Result.success("批量删除成功", true);
    }
}
