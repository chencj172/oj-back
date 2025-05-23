package com.chencj.user;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

/**
 * @ClassName: UserService
 * @Description:
 * @Author: chencj
 * @Datetime: 2025/3/30 10:37
 * @Version: 1.0
 */
@MapperScan("com.chencj.user.mapper")
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class UserApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }
}
