package com.chencj.pk.config;


import com.chencj.api.client.UserClient;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * @ClassName: WebsocketConfig
 * @Description:
 * @Author: chencj
 * @Datetime: 2025/4/27 15:33
 * @Version: 1.0
 */
@Configuration
@EnableWebSocket
public class WebsocketConfig implements WebSocketConfigurer {

    @Resource
    private UserClient userClient;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // addHandler(myWebSocketHandler(), "/pk/challenge") 相当于@ServerEndpoint("/pk/challenge")
        registry.addHandler(myWebSocketHandler(), "/pk/challenge")
                .addInterceptors(new MyWebSocketInterceptor(userClient)) // 确保添加拦截器
                .setAllowedOrigins("*");
    }

    @Bean
    public WebSocketHandler myWebSocketHandler() {
        return new MyMessageHandler();
    }
}
