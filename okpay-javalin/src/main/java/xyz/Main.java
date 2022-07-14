package xyz;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.cron.CronUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.StaticLog;
import com.murong.ecp.app.merchant.atc.MerchantUtil;
import com.murong.ecp.app.merchant.atc.RSASignUtil;
import io.javalin.Javalin;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author wujiawei
 * @see
 * @since 2022/3/30 23:02
 */
public class Main {
    
    public static void main(String[] args) {
        Javalin app = Javalin.create().start(7070);
        app.get("/", ctx -> {
            ctx.result("v1.0.0");
        }).get("/sign", ctx -> {
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
            SignResult sign = getSign(dataMap, certPass, certPath);
            StaticLog.info("sign: " + JSONUtil.toJsonStr(sign));
            ctx.json(sign);
        }).get("/request", ctx -> {
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
            SignResult sign = getSign(dataMap, certPass, certPath);
            StaticLog.info("sign: " + JSONUtil.toJsonStr(sign));
            Map<String, String> result = request(requestUrl, dataMap, sign.getMerchantSign(), sign.getMerchantCert());
            ctx.json(result);
        }).get("/telefen-sign", ctx -> {
            // 电信翼积分签名
            String body = ctx.queryParam("body");
            String token = ctx.queryParam("token");
            String sign = TeleFen.sign(body, token);
            ctx.json(sign);
        }).get("/telefen-request", ctx -> {
            // 电信翼积分发起请求
            String body = ctx.queryParam("body");
            String header = ctx.queryParam("header");
            String url = ctx.queryParam("url");
            String token = ctx.queryParam("token");
            String result = TeleFen.request(url, token, JSONUtil.parseObj(header), JSONUtil.parseObj(body));
            ctx.json(result);
        });
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
