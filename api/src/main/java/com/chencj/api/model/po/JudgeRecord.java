package com.chencj.api.model.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * judge_record
 */
@Data
public class JudgeRecord implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户Id
     */
    private Integer uid;

    /**
     * 题目Id
     */
    private Integer pid;

    /**
     * 生成时间
     */
    private LocalDateTime createTime;

    /**
     * 判题结果：通过就是Accepted，否则就是出错的状态
     */
    private String judgeResult;

    /**
     * 如果出错了，出错的输入
     */
    private String errorInput;

    /**
     * 出错的输出
     */
    private String errorOutput;

    /**
     * 提交的代码
     */
    private String code;

    /**
     * 编程语言
     */
    private String language;

    /**
     * 程序运行时间 ms
     */
    private Integer runTime;

    /**
     * 程序运行占用空间 kb
     */
    private Integer runMemory;

    private static final long serialVersionUID = 1L;
}