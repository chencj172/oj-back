package com.chencj.pk.controller;


import com.chencj.api.model.po.JudgeRecord;
import com.chencj.common.utils.Result;
import com.chencj.common.utils.UserContext;
import com.chencj.pk.service.PKService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName: PKController
 * @Description:
 * @Author: chencj
 * @Datetime: 2025/5/8 15:52
 * @Version: 1.0
 */
@RestController
@RequestMapping("/pk")
public class PKController {

    @Resource
    private PKService pkService;

    @PutMapping("/updatepkrecord")
    public Result<?> updatePKRecord(@RequestBody JudgeRecord judgeRecord) {
        return pkService.updatePKRecord(judgeRecord);
    }

    @GetMapping("/getChallengeResult")
    public Result<?> getChallengeResult() {
        return pkService.getChallengeResult(UserContext.getUser());
    }

}
