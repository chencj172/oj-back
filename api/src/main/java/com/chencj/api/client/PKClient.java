package com.chencj.api.client;

import com.chencj.api.model.po.JudgeRecord;
import com.chencj.common.utils.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @ClassName: PKClient
 * @Description:
 * @Author: chencj
 * @Datetime: 2025/5/8 16:45
 * @Version: 1.0
 */
@FeignClient("pk-service")
public interface PKClient {
    @PutMapping("/pk/updatepkrecord")
    Result<?> updatePKRecord(@RequestBody JudgeRecord judgeRecord);
}
