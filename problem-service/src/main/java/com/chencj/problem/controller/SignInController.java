package com.chencj.problem.controller;


import com.chencj.common.utils.Result;
import com.chencj.problem.service.DailyProblemService;
import com.chencj.problem.service.SignInService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName: SignInController
 * @Description: 每日一题相关
 * @Author: chencj
 * @Datetime: 2025/4/22 14:49
 * @Version: 1.0
 */
@Slf4j
@RestController
@RequestMapping("/sign")
public class SignInController {

    @Resource
    private SignInService signInService;

    @Resource
    private DailyProblemService dailyProblemService;

    @PutMapping("/{uid}/{pid}")
    public Result<?> signIn(@PathVariable("uid") Integer uid, @PathVariable("pid") Integer pid) {
        return signInService.signIn(uid, pid);
    }

    /**
     * yyyyMM形式的月份
     * @param date
     * @return
     */
    @GetMapping("/getUserSign/{date}")
    public Result<?> getUserSign(@PathVariable("date") String date, HttpServletResponse response) {
        response.setHeader("Connection", "keep-alive");
        return signInService.getUserSign(date);
    }

    /**
     * ms的时间戳
     * @param timeStamp
     * @return
     */
    @GetMapping("/getProblemOfMonth/{timeStamp}")
    public Result<?> getProblemOfMonth(@PathVariable("timeStamp") String timeStamp, HttpServletResponse response) {
        response.setHeader("Connection", "keep-alive");
        return dailyProblemService.getProblemOfMonth(Long.valueOf(timeStamp));
    }

}
