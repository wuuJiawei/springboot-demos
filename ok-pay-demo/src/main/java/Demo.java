import com.murong.ecp.app.merchant.atc.MerchantConfig;
import com.murong.ecp.app.merchant.atc.RSASignUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author wujiawei
 * @see
 * @since 2022/3/30 22:28
 */
public class Demo {
    
    public static void test() {
        System.out.println("service ==> : DirectPayment");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        //商户会计日期（自定义）
        String service = "DirectPayment";
        String encoding = "";
        MerchantConfig.getConfig().loadPropertiesFromSrc();
        String requestUrl = MerchantConfig.getConfig().getRequestUrl();
        String charset = MerchantConfig.getConfig().getCharset();
        String version = MerchantConfig.getConfig().getVersion();
        version = "1.4";
        String signType = MerchantConfig.getConfig().getSignType();
        String merchantId = MerchantConfig.getConfig().getMerchantId();
        String merCert = merchantId + ".p12";
        String merchantCertPath = MerchantConfig.getConfig().getMerchantCertPath() + merCert;
        String merchantCertPass = MerchantConfig.getConfig().getMerchantCertPass();
        System.out.println("merchantCertPass == " + merchantCertPass);
        
        String requestId = String.valueOf(System.currentTimeMillis());
        String merchantName = MerchantConfig.getConfig().getMerchantName();
        String pageReturnUrl = requestUrl + MerchantConfig.getConfig().getOfflineNotifyUrl();
        String offlineNotifyUrl = requestUrl + MerchantConfig.getConfig().getOfflineNotifyUrl();
        String clientIP = "180.115.194.185";
        
        //编码设置
        if ("00".equals(charset)) {
            encoding = "GBK";
        } else if ("01".equals(charset)) {
            encoding = "GB2312";
        } else if ("02".equals(charset)) {
            encoding = "UTF-8";
        } else {
            encoding = "GBK";
        }
        
        //获取表单提交的各参数
        
        String purchaserId = "f722844542ce6d62229da275f3019cb6";
        String appId = "00002";
        String orderId = "wx240504252416393216";
        String orderTime = sdf.format(new Date());
        String totalAmount = "1";
        String validUnit = "00";
        String validNum = "1";
        String showUrl = "";
        String productName = "aaa";
        String productId = "";
        String productDesc = "aaa";
        String backParam = "";
        
        Map<String, Object> dataMap = new LinkedHashMap<String, Object>();
        dataMap.put("charset", charset);
        dataMap.put("version", version);
        dataMap.put("signType", signType);
        dataMap.put("service", service);
        dataMap.put("pageReturnUrl", pageReturnUrl);
        dataMap.put("offlineNotifyUrl", offlineNotifyUrl);
        dataMap.put("clientIP", clientIP);
        dataMap.put("requestId", requestId);
        dataMap.put("purchaserId", purchaserId);
        dataMap.put("appId", appId);
        dataMap.put("merchantId", merchantId);
        dataMap.put("merchantName", merchantName);
        dataMap.put("orderId", orderId);
        dataMap.put("orderTime", orderTime);
        dataMap.put("totalAmount", totalAmount);
        dataMap.put("currency", "CNY");
        dataMap.put("validUnit", validUnit);
        dataMap.put("validNum", validNum);
        dataMap.put("showUrl", showUrl);
        dataMap.put("productName", productName);
        dataMap.put("productId", productId);
        dataMap.put("productDesc", productDesc);
        dataMap.put("backParam", backParam);
        
        Map requestMap = new HashMap();
        requestMap.putAll(dataMap);
        Set set = dataMap.keySet();
        Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            System.out.println(key + " ==> " + dataMap.get(key));
            // 去掉空值
            if ((dataMap.get(key) == null) || (dataMap.get(key) == "")) {
                requestMap.remove(key);
            }
        }
        //数据签名，hmac为签名后的消息摘要
        RSASignUtil util = new RSASignUtil(merchantCertPath, merchantCertPass);
        String sf = util.coverMap2String(requestMap);
        //out.println("签名原文: " + sf);
        String merchantSign = util.sign(sf, encoding);
        String merchantCert = util.getCertInfo();
        System.out.println("service ==> : DirectPayment end");
    
        System.out.println(merchantSign);
        System.out.println(merchantCert);
    }
    
    public static void main(String[] args) {
        Demo demo = new Demo();
        demo.test();
    }
    
    
}
