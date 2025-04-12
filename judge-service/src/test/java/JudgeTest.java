import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.alibaba.nacos.shaded.com.google.common.base.Splitter;
import com.chencj.judge.JudgeApplication;
import com.chencj.judge.model.LanguageConfig;
import com.chencj.judge.utils.LanguageConfigLoader;
import com.chencj.judge.utils.SandboxRun;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

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

        SandboxRun.compile(
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
    }

    @Test
    public void testDesFile() {
        SandboxRun.desFile("11212121");
    }

}
