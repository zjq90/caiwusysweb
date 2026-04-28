package com.caiwusys.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caiwusys.common.PageResult;
import com.caiwusys.common.Result;
import com.caiwusys.entity.FinanceRecord;
import com.caiwusys.service.FinanceRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/records")
@Api(tags = "财务管理模块")
public class FinanceRecordController {

    @Autowired
    private FinanceRecordService financeRecordService;

    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    @GetMapping
    @ApiOperation("获取记账列表（分页）")
    public Result<PageResult<FinanceRecord>> getRecords(
            @RequestParam(defaultValue = "1") @ApiParam("当前页码") Integer page,
            @RequestParam(defaultValue = "10") @ApiParam("每页数量") Integer size,
            @RequestParam(required = false) @ApiParam("类型：1-收入，2-支出") Integer type,
            @RequestParam(required = false) @ApiParam("搜索关键词") String keyword,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") @ApiParam("开始日期") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") @ApiParam("结束日期") LocalDate endDate,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Page<FinanceRecord> pageResult = financeRecordService.getPage(userId, page, size, type, keyword, startDate, endDate);
        PageResult<FinanceRecord> result = PageResult.of(pageResult.getCurrent(), pageResult.getSize(), 
                pageResult.getTotal(), pageResult.getRecords());
        return Result.success(result);
    }

    @GetMapping("/{id}")
    @ApiOperation("获取记账详情")
    public Result<FinanceRecord> getRecordDetail(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        FinanceRecord record = financeRecordService.getDetail(id, userId);
        if (record == null) {
            return Result.error("记录不存在");
        }
        return Result.success(record);
    }

    @PostMapping
    @ApiOperation("添加记账")
    public Result<Boolean> addRecord(@RequestBody FinanceRecord record, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        record.setUserId(userId);
        boolean result = financeRecordService.addRecord(record);
        return Result.success("添加成功", result);
    }

    @PutMapping("/{id}")
    @ApiOperation("修改记账")
    public Result<Boolean> updateRecord(@PathVariable Long id, @RequestBody FinanceRecord record, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        record.setId(id);
        boolean result = financeRecordService.updateRecord(record, userId);
        if (!result) {
            return Result.error("修改失败，记录不存在或无权修改");
        }
        return Result.success("修改成功", result);
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除记账")
    public Result<Boolean> deleteRecord(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        boolean result = financeRecordService.deleteRecord(id, userId);
        if (!result) {
            return Result.error("删除失败，记录不存在或无权删除");
        }
        return Result.success("删除成功", result);
    }

    @GetMapping("/monthly/summary")
    @ApiOperation("获取月度收支汇总")
    public Result<Map<String, Object>> getMonthlySummary(
            @RequestParam @ApiParam("月份，格式：yyyy-MM") String month,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        YearMonth ym = YearMonth.parse(month, MONTH_FORMATTER);
        Map<String, Object> summary = financeRecordService.getMonthlySummary(
                userId, ym.getYear(), ym.getMonthValue()
        );
        return Result.success(summary);
    }

    @GetMapping("/category/statistics")
    @ApiOperation("获取类别统计")
    public Result<List<Map<String, Object>>> getCategoryStatistics(
            @RequestParam @ApiParam("类型：1-收入，2-支出") Integer type,
            @RequestParam @ApiParam("月份，格式：yyyy-MM") String month,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        YearMonth ym = YearMonth.parse(month, MONTH_FORMATTER);
        List<Map<String, Object>> statistics = financeRecordService.getCategoryStatistics(
                userId, type, ym.getYear(), ym.getMonthValue()
        );
        return Result.success(statistics);
    }

    @GetMapping("/daily/statistics")
    @ApiOperation("获取每日收支统计")
    public Result<List<Map<String, Object>>> getDailyStatistics(
            @RequestParam @ApiParam("月份，格式：yyyy-MM") String month,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        YearMonth ym = YearMonth.parse(month, MONTH_FORMATTER);
        List<Map<String, Object>> statistics = financeRecordService.getDailyStatistics(
                userId, ym.getYear(), ym.getMonthValue()
        );
        return Result.success(statistics);
    }

    @GetMapping("/monthly/statistics")
    @ApiOperation("获取年度各月统计")
    public Result<List<Map<String, Object>>> getMonthlyStatistics(
            @RequestParam @ApiParam("年份，格式：yyyy") String year,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        int yearInt = Integer.parseInt(year);
        List<Map<String, Object>> statistics = financeRecordService.getMonthlyStatistics(userId, yearInt);
        return Result.success(statistics);
    }
}
