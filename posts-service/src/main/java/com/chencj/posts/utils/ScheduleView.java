package com.chencj.posts.utils;


import com.chencj.common.constant.RedisConstant;
import com.chencj.posts.model.po.Posts;
import com.chencj.posts.service.PostsService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @ClassName: ScheduleView
 * @Description:
 * @Author: chencj
 * @Datetime: 2025/6/15 16:47
 * @Version: 1.0
 */
@Slf4j
@Component
public class ScheduleView {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private PostsService postsService;

    @Scheduled(fixedRate = 1000 * 60)  // 5分钟（毫秒）
    public void IncreaseView() {
        Map<Object, Object> viewMap = stringRedisTemplate.opsForHash().entries(RedisConstant.POSTS_VIEW);
        for(Map.Entry<Object, Object> view : viewMap.entrySet()) {
            Integer id = Integer.parseInt((String) view.getKey());
            Long num = Long.valueOf((String) view.getValue());
            if (num != 0) {
                // 写回Redis
                if(stringRedisTemplate.hasKey(RedisConstant.POSTS_INFO + id)) {
                    stringRedisTemplate.opsForHash().increment(RedisConstant.POSTS_INFO + id, "views", num);
                }

                // 写回数据库
                postsService.lambdaUpdate()
                        .setSql("views = views + " + num)
                        .eq(Posts::getId, id)
                        .update();
            }

            // 重置为0
            stringRedisTemplate.opsForHash().put(RedisConstant.POSTS_VIEW, id.toString(), String.valueOf(0));
            if (!stringRedisTemplate.hasKey(RedisConstant.POSTS_INFO + id)) {
                // 帖子过期，删除对应的哈希
                stringRedisTemplate.opsForHash().delete(RedisConstant.POSTS_VIEW, id.toString());
            }
        }
    }
}
