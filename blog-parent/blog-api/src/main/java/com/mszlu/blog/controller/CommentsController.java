package com.mszlu.blog.controller;


import com.mszlu.blog.service.CommentsService;
import com.mszlu.blog.vo.Result;
import com.mszlu.blog.vo.params.CommentParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("comments")
public class CommentsController {

    @Autowired
    CommentsService commentsService;

    /**
     *查看评论
     * @param articleId
     * @return
     */
    @GetMapping("article/{id}")
    public Result getCommentsByArticleId(@PathVariable("id") Long articleId){

        return  commentsService.getCommentsByArticleId(articleId);
    }

    /**
     * 提交文章评论
     * @param commentParam
     * @return
     */
    @PostMapping("create/change")
    public Result  publishComment(@RequestBody CommentParam commentParam) {

        return commentsService.postComment(commentParam);
    }
}