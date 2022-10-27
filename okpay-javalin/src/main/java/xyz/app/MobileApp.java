package xyz.app;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.StaticLog;
import com.murong.ecp.app.merchant.atc.MerchantUtil;
import com.murong.ecp.app.merchant.atc.RSASignUtil;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class MobileApp {
    
    public static class Sign implements Handler {
    
        @Override
        public void handle(@NotNull Context ctx) {
            // 中环百联OK付签名
            String json = ctx.queryParam("json");
            Map<String, String> dataMap = new HashMap<>();
            JSONObject jsonObject = JSONUtil.parseObj(json);
            jsonObject.forEach((k, v) -> {
                StaticLog.info(k + " ==> " + v);
                dataMap.put(k, v == null ? "" : v.toString());
            });
    
            String certPass = ctx.queryParam("certPass");
            String certPath = ctx.queryParam("certPath");
            SignResult sign = MobileApp.getSign(dataMap, certPass, certPath);
            StaticLog.info("sign: " + JSONUtil.toJsonStr(sign));
            ctx.json(sign);
        }
    }
    
    public static class Request implements Handler {
    
        @Override
        public void handle(@NotNull Context ctx) throws Exception {
            // 中环百联OK付请求
            String requestUrl = ctx.queryParam("url");
            String json = ctx.queryParam("json");
            String certPass = ctx.queryParam("certPass");
            String certPath = ctx.queryParam("certPath");
            Map<String, String> dataMap = new HashMap<>();
            JSONObject jsonObject = JSONUtil.parseObj(json);
            jsonObject.forEach((k, v) -> {
                dataMap.put(k, v == null ? "" : v.toString());
            });
            SignResult sign = MobileApp.getSign(dataMap, certPass, certPath);
            StaticLog.info("sign: " + JSONUtil.toJsonStr(sign));
            Map<String, String> result = MobileApp.request(requestUrl, dataMap, sign.getMerchantSign(), sign.getMerchantCert());
            ctx.json(result);
        }
    }
    
    public static SignResult getSign(Map<String, String> dataMap, String certPass, String certPath) {
        Map requestMap = new HashMap();
        requestMap.putAll(dataMap);
        Set set = dataMap.keySet();
        Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            if (MapUtil.getStr(dataMap, key).equals("")) {
                requestMap.remove(key);
            }
        }
        
        //数据签名，hmac为签名后的消息摘要
        String merchantCertPath = certPath;
        String merchantCertPass = certPass;
        RSASignUtil util = new RSASignUtil(merchantCertPath, merchantCertPass);
        String sf = util.coverMap2String(requestMap);
        String merchantSign = util.sign(sf, "GBK");
        String merchantCert = util.getCertInfo();
        SignResult result = new SignResult();
        result.setMerchantSign(merchantSign);
        result.setMerchantCert(merchantCert);
        return result;
    }
    
    public static Map<String, String> request(String requestUrl, Map<String, String> dataMap, String merchantSign, String merchantCert) {
        String reqData = MapUtil.join(dataMap, "&", "=");
        String buf = reqData + "&merchantSign=" + merchantSign + "&merchantCert=" + merchantCert;
        StaticLog.info("请求报文: " + buf);
        Map<String, String> map = null;
        try {
            String res = MerchantUtil.sendAndRecv(requestUrl, buf, "00");
            StaticLog.info("请求成功，返回报文：" + res);
            map = HttpUtil.decodeParamMap(res, CharsetUtil.CHARSET_UTF_8);
        } catch (Throwable e) {
            StaticLog.error("请求失败，返回报文：" + e.getMessage());
            StaticLog.error(e);
            map = new HashMap<>(2);
            map.put("status", "FAILED");
            map.put("returnMessage", e.getMessage());
            map.put("returnCode", "SCM60001");
        }
        return map;
    }
    
}
