package com.payasia.common.util;

public class PayAsiaStringUtils {
	
	public static String getCamelCase(String colName){
		String[] parts =colName.split("_");
		StringBuilder camelCaseColName = new StringBuilder("");

		for (String part : parts) {

			camelCaseColName.append(part.substring(0, 1).toUpperCase()).append(part.substring(1).toLowerCase());
		}
		
		return camelCaseColName.toString();
	}
	
	public static String getColNumber(String dynamicFieldName){
		
		String colNumber = dynamicFieldName.substring(dynamicFieldName.lastIndexOf("_") + 1, dynamicFieldName.length());
		return colNumber;
	}
	
    public static String convertToRegEx(String str) {
        str = str.replaceAll("[{]", "[{]");
        str = str.replaceAll("[}]", "[}]");
        return str;
 }

}
