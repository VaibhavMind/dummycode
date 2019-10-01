package com.payasia.common.dto;

import java.io.Serializable;
import java.util.HashMap;

public class MessagePropertyDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private HashMap<String, String> properties = new HashMap<>();

	public HashMap<String, String> getProperties() {
		return properties;
	}

	public void setProperties(HashMap<String, String> properties) {
		this.properties = properties;
	}
	
	

}
