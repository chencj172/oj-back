package com.chencj.posts.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chencj.common.utils.Result;
import com.chencj.posts.model.po.Posts;

/**
 * @ClassName: PostsService
 * @Description:
 * @Author: chencj
 * @Datetime: 2025/6/8 16:19
 * @Version: 1.0
 */
public interface PostsService extends IService<Posts> {
    Result<?> getAllPosts();

    Result<?> getPostsById(Integer id);
}
