import cn.hutool.core.bean.BeanUtil;
import com.chencj.problem.ProblemApplication;
import com.chencj.problem.model.po.DailyProblem;
import com.chencj.problem.model.po.Problem;
import com.chencj.problem.service.DailyProblemService;
import com.chencj.problem.service.ProblemService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

/**
 * @ClassName: ProblemTest
 * @Description:
 * @Author: chencj
 * @Datetime: 2025/4/9 18:54
 * @Version: 1.0
 */

@SpringBootTest(classes = ProblemApplication.class)
public class ProblemTest {

    @Resource
    private ProblemService problemService;

    @Test
    public void insertProblem() {
        Problem problem = new Problem();
        // problem.setId(1);
        problem.setTitle("A+B");
        problem.setAcceptNum(0);
        problem.setSubmitNum(0);
        problem.setLevel(1);
        problem.setCaseInput("3 4");
        problem.setCaseOutput("7");
        problem.setAnswerInput("0 0\n\n1 2\n\n100 100\n\n555 5");
        problem.setAnswerOutput("0\n\n3\n\n200\n\n560");
        problem.setTimeLimit(1000);
        problem.setMemoryLimit(64);
        problem.setStackLimit(16);
        problem.setContent("" +
                "## 输入两个整数，求这两个整数的和是多少\n" +
                "\n" +
                "### 输入格式\n" +
                "\n" +
                "输入两个整数 \\( A, B \\)，用空格隔开\n" +
                "\n" +
                "### 输出格式\n" +
                "\n" +
                "输出一个整数，表示这两个数的和\n" +
                "\n" +
                "## 数据范围\n" +
                "\n" +
                "$0 \\leq A, B \\leq 10^8$\n" +
                "\n" +
                "$0 \\geq A, B \\geq 10^8$\n" +
                "\n" +
                "$E = mc^2$\n" +
                "\n" +
                "## 输入样例\n" +
                "\n" +
                "```shell\n" +
                "3 4\n" +
                "```\n" +
                "\n" +
                "## 输出样例\n" +
                "\n" +
                "```shell\n" +
                "7\n" +
                "```");

        // for (int i = 0; i < 50; i++) {
        //     problemService.save(problem);
        //     problem.setId(null);
        // }
        // problemService.updateById(problem);
        problemService.lambdaUpdate()
                .set(Problem::getContent, problem.getContent())
                .set(Problem::getAnswerInput, problem.getAnswerInput())
                .set(Problem::getAnswerOutput, problem.getAnswerOutput())
                .update();

    }

    @Test
    public void getProblem() {
        Problem problem = problemService.getById(222);
        System.out.println(problem);
    }

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void testRedis() {
        stringRedisTemplate.opsForValue().set("k1", "v1");
        System.out.println(stringRedisTemplate.opsForValue().get("k1"));
        stringRedisTemplate.delete("k1");
    }

    @Test
    public void testBeanToMap() {
        Problem problem = problemService.getById(222);
        System.out.println(BeanUtil.beanToMap(problem));
    }

    @Test
    public void testList() {
        List<Integer> ints = List.of(1, 2, 3, 4, 5);
        System.out.println(ints);
        System.out.println(String.valueOf(ints));
    }

    @Resource
    private DailyProblemService dailyProblemService;
    @Test
    public void testQueryDate() {
        ZoneId zoneId = ZoneId.of("Asia/Shanghai");
        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(System.currentTimeMillis()), zoneId);

        // 获取当月第一天和最后一天
        LocalDateTime firstDay = dateTime.with(TemporalAdjusters.firstDayOfMonth())
                .with(LocalTime.MIN); // 当月第一天 00:00:00
        LocalDateTime lastDay = dateTime.with(TemporalAdjusters.lastDayOfMonth())
                .with(LocalTime.MAX); // 当月最后一天 23:59:59.999

        // 题目集合
        List<DailyProblem> list = dailyProblemService.lambdaQuery().between(DailyProblem::getCreateTime, firstDay, lastDay).list();
    }

    @Test
    public void testNull() {
        stringRedisTemplate.opsForHash().increment("test", "1", 1L);
    }

}
