package xyz.easyboot;

import com.google.gson.Gson;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * 	1.	支持GET请求和POST请求。
 * 	2.	将参数中含有某些敏感信息部分采用json encode处理（中文/符号不转义）。
 * 	3.	确保按照固定排列方式进行排序。
 * 	4.	对上一步的整理的结果进行字符拼接。
 * 	5.	token = md5(base64_encode(固定密钥 + erp秘钥) + 字符串)。
 * 	6.	注意：当调用商家入驻的发送验证码和获取验证码信息时候，商家手机号商家名称，商户生产商家编号，商户接口erp秘钥即可（+是连接符，链接字符串）。
 * 	7.	将密钥进行base64加密。
 * 	8.	将密钥进行base64加密。
 * 	9.	拼接上一步得到的字符串。
 * 	10.	对整体再进行md5加密得到最终的token。
 * 	11.	timestamp15分钟过期，例如刷新的时间间隔。
 */
public class TokenGenerator {

    private static final String FIXED_KEY = "固定密钥";
    private static final String ERP_KEY = "你的ERP秘钥";
    private static final long EXPIRATION_TIME = 15 * 60 * 1000; // 15 minutes

    public static String generateToken(Map<String, String> params) throws NoSuchAlgorithmException {
        // 2. JSON encode处理
        Gson gson = new Gson();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            entry.setValue(gson.toJson(entry.getValue()));
        }

        // 3. 排序
        List<String> sortedKeys = new ArrayList<>(params.keySet());
        Collections.sort(sortedKeys);

        // 4. 拼接字符
        StringBuilder concatenated = new StringBuilder();
        for (String key : sortedKeys) {
            concatenated.append(params.get(key));
        }

        // 5. 生成token
        String base64Encoded = Base64.getEncoder().encodeToString((FIXED_KEY + ERP_KEY).getBytes(StandardCharsets.UTF_8));
        String toHash = base64Encoded + concatenated.toString();
        String token = md5(toHash);

        return token;
    }

    private static String md5(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(hash);
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public static void main(String[] args) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("param1", "value1");
            params.put("param2", "value2");

            String token = generateToken(params);
            System.out.println("生成的Token是: " + token);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("加密算法错误: " + e.getMessage());
        }
    }

}
