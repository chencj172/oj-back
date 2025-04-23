package com.chencj.problem.model.po;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * user_acproblem
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAcproblem implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户ID
     */
    private Integer uid;

    /**
     * 题目ID
     */
    private Integer pid;

    /**
     * 题目状态：通过、尝试
     */
    private Integer status;

    private static final long serialVersionUID = 1L;
}