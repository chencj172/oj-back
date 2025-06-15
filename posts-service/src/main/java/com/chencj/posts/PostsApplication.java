package com.chencj.posts;


import com.chencj.api.client.UserClient;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @ClassName: PostsApplication
 * @Description:
 * @Author: chencj
 * @Datetime: 2025/6/8 16:24
 * @Version: 1.0
 */
@MapperScan("com.chencj.posts.mapper")
@EnableFeignClients(clients = { UserClient.class })
@SpringBootApplication
@EnableScheduling
public class PostsApplication {
    public static void main(String[] args) {
        SpringApplication.run(PostsApplication.class, args);
    }
}
