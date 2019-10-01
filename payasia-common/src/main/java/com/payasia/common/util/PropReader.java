package com.payasia.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

public class PropReader implements Cloneable {

	private static Properties API_MESSAGE = null;
	
	// RESTRICT INSTANTIATION
	private PropReader() { }
	
	public static String getMessage(String key) {
		if(API_MESSAGE == null){
			synchronized (PropReader.class) {
				if(API_MESSAGE == null){
					String filePath ="payasia-api-msg.properties";
					InputStream is =  PropReader.class.getClassLoader().getResourceAsStream(filePath);
					try {
						API_MESSAGE = new Properties();
						API_MESSAGE.load(is);
					} catch (IOException e) {
						System.out.println("EXCEPTION READING properties FILE");
					} finally {
						if(is != null){
							try {
								is.close();
							} catch (IOException e) {}
						}
					}
				}
			}
		}
		if(API_MESSAGE != null){
			String string = API_MESSAGE.getProperty(key);
			if(!StringUtils.isEmpty(string)){
				return string.trim();
			}
		}
		return null;		
	}
	
}

