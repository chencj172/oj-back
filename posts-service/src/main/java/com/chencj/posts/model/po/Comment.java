package com.chencj.posts.model.po;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * comment
 */
@Data
public class Comment implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 关联的帖子id
     */
    private Integer postsId;

    /**
     * 父级评论id
     */
    private Integer parentId;

    /**
     * 内容
     */
    private String content;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 发布用户id
     */
    private Integer userId;

    private Integer replyUserId;

    private static final long serialVersionUID = 1L;
}