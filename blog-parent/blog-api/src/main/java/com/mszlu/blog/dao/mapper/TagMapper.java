package com.mszlu.blog.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mszlu.blog.dao.pojo.Tag;


import java.util.List;

public interface TagMapper extends BaseMapper<Tag> {

    /**
     * 根据文章id查询标签列表
     * @param articleId
     * @return
     */
//    @Select("select id,avatar,tag_name as tagName from ms_tag where id in (select tag_id from ms_article_tag where article_id= #{ articleId })")
    List<Tag> findTagsByArticleId(Long articleId);

    /**
     * 查询最热门的标签
     * @param limit
     * @return
     */
    List<Long> findHotsTagIDs(int limit);

    List<Tag> findTagByTagIds(List<Long> tagIds);


}
