package com.chencj.judge;


import com.chencj.api.client.ProblemClient;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @ClassName: JudgeApplication
 * @Description:
 * @Author: chencj
 * @Datetime: 2025/4/11 14:39
 * @Version: 1.0
 */
@SpringBootApplication
@MapperScan("com.chencj.judge.mapper")
@EnableFeignClients(clients = { ProblemClient.class })
public class JudgeApplication {
    public static void main(String[] args) {
        SpringApplication.run(JudgeApplication.class, args);
    }
}
