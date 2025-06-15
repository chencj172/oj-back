package com.chencj.posts.model.vo;


import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @ClassName: PostsDto
 * @Description:
 * @Author: chencj
 * @Datetime: 2025/6/8 16:28
 * @Version: 1.0
 */
@Data
public class PostsVo {
    private Integer id;
    private String title;
    private String content;
    private Integer userId;
    private String userName;
    private LocalDateTime createTime;
    private Integer views;
    private Integer comments;
    private Integer likes;
    private String tags;
    private List<String>tagList;
    private Integer status;
}
