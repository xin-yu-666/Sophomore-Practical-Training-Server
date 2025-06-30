package com.training.controller;

import com.training.common.Result;
import com.training.entity.Enterprise;
import com.training.entity.User;
import com.training.service.EnterpriseService;
import com.training.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/debug")
public class DebugController {
    
    @Autowired
    private EnterpriseService enterpriseService;
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/check-data")
    public Result<Map<String, Object>> checkData() {
        Map<String, Object> result = new HashMap<>();
        
        // 检查所有企业
        List<Enterprise> enterprises = enterpriseService.findAll();
        result.put("enterprises", enterprises);
        
        // 检查所有用户
        List<User> users = userService.findAll();
        result.put("users", users);
        
        // 检查test1用户
        User test1User = userService.findByUsername("test1");
        result.put("test1User", test1User);
        
        if (test1User != null && test1User.getEnterpriseId() != null) {
            Enterprise enterprise = enterpriseService.findById(test1User.getEnterpriseId());
            result.put("test1Enterprise", enterprise);
        }
        
        return Result.success(result);
    }
    
    @GetMapping("/test-join")
    public Result<User> testJoin() {
        User user = userService.findByUsernameWithEnterprise("test1");
        return Result.success(user);
    }
} 