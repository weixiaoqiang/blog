package com.mszlu.blog;


import com.mszlu.blog.dao.mapper.ArticlMapper;
import com.mszlu.blog.dao.pojo.Article;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.util.List;

@Slf4j
@SpringBootTest
class BlogAppTests {
    @Autowired
    ArticlMapper articlMapper;

    @Autowired

    private RedisTemplate<String,String> redisTemplate;

    @Test
   public void test(){
//        String nickename="李四";
//        List<Article> usersArticle = articlMapper.findUsersArticle(nickename);
//
//        ValueOperations<String, String> stringStringValueOperations = redisTemplate.opsForValue();
//        RedisSerializer<?> valueSerializer = redisTemplate.getValueSerializer();
        Long token = redisTemplate.getExpire("token");
        BoundListOperations<String, String> token1 = redisTemplate.boundListOps("token");
        System.out.print(token1);

    }
}
