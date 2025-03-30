package com.chencj.geteway.filter;

import com.chencj.geteway.config.AuthProperties;
import com.chencj.geteway.exception.UnauthorizedException;
import com.chencj.geteway.utils.JwtTool;
import jakarta.annotation.Resource;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @ClassName: AuthGlobalFilter
 * @Description:
 * @Author: chencj
 * @Datetime: 2025/3/15 10:32
 * @Version: 1.0
 */
@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    @Resource
    private AuthProperties authProperties;

    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Resource
    private JwtTool jwtTool;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 拿到request
        ServerHttpRequest request = exchange.getRequest();
        // 看请求的路径是不是需要拦截的
        if (isExclude(request.getPath().toString())) {
            // 不是的话直接放行
            return chain.filter(exchange);
        }

        // 是的话，通过请求头信息拿到对应的token
        String token = null;
        List<String> authorization = request.getHeaders().get("token");
        if(authorization != null && !authorization.isEmpty()) {
            token = authorization.get(0);
        }
        // 不为空进一步判断
        Integer userId = null;
        try {
            userId = jwtTool.parseToken(token);
        } catch (UnauthorizedException e) {
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        // 传递
        String userinfo = userId.toString();
        ServerWebExchange swe = exchange.mutate()
                .request(builder -> builder.header("userId", userinfo))
                .build();
        return chain.filter(swe);
    }

    private boolean isExclude(String path) {
        for (String excludePath : authProperties.getExcludePaths()) {
            if(antPathMatcher.match(excludePath, path)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
