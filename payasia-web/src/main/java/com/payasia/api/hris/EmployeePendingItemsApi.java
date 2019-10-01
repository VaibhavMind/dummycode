package com.payasia.api.hris;

import org.springframework.http.ResponseEntity;

import com.payasia.api.hris.model.SearchParam;
import com.payasia.api.swagger.SwaggerTags;
import com.payasia.common.form.HrisPendingItemsForm;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

@Api(tags="HRIS", description="HRIS related APIs")
public interface EmployeePendingItemsApi extends SwaggerTags{

	@ApiOperation(value = "Search Pending Items", notes = "Pending Items data can be provided with sorting and pagination.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> getPendingItems(SearchParam searchParamObj);

	@ApiOperation(value = "Review Pending Items", notes = "Review Pending Items data.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> reviewHrisPendingItem(String jsonStr);

	@ApiOperation(value = "Search Employees", notes = "Employees can be provided with sorting and pagination.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> searchEmployee(SearchParam searchParamObj);

	@ApiOperation(value = "Perform action on Pending Items", notes = "Desired operation can be performed on the basis of Request type.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> getDesiredItemsAction(HrisPendingItemsForm hrisPendingItemsForm, String requestType);

}
