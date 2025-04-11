package com.chencj.judge.consumer;


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
public class CodeConsumer {
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "code.queue"),
            arguments = @Argument(name = "x-queue-mode", value = "lazy"),
            exchange = @Exchange(name = "code.direct"),
            key = {"judge"}
    ))
    public void listenCode(String code) {
        // 调用go-judge进行判题
    }
}
