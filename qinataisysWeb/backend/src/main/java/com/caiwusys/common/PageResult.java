package com.caiwusys.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(description = "分页响应结果")
public class PageResult<T> {

    @ApiModelProperty(value = "当前页码")
    private Long current;

    @ApiModelProperty(value = "每页数量")
    private Long size;

    @ApiModelProperty(value = "总条数")
    private Long total;

    @ApiModelProperty(value = "总页数")
    private Long pages;

    @ApiModelProperty(value = "数据列表")
    private List<T> records;

    public static <T> PageResult<T> of(long current, long size, long total, List<T> records) {
        PageResult<T> result = new PageResult<>();
        result.setCurrent(current);
        result.setSize(size);
        result.setTotal(total);
        result.setPages(total % size == 0 ? total / size : total / size + 1);
        result.setRecords(records);
        return result;
    }
}
