package com.chencj.judge.service;


import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.DelayQueue;

/**
 * @ClassName: JudgeService
 * @Description:
 * @Author: chencj
 * @Datetime: 2025/4/11 15:41
 * @Version: 1.0
 */
@Service
public class JudgeService {
    @Resource
    private WebClient webClient;

    public String judgeProblem(String code) {
        BlockingQueue<Integer> blockingQueue = new DelayQueue();
        return "";
    }
}
