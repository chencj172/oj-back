package com.chencj.posts.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chencj.common.utils.Result;
import com.chencj.posts.model.po.Comment;

/**
 * @ClassName: CommentService
 * @Description:
 * @Author: chencj
 * @Datetime: 2025/6/22 15:28
 * @Version: 1.0
 */
public interface CommentService extends IService<Comment> {
    Result<?> getAllComment(Integer postsId);
}
