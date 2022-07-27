package com.example.client;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.example.dto.JsonResult;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * 接口响应结果解密
 * @author wujiawei
 * @see
 * @since 2022/6/27 10:06
 */
@Slf4j
public class DecryptTest {
    
    public static String key = "9f5d54580044d478";
    
    @Test
    public void helloTest() {
        String url = "http://localhost:8080/hello";
        HttpResponse response = HttpUtil.createGet(url).execute();
        String body = response.body();
        log.info(body);
        JSONObject jsonObject = JSONUtil.parseObj(body);
        String encryptText = jsonObject.getStr("data");
        String decryptJsonText = SecureUtil.aes(key.getBytes()).decryptStr(encryptText);
        log.info(decryptJsonText);
    }
    
}
