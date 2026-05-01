package com.caiwu.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caiwu.common.PageResult;
import com.caiwu.common.Result;
import com.caiwu.entity.News;
import com.caiwu.service.NewsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("/news")
public class NewsController {

    @Resource
    private NewsService newsService;

    @GetMapping("/list")
    public Result<PageResult<News>> list(@RequestParam(defaultValue = "1") Integer page,
                                            @RequestParam(defaultValue = "10") Integer size,
                                            @RequestParam(required = false) String title,
                                            @RequestParam(required = false) Integer status) {
        Page<News> pageResult = newsService.listPage(page, size, title, status);

        PageResult<News> result = new PageResult<>();
        result.setRecords(pageResult.getRecords());
        result.setTotal(pageResult.getTotal());
        result.setSize(pageResult.getSize());
        result.setCurrent(pageResult.getCurrent());
        result.setPages(pageResult.getPages());

        return Result.success(result);
    }

    @GetMapping("/get/{id}")
    public Result<News> getById(@PathVariable Long id) {
        News news = newsService.getById(id);
        return Result.success(news);
    }

    @PostMapping("/add")
    public Result<Boolean> add(@RequestBody News news) {
        log.info("添加财务新闻: title={}", news.getTitle());

        if (StrUtil.isBlank(news.getTitle())) {
            return Result.error("新闻标题不能为空");
        }

        if (news.getStatus() == null) {
            news.setStatus(1);
        }
        if (news.getViewCount() == null) {
            news.setViewCount(0);
        }

        boolean result = newsService.save(news);
        return result ? Result.success("添加成功", true) : Result.error("添加失败");
    }

    @PostMapping("/update")
    public Result<Boolean> update(@RequestBody News news) {
        log.info("更新财务新闻: id={}, title={}", news.getId(), news.getTitle());

        if (news.getId() == null) {
            return Result.error("ID不能为空");
        }

        if (StrUtil.isBlank(news.getTitle())) {
            return Result.error("新闻标题不能为空");
        }

        News existNews = newsService.getById(news.getId());
        if (existNews == null) {
            return Result.error("新闻不存在");
        }

        boolean result = newsService.updateById(news);
        return result ? Result.success("修改成功", true) : Result.error("修改失败");
    }

    @PostMapping("/delete/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        log.info("删除财务新闻: id={}", id);
        boolean result = newsService.removeById(id);
        return result ? Result.success("删除成功", true) : Result.error("删除失败");
    }

    @PostMapping("/batchDelete")
    public Result<Boolean> batchDelete(@RequestBody Long[] ids) {
        log.info("批量删除财务新闻: count={}", ids.length);
        for (Long id : ids) {
            boolean result = newsService.removeById(id);
            if (!result) {
                log.warn("批量删除失败: id={}", id);
                return Result.error("删除失败");
            }
        }
        return Result.success("批量删除成功", true);
    }
}
