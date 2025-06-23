package com.chencj.posts.model.vo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName: CommentVO
 * @Description:
 * @Author: chencj
 * @Datetime: 2025/6/22 15:38
 * @Version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentVO {
    CommentSignal topComment;
    List<CommentSignal> replyComments;
}
