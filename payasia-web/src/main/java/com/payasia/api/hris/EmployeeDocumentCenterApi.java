package com.payasia.api.hris;

import java.io.IOException;
import java.util.Locale;

import org.springframework.http.ResponseEntity;

import com.payasia.api.hris.model.SearchParam;
import com.payasia.api.swagger.SwaggerTags;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

@Api(tags="HRIS", description="HRIS related APIs")
public interface EmployeeDocumentCenterApi extends SwaggerTags{

	@ApiOperation(value = "Find Employee Document", notes = "This API finds Employee Document with sorting and pagination.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> searchEmployeeDocument(SearchParam searchParamObj, Long categoryId);

	@ApiOperation(value = "Download Employee Document", notes = "This API downloads Employee Document.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> downloadEmployeeDocument(String jsonStr) throws IOException;

	@ApiOperation(value = "Document Filter List", notes = "This API provides Document Filter List.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> getDocFilterList(Locale locale);
	
}
