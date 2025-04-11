package com.chencj.problem.model.dto;


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
    // 题目Id
    private Integer id;
    // 提交的代码
    private String code;
}
