package com.chencj.geteway.utils;


import cn.hutool.core.exceptions.ValidateException;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTValidator;
import com.chencj.geteway.exception.UnauthorizedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @ClassName: JwtTool
 * @Description: Jwt工具类,用于生成token和验证token
 * @Author: chencj
 * @Datetime: 2025/3/30 13:55
 * @Version: 1.0
 */

@Component
public class JwtTool {

    @Value("${config.jwt.secret}")
    private String key;

    @Value("${config.jwt.expire}")
    private Integer TTL;

    /**
     * 创建 access-token
     *
     * @return access-token
     */
    public String createToken(Integer userId) {
        // 1.生成jws
        return JWT.create()
                .setPayload("userId", userId)
                .setExpiresAt(new Date(System.currentTimeMillis() + TTL * 1000))
                .setKey(key.getBytes())
                .sign();
    }

    /**
     * 解析token
     *
     * @param token token
     * @return 解析刷新token得到的用户信息
     */
    public Integer parseToken(String token) {
        // 1.校验token是否为空
        if (token == null) {
            throw new UnauthorizedException("未登录");
        }
        // 2.校验并解析jwt
        JWT jwt;
        try {
            jwt = JWT.of(token).setKey(key.getBytes());
        } catch (Exception e) {
            throw new UnauthorizedException("无效的token", e);
        }
        // 2.校验jwt是否有效
        if (!jwt.verify()) {
            // 验证失败
            throw new UnauthorizedException("无效的token");
        }
        // 3.校验是否过期
        try {
            JWTValidator.of(jwt).validateDate();
        } catch (ValidateException e) {
            throw new UnauthorizedException("token已经过期");
        }
        // 4.数据格式校验
        Object userPayload = jwt.getPayload("userId");
        if (userPayload == null) {
            // 数据为空
            throw new UnauthorizedException("无效的token");
        }

        // 5.数据解析
        try {
            return Integer.valueOf(userPayload.toString());
        } catch (RuntimeException e) {
            // 数据格式有误
            throw new UnauthorizedException("无效的token");
        }
    }
}
