package com.mszlu.blog.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mszlu.blog.dao.dos.Archives;
import com.mszlu.blog.dao.pojo.Article;

import java.util.List;


public interface ArticlMapper extends BaseMapper<Article> {

         List<Archives> listArchives();

    /**
     * 获取文章列表
     * @param page
     * @param categoryId
     * @param tagId
     * @param year
     * @param month
     * @return
     */
       IPage<Article> listArchive(Page<Article> page,
                                   Long categoryId,
                                   Long tagId,
                                   String year,
                                   String month);


       List<Article> findMyArticle(Long id);


}
