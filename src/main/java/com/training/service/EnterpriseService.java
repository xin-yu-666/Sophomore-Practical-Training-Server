package com.training.service;

import com.training.entity.Enterprise;
import java.util.List;

public interface EnterpriseService {
    Enterprise findById(Long id);
    
    Enterprise findByName(String name);
    
    List<Enterprise> findAll();
    
    void create(Enterprise enterprise);
    
    void update(Enterprise enterprise);
    
    void deleteById(Long id);
    
    List<Enterprise> findByCondition(String name, String contact, Integer status);
} 