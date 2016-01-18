package org.frame.database.db.util;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.Gson;

public class JsonUtil {
	/**
	 * 对象转换json格式的字符串
	 * @param obj
	 * @return
	 */
	public static String toJson(Object obj){
		Gson gson = new Gson();
		String json = gson.toJson(obj);
		return json;
	}
	/**
	 * 将json格式的字符串转换成对象
	 * @param json
	 * @param clz
	 * @return
	 */
	public static Object fromJson(String json,Class<?> clz){
		Gson gson = new Gson();
		Object obj = gson.fromJson(json, clz);
		return obj;
	}
	
	/**
	 * 将json字符串转化成list集合
	 * @param json
	 * @param type
	 * @return
	 * 
	 * Type type = new TypeToken<List<QAnswer>>(){}.getType();
	 */
	public static <T> List<T> parsonJson2Obj(String json,Type type){
		List<T> list = null;
		Gson gson = new Gson();
		list = gson.fromJson(json, type);
		return list;
	}
}
