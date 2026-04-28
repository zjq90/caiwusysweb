package com.caiwusys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caiwusys.entity.Budget;
import com.caiwusys.entity.Category;
import com.caiwusys.mapper.BudgetMapper;
import com.caiwusys.mapper.CategoryMapper;
import com.caiwusys.mapper.FinanceRecordMapper;
import com.caiwusys.service.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class BudgetServiceImpl extends ServiceImpl<BudgetMapper, Budget> implements BudgetService {

    @Autowired
    private FinanceRecordMapper financeRecordMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public List<Budget> getByMonth(Long userId, Integer year, Integer month) {
        List<Budget> budgets = list(new LambdaQueryWrapper<Budget>()
                .eq(Budget::getUserId, userId)
                .eq(Budget::getYear, year)
                .eq(Budget::getMonth, month)
                .orderByAsc(Budget::getCategoryId));
        fillBudgetInfo(budgets, userId, year, month);
        return budgets;
    }

    @Override
    public Page<Budget> getPage(Long userId, Integer page, Integer size) {
        Page<Budget> pageParam = new Page<>(page, size);
        Page<Budget> result = page(pageParam, new LambdaQueryWrapper<Budget>()
                .eq(Budget::getUserId, userId)
                .orderByDesc(Budget::getYear)
                .orderByDesc(Budget::getMonth)
                .orderByDesc(Budget::getCreateTime));
        fillBudgetInfo(result.getRecords(), null, null, null);
        return result;
    }

    @Override
    public Budget getDetail(Long id, Long userId) {
        Budget budget = getById(id);
        if (budget != null && budget.getUserId().equals(userId)) {
            fillBudgetInfo(Arrays.asList(budget), budget.getUserId(), budget.getYear(), budget.getMonth());
            return budget;
        }
        return null;
    }

    @Override
    public boolean addBudget(Budget budget) {
        budget.setCreateTime(LocalDateTime.now());
        budget.setUpdateTime(LocalDateTime.now());
        return save(budget);
    }

    @Override
    public boolean updateBudget(Budget budget, Long userId) {
        Budget exist = getById(budget.getId());
        if (exist == null || !exist.getUserId().equals(userId)) {
            return false;
        }
        budget.setUserId(userId);
        budget.setUpdateTime(LocalDateTime.now());
        return updateById(budget);
    }

    @Override
    public boolean deleteBudget(Long id, Long userId) {
        Budget budget = getById(id);
        if (budget == null || !budget.getUserId().equals(userId)) {
            return false;
        }
        return removeById(id);
    }

    @Override
    public List<Budget> getBudgetReport(Long userId, Integer year, Integer month) {
        return getByMonth(userId, year, month);
    }

    private void fillBudgetInfo(List<Budget> budgets, Long userId, Integer year, Integer month) {
        for (Budget budget : budgets) {
            if (userId != null && year != null && month != null) {
                BigDecimal usedAmount;
                if (budget.getCategoryId() != null) {
                    usedAmount = financeRecordMapper.getCategoryExpense(
                            userId, budget.getCategoryId(), year, month
                    );
                    Category category = categoryMapper.selectById(budget.getCategoryId());
                    if (category != null) {
                        budget.setCategoryName(category.getName());
                    }
                } else {
                    usedAmount = financeRecordMapper.getMonthlyTotal(userId, 2, year, month);
                    budget.setCategoryName("总预算");
                }
                budget.setUsedAmount(usedAmount != null ? usedAmount : BigDecimal.ZERO);
                budget.setRemainingAmount(budget.getAmount().subtract(budget.getUsedAmount()));
                if (budget.getAmount().compareTo(BigDecimal.ZERO) > 0) {
                    budget.setUsageRate(budget.getUsedAmount()
                            .multiply(new BigDecimal(100))
                            .divide(budget.getAmount(), 2, RoundingMode.HALF_UP));
                } else {
                    budget.setUsageRate(BigDecimal.ZERO);
                }
            }
        }
    }
}
