package com.caiwusys.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.caiwusys.entity.Budget;

import java.util.List;

public interface BudgetService extends IService<Budget> {

    List<Budget> getByMonth(Long userId, Integer year, Integer month);

    Page<Budget> getPage(Long userId, Integer page, Integer size);

    Budget getDetail(Long id, Long userId);

    boolean addBudget(Budget budget);

    boolean updateBudget(Budget budget, Long userId);

    boolean deleteBudget(Long id, Long userId);

    List<Budget> getBudgetReport(Long userId, Integer year, Integer month);
}
