package com.caiwusys.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("budgets")
@ApiModel(description = "预算实体")
public class Budget implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "预算ID")
    private Long id;

    @ApiModelProperty(value = "用户ID")
    private Long userId;

    @ApiModelProperty(value = "类别ID，null表示总预算")
    private Long categoryId;

    @ApiModelProperty(value = "预算金额")
    private BigDecimal amount;

    @TableField("budget_year")
    @ApiModelProperty(value = "年份")
    private Integer year;

    @TableField("budget_month")
    @ApiModelProperty(value = "月份")
    private Integer month;

    @ApiModelProperty(value = "描述")
    private String description;

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
    @ApiModelProperty(value = "已使用金额")
    private BigDecimal usedAmount;

    @TableField(exist = false)
    @ApiModelProperty(value = "剩余金额")
    private BigDecimal remainingAmount;

    @TableField(exist = false)
    @ApiModelProperty(value = "使用比例")
    private BigDecimal usageRate;

    @TableField(exist = false)
    @ApiModelProperty(value = "类别名称")
    private String categoryName;
}
