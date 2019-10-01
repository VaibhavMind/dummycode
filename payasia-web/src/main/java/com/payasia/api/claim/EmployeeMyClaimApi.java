package com.payasia.api.claim;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.payasia.api.hris.model.SearchParam;
import com.payasia.api.swagger.SwaggerTags;
import com.payasia.common.form.AddClaimForm;
import com.payasia.common.form.ClaimApplicationItemAttach;
import com.payasia.common.form.ClaimApplicationItemForm;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@Api(tags="CLAIM", description="CLAIM related APIs")
public interface EmployeeMyClaimApi extends SwaggerTags {

	@ApiOperation(value = "Search My-Claims", notes = "My-Claim data can be searched using pagination and sorting.")
	@ApiImplicitParams({@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")})
	ResponseEntity<?> searchMyClaims(SearchParam searchParamObj, String requestType);

	@ApiOperation(value = "Apply My-Claim", notes = "Claim data can be applied.")
	@ApiImplicitParams({@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")})
	ResponseEntity<?> applyMyClaim();

	@ApiOperation(value = "Get Claim Template Items", notes = "Display My-Claim template Items.")
	@ApiImplicitParams({@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")})
	ResponseEntity<?> myClaimTemplateItems(Long employeeClaimTemplateId);

//	@ApiOperation(value = "Add Claim Template Item", notes = "Insert My-Claim template Item.")
	@ApiOperation(value = "View Claim Template Item Data", notes = "View My-Claim template Item data.")
	@ApiImplicitParams({@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")})
	ResponseEntity<?> viewClaimTemplateItemData(Long employeeClaimTemplateItemId, Long claimApplicationId, Long claimApplicationItemId);

	@ApiOperation(value = "Remove Claim Template Item", notes = "Delete My-Claim template Item.")
	@ApiImplicitParams({@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")})
	ResponseEntity<?> deleteMyClaimTemplateItem(Long claimApplicationItemId);
	
	@ApiOperation(value = "Lundin Block List", notes = "Data for Lundin Block List dropdown.")
	@ApiImplicitParams({@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")})
	ResponseEntity<?> lundinEmpBlockListDropDown();

	@ApiOperation(value = "Lundin AFE List", notes = "Data for Lundin Block List dropdown.")
	@ApiImplicitParams({@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")})
	ResponseEntity<?> lundinAFEEmpListDropDown(Long blockId);

	@ApiOperation(value = "View Employee Claim Item Data", notes = "Employee Claim Item Data can displayed here.")
	@ApiImplicitParams({@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")})
	ResponseEntity<?> getEmployeeClaimItemData(Long claimApplicationItemId);

	@ApiOperation(value = "Update Claim Template Item", notes = "Edit and Save Claim Template Item.")
	@ApiImplicitParams({@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")})
	ResponseEntity<?> updateClaimTemplateItem(ClaimApplicationItemForm claimApplicationItemForm);

	@ApiOperation(value = "Claim Application Item List", notes = "Claim Application Item List can be searched.")
	@ApiImplicitParams({@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")})
	ResponseEntity<?> getClaimApplicationItemList(Long claimApplicationId);

	@ApiOperation(value = "View Claim Application Data", notes = "Claim Application data can be viewed.")
	@ApiImplicitParams({@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")})
	ResponseEntity<?> viewClaimApplicationData(Long claimApplicationId);

	@ApiOperation(value = "Prints MY-Claim application", notes = "Claim application can be printed.")
	@ApiImplicitParams({@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")})
	ResponseEntity<?> doPrintMyClaimData(Long claimApplicationReviewerId);

	@ApiOperation(value = "Saves My-Claim data as draft", notes = "Claim data as draft can be saved.")
	@ApiImplicitParams({@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")})
	ResponseEntity<?> saveAsDraftFromWithdraw(AddClaimForm addClaimForm);

	@ApiOperation(value = "Persist My-Claim Application", notes = "Persists My-Claim application.")
	@ApiImplicitParams({@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")})
	ResponseEntity<?> persistMyClaimApplication(AddClaimForm addClaimForm);

	@ApiOperation(value = "Claim Batch Configuration", notes = "Claim batch configuration.")
	@ApiImplicitParams({@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")})
	ResponseEntity<?> getClaimBatchConf();

	@ApiOperation(value = "Create OR Save Claim Application", notes = "Claim Application can be saved.")
	@ApiImplicitParams({@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")})
	ResponseEntity<?> getSaveOrCreateClaimApplication(Long employeeClaimTemplateId);

	@ApiOperation(value = "Copy Claim Application", notes = "Claim Application can be copied.")
	@ApiImplicitParams({@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")})
	ResponseEntity<?> copyClaimApplication(AddClaimForm addClaimForm);

	@ApiOperation(value = "Upload Claim Application Item Attachment", notes = "Claim Application Item Attachment can be uploaded.")
	@ApiImplicitParams({@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")})
	ResponseEntity<?> uploadClaimItemAttachement(Long claimApplicationItemId, ClaimApplicationItemAttach claimApplicationItemAttach);

	@ApiOperation(value = "Upload Multiple Claim Application Item Attachments", notes = "Multiple Claim Application Item Attachments can be uploaded.")
	@ApiImplicitParams({@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")})
	ResponseEntity<?> uploadMultipleClaimItemAttachement(Long claimApplicationItemId, CommonsMultipartFile[] claimApplicationItemAttach);

	@ApiOperation(value = "Delete Claim Application Item Attachment", notes = "Claim Application Item Attachment can be deleted.")
	@ApiImplicitParams({@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")})
	ResponseEntity<?> deleteClaimApplicationAttachement(Long attachementId);

	@ApiOperation(value = "Delete Claim Application", notes = "Claim Application can be deleted.")
	@ApiImplicitParams({@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")})
	ResponseEntity<?> deleteClaimApplication(Long claimApplicationId);
	
	@ApiOperation(value = "Withdraw Claim", notes = "Calim data can be withdrawn.")
	@ApiImplicitParams({@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")})
	ResponseEntity<?> withdrawClaim(AddClaimForm addClaimForm);

	@ApiOperation(value = "Claim Preferences", notes = "Claim Preferences can be fetched.")
	@ApiImplicitParams({@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")})
	ResponseEntity<?> getClaimPreferences();
	
	@ApiOperation(value = "Forex Rate", notes = "Forex Rate can be fetched.")
	@ApiImplicitParams({@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")})
	ResponseEntity<?> getForexRate(String currencyDate, Long currencyId);

	@ApiOperation(value = "Save Claim Template Item", notes = "Claim Template Item can be saved.")
	@ApiImplicitParams({@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")})
	ResponseEntity<?> saveClaimTemplateItem(ClaimApplicationItemForm claimApplicationItemForm);

	@ApiOperation(value = "Claim Template Level configurations", notes = "Claim Template level configurations.")
	@ApiImplicitParams({@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")})
	ResponseEntity<?> getLoggedinEmpTemplates();

}
