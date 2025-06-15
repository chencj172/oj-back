package com.chencj.api.client;

import com.chencj.common.utils.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * @ClassName: UserClient
 * @Description:
 * @Author: chencj
 * @Datetime: 2025/4/22 11:47
 * @Version: 1.0
 */
@FeignClient("user-service")
public interface UserClient {
    @PostMapping("/user/checkLogin")
    Result<?> checkLogin(@RequestHeader(value = "token") String token);

    @GetMapping("/user/getUsername/{id}")
    Result<?> getUsername(@PathVariable(value = "id") Integer id);
}
