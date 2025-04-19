package com.chencj.problem.service.Impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chencj.api.model.po.JudgeRecord;
import com.chencj.common.constant.RedisConstant;
import com.chencj.common.utils.Result;
import com.chencj.common.utils.UserContext;
import com.chencj.problem.mapper.JudgeRecordMapper;
import com.chencj.problem.model.vo.JudgeRecordListVO;
import com.chencj.problem.model.vo.JudgeRecordVO;
import com.chencj.problem.service.JudgeRecordService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName: JudgeRecoreServiceImpl
 * @Description:
 * @Author: chencj
 * @Datetime: 2025/4/18 15:37
 * @Version: 1.0
 */
@Service
@Slf4j
public class JudgeRecordServiceImpl extends ServiceImpl<JudgeRecordMapper, JudgeRecord> implements JudgeRecordService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 获取评测记录列表
     * @param pid
     * @return
     */
    @Override
    public Result<?> getJudgeRecordList(Integer pid) {
        String Key = RedisConstant.PROBLEM_JUDGE_RECORD_LIST + UserContext.getUser() + ":" + pid;
        List<String> recordJsonStrList = stringRedisTemplate.opsForList().range(Key, 0, -1);
        List<JudgeRecordVO> judgeRecordVOList = null;
        if (stringRedisTemplate.hasKey(Key)) {
            // Redis中有相应的记录
            if (recordJsonStrList != null && !recordJsonStrList.isEmpty()) {
                judgeRecordVOList = recordJsonStrList.stream().map((jsonStr) -> JSONUtil.toBean(jsonStr, JudgeRecordVO.class)).toList();
                return Result.ok(new JudgeRecordListVO(judgeRecordVOList));
            }
        }

        // 否则就去数据库中去找
        List<JudgeRecord> judgeRecordList = lambdaQuery()
                .eq(JudgeRecord::getUid, UserContext.getUser())
                .eq(JudgeRecord::getPid, pid)
                .list();
        if(judgeRecordList.isEmpty()) {
            return Result.ok(Collections.emptyList());
        }

        judgeRecordVOList = judgeRecordList.stream().map((judgeRecord) -> BeanUtil.copyProperties(judgeRecord, JudgeRecordVO.class)).toList();
        // 存到Redis中
        List<String> jsonStrList = judgeRecordVOList.stream().map((JSONUtil::toJsonStr)).toList();
        stringRedisTemplate.opsForList().leftPushAll(Key, jsonStrList);
        stringRedisTemplate.expire(Key, 10, TimeUnit.MINUTES);

        return Result.ok(new JudgeRecordListVO(judgeRecordVOList.reversed()));
    }

    /**
     * 获取评测详情
     * @param id
     * @return
     */
    @Override
    public Result<?> getJudgeRecordDetail(Integer id) {
        String Key = RedisConstant.PROBLEM_JUDGE_DETAIL + id;
        String recordJsonStr = stringRedisTemplate.opsForValue().get(Key);
        if (!StrUtil.isBlank(recordJsonStr)) {
            // redis中存在
            return Result.ok(JSONUtil.toBean(recordJsonStr, JudgeRecord.class));
        }

        // 从数据库中找，然后更新redis
        JudgeRecord judgeRecord = getById(id);
        stringRedisTemplate.opsForValue().set(Key, JSONUtil.toJsonStr(judgeRecord));
        stringRedisTemplate.expire(Key, 10, TimeUnit.MINUTES);
        return Result.ok(judgeRecord);
    }

    @Override
    public Result<?> saveRecord(JudgeRecord judgeRecord) {
        // 更新数据库
        save(judgeRecord);

        // 再看要不要更新Redis中的记录列表
        String keyRecord = RedisConstant.PROBLEM_JUDGE_RECORD_LIST + judgeRecord.getUid() + ":" + judgeRecord.getPid();
        if (stringRedisTemplate.hasKey(keyRecord)) {
            // 存在就新插入一条评测记录
            JudgeRecordVO judgeRecordVO = BeanUtil.copyProperties(judgeRecord, JudgeRecordVO.class);
            stringRedisTemplate.opsForList().leftPush(keyRecord, JSONUtil.toJsonStr(judgeRecordVO));
        }
        return Result.ok();
    }
}
