package com.chencj.judge.controller;


import com.chencj.common.constant.RedisConstant;
import com.chencj.common.utils.Result;
import com.chencj.common.utils.UserContext;
import com.chencj.judge.service.JudgeService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: JudgeController
 * @Description:
 * @Author: chencj
 * @Datetime: 2025/4/16 09:40
 * @Version: 1.0
 */
@Slf4j
@RestController
@RequestMapping("/judge")
public class JudgeController {

    @Resource
    private JudgeService judgeService;

    @GetMapping("/getTestCaseStatus/{pid}")
    public Result<?> getTestCaseStatus(@PathVariable("pid") Integer pid) {
        // 回给前端当前评测数据的状态
        String Key = RedisConstant.PROBLEM_TESTCASE + UserContext.getUser() + ":" + pid;
        return Result.ok(judgeService.getTestCaseStatus(Key));
    }

    @GetMapping("/getJudgeStatus/{pid}")
    public Result<?> getJudgeStatus(@PathVariable("pid") Integer pid) {
        String Key = RedisConstant.PROBLEM_JUDGE + UserContext.getUser() + ":" + pid;
        log.info("judge result : {}", judgeService.getJudgeStatus(Key));
        return Result.ok(judgeService.getJudgeStatus(Key));
    }

}
