package com.mszlu.blog.controller;


import com.mszlu.blog.service.CategoryService;
import com.mszlu.blog.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("categorys")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     *获取全部文章分类
     * @return
     */
    @GetMapping
    public Result  getAllCategorys() {
        return categoryService.findAll();
    }

    /**
     *文章分类列表
     * @return
     */
    @GetMapping("detail")
    public  Result findAllCategoryDetail(){

        return  categoryService.findAllCategoryDetail();
    }

    /**
     *获取关联文章分类的文章
     * @param id
     * @return
     */
    @GetMapping("detail/{id}")
    public  Result findCategoryDetailById(@PathVariable("id") Long id){

     return  categoryService.findCategoryDetailById(id);
    }
}
