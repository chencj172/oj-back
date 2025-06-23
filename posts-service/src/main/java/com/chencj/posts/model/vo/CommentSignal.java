package com.chencj.posts.model.vo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @ClassName: CommentSignal
 * @Description:
 * @Author: chencj
 * @Datetime: 2025/6/22 15:44
 * @Version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentSignal {
    private Integer id;
    private String username;
    private LocalDateTime createTime;
    private String replyUsername;
    private String content;
}
