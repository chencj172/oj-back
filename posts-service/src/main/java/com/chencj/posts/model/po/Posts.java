package com.chencj.posts.model.po;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * posts
 */
@Data
public class Posts implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 帖子标题
     */
    private String title;

    /**
     * 帖子内容
     */
    private String content;

    /**
     * 用户Id
     */
    private Integer userId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 查看次数
     */
    private Integer views;

    /**
     * 评论数量
     */
    private Integer comments;

    /**
     * 收藏数量
     */
    private Integer likes;

    /**
     * 帖子关联的标签
     */
    private String tags;

    /**
     * 是否解决
     */
    private Integer status;

    private static final long serialVersionUID = 1L;
}