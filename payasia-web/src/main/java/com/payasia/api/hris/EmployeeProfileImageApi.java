package com.payasia.api.hris;

import java.io.IOException;

import org.springframework.http.ResponseEntity;

import com.payasia.api.swagger.SwaggerTags;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

@Api(tags="HRIS", description="HRIS related APIs")
public interface EmployeeProfileImageApi extends SwaggerTags{

	@ApiOperation(value = "View Profile Image", notes = "Profile Image can be viewed.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> employeeViewProfileImage() throws IOException;

}
