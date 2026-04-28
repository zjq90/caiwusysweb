package com.caiwusys.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("wishes")
@ApiModel(description = "心愿单实体")
public class Wish implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "心愿ID")
    private Long id;

    @ApiModelProperty(value = "用户ID")
    private Long userId;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "目标金额")
    private BigDecimal targetAmount;

    @ApiModelProperty(value = "当前金额")
    private BigDecimal currentAmount;

    @ApiModelProperty(value = "截止日期")
    private LocalDate deadline;

    @ApiModelProperty(value = "状态：0-进行中，1-已完成")
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;

    @TableLogic
    @ApiModelProperty(value = "是否删除")
    private Integer deleted;

    @TableField(exist = false)
    @ApiModelProperty(value = "进度百分比")
    private BigDecimal progress;
}
