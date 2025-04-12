package com.chencj.judge.config;


import io.netty.channel.ChannelOption;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

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
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(5)) // 响应超时
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000); // 连接超时
        return WebClient.builder()
                .baseUrl("https://jsonplaceholder.typicode.com")
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}
