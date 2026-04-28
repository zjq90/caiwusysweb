package com.caiwusys.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.caiwusys.entity.Wish;

import java.util.List;

public interface WishService extends IService<Wish> {

    List<Wish> getMyWishes(Long userId);

    Page<Wish> getPage(Long userId, Integer page, Integer size, Integer status);

    Wish getDetail(Long id, Long userId);

    boolean addWish(Wish wish);

    boolean updateWish(Wish wish, Long userId);

    boolean deleteWish(Long id, Long userId);
}
