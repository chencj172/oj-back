package com.chencj.posts.service.impl;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chencj.api.client.UserClient;
import com.chencj.common.constant.RedisConstant;
import com.chencj.common.utils.Result;
import com.chencj.posts.mapper.PostsMapper;
import com.chencj.posts.model.po.Posts;
import com.chencj.posts.model.vo.PostsListVo;
import com.chencj.posts.model.vo.PostsVo;
import com.chencj.posts.service.PostsService;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.awt.image.Kernel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.chencj.common.constant.RedisConstant.TAG_INFO;

/**
 * @ClassName: PostServiceImpl
 * @Description:
 * @Author: chencj
 * @Datetime: 2025/6/8 16:19
 * @Version: 1.0
 */
@Service
public class PostServiceImpl extends ServiceImpl<PostsMapper, Posts> implements PostsService {

    @Resource
    private UserClient userClient;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result<?> getAllPosts() {
        List<Posts> postsList = list();
        List<PostsVo> postsVoList = new ArrayList<>();
        // 封装返回帖子数据
        for (Posts posts : postsList) {
            PostsVo postsVo = BeanUtil.copyProperties(posts, PostsVo.class);
            postsVo.setTagList(new ArrayList<>());
            // 封装用户名
            Result<?> r = userClient.getUsername(postsVo.getUserId());
            postsVo.setUserName((String) r.getData());

            // 封装标签
            String[] tagsStr = postsVo.getTags().split(",");
            for (String s : tagsStr) {
                Object tag = stringRedisTemplate.opsForHash().get(TAG_INFO, s);
                postsVo.getTagList().add((String) tag);
            }
            Integer num = 0;
            Object o = stringRedisTemplate.opsForHash().get(RedisConstant.POSTS_VIEW, posts.getId().toString());
            if(o != null) num = Integer.parseInt((String) o);
            postsVo.setViews(postsVo.getViews() + (num == null ? 0 : num));
            postsVoList.add(postsVo);
        }

        return Result.ok(new PostsListVo(postsVoList));
    }

    @Override
    public Result<?> getPostsById(Integer id) {
        // 先增加访问量
        String view_key = RedisConstant.POSTS_VIEW;
        stringRedisTemplate.opsForHash().increment(view_key, id.toString(), 1L);
        Integer num = Integer.parseInt((String) stringRedisTemplate.opsForHash().get(view_key, id.toString()));

        // 然后封装详情
        String key = RedisConstant.POSTS_INFO + id;
        PostsVo postsVo = new PostsVo();
        if(stringRedisTemplate.hasKey(key)) {
            Map<Object, Object> postsMap = stringRedisTemplate.opsForHash().entries(key);
            BeanUtil.fillBeanWithMap(postsMap, postsVo, true);
        } else {
            Posts posts = getById(id);
            postsVo = BeanUtil.copyProperties(posts, PostsVo.class);
            postsVo.setTagList(new ArrayList<>());
            Result<?> r = userClient.getUsername(postsVo.getUserId());
            postsVo.setUserName((String) r.getData());
            String[] tagsStr = postsVo.getTags().split(",");
            for (String s : tagsStr) {
                Object tag = stringRedisTemplate.opsForHash().get(TAG_INFO, s);
                postsVo.getTagList().add((String) tag);
            }
            Map<String, String> stringMap = BeanUtil.beanToMap(postsVo)
                    .entrySet()
                    .stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> entry.getValue() != null ? entry.getValue().toString() : null
                    ));
            stringRedisTemplate.opsForHash().putAll(key, stringMap);
            stringRedisTemplate.expire(key, 1, TimeUnit.HOURS);
        }

        postsVo.setViews(postsVo.getViews() + (num == null ? 0 : num));
        return Result.ok(postsVo);
    }
}
