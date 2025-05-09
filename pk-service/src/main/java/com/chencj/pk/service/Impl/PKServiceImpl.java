package com.chencj.pk.service.Impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chencj.api.model.po.JudgeRecord;
import com.chencj.common.constant.RedisConstant;
import com.chencj.common.constant.StringConstant;
import com.chencj.common.utils.Result;
import com.chencj.pk.mapper.PkRecordMapper;
import com.chencj.pk.model.po.PkRecord;
import com.chencj.pk.service.PKService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * @ClassName: PKServiceImpl
 * @Description:
 * @Author: chencj
 * @Datetime: 2025/5/8 15:55
 * @Version: 1.0
 */
@Service
@Slf4j
public class PKServiceImpl extends ServiceImpl<PkRecordMapper, PkRecord> implements PKService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    // private final static ThreadPoolExecutor threadPool = new ThreadPoolExecutor(1, 3, 2, TimeUnit.SECONDS, new ArrayBlockingQueue<>(100));

    @Override
    public Result<?> updatePKRecord(JudgeRecord judgeRecord) {
        String Key1 = RedisConstant.CHALLENGE_RECORD + judgeRecord.getUid();
        Map<Object, Object> userMap1 = stringRedisTemplate.opsForHash().entries(Key1);
        String pkResult = (String) userMap1.get(StringConstant.CHALLENGE_RESULT);
        log.info("user : {}, result : {}", judgeRecord.getUid(), pkResult);

        if(StringConstant.TESTCASE_STATUS_PADDING.equals(pkResult)) {
            // 说明对方还没做出来，更新双方的状态
            stringRedisTemplate.opsForHash().put(Key1, StringConstant.CHALLENGE_RESULT, StringConstant.VICTORY);
            stringRedisTemplate.opsForHash().put(Key1, "timestamp", LocalDateTime.now().toString());

            Object otherUser = userMap1.get(StringConstant.OTHER_SIDE);
            log.info("otherUser : {}", otherUser);
            String Key2 = RedisConstant.CHALLENGE_RECORD + otherUser;
            stringRedisTemplate.opsForHash().put(Key2, StringConstant.CHALLENGE_RESULT, StringConstant.FAILED);
            stringRedisTemplate.opsForHash().put(Key2, "timestamp", LocalDateTime.now().toString());

            // 数据库添加pk记录
            save(new PkRecord(null, LocalDateTime.now(), judgeRecord.getUid(), StringConstant.VICTORY));
            save(new PkRecord(null, LocalDateTime.now(), Integer.valueOf(otherUser.toString()), StringConstant.FAILED));
            // threadPool.execute(() -> {
            //
            // });
        }
        return null;
    }

    @Override
    public Result<?> getChallengeResult(Integer userId) {
        String Key = RedisConstant.CHALLENGE_RECORD + userId;
        Object result = stringRedisTemplate.opsForHash().get(Key, StringConstant.CHALLENGE_RESULT);
        return Result.ok(result);
    }
}
