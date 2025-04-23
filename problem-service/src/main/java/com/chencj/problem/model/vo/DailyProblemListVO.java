package com.chencj.problem.model.vo;


import com.chencj.problem.model.po.DailyProblem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName: DailyProblemListVO
 * @Description:
 * @Author: chencj
 * @Datetime: 2025/4/22 19:12
 * @Version: 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyProblemListVO {
    List<DailyProblem> dailyProblemList;
}