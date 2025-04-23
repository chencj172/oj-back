package com.chencj.judge.service;


import com.chencj.api.client.ProblemClient;
import com.chencj.api.model.po.JudgeRecord;
import com.chencj.common.constant.RedisConstant;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName: UpdateJudgeRecord
 * @Description:
 * @Author: chencj
 * @Datetime: 2025/4/18 16:47
 * @Version: 1.0
 */
@AllArgsConstructor
public class UpdateJudgeRecord implements Runnable {

    private JudgeRecord judgeRecord;
    private StringRedisTemplate stringRedisTemplate;
    private ProblemClient problemClient;

    private Map<String, String> recordToMap(JudgeRecord judgeRecord) {
        Map<String, String> map = new HashMap<>();
        map.put("judgeResult", judgeRecord.getJudgeResult());
        map.put("errorInput", judgeRecord.getErrorInput());
        map.put("errorOutput", judgeRecord.getErrorOutput());
        map.put("runTime", String.valueOf(judgeRecord.getRunTime()));

        return map;
    }

    @Override
    public void run() {
        // 判题结果先存到Redis中，供前端查询
        String Key = RedisConstant.PROBLEM_JUDGE + judgeRecord.getUid() + ":" + judgeRecord.getPid();
        stringRedisTemplate.opsForHash().put(Key, "status", "success");

        Map<String, String> stringObjectMap = recordToMap(judgeRecord);
        stringRedisTemplate.opsForHash().putAll(Key, stringObjectMap);
        stringRedisTemplate.expire(Key, 1, TimeUnit.MINUTES);

        // 远程调用更新数据库、redis
        problemClient.saveJudge(judgeRecord);
    }
}
