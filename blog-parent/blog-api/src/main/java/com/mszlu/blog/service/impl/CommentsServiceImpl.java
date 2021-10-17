package com.mszlu.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mszlu.blog.dao.mapper.ArticlMapper;
import com.mszlu.blog.dao.mapper.CommentsMapper;
import com.mszlu.blog.dao.pojo.Article;
import com.mszlu.blog.dao.pojo.Comment;
import com.mszlu.blog.dao.pojo.SysUser;
import com.mszlu.blog.service.CommentsService;
import com.mszlu.blog.service.SysUserService;
import com.mszlu.blog.service.ThreadService;
import com.mszlu.blog.utils.UserThreadLocal;
import com.mszlu.blog.vo.CommentVo;
import com.mszlu.blog.vo.Result;
import com.mszlu.blog.vo.UserVo;
import com.mszlu.blog.vo.params.CommentParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.ParameterResolutionDelegate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommentsServiceImpl implements CommentsService {

    @Autowired
    private CommentsMapper commentsMapper;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private ThreadService threadService;

    @Autowired
    private ArticlMapper articlMapper;

    /**
     * 获取文章评论
     * @param articleId
     * @return
     */
    @Override
    public Result getCommentsByArticleId(Long articleId) {
        /**
         * 1. 根据文章id 查询 评论列表 从 comment 表中查询
         * 2. 根据作者的id 查询作者的信息
         * 3. 判断 如果 level = 1 要去查询它有没有子评论
         * 4. 如果有 根据评论id 进行查询 （parent_id）
         */
        LambdaQueryWrapper<Comment> commentLambdaQueryWrapper=new LambdaQueryWrapper<>();
        commentLambdaQueryWrapper.eq(Comment::getArticleId,articleId);
        commentLambdaQueryWrapper.eq(Comment::getLevel,1);
        commentLambdaQueryWrapper.orderByDesc(Comment::getCreateDate);
        List<Comment> comments=commentsMapper.selectList(commentLambdaQueryWrapper);
        List<CommentVo> commentVos=copyList(comments);
        return Result.success(commentVos);
    }

    /**
     * 评论提交
     * @param commentParam
     * @return
     */
    @Override
    public Result postComment(CommentParam commentParam) {
        SysUser sysUser = UserThreadLocal.get();
        Comment comment=new Comment();
        comment.setArticleId(commentParam.getArticleId());
        comment.setAuthorId(sysUser.getId());
        comment.setContent(commentParam.getContent());
        comment.setCreateDate(System.currentTimeMillis());
        Long parent = commentParam.getParent();
        if(parent== null|| parent==0){
            comment.setLevel(1);
        }else {
            comment.setLevel(2);
        }
        comment.setParentId(parent== null ? 0 : parent);
        comment.setToUid(commentParam.getToUserId()==null ? 0:commentParam.getToUserId());
        commentsMapper.insert(comment);

        //评论完后，评论+1
        // (直接去数据库查，在多人访问会使查询速度过慢，不能用于大量访问，可以使用redis中间件,但其业务逻辑需要修改)
        Article article = articlMapper.selectById(commentParam.getArticleId());
        threadService.uqateArticleCommentCounts(articlMapper,article);

        return Result.success(null);
    }


    //文章评论的等级
    private List<CommentVo> findCommentByParentId(Long id) {

        LambdaQueryWrapper<Comment> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getParentId,id);
        queryWrapper.eq(Comment::getLevel,2);
        return copyList(commentsMapper.selectList(queryWrapper));

    }
    private List<CommentVo> copyList(List<Comment> comments) {
        List<CommentVo> commentVo=new ArrayList<>();
        for(Comment comment:comments){
            commentVo.add(copy(comment));
        }

        return  commentVo;
    }

    private CommentVo copy(Comment comment) {
        CommentVo commentVo=new CommentVo();
        BeanUtils.copyProperties(comment,commentVo);

        //作者信息
        commentVo.setId(String.valueOf(comment.getId()));
        UserVo userVo=sysUserService.findUserVoById(comment.getAuthorId());
        commentVo.setAuthor(userVo);
        //子评论
        Integer level = comment.getLevel();
        if(1==level){
          Long id = comment.getId();
            List<CommentVo> commentVoList= findCommentByParentId(id);
            commentVo.setChildrens(commentVoList);
        }
        //to User 给谁评论
        if (level>1){
            Long toUid = comment.getToUid();
            UserVo toUserVo =sysUserService.findUserVoById(toUid);
            commentVo.setToUser(toUserVo);
        }

        return commentVo;
    }


}
