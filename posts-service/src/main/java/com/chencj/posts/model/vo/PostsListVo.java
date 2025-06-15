package com.chencj.posts.model.vo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName: PostsListDto
 * @Description:
 * @Author: chencj
 * @Datetime: 2025/6/8 16:28
 * @Version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostsListVo {
    List<PostsVo>postsList;
}
