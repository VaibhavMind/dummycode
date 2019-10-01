package com.payasia.common.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

public class ResponseDataConverter {

	private static final Logger LOGGER = Logger.getLogger(ResponseDataConverter.class);

	public static String getJsonURLEncodedData(Object response) {
		return getURLEncoded(getObjectToJson(response));
	}

	public static String getJsonURLEncodedDataForList(Object response) {
		return getURLEncoded(getListToJson(response));
	}

	public static String getObjectToJson(Object response) {
		return JSONObject.fromObject(response, new JsonConfig()).toString();
	}

	public static String getObjectToString(Object response) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_NULL);
		try {
			return mapper.writeValueAsString(response);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String getListToJson(Object response) {
		return JSONArray.fromObject(response, new JsonConfig()).toString();
	}

	public static String getURLEncoded(String data) {

		try {
			return URLEncoder.encode(data, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	/*
	 * public static String getURLDecoded(String data) {
	 * 
	 * try { return URLEncoder.encode(data, "UTF-8");
	 * 
	 * } catch (UnsupportedEncodingException e) { LOGGER.error(e.getMessage(),
	 * e); }
	 * 
	 * return null; }
	 */

}
