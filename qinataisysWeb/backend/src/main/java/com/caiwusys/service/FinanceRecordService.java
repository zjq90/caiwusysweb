package com.caiwusys.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.caiwusys.entity.FinanceRecord;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface FinanceRecordService extends IService<FinanceRecord> {

    Page<FinanceRecord> getPage(Long userId, Integer page, Integer size, Integer type, String keyword, LocalDate startDate, LocalDate endDate);

    FinanceRecord getDetail(Long id, Long userId);

    boolean addRecord(FinanceRecord record);

    boolean updateRecord(FinanceRecord record, Long userId);

    boolean deleteRecord(Long id, Long userId);

    List<Map<String, Object>> getCategoryStatistics(Long userId, Integer type, Integer year, Integer month);

    Map<String, Object> getMonthlySummary(Long userId, Integer year, Integer month);

    List<Map<String, Object>> getDailyStatistics(Long userId, Integer year, Integer month);

    List<Map<String, Object>> getMonthlyStatistics(Long userId, Integer year);

    Map<String, Object> getMonthComparison(Long userId, Integer currentYear, Integer currentMonth, Integer lastYear, Integer lastMonth);
}
