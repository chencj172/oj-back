package com.chencj.problem.service;


import com.chencj.problem.model.po.DailyProblem;
import com.chencj.problem.model.po.Problem;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.Random;

/**
 * @ClassName: DailyProblemService
 * @Description: 负责生成每日一题
 * @Author: chencj
 * @Datetime: 2025/4/22 15:54
 * @Version: 1.0
 */
@Configuration
@EnableScheduling
public class CreateDailyProblem {

    @Resource
    private DailyProblemService dailyProblemService;

    @Resource
    private ProblemService problemService;

    @Scheduled(cron = "0 1 0 * * *")
    public void dailyProblem() {
        // 根据策略生成题目的index
        int pid = selectDailyProblem();
        String title = problemService.lambdaQuery()
                .eq(Problem::getId, pid)
                .one()
                .getTitle();


        dailyProblemService.save(new DailyProblem(null, pid, LocalDateTime.now(), title));
    }

    private Integer selectDailyProblem() {
        // 随机选择
        long total = problemService.count();
        Random rand = new Random(System.currentTimeMillis()); // 使用日期作为随机种子
        int pid = 204 + rand.nextInt((int) total);

        // 看看有没有选到过有的话需要重新选
        while (dailyProblemService.lambdaQuery().eq(DailyProblem::getPid, pid).count() != 0) {
            pid = 204 + rand.nextInt((int) total);
        }

        return pid;
    }
}
