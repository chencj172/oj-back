package com.chencj.problem.controller;


import com.chencj.api.model.po.JudgeRecord;
import com.chencj.common.model.ProblemCodeDto;
import com.chencj.common.utils.Result;
import com.chencj.common.utils.UserContext;
import com.chencj.problem.service.JudgeRecordService;
import com.chencj.problem.service.ProblemService;
import com.chencj.problem.service.UserAcproblemService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName: ProblemController
 * @Description:
 * @Author: chencj
 * @Datetime: 2025/4/9 19:14
 * @Version: 1.0
 */
@RestController
@RequestMapping("/problem")
@Slf4j
public class ProblemController {

    @Resource
    private ProblemService problemService;

    @Resource
    private JudgeRecordService judgeRecordService;

    @GetMapping("/search")
    public Result<?> searchProblem(
            HttpServletRequest request,
            @RequestParam(value = "level", required = false) Integer level,
            @RequestParam(value = "word", required = false) String word,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize) {
        log.info("token : {}", request.getHeader("token"));
        return problemService.search(request.getHeader("token"), level, word, pageNum, pageSize);
    }

    @GetMapping("/getById/{id}")
    public Result<?> getProblemById(@PathVariable("id") Integer id) {
        return problemService.getProblemById(id);
    }

    /**
     * 运行测试用例
     * @param problemCodeDto
     * @return
     */
    @PostMapping("/testCase")
    public Result<?> testCase(@RequestBody ProblemCodeDto problemCodeDto) {
        problemCodeDto.setUid(UserContext.getUser());
        return problemService.testCase(problemCodeDto);
    }

    /**
     * 判题
     * @param problemCodeDto
     * @return
     */
    @PostMapping("/judge")
    public Result<?> judgeProblem(@RequestBody ProblemCodeDto problemCodeDto) {
        problemCodeDto.setUid(UserContext.getUser());
        return problemService.judge(problemCodeDto);
    }

    @GetMapping("/getJudgeRecordList/{pid}")
    public Result<?> getJudgeRecordList(@PathVariable("pid") Integer pid) {
        return judgeRecordService.getJudgeRecordList(pid);
    }

    @GetMapping("/getJudgeRecordDetail/{id}")
    public Result<?> getJudgeRecordDetail(@PathVariable("id") Integer id) {
        return judgeRecordService.getJudgeRecordDetail(id);
    }

    @PostMapping("/saveJudgeRecord")
    public Result<?> saveJudgeRecord(@RequestBody JudgeRecord judgeRecord) {
        return judgeRecordService.saveRecord(judgeRecord);
    }

    @GetMapping("/getAllTag")
    public Result<?> getAllTag() {
        return problemService.getAllTag();
    }

}
