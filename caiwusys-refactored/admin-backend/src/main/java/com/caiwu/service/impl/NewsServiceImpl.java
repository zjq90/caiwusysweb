package com.caiwu.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caiwu.entity.News;
import com.caiwu.mapper.NewsMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class NewsServiceImpl extends ServiceImpl<NewsMapper, News> implements NewsService {

    @Resource
    private NewsMapper newsMapper;

    @Override
    public Page<News> listPage(Integer page, Integer size, String title, Integer status) {
        Page<News> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<News> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(title)) {
            queryWrapper.like(News::getTitle, title);
        }
        if (status != null) {
            queryWrapper.eq(News::getStatus, status);
        }
        queryWrapper.orderByDesc(News::getCreateTime);
        return newsMapper.selectPage(pageParam, queryWrapper);
    }
}
