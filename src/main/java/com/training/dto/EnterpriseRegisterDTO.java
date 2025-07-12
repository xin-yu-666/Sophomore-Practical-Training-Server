package com.training.dto;

import lombok.Data;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class EnterpriseRegisterDTO {
    @NotBlank(message = "企业名称不能为空")
    private String name;
    
    @NotBlank(message = "联系人不能为空")
    private String contact;
    
    @NotBlank(message = "联系电话不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    
    @NotBlank(message = "电子邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;
    
    @NotBlank(message = "用户名不能为空")
    private String username;
    
    @NotBlank(message = "密码不能为空")
    private String password;

    @NotBlank(message = "验证码不能为空")
    private String captcha;

    @NotBlank(message = "验证码ID不能为空")
    private String captchaId;
} 