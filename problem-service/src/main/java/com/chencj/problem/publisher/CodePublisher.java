package com.chencj.problem.publisher;


import com.chencj.common.constant.RabbitMQConstant;
import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * @ClassName: CodePublisher
 * @Description:
 * @Author: chencj
 * @Datetime: 2025/4/11 14:29
 * @Version: 1.0
 */
@Component
public class CodePublisher {

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 将消息发送到判题队列
     * @param msg
     */
    public void publishJudgeCodeToQueue(String msg) {
        rabbitTemplate.convertAndSend(RabbitMQConstant.CODE_EXCHANGE, RabbitMQConstant.CODE_ROUTING_KEY, msg);
    }

    /**
     * 将消息发送到测试队列
     * @param msg
     */
    public void publishTestCodeToQueue(String msg) {
        rabbitTemplate.convertAndSend(RabbitMQConstant.CODE_EXCHANGE, RabbitMQConstant.TESTCASE_CODE_ROUTING_KEY, msg);
    }

    /**
     * 将消息发送到每日一题判题队列
     * @param msg
     */
    public void publishDailyProblemCodeToQueue(String msg) {
        rabbitTemplate.convertAndSend(RabbitMQConstant.CODE_EXCHANGE, RabbitMQConstant.DAILY_PROBLEM_CODE_ROUTING_KEY, msg);
    }

    /**
     * 将消息发送到pk队列里面
     * @param msg
     */
    public void publishPKCodeToQueue(String msg) {
        rabbitTemplate.convertAndSend(RabbitMQConstant.CODE_EXCHANGE, RabbitMQConstant.PK_CODE_ROUTING_KEY, msg);
    }
}
