package com.payasia.api.hris;

import org.springframework.http.ResponseEntity;

import com.payasia.api.swagger.SwaggerTags;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

@Api(tags="COMMON", description="COMMON related APIs")
public interface LanguageApi extends SwaggerTags{

	@ApiOperation(value = "Change Language", notes = "Language can be changed using this API.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> setLocaleData(String jsonStr);

}
