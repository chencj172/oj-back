import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import com.chencj.user.UserApplication;
import com.chencj.user.utils.JwtTool;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

/**
 * @ClassName: JwtTest
 * @Description:
 * @Author: chencj
 * @Datetime: 2025/3/30 14:06
 * @Version: 1.0
 */
@SpringBootTest(classes = UserApplication.class)
public class JwtTest {

    @Value("${config.jwt.secret}")
    private String key;

    @Resource
    private JwtTool jwtTool;

    @Test
    public void testJwt() throws InterruptedException {
        String token = JWT.create()
                .setPayload("userId", 2)
                .setExpiresAt(new Date(System.currentTimeMillis() + 1))
                .setKey(key.getBytes())
                .sign();
        System.out.println(token);

        Thread.sleep(1000);
        System.out.println(jwtTool.parseToken(token));
    }

}
