package com.mszlu.blog.service;

import com.mszlu.blog.vo.CategoryVo;
import com.mszlu.blog.vo.Result;

public interface CategoryService {
    CategoryVo findCategoryById(Long catgoryId);

    Result findAll();

    Result findAllCategoryDetail();

    Result findCategoryDetailById(Long id);
}
