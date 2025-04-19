package com.chencj.api.client;

import com.chencj.api.model.po.JudgeRecord;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @ClassName: ProblemClient
 * @Description:
 * @Author: chencj
 * @Datetime: 2025/4/19 15:53
 * @Version: 1.0
 */
@FeignClient("problem-service")
public interface ProblemClient {
    @PostMapping("/problem/saveJudgeRecord")
    void saveJudge(JudgeRecord judgeRecord);
}
