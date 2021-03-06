package com.mszlu.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mszlu.blog.dao.dos.Archives;
import com.mszlu.blog.dao.mapper.ArticlMapper;
import com.mszlu.blog.dao.mapper.ArticleBodyMapper;
import com.mszlu.blog.dao.mapper.ArticleTagMapper;
import com.mszlu.blog.dao.mapper.SysUserMapper;
import com.mszlu.blog.dao.pojo.*;
import com.mszlu.blog.service.*;
import com.mszlu.blog.utils.JWTUtils;
import com.mszlu.blog.utils.UserThreadLocal;
import com.mszlu.blog.vo.ArticleBodyVo;
import com.mszlu.blog.vo.ArticleVo;
import com.mszlu.blog.vo.Result;
import com.mszlu.blog.vo.TagVo;
import com.mszlu.blog.vo.params.ArticleBodyParam;
import com.mszlu.blog.vo.params.ArticleParam;
import com.mszlu.blog.vo.params.LoginParam;
import com.mszlu.blog.vo.params.PageParams;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.lang.management.LockInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Handler;


@Slf4j
@Service
public class ArticleServiceImpl implements ArticleService {


    @Autowired
   private ArticlMapper articlMapper;
    @Autowired
   private SysUserMapper sysUserMapper;
    @Autowired
   private TagService tagService;
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ArticleTagMapper articleTagMapper;

    @Autowired
    private LoginService loginService;


    /**
     * ????????????Article???
     * @param pageParams
     * @return
     */
    @Override public Result listArticle(PageParams pageParams) {
        Page<Article> page=new Page<>(pageParams.getPage(),pageParams.getPageSize());

        IPage<Article> archivesIPage = articlMapper.listArchive(page, pageParams.getCategoryId(), pageParams.getTagId(), pageParams.getYear(), pageParams.getMonth());
        List<Article> records = archivesIPage.getRecords();
        return Result.success(copyList(records,true,true,true,true));
    }




    /**
     * ??????????????????
     * @param limit
     * @return
     */
    @Override
    public Result hotsArticle(int limit) {
        LambdaQueryWrapper<Article> articleLambdaQueryWrapper=new LambdaQueryWrapper<>();
        articleLambdaQueryWrapper.orderByDesc(Article::getViewCounts);
        articleLambdaQueryWrapper.select(Article::getId,Article::getTitle);
        articleLambdaQueryWrapper.last("limit "+limit);
        List<Article> articles = articlMapper.selectList(articleLambdaQueryWrapper);
        return  Result.success(copyList(articles,false,false));
    }

    /**
     * ??????????????????
     * @param limit
     * @return
     */
    @Override
    public Result newArticle(int limit) {
        LambdaQueryWrapper<Article> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Article::getCreateDate);
        queryWrapper.select(Article::getId,Article::getTitle,Article::getCreateDate);
        queryWrapper.last("limit "+limit);
        List<Article> articles = articlMapper.selectList(queryWrapper);
        return Result.success(copyList(articles,false,false));
    }

    /**
     * ????????????
     * @return
     */
    @Override
    public Result listArchives() {
        List<Archives> archivesList = articlMapper.listArchives();
        return Result.success(archivesList);
    }

    /**
     * ????????????
     * @param articleId
     * @return
     */

    @Autowired
    private ThreadService threadService;

    @Override
    public Result findArticleId(Long articleId) {
        /**
         * 1. ??????id?????? ????????????
         * 2. ??????bodyId???categoryId ??????????????????
         */
        Article  article =articlMapper.selectById(articleId);
        ArticleVo articleVo = copy(article, true, true, true, true);
        //????????????????????????????????????????????????????????????
        //??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        // ?????? ???????????????????????? ?????? ?????????????????????????????????????????? ?????????????????????
        //?????????  ????????????????????? ?????????????????????????????????????????????????????????
        threadService.updateArticleViewCount(articlMapper,article);
        return Result.success(articleVo);
    }


    /**
     * ????????????
     * @param articleParam
     * @return
     */
    @Override
    public Result publishArticle(ArticleParam articleParam) {
        //????????? ??????????????????????????????
        SysUser sysUser = UserThreadLocal.get();


        /**
         * 1. ???????????? ?????? ??????Article??????
         * 2. ??????id  ?????????????????????
         * 3. ??????  ????????????????????? ??????????????????
         * 4. body ???????????? article bodyId
         */
        Article article=new Article();
        article.setAuthorId(sysUser.getId());
        article.setViewCounts(0);
        article.setCommentCounts(0);
        article.setCreateDate(System.currentTimeMillis());
        article.setSummary(articleParam.getSummary());
        article.setTitle(articleParam.getTitle());
        article.setWeight(0);
        article.setCategoryId(Long.valueOf(articleParam.getCategory().getId()));

        articlMapper.insert(article);

        //articleTag
        List<TagVo> tags = articleParam.getTags();
        if(tags!=null){
            for(TagVo tag:tags){
                Long id = article.getId();
                ArticleTag articleTag=new ArticleTag();
                articleTag.setArticleId(id);
                articleTag.setTagId(Long.valueOf(tag.getId()));
                articleTagMapper.insert(articleTag);
            }
        }
        //body
        ArticleBodyParam body = articleParam.getBody();
        ArticleBody articleBody=new ArticleBody();
        articleBody.setContent(body.getContent());
        articleBody.setContentHtml(body.getContentHtml());
        articleBody.setArticleId(article.getId());
        articleBodyMapper.insert(articleBody);

        //??????articleBodyId
        article.setBodyId(articleBody.getId());
        articlMapper.updateById(article);

        Map<String,String> map=new HashMap<>();
        map.put("id",article.getId().toString());
        return Result.success(map);
    }


    /**
     * ??????????????????
     * @param
     * @return
     */
    @Override
    public Result findMyArticle(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        SysUser sysUser = loginService.checkToken(authorization);
       Long sysUserId = sysUser.getId();
        List<Article> myArticle = articlMapper.findMyArticle(sysUserId);
        return Result.success(copyList(myArticle,false,false));
    }

    @Autowired
    private ArticleBodyMapper articleBodyMapper;
   //??????????????????
    public ArticleBodyVo findArticleBodyById(Long bodyId) {
        ArticleBody articleBody = articleBodyMapper.selectById(bodyId);
        ArticleBodyVo articleBodyVo=new ArticleBodyVo();
        articleBodyVo.setContent(articleBody.getContent());
        return articleBodyVo;
    }



    private List<ArticleVo> copyList(List<Article> recods, boolean isTag, boolean isAuthor) {
        List<ArticleVo> articleVoList=new ArrayList<>();
        for(Article recod:recods){
            articleVoList.add(copy(recod,isTag,isAuthor,false,false));
        }
        return articleVoList;
    }
    private List<ArticleVo> copyList(List<Article> recods, boolean isTag, boolean isAuthor,boolean isBody,boolean isCategory) {
        List<ArticleVo> articleVoList=new ArrayList<>();
        for(Article recod:recods){
            articleVoList.add(copy(recod,isTag,isAuthor,isBody,isCategory));
        }
        return articleVoList;
    }

    private ArticleVo copy(Article article,boolean isTag,boolean isAuthor,boolean isBody,boolean isCategory){
        ArticleVo articleVo=new ArticleVo();
        articleVo.setId(String.valueOf(article.getId()));
        BeanUtils.copyProperties(article,articleVo);

        articleVo.setCreateDate(new DateTime(article.getCreateDate()).toString("yyyy-MM-dd HH:mm"));

        if(isTag){
            Long articleId=article.getId();
            articleVo.setTags(tagService.findTagsByArticleId(articleId));
        }
        if(isAuthor){
            Long authorId=article.getAuthorId();
            articleVo.setAuthor(sysUserMapper.selectById(authorId).getNickname());
        }
        if(isBody){
            Long bodyId=article.getBodyId();
            articleVo.setBody(findArticleBodyById(bodyId));
        }
        if(isCategory){
            Long categoryId=article.getCategoryId();
            articleVo.setCategory(categoryService.findCategoryById(categoryId));
        }
        return articleVo;
    }



}

