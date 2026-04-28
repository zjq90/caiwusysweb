package com.caiwusys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caiwusys.entity.News;
import com.caiwusys.mapper.NewsMapper;
import com.caiwusys.service.NewsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class NewsServiceImpl extends ServiceImpl<NewsMapper, News> implements NewsService {

    @Override
    public Page<News> getPage(Integer page, Integer size, String keyword) {
        Page<News> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<News> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(keyword)) {
            wrapper.like(News::getTitle, keyword)
                    .or().like(News::getSummary, keyword);
        }
        wrapper.orderByDesc(News::getPublishTime)
                .orderByDesc(News::getCreateTime);
        return page(pageParam, wrapper);
    }

    @Override
    public News getDetail(Long id) {
        News news = getById(id);
        if (news != null) {
            news.setViewCount(news.getViewCount() + 1);
            news.setUpdateTime(LocalDateTime.now());
            updateById(news);
        }
        return news;
    }
}
