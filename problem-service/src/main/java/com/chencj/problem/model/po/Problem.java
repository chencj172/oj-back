package com.chencj.problem.model.po;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * problem
 */
@Data
public class Problem implements Serializable {
    /**
     * 题目id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 标题
     */
    private String title;

    /**
     * 题目内容
     */
    private String content;

    /**
     * 题目答案输入
     */
    private String answerInput;

    /**
     * 题目提交数
     */
    private Integer submitNum;

    /**
     * 题目通过数
     */
    private Integer acceptNum;

    /**
     * 题目难度
     */
    private Integer level;

    /**
     * 题目答案输出
     */
    private String answerOutput;

    /**
     * 测试样例输入
     */
    private String caseInput;

    /**
     * 测试样例输出
     */
    private String caseOutput;

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

    private static final long serialVersionUID = 1L;
}