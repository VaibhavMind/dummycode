package com.payasia.api.dashboard;

import org.springframework.http.ResponseEntity;

import com.payasia.api.hris.model.SearchParam;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

@Api(tags="DASHBOARD", description="DASHBOARD related APIs")
public interface RecentActivityApi {

	@ApiOperation(value = "Counts all the recent activities", notes = "Calculates the total number of recent activities.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> getAllRecentActivityListCount(String type);

	@ApiOperation(value = "Finds the list of recent activities", notes = "Recent Activity data can be provided with sorting and pagination.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> getAllRecentActivityList(String recentActivityType, SearchParam searchParamObj);

}
