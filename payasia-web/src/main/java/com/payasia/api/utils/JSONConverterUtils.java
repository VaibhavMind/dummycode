package com.payasia.api.utils;
import com.google.gson.Gson;

/**
 * @author manojkumar2
 * @Param : JSONConverterUtils.java
 * @param : This class is used to convert object to JSON String & JSON String to Object  
*/
public final class JSONConverterUtils {

	public static String objectToJsonString(Object object){
		Gson gsonObj = new Gson();
 	    return gsonObj.toJson(object);
	}
	
	public static Object jsonStringToObject(String jsonString ,Class<?> clz){
		Gson gsonObj = new Gson();
		Object  object  = gsonObj.fromJson(jsonString, clz);
		return object;
	}
	
}
