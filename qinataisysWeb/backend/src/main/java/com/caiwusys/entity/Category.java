package com.caiwusys.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("categories")
@ApiModel(description = "收支类别实体")
public class Category implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "类别ID")
    private Long id;

    @ApiModelProperty(value = "用户ID，0表示系统默认")
    private Long userId;

    @ApiModelProperty(value = "类别名称")
    private String name;

    @ApiModelProperty(value = "类型：1-收入，2-支出")
    private Integer type;

    @ApiModelProperty(value = "图标")
    private String icon;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;

    @TableLogic
    @ApiModelProperty(value = "是否删除")
    private Integer deleted;
}
