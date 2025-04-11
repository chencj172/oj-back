import com.chencj.problem.ProblemApplication;
import com.chencj.problem.model.po.Problem;
import com.chencj.problem.service.ProblemService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

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
        problem.setAnswerInput("0 0\n1 2\n100 100\n555 5");
        problem.setAnswerOutput("0\n3\n200\n560");
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

        // for (int i = 0; i < 200; i++) {
        //     problemService.save(problem);
        //     problem.setId(null);
        // }
        // problemService.updateById(problem);
        problemService.lambdaUpdate()
                .set(Problem::getContent, problem.getContent())
                .update();

    }

    @Test
    public void getProblem() {
        Problem problem = problemService.getById(1);
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

}
