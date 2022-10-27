package xyz.card.util;

import cn.hutool.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class SignUtils {
	/**
	 * 生成签名前的字符串
	 * @param paramMap
	 * @return
	 */
	public static String getParamStr(Map<String,Object> paramMap){
		ArrayList<String> list = new ArrayList<String>();
		for (String key : paramMap.keySet()) {
			if(paramMap.get(key)!=null&&paramMap.get(key).toString().length()>0){
				if(key.equalsIgnoreCase("sign")) {
					continue;
				}
				list.add(key);
			}
		}
		Collections.sort(list);
		String returnStr = "";
		for(int i = 0; i < list.size(); i++){
			String key = list.get(i);
			String value = paramMap.get(key).toString();
			returnStr += (key + "="+value+"&");
		}
		returnStr = returnStr.substring(0, returnStr.length()-1);
		return returnStr.trim();
	}
	
	public static String getParamStr(JSONObject jsonParam){
		ArrayList<String> list = new ArrayList<String>();
		for (String key : jsonParam.keySet()) {
			if(jsonParam.get(key)!=null&&jsonParam.get(key).toString().length()>0){
				if(key.equalsIgnoreCase("sign")) {
					continue;
				}
				list.add(key);
			}
		}
		Collections.sort(list);
		String returnStr = "";
		for(int i = 0; i < list.size(); i++){
			String key = list.get(i);
			String value = jsonParam.get(key).toString();
			returnStr += (key + "="+value+"&");
		}
		returnStr = returnStr.substring(0, returnStr.length()-1);
		return returnStr.trim();
	}
	
	
	
}
