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
    private final static JSONArray files_compile;
    private final static JSONArray files_run;

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

        files_compile = new JSONArray();
        files_compile.put(files_content);
        files_compile.put(files_stdout);
        files_compile.put(files_stderr);

        files_run = new JSONArray();
        files_run.put(files_content);
        files_run.put(files_stdout);
        files_run.put(files_stderr);
    }

    /**
     * @param args 编译源代码
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

        cmd.set("files", files_compile);

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

        // JSONArray jsonArray = JSONUtil.parseArray(ret);
        // // 拿到状态码
        // JSONObject retJson = (JSONObject) jsonArray.get(0);
        // System.out.println(retJson.getStr("status"));
        // // 拿到临时文件ID
        // JSONObject fileIds = (JSONObject) retJson.get("fileIds");
        // System.out.println(fileIds.getStr(exeName));
        return JSONUtil.parseArray(ret);
    }

    /**
     * 运行编译产生的可执行文件
     * @param exeName 可执行文件名称
     * @param fileId 可执行文件在内存的Id
     * @param args 运行命令
     * @param env  运行环境
     * @param content 输入
     * @param cpuLimit cpu限制 ns
     * @param memoryLimit 内存限制 byte
     * @param stackLimit 栈内存限制 byte
     * @param procLimit  线程数量限制
     * @param cpuRealLimit 等待时间限制，单位纳秒 （通常为 cpuLimit 两倍）
     * @return
     */
    public static JSONArray run(
            String exeName,
            String fileId,
            List<String> args,
            List<String> env,
            String content,
            Long cpuLimit,
            Long memoryLimit,
            Long stackLimit,
            Long procLimit,
            Long cpuRealLimit) {
        JSONObject cmd = new JSONObject();
        cmd.set("args", args);
        cmd.set("env", env);

        JSONObject files_content = new JSONObject();
        files_content.set("content", content);
        files_run.set(0, files_content);
        cmd.set("files", files_run);

        cmd.set("cpuLimit", cpuLimit * 1000);
        cmd.set("clockLimit", cpuRealLimit * 1000);
        cmd.set("memoryLimit", memoryLimit);
        cmd.set("stackLimit", stackLimit);
        cmd.set("procLimit", procLimit);

        cmd.set("copyIn", new JSONObject().set(exeName, new JSONObject().set("fileId", fileId)));

        JSONObject param = new JSONObject();
        param.set("cmd", new JSONArray().put(cmd));

        // log.info("run cmd : {}", param);

        String ret = webClient.post()
                .uri("/run")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(param)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        // JSONArray jsonArray = JSONUtil.parseArray(ret);
        // // 获取运行状态
        // JSONObject runObj = (JSONObject) jsonArray.get(0);
        // String runStatus = runObj.getStr("status");
        // log.info(runStatus);
        // // 获取输出结果
        // JSONObject filesObj = (JSONObject) runObj.get("files");
        // log.info(filesObj.getStr("stdout"));

        return JSONUtil.parseArray(ret);
    }

    /**
     * 删除编译产生的文件，防止内存泄漏
     * @param fileId
     */
    public static void desFile(String fileId) {
        try {
            webClient.delete()
                    .uri("/file/{id}", fileId)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
        } catch (WebClientResponseException ex) {
            log.error("error : {}", ex.getStatusCode());
        }

    }
}
