package com.caiwusys.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@ApiModel(description = "修改密码参数")
public class UpdatePasswordDTO {

    @NotBlank(message = "旧密码不能为空")
    @ApiModelProperty(value = "旧密码", required = true)
    private String oldPassword;

    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度为6-20个字符")
    @ApiModelProperty(value = "新密码", required = true)
    private String newPassword;

    @NotBlank(message = "确认密码不能为空")
    @ApiModelProperty(value = "确认密码", required = true)
    private String confirmPassword;
}
