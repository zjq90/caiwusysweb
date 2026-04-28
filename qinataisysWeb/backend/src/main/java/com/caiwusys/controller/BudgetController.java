package com.caiwusys.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caiwusys.common.PageResult;
import com.caiwusys.common.Result;
import com.caiwusys.entity.Budget;
import com.caiwusys.service.BudgetService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/budgets")
@Api(tags = "财务预算模块")
public class BudgetController {

    @Autowired
    private BudgetService budgetService;

    @GetMapping
    @ApiOperation("获取预算列表（分页）")
    public Result<PageResult<Budget>> getBudgets(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Page<Budget> pageResult = budgetService.getPage(userId, page, size);
        PageResult<Budget> result = PageResult.of(pageResult.getCurrent(), pageResult.getSize(),
                pageResult.getTotal(), pageResult.getRecords());
        return Result.success(result);
    }

    @GetMapping("/month")
    @ApiOperation("获取某月预算列表")
    public Result<List<Budget>> getBudgetsByMonth(
            @RequestParam Integer year,
            @RequestParam Integer month,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<Budget> budgets = budgetService.getByMonth(userId, year, month);
        return Result.success(budgets);
    }

    @GetMapping("/{id}")
    @ApiOperation("获取预算详情")
    public Result<Budget> getBudgetDetail(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Budget budget = budgetService.getDetail(id, userId);
        if (budget == null) {
            return Result.error("预算不存在");
        }
        return Result.success(budget);
    }

    @PostMapping
    @ApiOperation("添加预算")
    public Result<Boolean> addBudget(@RequestBody Budget budget, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        budget.setUserId(userId);
        boolean result = budgetService.addBudget(budget);
        return Result.success("添加成功", result);
    }

    @PutMapping("/{id}")
    @ApiOperation("修改预算")
    public Result<Boolean> updateBudget(@PathVariable Long id, @RequestBody Budget budget, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        budget.setId(id);
        boolean result = budgetService.updateBudget(budget, userId);
        if (!result) {
            return Result.error("修改失败，预算不存在或无权修改");
        }
        return Result.success("修改成功", result);
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除预算")
    public Result<Boolean> deleteBudget(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        boolean result = budgetService.deleteBudget(id, userId);
        if (!result) {
            return Result.error("删除失败，预算不存在或无权删除");
        }
        return Result.success("删除成功", result);
    }

    @GetMapping("/report")
    @ApiOperation("获取预算报表")
    public Result<List<Budget>> getBudgetReport(
            @RequestParam Integer year,
            @RequestParam Integer month,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<Budget> report = budgetService.getBudgetReport(userId, year, month);
        return Result.success(report);
    }
}
