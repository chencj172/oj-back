package com.chencj.problem.model.vo;


import lombok.Data;

import java.time.LocalDateTime;

/**
 * @ClassName: JudgeRecordVO
 * @Description:
 * @Author: chencj
 * @Datetime: 2025/4/19 09:47
 * @Version: 1.0
 */
@Data
public class JudgeRecordVO {
    private Integer id;
    private String judgeResult;
    private String language;
    private LocalDateTime createTime;
    private Integer runTime;
}
