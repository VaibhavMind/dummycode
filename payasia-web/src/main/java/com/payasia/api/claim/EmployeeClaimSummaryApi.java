package com.payasia.api.claim;

import java.math.BigDecimal;

import org.springframework.http.ResponseEntity;

import com.payasia.api.hris.model.SearchParam;
import com.payasia.api.swagger.SwaggerTags;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Api(tags="CLAIM", description="CLAIM related APIs")
public interface EmployeeClaimSummaryApi extends SwaggerTags {
	
	@ApiOperation(value = "Finds claim summary data details", notes = "Claim data can be provided with sorting and pagination.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> getEmpClaimSummaryDetails(@ApiParam(value = "Conditions for searching, sorting and pagination can be given in this object.", required = true) SearchParam searchParamObj);
	
	@ApiOperation(value = "Finds claim adjustments", notes = "Claim data can be provided with sorting and pagination.")
	@ApiImplicitParams({@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")})
	ResponseEntity<?> getEmpClaimAdjustments(long employeeClaimTemplateId, @ApiParam(value = "Conditions for searching, sorting and pagination can be given in this object.", required = true) SearchParam searchParamObj);

	@ApiOperation(value = "Finds claim item entitlement", notes = "Claim data can be provided with sorting and pagination.")
	@ApiImplicitParams({@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")})
	ResponseEntity<?> getEmpAllClaimItemEntitlement(long employeeClaimTemplateId, BigDecimal entitlement);

	@ApiOperation(value = "Finds claim summary data details", notes = "Claim data can be provided with sorting and pagination.")
	@ApiImplicitParams({@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")})
	ResponseEntity<?> getEmpClaimTemplateItemEntDetails(long employeeClaimTemplateId, @ApiParam(value = "Conditions for searching, sorting and pagination can be given in this object.", required = true) SearchParam searchParamObj);

}
