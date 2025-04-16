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

    public void publishJudgeCodeToQueue(String msg) {
        rabbitTemplate.convertAndSend(RabbitMQConstant.CODE_EXCHANGE, RabbitMQConstant.CODE_ROUTING_KEY, msg);
    }

    public void publishTestCodeToQueue(String msg) {
        rabbitTemplate.convertAndSend(RabbitMQConstant.TESTCASE_CODE_EXCHANGE, RabbitMQConstant.TESTCASE_CODE_ROUTING_KEY, msg);
    }

}
