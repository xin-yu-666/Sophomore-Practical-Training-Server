package com.training.service.impl;

import com.training.entity.Enterprise;
import com.training.mapper.EnterpriseMapper;
import com.training.service.EnterpriseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EnterpriseServiceImpl implements EnterpriseService {
    @Autowired
    private EnterpriseMapper enterpriseMapper;

    @Override
    public Enterprise findById(Long id) {
        return enterpriseMapper.findById(id);
    }

    @Override
    public Enterprise findByName(String name) {
        return enterpriseMapper.findByName(name);
    }

    @Override
    public List<Enterprise> findAll() {
        return enterpriseMapper.findAll();
    }

    @Override
    @Transactional
    public void create(Enterprise enterprise) {
        if (enterprise == null) {
            throw new NullPointerException("参数不能为空");
        }
        enterpriseMapper.insert(enterprise);
    }

    @Override
    @Transactional
    public void update(Enterprise enterprise) {
        if (enterprise == null) {
            throw new NullPointerException("参数不能为空");
        }
        enterpriseMapper.update(enterprise);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        enterpriseMapper.deleteById(id);
    }

    @Override
    public List<Enterprise> findByCondition(String name, String contact, Integer status) {
        return enterpriseMapper.findByCondition(name, contact, status);
    }
}