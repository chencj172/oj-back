package com.chencj.judge.controller;


import com.chencj.common.constant.RedisConstant;
import com.chencj.common.utils.Result;
import com.chencj.judge.service.JudgeService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: JudgeController
 * @Description:
 * @Author: chencj
 * @Datetime: 2025/4/16 09:40
 * @Version: 1.0
 */
@RestController("/judge")
public class JudgeController {

    @Resource
    private JudgeService judgeService;

    @GetMapping("/getTestCaseStatus/{uid}/{pid}")
    public Result<?> getTestCaseStatus(@PathVariable("uid") Integer uid, @PathVariable("pid") Integer pid) {
        // 回给前端当前评测数据的状态
        String Key = RedisConstant.PROBLEM_TESTCASE + uid + ":" + pid;
        return Result.ok(judgeService.getTestCaseStatus(Key));
    }

}
