package com.caiwusys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caiwusys.entity.Memo;
import com.caiwusys.mapper.MemoMapper;
import com.caiwusys.service.MemoService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MemoServiceImpl extends ServiceImpl<MemoMapper, Memo> implements MemoService {

    @Override
    public List<Memo> getMyMemos(Long userId) {
        return list(new LambdaQueryWrapper<Memo>()
                .eq(Memo::getUserId, userId)
                .eq(Memo::getStatus, 0)
                .orderByDesc(Memo::getImportant)
                .orderByDesc(Memo::getReminderTime)
                .orderByDesc(Memo::getCreateTime));
    }

    @Override
    public Page<Memo> getPage(Long userId, Integer page, Integer size, Integer status, Integer important) {
        Page<Memo> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Memo> wrapper = new LambdaQueryWrapper<Memo>()
                .eq(Memo::getUserId, userId);
        if (status != null) {
            wrapper.eq(Memo::getStatus, status);
        }
        if (important != null) {
            wrapper.eq(Memo::getImportant, important);
        }
        wrapper.orderByDesc(Memo::getImportant)
                .orderByDesc(Memo::getReminderTime)
                .orderByDesc(Memo::getCreateTime);
        return page(pageParam, wrapper);
    }

    @Override
    public Memo getDetail(Long id, Long userId) {
        Memo memo = getById(id);
        if (memo != null && memo.getUserId().equals(userId)) {
            return memo;
        }
        return null;
    }

    @Override
    public boolean addMemo(Memo memo) {
        if (memo.getImportant() == null) {
            memo.setImportant(0);
        }
        if (memo.getStatus() == null) {
            memo.setStatus(0);
        }
        memo.setCreateTime(LocalDateTime.now());
        memo.setUpdateTime(LocalDateTime.now());
        return save(memo);
    }

    @Override
    public boolean updateMemo(Memo memo, Long userId) {
        Memo exist = getById(memo.getId());
        if (exist == null || !exist.getUserId().equals(userId)) {
            return false;
        }
        memo.setUserId(userId);
        memo.setUpdateTime(LocalDateTime.now());
        return updateById(memo);
    }

    @Override
    public boolean deleteMemo(Long id, Long userId) {
        Memo memo = getById(id);
        if (memo == null || !memo.getUserId().equals(userId)) {
            return false;
        }
        return removeById(id);
    }
}
