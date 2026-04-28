package com.caiwusys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caiwusys.entity.Category;
import com.caiwusys.entity.FinanceRecord;
import com.caiwusys.mapper.CategoryMapper;
import com.caiwusys.mapper.FinanceRecordMapper;
import com.caiwusys.service.FinanceRecordService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FinanceRecordServiceImpl extends ServiceImpl<FinanceRecordMapper, FinanceRecord> implements FinanceRecordService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public Page<FinanceRecord> getPage(Long userId, Integer page, Integer size, Integer type, String keyword, LocalDate startDate, LocalDate endDate) {
        Page<FinanceRecord> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<FinanceRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FinanceRecord::getUserId, userId);
        if (type != null) {
            wrapper.eq(FinanceRecord::getType, type);
        }
        if (StringUtils.isNotBlank(keyword)) {
            wrapper.like(FinanceRecord::getDescription, keyword);
        }
        if (startDate != null) {
            wrapper.ge(FinanceRecord::getRecordTime, startDate.atStartOfDay());
        }
        if (endDate != null) {
            wrapper.le(FinanceRecord::getRecordTime, endDate.atTime(23, 59, 59));
        }
        wrapper.orderByDesc(FinanceRecord::getRecordTime);
        Page<FinanceRecord> result = page(pageParam, wrapper);
        for (FinanceRecord record : result.getRecords()) {
            fillCategoryInfo(record);
        }
        return result;
    }

    @Override
    public FinanceRecord getDetail(Long id, Long userId) {
        FinanceRecord record = getById(id);
        if (record != null && record.getUserId().equals(userId)) {
            fillCategoryInfo(record);
            return record;
        }
        return null;
    }

    @Override
    public boolean addRecord(FinanceRecord record) {
        record.setCreateTime(LocalDateTime.now());
        record.setUpdateTime(LocalDateTime.now());
        return save(record);
    }

    @Override
    public boolean updateRecord(FinanceRecord record, Long userId) {
        FinanceRecord exist = getById(record.getId());
        if (exist == null || !exist.getUserId().equals(userId)) {
            return false;
        }
        record.setUserId(userId);
        record.setUpdateTime(LocalDateTime.now());
        return updateById(record);
    }

    @Override
    public boolean deleteRecord(Long id, Long userId) {
        FinanceRecord record = getById(id);
        if (record == null || !record.getUserId().equals(userId)) {
            return false;
        }
        return removeById(id);
    }

    @Override
    public List<Map<String, Object>> getCategoryStatistics(Long userId, Integer type, Integer year, Integer month) {
        return baseMapper.getCategoryStatistics(userId, type, year, month);
    }

    @Override
    public Map<String, Object> getMonthlySummary(Long userId, Integer year, Integer month) {
        Map<String, Object> result = new HashMap<>();
        BigDecimal income = baseMapper.getMonthlyTotal(userId, 1, year, month);
        BigDecimal expense = baseMapper.getMonthlyTotal(userId, 2, year, month);
        result.put("income", income != null ? income : BigDecimal.ZERO);
        result.put("expense", expense != null ? expense : BigDecimal.ZERO);
        result.put("balance", (income != null ? income : BigDecimal.ZERO)
                .subtract(expense != null ? expense : BigDecimal.ZERO));
        return result;
    }

    @Override
    public List<Map<String, Object>> getDailyStatistics(Long userId, Integer year, Integer month) {
        List<Map<String, Object>> rawData = baseMapper.getDailyStatistics(userId, year, month);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> item : rawData) {
            Map<String, Object> newItem = new HashMap<>(item);
            Object dayObj = item.get("day");
            if (dayObj != null) {
                String dayStr = dayObj.toString();
                String formattedDate = String.format("%04d-%02d-%02d", year, month, Integer.parseInt(dayStr));
                newItem.put("date", formattedDate);
            }
            result.add(newItem);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getMonthlyStatistics(Long userId, Integer year) {
        List<Map<String, Object>> rawData = baseMapper.getMonthlyStatistics(userId, year);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> item : rawData) {
            Map<String, Object> newItem = new HashMap<>(item);
            Object monthObj = item.get("month");
            if (monthObj != null) {
                String formattedMonth = String.format("%04d-%02d", year, Integer.parseInt(monthObj.toString()));
                newItem.put("month", formattedMonth);
            }
            result.add(newItem);
        }
        return result;
    }

    @Override
    public Map<String, Object> getMonthComparison(Long userId, Integer currentYear, Integer currentMonth, Integer lastYear, Integer lastMonth) {
        Map<String, Object> result = new HashMap<>();
        
        Map<String, Object> current = getMonthlySummary(userId, currentYear, currentMonth);
        Map<String, Object> last = getMonthlySummary(userId, lastYear, lastMonth);
        
        result.put("current", current);
        result.put("last", last);
        
        BigDecimal currentIncome = (BigDecimal) current.get("income");
        BigDecimal lastIncome = (BigDecimal) last.get("income");
        BigDecimal currentExpense = (BigDecimal) current.get("expense");
        BigDecimal lastExpense = (BigDecimal) last.get("expense");
        
        result.put("incomeChange", calculateChange(currentIncome, lastIncome));
        result.put("expenseChange", calculateChange(currentExpense, lastExpense));
        
        result.put("currentCategoryExpense", baseMapper.getExpenseByCategory(userId, currentYear, currentMonth));
        result.put("lastCategoryExpense", baseMapper.getExpenseByCategory(userId, lastYear, lastMonth));
        
        return result;
    }

    private void fillCategoryInfo(FinanceRecord record) {
        if (record.getCategoryId() != null) {
            Category category = categoryMapper.selectById(record.getCategoryId());
            if (category != null) {
                record.setCategoryName(category.getName());
                record.setCategoryIcon(category.getIcon());
            }
        }
    }

    private Map<String, Object> calculateChange(BigDecimal current, BigDecimal last) {
        Map<String, Object> change = new HashMap<>();
        BigDecimal diff = current.subtract(last);
        change.put("diff", diff);
        if (last.compareTo(BigDecimal.ZERO) > 0) {
            change.put("rate", diff.multiply(new BigDecimal(100)).divide(last, 2, RoundingMode.HALF_UP));
        } else {
            change.put("rate", current.compareTo(BigDecimal.ZERO) > 0 ? new BigDecimal(100) : BigDecimal.ZERO);
        }
        change.put("trend", diff.compareTo(BigDecimal.ZERO) >= 0 ? "up" : "down");
        return change;
    }
}
