package xyz.card;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HtmlUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.StaticLog;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import xyz.app.SignResult;
import xyz.card.util.RSAUtils;
import xyz.card.util.SignUtils;

/**
 * 卡包
 */
public class CardPackage {
    
    public static class Sign implements Handler {
        @Override
        public void handle(@NotNull Context ctx) throws Exception {
            String json = ctx.queryParam("json");
            String key = ctx.queryParam("key");
            
            key = StrUtil.replace(key, "\\", "");
            JSONObject jsonObject = JSONUtil.parseObj(json);
            String sign = sign(jsonObject, key);
            SignResult result = new SignResult();
            result.setMerchantSign(sign);
            StaticLog.info("sign: " + JSONUtil.toJsonStr(sign));
            ctx.json(result);
        }
    }
    
    public static class SignAndRequest implements Handler {
    
        @Override
        public void handle(@NotNull Context ctx) throws Exception {
            String url = ctx.queryParam("url");
            String json = ctx.queryParam("json");
            String key = ctx.queryParam("key");
    
            key = StrUtil.replace(key, "\\", "");
            JSONObject jsonObject = JSONUtil.parseObj(json);
            String sign = sign(jsonObject, key);
            jsonObject.set("sign", sign);
    
            HttpRequest request = HttpUtil.createPost(url).body(jsonObject.toString());
            StaticLog.info(request.toString());
            HttpResponse response = request.execute();
            response.close();
            StaticLog.info(response.toString());
            ctx.json(response.body());
        }
    }
    
    /*
     * 签名方法
     */
    private static String sign(JSONObject json, String key){
        RSAUtils rsa = new RSAUtils();
        String str = SignUtils.getParamStr(json);
        byte[] result = null;
        try {
            result =  rsa.signRSA(str.getBytes("utf-8"),true,"utf-8",key);
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
        return new String(result);
    }
    
    /*
     * 验签方法
     */
    private boolean verifySign(String pubKey, String reqStr){
        JSONObject reqJson = JSONUtil.parseObj(reqStr);
        RSAUtils rsa = new RSAUtils();
        String str = SignUtils.getParamStr(reqJson);
        String sign = reqJson.getStr("sign");
        try {
            return rsa.verifyRSA(str, sign, pubKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
}
