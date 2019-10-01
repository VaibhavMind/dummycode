package com.payasia.test.util;

import java.io.IOException;
import java.nio.charset.Charset;

import org.springframework.http.MediaType;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestUtil {

	public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(),
			MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));
	
	public static final MediaType APPLICATION_XML_UTF8 = new MediaType(MediaType.APPLICATION_XML.getType(),
			MediaType.APPLICATION_XML.getSubtype(), Charset.forName("utf8"));
	
	// For Json Object return type --> No need as we have used produces = {MediaType.APPLICATION_JSON_UTF8_VALUE} intead of produces = {MediaType.APPLICATION_JSON} in EmployeeLeaveReportsApiImpl class.
	public static final MediaType APPLICATION_JSON_ISO_8859_1 = new MediaType(MediaType.APPLICATION_JSON.getType(),
			MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("ISO-8859-1"));

	public static byte[] convertObjectToJsonBytes(Object object) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		return mapper.writeValueAsBytes(object);
	}

	public static String createStringWithLength(int length) {
		StringBuilder builder = new StringBuilder();

		for (int index = 0; index < length; index++) {
			builder.append("a");
		}
		return builder.toString();
	}

	public static String asJsonString(final Object obj) {
	    try {
	        return new ObjectMapper().writeValueAsString(obj);
	    } catch (Exception e) {
	        throw new RuntimeException(e);
	    }
	}

	
}
