package xyz.easyboot.test;

import cn.hutool.Hutool;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import xyz.easyboot.config.json.DictTypeHandler;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


@Slf4j
public class XxlTest2 {

    public static void main(String[] args) {
        String url = "http://localhost:9997/run";
        Map<String, Object> body = new HashMap<>();
        body.put("jobId", 111111);
        body.put("executorHandler", "offlinePayJob");
        body.put("glueType", "BEAN");

        String requestBody = JSONUtil.toJsonStr(body);
        String posted = HttpUtil.post(url, requestBody);

        log.info("xxl-job remoting (url="+url+") request body:"+requestBody);
        log.info("xxl-job remoting (url="+url+") response body:"+posted);
    }

}
