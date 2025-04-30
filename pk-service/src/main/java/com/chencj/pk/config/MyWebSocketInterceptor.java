package com.chencj.pk.config;

import com.chencj.api.client.UserClient;
import com.chencj.common.utils.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Slf4j
@Data
@AllArgsConstructor
public class MyWebSocketInterceptor implements HandshakeInterceptor {

    private UserClient userClient;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {
        // 获取请求参数或头信息
        String token = request.getHeaders().getFirst("Sec-WebSocket-Protocol");
        // 验证token有效性
        Result<?> res = userClient.checkLogin(token);
        if (res.getCode() != 200) {
            return false;
        }
        attributes.put("userId", res.getData());
        // 返回true继续握手，返回false中断握手
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception exception) {
        // 握手后的逻辑处理
        log.info("建立连接成功...");

        // 客户端使用子协议，响应体也要加上子协议
        HttpServletRequest httpRequest = ((ServletServerHttpRequest) request).getServletRequest();
        HttpServletResponse httpResponse = ((ServletServerHttpResponse) response).getServletResponse();
        if (httpRequest.getHeader("Sec-WebSocket-Protocol") != null) {
            httpResponse.addHeader("Sec-WebSocket-Protocol", httpRequest.getHeader("Sec-WebSocket-Protocol"));
        }
    }
}