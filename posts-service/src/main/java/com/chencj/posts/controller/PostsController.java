package com.chencj.posts.controller;


import com.chencj.common.utils.Result;
import com.chencj.posts.service.PostsService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: PostsController
 * @Description:
 * @Author: chencj
 * @Datetime: 2025/6/8 16:23
 * @Version: 1.0
 */
@RestController
@RequestMapping("/posts")
public class PostsController {

    @Resource
    private PostsService postsService;

    /**
     * 获取帖子列表
     * @return
     */
    @GetMapping("/getAllPosts")
    public Result<?> getAllPosts() {
        return postsService.getAllPosts();
    }

    /**
     * 获取帖子详情
     * @param id
     * @return
     */
    @GetMapping("/getPostsById/{id}")
    public Result<?> getPostsById(@PathVariable("id") Integer id) {
        return postsService.getPostsById(id);
    }

}
