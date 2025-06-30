package com.training.controller;

import com.training.annotation.RequirePermission;
import com.training.common.Result;
import com.training.entity.Enterprise;
import com.training.service.EnterpriseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/enterprises")
public class EnterpriseController {
    @Autowired
    private EnterpriseService enterpriseService;

    @GetMapping("/public")
    public Result<List<Enterprise>> getPublicList() {
        // 只返回启用状态的企业，供注册时选择
        List<Enterprise> enterprises = enterpriseService.findByCondition(null, null, 1);
        return Result.success(enterprises);
    }

    @GetMapping
    @RequirePermission("ENTERPRISE_MANAGE")
    public Result<List<Enterprise>> list(@RequestParam(required = false) String name,
                                       @RequestParam(required = false) String contact,
                                       @RequestParam(required = false) Integer status) {
        List<Enterprise> enterprises = enterpriseService.findByCondition(name, contact, status);
        return Result.success(enterprises);
    }

    @GetMapping("/{id}")
    @RequirePermission("ENTERPRISE_MANAGE")
    public Result<Enterprise> getById(@PathVariable Long id) {
        Enterprise enterprise = enterpriseService.findById(id);
        return Result.success(enterprise);
    }

    @PostMapping
    @RequirePermission("ENTERPRISE_MANAGE")
    public Result<Void> create(@Valid @RequestBody Enterprise enterprise) {
        enterpriseService.create(enterprise);
        return Result.success();
    }

    @PutMapping("/{id}")
    @RequirePermission("ENTERPRISE_MANAGE")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody Enterprise enterprise) {
        enterprise.setId(id);
        enterpriseService.update(enterprise);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @RequirePermission("ENTERPRISE_MANAGE")
    public Result<Void> delete(@PathVariable Long id) {
        enterpriseService.deleteById(id);
        return Result.success();
    }
} 