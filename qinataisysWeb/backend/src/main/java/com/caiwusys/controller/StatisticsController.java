package com.caiwusys.controller;

import com.caiwusys.common.Result;
import com.caiwusys.service.FinanceRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
@Api(tags = "财务统计与分析模块")
public class StatisticsController {

    @Autowired
    private FinanceRecordService financeRecordService;

    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    @GetMapping("/month-comparison")
    @ApiOperation("月度对比分析（本月与上月对比）")
    public Result<Map<String, Object>> getMonthComparison(
            @RequestParam String currentMonth,
            @RequestParam String lastMonth,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        
        YearMonth currentYm = YearMonth.parse(currentMonth, MONTH_FORMATTER);
        YearMonth lastYm = YearMonth.parse(lastMonth, MONTH_FORMATTER);
        
        Map<String, Object> comparison = financeRecordService.getMonthComparison(
                userId, 
                currentYm.getYear(), currentYm.getMonthValue(),
                lastYm.getYear(), lastYm.getMonthValue()
        );
        return Result.success(comparison);
    }

    @GetMapping("/yearly-overview")
    @ApiOperation("年度概览统计")
    public Result<Map<String, Object>> getYearlyOverview(
            @RequestParam String year,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        int yearInt = Integer.parseInt(year);
        
        Map<String, Object> overview = new HashMap<>();
        
        List<Map<String, Object>> monthlyStats = financeRecordService.getMonthlyStatistics(userId, yearInt);
        overview.put("monthlyStatistics", monthlyStats);
        
        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;
        
        for (Map<String, Object> stat : monthlyStats) {
            BigDecimal income = (BigDecimal) stat.get("income");
            BigDecimal expense = (BigDecimal) stat.get("expense");
            if (income != null) totalIncome = totalIncome.add(income);
            if (expense != null) totalExpense = totalExpense.add(expense);
        }
        
        overview.put("totalIncome", totalIncome);
        overview.put("totalExpense", totalExpense);
        overview.put("totalBalance", totalIncome.subtract(totalExpense));
        
        return Result.success(overview);
    }

    @GetMapping("/category-trend")
    @ApiOperation("类别趋势分析")
    public Result<Map<String, Object>> getCategoryTrend(
            @RequestParam Integer type,
            @RequestParam String month,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        
        YearMonth ym = YearMonth.parse(month, MONTH_FORMATTER);
        
        Map<String, Object> trend = new HashMap<>();
        
        List<Map<String, Object>> categoryStats = financeRecordService.getCategoryStatistics(
                userId, type, ym.getYear(), ym.getMonthValue()
        );
        trend.put("categoryStatistics", categoryStats);
        
        return Result.success(trend);
    }
}
