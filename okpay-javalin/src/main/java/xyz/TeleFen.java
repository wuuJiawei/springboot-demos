package xyz;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.StaticLog;
import io.javalin.http.util.JsonEscapeUtil;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * 电信翼积分
 * @author wujiawei
 * @see
 * @since 2022/6/23 11:34
 */
public class TeleFen {
    
    /**
     * 签名
     * @param bodyStr 为POST请求的内容，如果为GET,使用””代替
     * @param token appId对应的token信息，在申请的时候分配给业务使用的Id
     * @return
     */
    public static String sign(String bodyStr, String token) {
        String txId = String.valueOf(System.currentTimeMillis());
        String reqData = txId + "_" + bodyStr + "_" + token;
        // MD5(txId+"_"+body +"_"+token)
        String md5 = SecureUtil.md5(reqData);
        return md5;
    }
    
    public static String request(String url, String token, JSONObject header, JSONObject body) {
        String txId = String.valueOf(System.currentTimeMillis());
        String bodyStr = body.toString();
        
        // 签名: MD5(txId+"_"+body +"_"+token)
        String reqData = txId + "_" + bodyStr + "_" + token;
        String sign = SecureUtil.md5(reqData);
        header.set("txId", txId);
        header.set("sign", sign);
        
        // 发起请求
        HttpRequest request = HttpRequest.post(url);
        for (String key : header.keySet()) {
            request.header(key, header.getStr(key));
        }
        request.header("Content-Type", "application/json;charset=UTF-8");
        request.body(bodyStr);
    
        // 解析响应
        HttpResponse response = request.execute();
        response.close();
        if (!response.isOk()) {
            return fail();
        }
        String result = response.body();
        StaticLog.info(result);
        if (StrUtil.isEmpty(result)) {
            return fail();
        }
        return result;
    }
    
    private static String fail() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.set("code", "FAIL");
        jsonObject.set("errormsg", "Network error");
        return jsonObject.toString();
    }
    
}
