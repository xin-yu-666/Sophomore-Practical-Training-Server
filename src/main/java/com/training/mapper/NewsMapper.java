package com.training.mapper;

import com.training.entity.News;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NewsMapper {
    List<News> findAll(@Param("title") String title,
                       @Param("summary") String summary,
                       @Param("author") String author,
                       @Param("userId") Long userId,
                       @Param("status") Integer status);

    List<News> findByCondition(@Param("title") String title,
                               @Param("summary") String summary,
                               @Param("author") String author,
                               @Param("userId") Long userId,
                               @Param("status") Integer status,
                               @Param("offset") int offset,
                               @Param("size") int size);

    int countByCondition(@Param("title") String title,
                         @Param("summary") String summary,
                         @Param("author") String author,
                         @Param("userId") Long userId,
                         @Param("status") Integer status);

    News findById(Long id);

    int insert(News news);

    int update(News news);

    int delete(Long id);
} 