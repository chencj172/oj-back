package com.chencj.problem.service;

import com.chencj.common.utils.Result;

/**
 * @ClassName: SignInService
 * @Description:
 * @Author: chencj
 * @Datetime: 2025/4/22 14:54
 * @Version: 1.0
 */
public interface SignInService {

    Result<?> signIn(Integer uid, Integer pid);

    Result<?> getUserSign(String date);
}
