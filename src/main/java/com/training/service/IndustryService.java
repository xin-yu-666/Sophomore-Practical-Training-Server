package com.training.service;

import com.training.entity.Industry;
import com.training.common.Result;
import java.util.Map;
import java.util.List;

public interface IndustryService {
    Result<Map<String, Object>> getList(int page, int pageSize, String query);
    Result<Industry> getById(Long id);
    Result<Industry> create(Industry industry);
    Result<Industry> update(Industry industry);
    Result<Boolean> delete(Long id);
} 