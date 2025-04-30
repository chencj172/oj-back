package com.chencj.api.client;

import com.chencj.api.model.po.JudgeRecord;
import com.chencj.common.utils.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

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

    @PutMapping("/sign/{uid}/{pid}")
    Result<?> signIn(@PathVariable("uid") Integer uid, @PathVariable("pid") Integer pid);
}
