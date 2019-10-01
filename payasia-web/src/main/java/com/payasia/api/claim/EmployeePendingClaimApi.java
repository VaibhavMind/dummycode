package com.payasia.api.claim;

import org.springframework.http.ResponseEntity;

import com.payasia.api.hris.model.SearchParam;
import com.payasia.api.swagger.SwaggerTags;
import com.payasia.common.form.ClaimApplicationItemWorkflowForm;
import com.payasia.common.form.PendingClaimsForm;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@Api(tags="CLAIM", description="CLAIM related APIs")
public interface EmployeePendingClaimApi extends SwaggerTags {

	@ApiOperation(value = "View Pending claims", notes = "Pending Claim data can be viewed with sorting and pagination.")
	@ApiImplicitParams({@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")})
	ResponseEntity<?> viewPendingClaims(SearchParam searchParamObj);

	@ApiOperation(value = "Review Pending claims", notes = "Pending Claim data can be reviewed with sorting and pagination.")
	@ApiImplicitParams({@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")})
	ResponseEntity<?> doReviewPendingClaim(Long claimApplicationreviewerId);

	@ApiOperation(value = "Save Override Review Pending claim", notes = "Pending Claim data can be overridden.")
	@ApiImplicitParams({@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")})
	ResponseEntity<?> doSaveOverrideReviewClaim(ClaimApplicationItemWorkflowForm claimApplicationItemWorkflowForm);

	@ApiOperation(value = "Save Reject Review Pending claim", notes = "Pending Claim data can be rejected.")
	@ApiImplicitParams({@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")})
	ResponseEntity<?> doSaveRejectReviewClaim(ClaimApplicationItemWorkflowForm claimApplicationItemWorkflowForm);

	@ApiOperation(value = "Find details of claim item", notes = "Details of a paricular claim item is displayed.")
	@ApiImplicitParams({@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")})
	ResponseEntity<?> claimItemDataDetail(Long claimItemId);
	
	@ApiOperation(value = "Processes pending claim data", notes = "Approves, Forwards or rejects a pending claim.")
	@ApiImplicitParams({@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")})
	ResponseEntity<?> processPendingClaimData(String claimRequestType, PendingClaimsForm pendingClaimsForm);

	@ApiOperation(value = "Prints pending claim application", notes = "Prints the claim application.")
	@ApiImplicitParams({@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")})
	ResponseEntity<?> doPrintPendingClaimData(Long claimApplicationReviewerId);

	@ApiOperation(value = "Downloads pending claim attached documents", notes = "Download the claim attachment.")
	@ApiImplicitParams({@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")})
	ResponseEntity<?> viewClaimAttachment(Long claimApplicationItemAttachmentId);

}
