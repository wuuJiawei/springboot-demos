package com.example.client;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

/**
 * 接口请求签名加密
 * @author wujiawei
 * @see
 * @since 2022/6/27 10:14
 */
@Slf4j
public class RequestTest {
    
    @Test
    public void getTest() {
        long timestamp = System.currentTimeMillis();
        String nonceStr = RandomUtil.randomString(8);
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("a", "随便");
        paramMap.put("b", 13213);
        paramMap.put("timestamp", timestamp);
        paramMap.put("nonceStr", nonceStr);
        paramMap.put("sign", sign(paramMap));
        String getResp =  HttpUtil.createGet("http://localhost:8080/get").form(paramMap).execute().body();
        log.info(getResp);
        printResp(getResp);
    }
    
    @Test
    public void postTest() {
        long timestamp = System.currentTimeMillis();
        String nonceStr = RandomUtil.randomString(8);
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("a", "随便");
        paramMap.put("b", 13213);
        paramMap.put("timestamp", timestamp);
        paramMap.put("nonceStr", nonceStr);
        paramMap.put("sign", sign(paramMap));
        String postResp = HttpUtil.createPost("http://localhost:8080/post").form(paramMap).execute().body();
        log.info(postResp);
        printResp(postResp);
    }
    
    private void printResp(String body) {
        JSONObject jsonObject = JSONUtil.parseObj(body);
        String encryptText = jsonObject.getStr("data");
        String decryptJsonText = SecureUtil.aes(DecryptTest.key.getBytes()).decryptStr(encryptText);
        log.info(decryptJsonText);
    }
    
    private String sign(Map<String, Object> params) {
        String sign = SecureUtil.md5(createLinkString(params));
        log.info(sign);
        return sign;
    }
    
    /**
     * 创建${key}=${value}的字符串
     * @param params 需要排序并参与字符拼接的参数组
     * @return 拼接后字符串
     */
    public static String createLinkString(Map<String, Object> params) {
        StringBuilder content = new StringBuilder();
        List<String> keys = new ArrayList<>(params.keySet());
        Collections.sort(keys);
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            Object obj = params.get(key);
            String value = "";
            if (obj != null) {
                value = obj.toString();
            }
            // 拼接时，不包括最后一个&字符
            if (i == keys.size() - 1) {
                content.append(key + "=" + value);
            } else {
                content.append(key + "=" + value + "&");
            }
        }
        return content.toString();
    }
    
}
