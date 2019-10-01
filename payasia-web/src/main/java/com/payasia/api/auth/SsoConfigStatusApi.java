package com.payasia.api.auth;

import org.springframework.http.ResponseEntity;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Api(tags="SSO", description="SSO related APIs")
public interface SsoConfigStatusApi {

	@ApiOperation(value = "Finds SSO status", notes = "SSO status can be provided company-wise.")
	ResponseEntity<?> getSsoStatus(@ApiParam(value = "Company code is taken as request parameter.") String companyCode);

}
