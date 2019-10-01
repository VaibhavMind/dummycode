package com.payasia.api.client.admin;

import org.springframework.http.ResponseEntity;

import com.payasia.api.hris.model.SearchParam;
import com.payasia.api.swagger.SwaggerTags;
import com.payasia.common.form.HRLetterForm;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONException;

@Api(tags="Admin HR-Letter", description="Admin HRIS related APIs")
public interface AdminHrLetterApi extends SwaggerTags{

	@ApiOperation(value = "Search Admin HR-Letter", notes = "Admin HR-Letter data can be provided with sorting and pagination.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> searchAdminHrLetters(SearchParam searchParamObj);

	@ApiOperation(value = "Save Admin HR-Letter", notes = "Admin HR-Letter data can be saved.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> saveHrLetter(HRLetterForm hrLetterForm);

	@ApiOperation(value = "Fetch Admin HR-Letter details", notes = "Admin HR-Letter data can be fetched on the basis of letterId.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> getHrLetterDetails(long letterId);

	@ApiOperation(value = "Edit Admin HR-Letter", notes = "Admin HR-Letter data can be updated.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> updateHrLetter(HRLetterForm hrLetterForm);

	@ApiOperation(value = "Fetch Employee Ids", notes = "Employee Ids can be provided.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> getEmployeeIds(String searchString);

	@ApiOperation(value = "Preview Admin HR-Letter", notes = "Admin HR-Letter data can be previewed.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> previewHrLetter(String jsonStr) throws JSONException;

	@ApiOperation(value = "Send Admin HR-Letter", notes = "Admin HR-Letter data can be sent.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> sendHrLetter(String jsonStr);

	@ApiOperation(value = "Delete Admin HR-Letter", notes = "Admin HR-Letter data can be deleted.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> deleteHrLetter(Long letterId);

	@ApiOperation(value = "Fetch Edit Employee details", notes = "Edit Employees data can be fetched on the basis of letterId.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> getEditEmployeeFilterList(Long letterId);

	@ApiOperation(value = "Fetch Employee Filter List", notes = "Employee Filter List can be fetched on the basis of companyId.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> getEmployeeFilterList();

}
