package com.chencj.pk.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.chencj.api.model.po.JudgeRecord;
import com.chencj.common.utils.Result;
import com.chencj.pk.model.po.PkRecord;
import org.springframework.stereotype.Service;

/**
 * @ClassName: PKService
 * @Description:
 * @Author: chencj
 * @Datetime: 2025/5/8 15:54
 * @Version: 1.0
 */
public interface PKService extends IService<PkRecord> {
    Result<?> updatePKRecord(JudgeRecord judgeRecord);

    Result<?> getChallengeResult(Integer userId);
}
