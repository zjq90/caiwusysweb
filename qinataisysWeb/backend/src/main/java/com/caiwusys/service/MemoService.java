package com.caiwusys.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.caiwusys.entity.Memo;

import java.util.List;

public interface MemoService extends IService<Memo> {

    List<Memo> getMyMemos(Long userId);

    Page<Memo> getPage(Long userId, Integer page, Integer size, Integer status, Integer important);

    Memo getDetail(Long id, Long userId);

    boolean addMemo(Memo memo);

    boolean updateMemo(Memo memo, Long userId);

    boolean deleteMemo(Long id, Long userId);
}
