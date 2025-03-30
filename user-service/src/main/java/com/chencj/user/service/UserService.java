package com.chencj.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chencj.common.utils.Result;
import com.chencj.user.model.dto.UserDto;
import com.chencj.user.model.po.User;

/**
 * @ClassName: UserService
 * @Description:
 * @Author: chencj
 * @Datetime: 2025/3/30 11:08
 * @Version: 1.0
 */
public interface UserService extends IService<User> {
    Result<?> login(UserDto userDto);

    Result<?> register(UserDto userDto);

    Result<?> getUserInfo();
}
