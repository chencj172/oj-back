package com.chencj.user.model.po;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * user
 */
@Data
public class User implements Serializable {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 密码
     */
    private String userPassword;

    /**
     * 手机号
     */
    private String userPhone;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户简介
     */
    private String userProfile;

    /**
     * 用户角色：0代表普通用户、1代表管理员
     */
    private Integer userRole;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 用户是否注销：0表示未注销、1表示已注销
     */
    private Integer isDelete;

    private static final long serialVersionUID = 1L;
}