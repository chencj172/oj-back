package com.chencj.problem.controller;


import com.chencj.common.model.ProblemCodeDto;
import com.chencj.common.utils.Result;
import com.chencj.common.utils.UserContext;
import com.chencj.problem.service.ProblemService;
import jakarta.annotation.Resource;
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
public class ProblemController {

    @Resource
    private ProblemService problemService;

    @GetMapping("/search")
    public Result<?> searchProblem(
            @RequestParam(value = "level", required = false) Integer level,
            @RequestParam(value = "word", required = false) String word,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize) {
        return problemService.search(level, word, pageNum, pageSize);
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
}
