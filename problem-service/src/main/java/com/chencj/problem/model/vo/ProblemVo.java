package com.chencj.problem.model.vo;


import lombok.Data;

/**
 * @ClassName: ProblemVo
 * @Description:
 * @Author: chencj
 * @Datetime: 2025/4/9 19:30
 * @Version: 1.0
 */
@Data
public class ProblemVo {
    private Integer id;
    private String title;
    private Integer level;
    private Integer submitNum;
    private Integer acceptNum;
}
