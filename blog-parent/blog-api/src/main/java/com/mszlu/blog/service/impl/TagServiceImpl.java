package com.mszlu.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mszlu.blog.dao.mapper.TagMapper;
import com.mszlu.blog.dao.pojo.Tag;
import com.mszlu.blog.service.TagService;
import com.mszlu.blog.vo.Result;
import com.mszlu.blog.vo.TagVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class TagServiceImpl implements TagService {

    @Autowired
     private TagMapper tagMapper;



    //通过文章Id查找文章关联标签
    @Override
    public List<TagVo> findTagsByArticleId(Long articleId) {
       List<Tag> tagList=tagMapper.findTagsByArticleId(articleId);
        return copyList(tagList);
    }

    /**
     * 最热标签展示
     * @param limit
     * @return
     */
    @Override
    public Result hots(int limit) {
        /**
         * 1. 标签所拥有的文章数量最多 最热标签
         * 2. 查询 根据tag_id 分组 计数，从大到小 排列 取前 limit个
         */

        List<Long> tagIDs = tagMapper.findHotsTagIDs(limit);
        if(CollectionUtils.isEmpty(tagIDs)){
            return Result.success(Collections.emptyList());
        }
        List<Tag> tagByTagIds = tagMapper.findTagByTagIds(tagIDs);
        return Result.success(tagByTagIds);
    }

    /**
     * 获取全部标签
     * @return
     */
    @Override
    public Result findAll() {
        LambdaQueryWrapper<Tag> queryWrapper=new LambdaQueryWrapper<>();
        List<Tag> tags = tagMapper.selectList(queryWrapper);
        List<TagVo> tagVoList = copyList(tags);
        return Result.success(tagVoList);
    }

    /**
     * 获取标签列表
     * @return
     */
    @Override
    public Result findAllDetail() {
        LambdaQueryWrapper<Tag> queryWrapper=new LambdaQueryWrapper<>();
        List<Tag> tags = tagMapper.selectList(queryWrapper);
        return  Result.success(tags);
    }

    /**
     * 获取标签关联文章
     * @param id
     * @return
     */

    @Override
    public Result findDetailById(Long id) {
        Tag tag = tagMapper.selectById(id);

        return Result.success(copy(tag));
    }

    private TagVo copy(Tag tag){
        TagVo tagVo = new TagVo();
        BeanUtils.copyProperties(tag,tagVo);
        tagVo.setId(String.valueOf(tag.getId()));
        return tagVo;
    }
    public List<TagVo> copyList(List<Tag> tagList){
        List<TagVo> tagVoList = new ArrayList<>();
        for (Tag tag : tagList) {
            tagVoList.add(copy(tag));
        }
        return tagVoList;
    }
}
