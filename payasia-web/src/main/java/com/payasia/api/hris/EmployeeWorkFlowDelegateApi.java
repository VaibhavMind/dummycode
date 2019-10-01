package com.payasia.api.hris;

import org.springframework.http.ResponseEntity;

import com.payasia.api.hris.model.SearchParam;
import com.payasia.api.swagger.SwaggerTags;
import com.payasia.common.form.WorkFlowDelegateForm;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

@Api(tags="HRIS", description="HRIS related APIs")
public interface EmployeeWorkFlowDelegateApi extends SwaggerTags{

	@ApiOperation(value = "Save WorkFlow Delegate", notes = "Save WorkFlow Delegate data.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> saveEmployeeWorkFlowDelegate(WorkFlowDelegateForm workFlowDelegateForm);

	@ApiOperation(value = "Update WorkFlow Delegate", notes = "Update WorkFlow Delegate data.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> updateEmployeeWorkFlowDelegate(WorkFlowDelegateForm workFlowDelegateForm, long workflowDelegateId);

	@ApiOperation(value = "Delete WorkFlow Delegate", notes = "Delete WorkFlow Delegate data.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> deleteWorkFlowDelegate(String jsonStr);

	@ApiOperation(value = "Search WorkFlow Delegate", notes = "WorkFlow Delegate data can be searched with sorting and pagination .")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> searchEmployeeWorkFlowDelegateData(SearchParam searchParamObj, String workFlowType);
	
	@ApiOperation(value = "View WorkFlow Delegate", notes = "View WorkFlow Delegate data.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> viewWorkFlowDelegate(String jsonStr);

	@ApiOperation(value = "Search Employee", notes = "Employee can be searched with sorting and pagination.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> searchEmployee(SearchParam searchParamObj);

	@ApiOperation(value = "View  WorkFlow Filter List", notes = "View WorkFlow Delegate List data.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> getWorkFilterList();
	
}
