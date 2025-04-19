import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.chencj.judge.JudgeApplication;
import com.chencj.judge.model.LanguageConfig;
import com.chencj.judge.utils.LanguageConfigLoader;
import com.chencj.judge.utils.SandboxRun;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: JudgeTest
 * @Description:
 * @Author: chencj
 * @Datetime: 2025/4/12 10:28
 * @Version: 1.0
 */
@SpringBootTest(classes = JudgeApplication.class)
public class JudgeTest {

    @Test
    public void testLanguageConfig() {
        LanguageConfigLoader languageConfigLoader = new LanguageConfigLoader();
    }

    @Test
    public void testJsonArray() {
        List<String>list = List.of("/usr/bin/g++", "a.cc", "-o", "a");
        JSONObject args = new JSONObject();
        args.set("args", list);
        System.out.println(args);
    }

    @Test
    public void testParam() {
        LanguageConfigLoader languageConfigLoader = new LanguageConfigLoader();
        LanguageConfig languageConfig = languageConfigLoader.getLanguageConfigByName("C++");

        JSONArray compile = SandboxRun.compile(
                languageConfig.getSrcName(),
                languageConfig.getExeName(),
                LanguageConfigLoader.splitBySpace(languageConfig.getCompileCommand()),
                LanguageConfigLoader.splitBySpace(languageConfig.getCompileEnv()),
                languageConfig.getMaxCpuTime() * 1000,
                languageConfig.getMaxMemory(),
                256 * 1024 * 1024L,
                10L,
                languageConfig.getMaxRealTime() * 1000,
                "#include <iostream>\nusing namespace std;\nint main() {\nint a, b;\ncin >> a >> b;\ncout << a + b << endl;\n}");

        JSONObject retJson = (JSONObject) compile.get(0);
        // 拿到临时文件ID
        JSONObject fileIds = (JSONObject) retJson.get("fileIds");
        String fileId = fileIds.getStr("main");

        // 运行
        JSONArray run = SandboxRun.run(
                languageConfig.getExeName(),
                fileId,
                LanguageConfigLoader.splitBySpace(languageConfig.getRunCommand()),
                LanguageConfigLoader.splitBySpace(languageConfig.getRunEnv()),
                "1 1",
                languageConfig.getMaxCpuTime() * 1000,
                languageConfig.getMaxMemory(),
                256 * 1024 * 1024L,
                10L,
                languageConfig.getMaxRealTime() * 1000);

        // 删除临时文件
        SandboxRun.desFile(fileId);
    }

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Test
    public void testDesFile() {
        stringRedisTemplate.delete("q1");
        // SandboxRun.desFile("11212121");

    }

    @Test
    public void testRedisList() {
        stringRedisTemplate.opsForList().leftPushAll("test", List.of("1", "2", "3"));
        List<String> range = stringRedisTemplate.opsForList().range("test", 0, -1);
        System.out.println(range);
    }

}
