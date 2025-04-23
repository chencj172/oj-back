package com.chencj.problem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chencj.common.utils.Result;
import com.chencj.problem.model.po.DailyProblem;

/**
 * @ClassName: DailyProblemService
 * @Description:
 * @Author: chencj
 * @Datetime: 2025/4/22 16:19
 * @Version: 1.0
 */
public interface DailyProblemService extends IService<DailyProblem> {
    Result<?> getProblemOfMonth(Long timeStamp);
}
