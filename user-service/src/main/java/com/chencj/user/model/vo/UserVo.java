package com.chencj.user.model.vo;


import lombok.Data;

import java.time.LocalDateTime;

/**
 * @ClassName: UserVo
 * @Description:
 * @Author: chencj
 * @Datetime: 2025/3/30 15:19
 * @Version: 1.0
 */
@Data
public class UserVo {
    private String userAccount;
    private String userPhone;
    private String userName;
    private String userAvatar;
    private String userProfile;
    private LocalDateTime createTime;
}
