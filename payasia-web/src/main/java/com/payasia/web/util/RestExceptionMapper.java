package com.payasia.web.util;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class RestExceptionMapper implements ExceptionMapper<Exception> {

	@Override
	public Response toResponse(Exception arg0) {
		return Response.ok(arg0.getMessage()).build();
	}

}