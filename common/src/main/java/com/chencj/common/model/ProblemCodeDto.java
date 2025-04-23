package com.chencj.common.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName: ProblemDto
 * @Description:
 * @Author: chencj
 * @Datetime: 2025/4/11 14:46
 * @Version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProblemCodeDto {
    /**
     * 用户Id
     */
    private Integer uid;

    /**
     * 题目Id
     */
    private Integer pid;

    /**
     * 测试样例/答案输入
     */
    private String input;

    /**
     * 测试样例/答案输出
     */
    private String output;

    /**
     * 提交的代码
     */
    private String code;

    /**
     * 代码语言
     */
    private String language;

    /**
     * 题目的时间限制 ms
     */
    private Integer timeLimit;

    /**
     * 题目的空间限制 mb
     */
    private Integer memoryLimit;

    /**
     * 题目的栈空间限制 mb
     */
    private Integer stackLimit;

    /**
     * 1 表示普通判题
     * 2 表示每日一题判题
     */
    private Integer origin;
}
