package com.mszlu.blog.controller;




import com.mszlu.blog.common.aop.LogAnnotation;
import com.mszlu.blog.common.cache.Cache;
import com.mszlu.blog.service.ArticleService;
import com.mszlu.blog.vo.Result;

import com.mszlu.blog.vo.params.ArticleParam;
import com.mszlu.blog.vo.params.PageParams;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


//json数据进行交互
@RestController
@RequestMapping("articles")
public class ArticleController {

    @Autowired
    ArticleService articleService;

    /**
     * 首页 文章列表
     * 分页查询
     * @param pageParams
     * @return
     */
    @LogAnnotation(module = "文章",operator = "获取文章列表")
    @Cache(expire = 1 * 30 * 1000,name = "listArticle")
    @PostMapping
    public Result listArticle(@RequestBody PageParams pageParams){

        return  articleService.listArticle(pageParams);
    }

    /**
     * 首页最热文章
     * @return
     */
    @PostMapping("hot")
    public Result hotArticle(){
        int limit = 5;
        return articleService.hotsArticle(limit);
    }

    /**
     * 首页最新文章
     * @return
     */
    @PostMapping("new")
    public  Result newArticle(){
        int limit=4;
        return  articleService.newArticle(limit);
    }

    /**
     * 文章归档
     * @return
     */

    @PostMapping("listArchives")
    public Result listArchives(){
        return  articleService.listArchives();
    }


    /**
     * 查看文章
     * @param articleId
     * @return
     */
    @PostMapping("view/{id}")
    public Result viewArticle(@PathVariable("id") Long articleId){

        return  articleService.findArticleId(articleId);
    }

    /**
     * 提交文章
     * @param articleParam
     * @return
     */

    @PostMapping("publish")
    public Result publishArticle(@RequestBody ArticleParam articleParam){

        return  articleService.publishArticle(articleParam);
    }

    @PostMapping("myArticles")
     public  Result myArticle(HttpServletRequest request){
        return  articleService.findMyArticle(request);
    }

}
