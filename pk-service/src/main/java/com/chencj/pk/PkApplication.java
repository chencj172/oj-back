package com.chencj.pk;


import com.chencj.api.client.UserClient;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @ClassName: PkApplication
 * @Description:
 * @Author: chencj
 * @Datetime: 2025/4/27 15:24
 * @Version: 1.0
 */
@SpringBootApplication
@MapperScan("com.chencj.pk.mapper")
@EnableFeignClients(clients = {UserClient.class})
public class PkApplication {
    public static void main(String[] args) {
        SpringApplication.run(PkApplication.class, args);
    }
}
