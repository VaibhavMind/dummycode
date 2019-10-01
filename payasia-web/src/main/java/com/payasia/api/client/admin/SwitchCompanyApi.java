package com.payasia.api.client.admin;

import org.springframework.http.ResponseEntity;

import com.payasia.api.hris.model.SearchParam;
import com.payasia.api.swagger.SwaggerTags;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

@Api(tags="Switch Company", description="Switch Company related APIs")
public interface SwitchCompanyApi extends SwaggerTags {

	@ApiOperation(value = "Display Company List", notes = "Company List can be provided with sorting and pagination.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> getSwitchCompanyList(SearchParam searchParamObj, Boolean includeInactiveCompany);

	@ApiOperation(value = "Switch Company", notes = "Company can be switched on the basis of companyId and companyCode.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> switchCompany(Long companyId, String companyCode);

}
