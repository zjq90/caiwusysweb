package com.caiwusys.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caiwusys.common.PageResult;
import com.caiwusys.common.Result;
import com.caiwusys.entity.Memo;
import com.caiwusys.service.MemoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/memos")
@Api(tags = "备忘录模块")
public class MemoController {

    @Autowired
    private MemoService memoService;

    @GetMapping("/my")
    @ApiOperation("获取我的备忘录列表")
    public Result<List<Memo>> getMyMemos(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<Memo> memos = memoService.getMyMemos(userId);
        return Result.success(memos);
    }

    @GetMapping
    @ApiOperation("获取备忘录列表（分页）")
    public Result<PageResult<Memo>> getMemos(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer important,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Page<Memo> pageResult = memoService.getPage(userId, page, size, status, important);
        PageResult<Memo> result = PageResult.of(pageResult.getCurrent(), pageResult.getSize(),
                pageResult.getTotal(), pageResult.getRecords());
        return Result.success(result);
    }

    @GetMapping("/{id}")
    @ApiOperation("获取备忘录详情")
    public Result<Memo> getMemoDetail(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Memo memo = memoService.getDetail(id, userId);
        if (memo == null) {
            return Result.error("备忘录不存在");
        }
        return Result.success(memo);
    }

    @PostMapping
    @ApiOperation("添加备忘录")
    public Result<Boolean> addMemo(@RequestBody Memo memo, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        memo.setUserId(userId);
        boolean result = memoService.addMemo(memo);
        return Result.success("添加成功", result);
    }

    @PutMapping("/{id}")
    @ApiOperation("修改备忘录")
    public Result<Boolean> updateMemo(@PathVariable Long id, @RequestBody Memo memo, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        memo.setId(id);
        boolean result = memoService.updateMemo(memo, userId);
        if (!result) {
            return Result.error("修改失败，备忘录不存在或无权修改");
        }
        return Result.success("修改成功", result);
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除备忘录")
    public Result<Boolean> deleteMemo(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        boolean result = memoService.deleteMemo(id, userId);
        if (!result) {
            return Result.error("删除失败，备忘录不存在或无权删除");
        }
        return Result.success("删除成功", result);
    }
}
