package com.mszlu.blog.service;


import com.mszlu.blog.vo.Result;
import com.mszlu.blog.vo.params.ArticleParam;
import com.mszlu.blog.vo.params.LoginParam;
import com.mszlu.blog.vo.params.PageParams;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public interface ArticleService {

    /**
     *首页 分页查询 文章列表
     * @param pageParams
     * @return
     */
    Result listArticle(PageParams pageParams);

    /**
     * 首页最热文章
     * @param limit
     * @return
     */
    Result hotsArticle(int limit);

    /**
     * 首页最新文章
     * @param limit
     * @return
     */
    Result newArticle(int limit);

    /**
     * 首页文档归档
     * @param
     * @return
     */
    Result listArchives();

    Result findArticleId(Long articleId);

    /**
     * 提交文章
     * @param articleParam
     * @return
     */
    Result publishArticle(ArticleParam articleParam);

    /**
     * 获取我的文章
     * @param
     * @return
     */
    Result findMyArticle(HttpServletRequest request);
}
