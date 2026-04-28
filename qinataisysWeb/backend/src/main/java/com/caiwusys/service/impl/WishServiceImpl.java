package com.caiwusys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caiwusys.entity.Wish;
import com.caiwusys.mapper.WishMapper;
import com.caiwusys.service.WishService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class WishServiceImpl extends ServiceImpl<WishMapper, Wish> implements WishService {

    @Override
    public List<Wish> getMyWishes(Long userId) {
        List<Wish> wishes = list(new LambdaQueryWrapper<Wish>()
                .eq(Wish::getUserId, userId)
                .orderByAsc(Wish::getStatus)
                .orderByDesc(Wish::getCreateTime));
        fillProgress(wishes);
        return wishes;
    }

    @Override
    public Page<Wish> getPage(Long userId, Integer page, Integer size, Integer status) {
        Page<Wish> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Wish> wrapper = new LambdaQueryWrapper<Wish>()
                .eq(Wish::getUserId, userId);
        if (status != null) {
            wrapper.eq(Wish::getStatus, status);
        }
        wrapper.orderByAsc(Wish::getStatus)
                .orderByDesc(Wish::getCreateTime);
        Page<Wish> result = page(pageParam, wrapper);
        fillProgress(result.getRecords());
        return result;
    }

    @Override
    public Wish getDetail(Long id, Long userId) {
        Wish wish = getById(id);
        if (wish != null && wish.getUserId().equals(userId)) {
            fillProgress(Arrays.asList(wish));
            return wish;
        }
        return null;
    }

    @Override
    public boolean addWish(Wish wish) {
        if (wish.getCurrentAmount() == null) {
            wish.setCurrentAmount(BigDecimal.ZERO);
        }
        if (wish.getStatus() == null) {
            wish.setStatus(0);
        }
        wish.setCreateTime(LocalDateTime.now());
        wish.setUpdateTime(LocalDateTime.now());
        return save(wish);
    }

    @Override
    public boolean updateWish(Wish wish, Long userId) {
        Wish exist = getById(wish.getId());
        if (exist == null || !exist.getUserId().equals(userId)) {
            return false;
        }
        wish.setUserId(userId);
        wish.setUpdateTime(LocalDateTime.now());
        return updateById(wish);
    }

    @Override
    public boolean deleteWish(Long id, Long userId) {
        Wish wish = getById(id);
        if (wish == null || !wish.getUserId().equals(userId)) {
            return false;
        }
        return removeById(id);
    }

    private void fillProgress(List<Wish> wishes) {
        for (Wish wish : wishes) {
            if (wish.getTargetAmount() != null && wish.getTargetAmount().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal current = wish.getCurrentAmount() != null ? wish.getCurrentAmount() : BigDecimal.ZERO;
                wish.setProgress(current
                        .multiply(new BigDecimal(100))
                        .divide(wish.getTargetAmount(), 2, BigDecimal.ROUND_HALF_UP));
            } else {
                wish.setProgress(BigDecimal.ZERO);
            }
        }
    }
}
