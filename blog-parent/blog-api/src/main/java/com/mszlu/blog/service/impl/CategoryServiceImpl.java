package com.mszlu.blog.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mszlu.blog.dao.mapper.CategoryMapepr;
import com.mszlu.blog.dao.pojo.Category;
import com.mszlu.blog.service.CategoryService;
import com.mszlu.blog.vo.CategoryVo;
import com.mszlu.blog.vo.Result;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapepr categoryMapepr;

    /**
     * 查找标签
     * @param categoryId
     * @return
     */
    @Override
    public CategoryVo findCategoryById(Long categoryId) {
        Category category = categoryMapepr.selectById(categoryId);
        CategoryVo categoryVo=new CategoryVo();
        BeanUtils.copyProperties(category,categoryVo);
        categoryVo.setId(String.valueOf(category.getId()));
        return categoryVo;
    }

    /**
     * 获取全部标签
     * @return
     */
    @Override
    public Result findAll() {
        LambdaQueryWrapper<Category> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.select(Category::getId,Category::getCategoryName);
        List<Category> categories = categoryMapepr.selectList(queryWrapper);
        List<CategoryVo> categoryVoList= copyList(categories);
        return Result.success(categoryVoList);
    }

    /**
     * 获取标签列表
     * @return
     */
    @Override
    public Result findAllCategoryDetail() {
        LambdaQueryWrapper<Category> queryWrapper=new LambdaQueryWrapper<>();
        List<Category> categories = categoryMapepr.selectList(queryWrapper);
        return Result.success(categories);
    }

    /**
     * 获取关联标签的文章
     * @param id
     * @return
     */
    @Override
    public Result findCategoryDetailById(Long id) {
        Category category = categoryMapepr.selectById(id);
        return Result.success(copy(category));
    }


    public CategoryVo copy(Category category){
        CategoryVo categoryVo = new CategoryVo();
        BeanUtils.copyProperties(category,categoryVo);
        categoryVo.setId(String.valueOf(category.getId()));
        return categoryVo;
    }
    public List<CategoryVo> copyList(List<Category> categoryList){
        List<CategoryVo> categoryVoList = new ArrayList<>();
        for (Category category : categoryList) {
            categoryVoList.add(copy(category));
        }
        return categoryVoList;
    }
}
