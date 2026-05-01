package com.caiwu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.caiwu.entity.News;

public interface NewsService extends IService<News> {

    Page<News> listPage(Integer page, Integer size, String title, Integer status);
}
