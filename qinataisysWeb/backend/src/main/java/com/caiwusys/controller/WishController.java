package com.caiwusys.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caiwusys.common.PageResult;
import com.caiwusys.common.Result;
import com.caiwusys.entity.Wish;
import com.caiwusys.service.WishService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/wishes")
@Api(tags = "心愿单模块")
public class WishController {

    @Autowired
    private WishService wishService;

    @GetMapping("/my")
    @ApiOperation("获取我的心愿列表")
    public Result<List<Wish>> getMyWishes(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<Wish> wishes = wishService.getMyWishes(userId);
        return Result.success(wishes);
    }

    @GetMapping
    @ApiOperation("获取心愿列表（分页）")
    public Result<PageResult<Wish>> getWishes(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Page<Wish> pageResult = wishService.getPage(userId, page, size, status);
        PageResult<Wish> result = PageResult.of(pageResult.getCurrent(), pageResult.getSize(),
                pageResult.getTotal(), pageResult.getRecords());
        return Result.success(result);
    }

    @GetMapping("/{id}")
    @ApiOperation("获取心愿详情")
    public Result<Wish> getWishDetail(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Wish wish = wishService.getDetail(id, userId);
        if (wish == null) {
            return Result.error("心愿不存在");
        }
        return Result.success(wish);
    }

    @PostMapping
    @ApiOperation("添加心愿")
    public Result<Boolean> addWish(@RequestBody Wish wish, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        wish.setUserId(userId);
        boolean result = wishService.addWish(wish);
        return Result.success("添加成功", result);
    }

    @PutMapping("/{id}")
    @ApiOperation("修改心愿")
    public Result<Boolean> updateWish(@PathVariable Long id, @RequestBody Wish wish, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        wish.setId(id);
        boolean result = wishService.updateWish(wish, userId);
        if (!result) {
            return Result.error("修改失败，心愿不存在或无权修改");
        }
        return Result.success("修改成功", result);
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除心愿")
    public Result<Boolean> deleteWish(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        boolean result = wishService.deleteWish(id, userId);
        if (!result) {
            return Result.error("删除失败，心愿不存在或无权删除");
        }
        return Result.success("删除成功", result);
    }
}
