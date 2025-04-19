package com.chencj.problem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chencj.api.model.po.JudgeRecord;
import com.chencj.common.utils.Result;

/**
 * @ClassName: JudgeRecordService
 * @Description:
 * @Author: chencj
 * @Datetime: 2025/4/18 15:36
 * @Version: 1.0
 */
public interface JudgeRecordService extends IService<JudgeRecord> {
    Result<?> getJudgeRecordList(Integer pid);

    Result<?> getJudgeRecordDetail(Integer id);

    Result<?> saveRecord(JudgeRecord judgeRecord);
}
