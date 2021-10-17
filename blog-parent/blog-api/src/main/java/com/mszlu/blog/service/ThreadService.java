package com.mszlu.blog.service;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mszlu.blog.dao.mapper.ArticlMapper;
import com.mszlu.blog.dao.pojo.Article;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class ThreadService {

    @Async("taskExecutor")
    public void updateArticleViewCount(ArticlMapper articleMapper, Article article){
        Integer viewCounts = article.getViewCounts();
        Article articleUpdate=new Article();
        articleUpdate.setViewCounts(viewCounts+1);
        LambdaQueryWrapper<Article> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Article::getId,article.getId());
        //设置一个 为了在多线程的环境下 线程安全(在多人访问下保证去访问的是你当前数据库访问的数据，保证了一致性)
        queryWrapper.eq(Article::getViewCounts,viewCounts);

        articleMapper.update(articleUpdate,queryWrapper);
    }

    @Async("taskExecutor")
    public void uqateArticleCommentCounts(ArticlMapper articlMapper, Article article){
        Integer commentCounts = article.getCommentCounts();
        Article articleUpdate=new Article();
        articleUpdate.setCommentCounts(commentCounts+1);
        LambdaQueryWrapper<Article> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Article::getId,article.getId());

        queryWrapper.eq(Article::getCommentCounts,commentCounts);

        articlMapper.update(articleUpdate,queryWrapper);
    }
}
