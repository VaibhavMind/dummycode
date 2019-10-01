package com.payasia.api.common.hris;

import org.springframework.http.ResponseEntity;

import com.payasia.api.hris.model.SearchParam;
import com.payasia.api.swagger.SwaggerTags;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

@Api(tags="COMMON-APIs")
public interface CommonApi extends SwaggerTags{
	
	@ApiOperation(value = "Display announcements", notes = "Announcement data can be provided with sorting and pagination.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> getAnouncementList(SearchParam searchParamObj);

}
