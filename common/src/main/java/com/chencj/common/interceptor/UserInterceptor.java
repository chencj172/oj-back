package com.chencj.common.interceptor;


import cn.hutool.core.util.StrUtil;
import com.chencj.common.utils.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;


/**
 * @ClassName: UserInterceptor
 * @Description:
 * @Author: chencj
 * @Datetime: 2025/3/15 13:49
 * @Version: 1.0
 */

public class UserInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 存储用户信息
        String userId = request.getHeader("userId");
        if(!StrUtil.isBlank(userId)) {
            UserContext.setUser(Integer.valueOf(userId));
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 销毁用户信息
        UserContext.removeUser();
    }
}
