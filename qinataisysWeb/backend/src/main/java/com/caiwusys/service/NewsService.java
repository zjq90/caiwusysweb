package com.caiwusys.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.caiwusys.entity.News;

public interface NewsService extends IService<News> {

    Page<News> getPage(Integer page, Integer size, String keyword);

    News getDetail(Long id);
}
