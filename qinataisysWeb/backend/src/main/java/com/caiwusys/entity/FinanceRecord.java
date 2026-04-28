package com.caiwusys.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("finance_records")
@ApiModel(description = "财务记录实体")
public class FinanceRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "记录ID")
    private Long id;

    @ApiModelProperty(value = "用户ID")
    private Long userId;

    @ApiModelProperty(value = "类型：1-收入，2-支出")
    private Integer type;

    @ApiModelProperty(value = "类别ID")
    private Long categoryId;

    @ApiModelProperty(value = "金额")
    private BigDecimal amount;

    @ApiModelProperty(value = "记账时间")
    private LocalDateTime recordTime;

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
    @ApiModelProperty(value = "类别名称")
    private String categoryName;

    @TableField(exist = false)
    @ApiModelProperty(value = "类别图标")
    private String categoryIcon;
}
