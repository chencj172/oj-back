package com.chencj.problem.service.Impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chencj.api.model.po.JudgeRecord;
import com.chencj.common.constant.RedisConstant;
import com.chencj.common.constant.StringConstant;
import com.chencj.common.utils.Result;
import com.chencj.common.utils.UserContext;
import com.chencj.problem.mapper.JudgeRecordMapper;
import com.chencj.problem.model.po.Problem;
import com.chencj.problem.model.po.UserAcproblem;
import com.chencj.problem.model.vo.JudgeRecordListVO;
import com.chencj.problem.model.vo.JudgeRecordVO;
import com.chencj.problem.service.JudgeRecordService;
import com.chencj.problem.service.ProblemService;
import com.chencj.problem.service.UserAcproblemService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
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

    @Resource
    private ProblemService problemService;

    @Resource
    private UserAcproblemService userAcproblemService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    /**
     * 获取评测记录列表
     *
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
        if (judgeRecordList.isEmpty()) {
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
     *
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

    /**
     * 保存评测记录，并且手动管理事务
     * @param judgeRecord
     * @return
     */
    @Override
    public Result<?> saveRecord(JudgeRecord judgeRecord) {
        transactionTemplate.execute(status -> {
            try {
                // 更新数据库相关信息
                save(judgeRecord);
                Problem problem = problemService.getById(judgeRecord.getPid());

                int num = (StringConstant.ACCEPTED.equals(judgeRecord.getJudgeResult()) ? 1 : 0);

                // 更新
                problemService.lambdaUpdate()
                        .set(Problem::getSubmitNum, problem.getSubmitNum() + 1)
                        .set(Problem::getAcceptNum, problem.getAcceptNum() + num)
                        .eq(Problem::getId, judgeRecord.getPid())
                        .update();

                // 更新用户通过列表
                Long count = userAcproblemService.lambdaQuery()
                        .eq(UserAcproblem::getUid, judgeRecord.getUid())
                        .eq(UserAcproblem::getPid, judgeRecord.getPid())
                        .count();

                if (count != null && count == 0 && num == 1) {
                    // 更新数据库
                    userAcproblemService.save(new UserAcproblem(null, judgeRecord.getUid(), judgeRecord.getPid(), 1));
                    // 删除Redis中的用户通过列表
                    // String keyUserAcList = RedisConstant.USER_AC_PROBLEM_LIST + judgeRecord.getUid();
                    // stringRedisTemplate.delete(keyUserAcList);
                } else if (count != null && count == 0) {
                    userAcproblemService.save(new UserAcproblem(null, judgeRecord.getUid(), judgeRecord.getPid(), 0));
                }
                // 删除Redis中的记录列表
                String keyRecord = RedisConstant.PROBLEM_JUDGE_RECORD_LIST + judgeRecord.getUid() + ":" + judgeRecord.getPid();
                stringRedisTemplate.delete(keyRecord);

            } catch (Exception e) {
                // 异常回滚
                log.error("保存信息异常: {}", e.getMessage());
                status.setRollbackOnly();
            }
            return null;
        });

        return Result.ok();
    }
}
