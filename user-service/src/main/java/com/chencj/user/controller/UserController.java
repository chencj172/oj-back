package com.chencj.user.controller;


import cn.hutool.json.JSONObject;
import com.chencj.common.utils.Result;
import com.chencj.user.model.dto.UserDto;
import com.chencj.user.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName: UserController
 * @Description:
 * @Author: chencj
 * @Datetime: 2025/3/30 11:09
 * @Version: 1.0
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/login")
    public Result<?> login(@RequestBody UserDto userDto) {
        return userService.login(userDto);
    }

    @PostMapping("/register")
    public Result<?> register(@RequestBody UserDto userDto) {
        return userService.register(userDto);
    }

    @GetMapping("/user-info")
    public Result<?> getUserInfo() {
        return userService.getUserInfo();
    }

    @PostMapping("/checkLogin")
    public Result<?> checkLogin(HttpServletRequest request) {
        return userService.checkLogin(request.getHeader("token"));
    }

}
