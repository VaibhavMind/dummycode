package com.payasia.api.leave;

import org.springframework.http.ResponseEntity;

import com.payasia.api.hris.model.SearchParam;
import com.payasia.api.swagger.SwaggerTags;
import com.payasia.common.form.LeaveReportsForm;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@Api(tags="LEAVE REPORTS", description="LEAVE REPORTS related APIs")
public interface EmployeeLeaveReportsApi extends SwaggerTags {

	@ApiOperation(value = "Leave-Type List", notes = "Leave-Type List.")
	@ApiImplicitParams({@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")})
	ResponseEntity<?> getLeaveTypeList();
	
	@ApiOperation(value = "Types of Leave Report", notes = "List of Leave Report names.")
	@ApiImplicitParams({@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")})
	ResponseEntity<?> getLeaveReportList();
	
	@ApiOperation(value = "Leave Preference Detail", notes = "Leave Preference Detail.")
	@ApiImplicitParams({@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")})
	ResponseEntity<?> getLeavePreferenceDetail();
	
	@ApiOperation(value = "Types of Leave Transaction", notes = "List of Leave Transaction names.")
	@ApiImplicitParams({@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")})
	ResponseEntity<?> getLeaveTransactionList();
	
	@ApiOperation(value = "Employee Filter List", notes = "Employee Filter List.")
	@ApiImplicitParams({@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")})
	ResponseEntity<?> getEmployeeFilterList();

	@ApiOperation(value = "Employee Search", notes = "Search employee for manager.")
	@ApiImplicitParams({@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")})
	ResponseEntity<?> searchEmployeeForManager(SearchParam searchParamObj, String metaData);
	
	@ApiOperation(value = "Show Leave Report Data", notes = "Leave Report Data in grid.")
	@ApiImplicitParams({@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")})
	ResponseEntity<?> showReportData(String requestReportType, LeaveReportsForm leaveReportsForm);
	
	@ApiOperation(value = "Year List", notes = "Year List for YSR(Yearwise Summary Report).")
	@ApiImplicitParams({@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")})
	ResponseEntity<?> getYearList();

	@ApiOperation(value = "Generate Leave Report", notes = "Generate Leave Report Data in PDF or Excel.")
	@ApiImplicitParams({@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")})
	ResponseEntity<?> generateReportDataPdfOrExcel(String requestReportType, LeaveReportsForm leaveReportsForm);

}
