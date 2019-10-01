package com.payasia.api.hris;

import org.springframework.http.ResponseEntity;

import com.payasia.api.swagger.SwaggerTags;
import com.payasia.common.form.EmployeeContactUSForm;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

@Api(tags="HRIS", description="HRIS related APIs")
public interface EmployeeContactUsApi extends SwaggerTags{
	
	@ApiOperation(value = "Send mail", notes = "This API is used to send the contents of a form.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> sendMail(EmployeeContactUSForm employeeContactUSForm);

}
