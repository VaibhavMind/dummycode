package com.payasia.api.hris;

import org.springframework.http.ResponseEntity;

import com.payasia.api.swagger.SwaggerTags;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

@Api(tags="HRIS", description="HRIS related APIs")
public interface EmployeeChangePasswordApi extends SwaggerTags {

	@ApiOperation(value = "Change Password", notes = "This API is used to change password.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> changePassword(String jsonStr);

	@ApiOperation(value = "Change Password after Login", notes = "This API is used to change password after Login.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> changePasswordAfterLogin(String jsonStr);

}
