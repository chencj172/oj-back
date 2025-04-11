package com.chencj.judge.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * @ClassName: WebClientConfig
 * @Description:
 * @Author: chencj
 * @Datetime: 2025/4/11 15:37
 * @Version: 1.0
 */
@Configuration
public class WebClientConfig {
    @Bean
    public WebClient webClient() {
        return WebClient.create("http://localhost:5050");
    }
}
