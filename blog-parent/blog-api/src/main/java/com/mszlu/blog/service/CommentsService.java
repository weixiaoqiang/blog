package com.mszlu.blog.service;


import com.mszlu.blog.vo.Result;
import com.mszlu.blog.vo.params.CommentParam;

public interface CommentsService {
    Result getCommentsByArticleId(Long articleId);

    Result postComment(CommentParam commentParam);
}
