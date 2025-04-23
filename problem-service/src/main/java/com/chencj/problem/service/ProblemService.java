package com.chencj.problem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chencj.common.model.ProblemCodeDto;
import com.chencj.common.utils.Result;
import com.chencj.problem.model.po.Problem;

/**
 * @ClassName: ProblemService
 * @Description:
 * @Author: chencj
 * @Datetime: 2025/4/9 18:55
 * @Version: 1.0
 */
public interface ProblemService extends IService<Problem> {
    Result<?> search(String token, Integer level, String word, Integer pageNum, Integer pageSize);

    Result<?> getProblemById(Integer id);

    Result<?> judge(ProblemCodeDto testCaseCodeDto);

    Result<?> testCase(ProblemCodeDto testCaseCodeDto);
}
