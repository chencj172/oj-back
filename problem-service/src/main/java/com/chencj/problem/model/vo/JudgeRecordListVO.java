package com.chencj.problem.model.vo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName: JudgeRecordListVO
 * @Description:
 * @Author: chencj
 * @Datetime: 2025/4/19 09:50
 * @Version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JudgeRecordListVO {
    List<JudgeRecordVO> judgeRecordList;
}
