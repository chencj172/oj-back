package com.chencj.posts.controller;


import com.chencj.common.utils.Result;
import com.chencj.posts.model.po.Comment;
import com.chencj.posts.service.CommentService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName: CommentController
 * @Description:
 * @Author: chencj
 * @Datetime: 2025/6/22 15:29
 * @Version: 1.0
 */
@RestController
@RequestMapping("/comment")
public class CommentController {

    @Resource
    private CommentService commentService;

    @PostMapping("/create")
    public Result<?> createComment(@RequestBody Comment comment) {
        boolean save = commentService.save(comment);
        return save ? Result.ok() : Result.error("发布评论失败");
    }

    @GetMapping("/get/{postsId}")
    public Result<?> getAllComment(@PathVariable("postsId") Integer postsId) {
        return commentService.getAllComment(postsId);
    }

}
