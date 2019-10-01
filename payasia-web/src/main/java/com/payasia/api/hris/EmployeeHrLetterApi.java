package com.payasia.api.hris;

import org.codehaus.jettison.json.JSONException;
import org.springframework.http.ResponseEntity;

import com.payasia.api.hris.model.SearchParam;
import com.payasia.api.swagger.SwaggerTags;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

@Api(tags="HRIS", description="HRIS related APIs")
public interface EmployeeHrLetterApi extends SwaggerTags{

	@ApiOperation(value = "Search HR-Letter", notes = "HR-Letter data can be provided with sorting and pagination.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> doSearchHrLetter(SearchParam searchParam);

	@ApiOperation(value = "Generate HR-Letter", notes = "HR-Letter can be generated on the basis of HR-Letter ID.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> doGenerateHrLetter(String letterId) throws JSONException;
	
	@ApiOperation(value = "Send HR-Letter", notes = "HR-Letter can be sent on the basis of HR-Letter ID.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> doSendHrLetter(String jsonStr);

	@ApiOperation(value = "Preview HR-Letter", notes = "HR-Letter can be previewed on the basis of HR-Letter ID.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> doPreviewHrLetterinPDF(String jsonStr);


}
