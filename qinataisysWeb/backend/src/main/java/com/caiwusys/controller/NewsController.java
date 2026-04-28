package com.caiwusys.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caiwusys.common.PageResult;
import com.caiwusys.common.Result;
import com.caiwusys.entity.News;
import com.caiwusys.service.NewsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/news")
@Api(tags = "财务新闻模块")
public class NewsController {

    @Autowired
    private NewsService newsService;

    @GetMapping
    @ApiOperation("获取新闻列表（分页）")
    public Result<PageResult<News>> getNewsList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {
        Page<News> pageResult = newsService.getPage(page, size, keyword);
        PageResult<News> result = PageResult.of(pageResult.getCurrent(), pageResult.getSize(),
                pageResult.getTotal(), pageResult.getRecords());
        return Result.success(result);
    }

    @GetMapping("/{id}")
    @ApiOperation("获取新闻详情")
    public Result<News> getNewsDetail(@PathVariable Long id) {
        News news = newsService.getDetail(id);
        if (news == null) {
            return Result.error("新闻不存在");
        }
        return Result.success(news);
    }
}
