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

    public void publishCodeToQueue(String code) {
        rabbitTemplate.convertAndSend(RabbitMQConstant.CODE_EXCHANGE, RabbitMQConstant.CODE_ROUTING_KEY, code);
    }

}
