package com.chencj.judge.consumer;


import cn.hutool.json.JSONUtil;
import com.chencj.api.client.PKClient;
import com.chencj.api.client.ProblemClient;
import com.chencj.api.client.UserClient;
import com.chencj.api.model.po.JudgeRecord;
import com.chencj.common.constant.StringConstant;
import com.chencj.common.model.ProblemCodeDto;
import com.chencj.judge.service.JudgeService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * @ClassName: CodeConsumer
 * @Description:
 * @Author: chencj
 * @Datetime: 2025/4/11 14:40
 * @Version: 1.0
 */
@Component
@Slf4j
public class CodeConsumer {

    @Resource
    private JudgeService judgeService;

    @Resource
    private ProblemClient problemClient;

    @Resource
    private PKClient pkClient;

    /**
     * 监听普通判题的请求
     * @param judgeJson
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "judge.queue"),
            arguments = @Argument(name = "x-queue-mode", value = "lazy"),
            exchange = @Exchange(name = "judge.direct"),
            key = {"judge"}
    ))
    public void listenJudge(String judgeJson) {
        ProblemCodeDto problemCodeDto = JSONUtil.toBean(judgeJson, ProblemCodeDto.class);
        judgeService.judgeProblem(problemCodeDto);
    }

    /**
     * 监听每日一题判题的请求
     * @param judgeJson
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "dailyproblem.queue"),
            arguments = @Argument(name = "x-queue-mode", value = "lazy"),
            exchange = @Exchange(name = "judge.direct"),
            key = {"dailyjudge"}
    ))
    public void listenDailyProblemJudge(String judgeJson) {
        ProblemCodeDto problemCodeDto = JSONUtil.toBean(judgeJson, ProblemCodeDto.class);
        JudgeRecord judgeRecord = judgeService.judgeProblem(problemCodeDto);
        log.info("judgeRecord : {}" ,judgeRecord);
        if(judgeRecord != null && StringConstant.ACCEPTED.equals(judgeRecord.getJudgeResult())) {
            // 保存用户签到结果
            problemClient.signIn(judgeRecord.getUid(), judgeRecord.getPid());
        }
    }

    /**
     * pk的监听队列
     * @param judgeJson
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "pk.queue"),
            arguments = @Argument(name = "x-queue-mode", value = "lazy"),
            exchange = @Exchange(name = "judge.direct"),
            key = {"PKjudge"}
    ))
    public void listenPKJudge(String judgeJson) {
        ProblemCodeDto problemCodeDto = JSONUtil.toBean(judgeJson, ProblemCodeDto.class);
        JudgeRecord judgeRecord = judgeService.judgeProblem(problemCodeDto);
        // 更新pk的redis
        if(StringConstant.ACCEPTED.equals(judgeRecord.getJudgeResult())) {
            pkClient.updatePKRecord(judgeRecord);
        }
    }

    /**
     * 监听运行测试用例的请求
     * @param testCaseJson
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "testcase.queue"),
            arguments = @Argument(name = "x-queue-mode", value = "lazy"),
            exchange = @Exchange(name = "judge.direct"),
            key = {"testcase"}
    ))
    public void listenTestCase(String testCaseJson) {
        // 拿到评测相关信息
        ProblemCodeDto problemCodeDto = JSONUtil.toBean(testCaseJson, ProblemCodeDto.class);
        judgeService.testProblemCase(problemCodeDto);
    }
}
