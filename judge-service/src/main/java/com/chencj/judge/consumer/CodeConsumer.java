package com.chencj.judge.consumer;


import cn.hutool.json.JSONUtil;
import com.chencj.common.model.ProblemCodeDto;
import com.chencj.judge.service.JudgeService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

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

    /**
     * 监听判题的请求
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
