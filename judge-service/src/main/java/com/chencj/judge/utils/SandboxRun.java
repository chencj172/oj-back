package com.chencj.judge.utils;


import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import io.netty.channel.ChannelOption;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.List;

/**
 * @ClassName: SandboxRun
 * @Description: 安全沙箱，负责编译和运行源代码文件
 * @Author: chencj
 * @Datetime: 2025/4/12 10:54
 * @Version: 1.0
 */
@Slf4j
public class SandboxRun {
    private final static HttpClient httpClient = HttpClient.create()
            .responseTimeout(Duration.ofSeconds(5)) // 响应超时
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000); // 连接超时

    private final static WebClient webClient;
    private final static JSONArray files;

    static {
        webClient = WebClient.builder()
                .baseUrl("http://localhost:5050")
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    static {
        JSONObject files_content = new JSONObject();
        files_content.set("content", "");

        JSONObject files_stdout = new JSONObject();
        files_stdout.set("name", "stdout");
        files_stdout.set("max", 10240);

        JSONObject files_stderr = new JSONObject();
        files_stderr.set("name", "stderr");
        files_stderr.set("max", 10240);

        files = new JSONArray();
        files.put(files_content);
        files.put(files_stdout);
        files.put(files_stderr);
    }

    /**
     * @param args 编译的命令
     * @param env 编译的环境
     * @param cpuLimit cpu时间限制 ns
     * @param memoryLimit 内存限制 byte
     * @param stackLimit 栈内存限制 byte
     * @param procLimit 线程数量限制
     * @param cpuRealLimit 等待时间限制，单位纳秒 （通常为 cpuLimit 两倍）
     * @param code 源代码
     * @return
     */
    public static JSONArray compile(
            String srcName,
            String exeName,
            List<String> args,
            List<String> env,
            Long cpuLimit,
            Long memoryLimit,
            Long stackLimit,
            Long procLimit,
            Long cpuRealLimit,
            String code) {
        JSONObject cmd = new JSONObject();
        cmd.set("args", args);
        cmd.set("env", env);


        cmd.set("files", files);

        cmd.set("cpuLimit", cpuLimit * 1000);
        cmd.set("clockLimit", cpuRealLimit * 1000);
        cmd.set("memoryLimit", memoryLimit);
        cmd.set("stackLimit", stackLimit);
        cmd.set("procLimit", procLimit);

        JSONObject copyIn_content = new JSONObject();
        copyIn_content.set("content", code);
        JSONObject copyIn_fileName = new JSONObject();
        copyIn_fileName.set(srcName, copyIn_content);
        cmd.set("copyIn", copyIn_fileName);

        cmd.set("copyOut", List.of("stdout", "stderr"));
        cmd.set("copyOutCached", List.of(exeName));

        JSONObject param = new JSONObject();
        param.set("cmd", new JSONArray().put(cmd));

        String ret = webClient.post()
                .uri("/run")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(param)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        JSONArray jsonArray = JSONUtil.parseArray(ret);
        // 拿到状态码
        JSONObject retJson = (JSONObject) jsonArray.get(0);
        System.out.println(retJson.getStr("status"));
        // 拿到临时文件ID
        JSONObject fileIds = (JSONObject) retJson.get("fileIds");
        System.out.println(fileIds.getStr(exeName));
        return jsonArray;
    }

    /**
     * 删除编译产生的文件
     * @param filaId
     */
    public static void desFile(String filaId) {
        try {
            webClient.delete()
                    .uri("/file/{id}", filaId)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
        } catch (WebClientResponseException ex) {
            log.error("error : {}", ex.getStatusCode());
        }

    }
}
