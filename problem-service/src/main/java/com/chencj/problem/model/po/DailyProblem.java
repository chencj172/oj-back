package com.chencj.problem.model.po;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * daily_problem
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class    DailyProblem implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 题目Id
     */
    private Integer pid;

    /**
     * 生成时间
     */
    private LocalDateTime createTime;

    /**
     * 标题
     */
    private String title;

    private static final long serialVersionUID = 1L;
}