package com.payasia.api.claim;

import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@ApiResponses(value = { 
		@ApiResponse(code = 200, message = "OK"), 
		@ApiResponse(code = 401, message = "Unauthorized User"),
		@ApiResponse(code = 404, message = "URL not found"),
		@ApiResponse(code = 409, message = "No data"),
		@ApiResponse(code = 500, message = "Internal Server Error")})

public interface ClaimSwaggerTag {}
