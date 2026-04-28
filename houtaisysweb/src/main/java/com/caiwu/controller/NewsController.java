package com.caiwu.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caiwu.common.Result;
import com.caiwu.entity.News;
import com.caiwu.service.NewsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/news")
public class NewsController {

    @Resource
    private NewsService newsService;

    @GetMapping("/list")
    public String listPage() {
        return "news/list";
    }

    @GetMapping("/data")
    @ResponseBody
    public Result<Page<News>> list(@RequestParam(defaultValue = "1") Integer page,
                                     @RequestParam(defaultValue = "10") Integer size,
                                     @RequestParam(required = false) String title,
                                     @RequestParam(required = false) Integer status) {
        Page<News> result = newsService.listPage(page, size, title, status);
        return Result.success(result);
    }

    @GetMapping("/add")
    public String addPage() {
        return "news/add";
    }

    @PostMapping("/add")
    @ResponseBody
    public Result<Boolean> add(@RequestBody News news) {
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

    @GetMapping("/edit/{id}")
    public String editPage(@PathVariable Long id, Model model) {
        News news = newsService.getById(id);
        model.addAttribute("news", news);
        return "news/edit";
    }

    @GetMapping("/get/{id}")
    @ResponseBody
    public Result<News> get(@PathVariable Long id) {
        News news = newsService.getById(id);
        return Result.success(news);
    }

    @PostMapping("/update")
    @ResponseBody
    public Result<Boolean> update(@RequestBody News news) {
        if (StrUtil.isBlank(news.getTitle())) {
            return Result.error("新闻标题不能为空");
        }
        News existNews = newsService.getById(news.getId());
        if (existNews == null) {
            return Result.error("新闻不存在");
        }
        news.setUpdateTime(LocalDateTime.now());
        boolean result = newsService.updateById(news);
        return result ? Result.success("修改成功", true) : Result.error("修改失败");
    }

    @PostMapping("/delete/{id}")
    @ResponseBody
    public Result<Boolean> delete(@PathVariable Long id) {
        boolean result = newsService.removeById(id);
        return result ? Result.success("删除成功", true) : Result.error("删除失败");
    }

    @PostMapping("/batchDelete")
    @ResponseBody
    public Result<Boolean> batchDelete(@RequestBody Long[] ids) {
        for (Long id : ids) {
            boolean result = newsService.removeById(id);
            if (!result) {
                return Result.error("删除失败");
            }
        }
        return Result.success("批量删除成功", true);
    }
}
