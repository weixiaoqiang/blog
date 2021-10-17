package com.mszlu.blog.controller;

import com.mszlu.blog.service.TagService;
import com.mszlu.blog.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("tags")
public class TagController {

    @Autowired
    private TagService tagService;

    /**
     * 最热标签展示
     * @return
     */
    @GetMapping("hot")
    public Result hot(){
        int limit=6;
        Result hots = tagService.hots(limit);
        return hots;
    }


    /**
     * 获取全部标签
     * @return
     */
    @GetMapping
    public Result getAllTags(){
        return  tagService.findAll();
    }

    /**
     * 获取标签列表
     * @return
     */
    @GetMapping("detail")
    public Result findAllDetail(){
        return  tagService.findAllDetail();
    }

    /**
     * 获取标签关联文章
     * @param id
     * @return
     */
    @GetMapping("detail/{id}")
    public Result findDetailById(@PathVariable("id") Long id){
        return  tagService.findDetailById(id);
    }

}
