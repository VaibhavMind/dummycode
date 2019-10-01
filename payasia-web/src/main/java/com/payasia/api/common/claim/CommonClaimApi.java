package com.payasia.api.common.claim;

import org.springframework.http.ResponseEntity;

import com.payasia.api.hris.model.SearchParam;
import com.payasia.api.swagger.SwaggerTags;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@Api(tags="CLAIM", description="CLAIM related APIs")
public interface CommonClaimApi extends SwaggerTags{

	@ApiOperation(value = "Search claim template", notes = "Claim template data can be searched with sorting and pagination.")
	@ApiImplicitParams({@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")})
	ResponseEntity<?> searchClaimTemplateItem(Long claimApplicationId, SearchParam searchParamObj);

	@ApiOperation(value = "View claim reviewers", notes = "View reviewers for claim")
	@ApiImplicitParams({@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")})
	ResponseEntity<?> claimReviewers(Long claimApplicationId);

	@ApiOperation(value = "Currency Name List", notes = "Get Currency Name list")
	@ApiImplicitParams({@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")})
	ResponseEntity<?> getCurrencyNameList();

}
