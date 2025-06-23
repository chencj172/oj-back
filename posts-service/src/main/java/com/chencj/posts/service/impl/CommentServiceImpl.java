package com.chencj.posts.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chencj.api.client.UserClient;
import com.chencj.common.constant.RedisConstant;
import com.chencj.common.utils.Result;
import com.chencj.posts.mapper.CommentMapper;
import com.chencj.posts.model.po.Comment;
import com.chencj.posts.model.vo.CommentSignal;
import com.chencj.posts.model.vo.CommentVO;
import com.chencj.posts.model.vo.CommentVOList;
import com.chencj.posts.service.CommentService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName: CommentServiceImpl
 * @Description:
 * @Author: chencj
 * @Datetime: 2025/6/22 15:28
 * @Version: 1.0
 */
@Service
@Slf4j
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService{

    @Resource
    private UserClient userClient;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result<?> getAllComment(Integer postsId) {
        String key = RedisConstant.POSTS_COMMENT + postsId;
        CommentVOList commentList = null;
        if(stringRedisTemplate.hasKey(key)) {
            String commentsStr = stringRedisTemplate.opsForValue().get(key);
            commentList = JSONUtil.toBean(commentsStr, CommentVOList.class);
        } else {
            // 先拿到该帖子下所有的评论
            List<Comment> comments = lambdaQuery().eq(Comment::getPostsId, postsId)
                    .orderByAsc(Comment::getCreateTime)
                    .list();
            if(comments == null || comments.isEmpty()) {
                return Result.ok(Collections.emptyList());
            }

            commentList = new CommentVOList();
            commentList.setCommentVOS(new ArrayList<>());
            List<Boolean> flag = new ArrayList<>(Collections.nCopies(comments.size(), false));

            for(Comment comment : comments) {
                if(comment.getParentId() == -1) {
                    // 先封装top
                    CommentSignal commentSignal = new CommentSignal();
                    commentSignal.setId(comment.getId());
                    commentSignal.setReplyUsername("");
                    commentSignal.setContent(comment.getContent());
                    commentSignal.setCreateTime(comment.getCreateTime());
                    Result<?> r = userClient.getUsername(comment.getUserId());
                    commentSignal.setUsername((String) r.getData());

                    // 然后封装下面的评论
                    List<CommentSignal> replyComments = new ArrayList<>();
                    getReplyComment(comments, flag, replyComments, comment.getId());
                    commentList.getCommentVOS().add(new CommentVO(commentSignal, replyComments));
                }
            }

            stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(commentList), 1, TimeUnit.DAYS);
        }

        return Result.ok(commentList);
    }

    /**
     *
     * @param comments
     * @param flag
     * @param replyComments
     * @param id 评论id
     */
    private void getReplyComment(List<Comment> comments, List<Boolean> flag, List<CommentSignal> replyComments, Integer id) {
        for(int i=0;i< comments.size();i++) {
            if(flag.get(i) || comments.get(i).getParentId() == -1) continue;
            if(comments.get(i).getParentId().equals(id)) {
                CommentSignal commentSignal = new CommentSignal();
                commentSignal.setId(comments.get(i).getId());
                commentSignal.setContent(comments.get(i).getContent());
                commentSignal.setCreateTime(comments.get(i).getCreateTime());
                Result<?> replyUsername = userClient.getUsername(comments.get(i).getReplyUserId());
                commentSignal.setReplyUsername((String) replyUsername.getData());
                Result<?> r = userClient.getUsername(comments.get(i).getUserId());
                commentSignal.setUsername((String) r.getData());
                replyComments.add(commentSignal);
                flag.set(i, true);
                getReplyComment(comments, flag, replyComments, commentSignal.getId());
            }
        }
    }
}
