package com.payasia.api.hris;

import org.springframework.http.ResponseEntity;

import com.payasia.api.hris.model.SearchParam;
import com.payasia.api.swagger.SwaggerTags;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

@Api(tags="HRIS", description="HRIS related APIs")
public interface EmployeeMyRequestApi extends SwaggerTags{
	
	@ApiOperation(value = "View Change Request", notes = "View HRIS My Request data.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> viewChangeRequest(String jsonStr);

	@ApiOperation(value = "Withdraw Change Request", notes = "Withdraw HRIS My Request data.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> withdrawChangeRequest(String jsonStr);

	@ApiOperation(value = "Search HRIS My Request", notes = "HRIS My Request data can be provided with sorting and pagination on the basis of Request type.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> getDesiredRequest(SearchParam searchParamObj, String requestType);

}

