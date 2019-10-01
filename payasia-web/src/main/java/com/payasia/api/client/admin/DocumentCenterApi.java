package com.payasia.api.client.admin;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.payasia.api.hris.model.SearchParam;
import com.payasia.api.swagger.SwaggerTags;
import com.payasia.common.form.CompanyDocumentCenterForm;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
@Api(tags="ADMIN-DOCUMENT-CENTER")
public interface DocumentCenterApi extends SwaggerTags{

	@ApiOperation(value = "Delete Document")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> deleteCompanyDocument(Long docId);

	@ApiOperation(value = "Search Document")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> searchDocument(SearchParam searchParamObj, Long categoryId);

	@ApiOperation(value = "Edit Uploaded Data")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> editUploadedData(Long docId);
	
	@ApiOperation(value = "Upload Document")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> uploadDocument(String jsonStr, CommonsMultipartFile files);

	@ApiOperation(value = "Download Document")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> downloadDocuments(Long documentId) throws IOException;

	@ApiOperation(value = "UpdateDocument")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> updateDocument(CompanyDocumentCenterForm companyDocumentCenterForm);
	
	@ApiOperation(value = "Delete Tax Document")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> deleteTaxDocument(Long[] docDetailsIds);
	
	@ApiOperation(value = "Get Employee Filter List")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> getEmployeeFilterList();

	@ApiOperation(value = "Get Edit Employee Filter List")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> getEditEmployeeFilterList(CompanyDocumentCenterForm companyDocumentCenterForm);

	@ApiOperation(value = "Search Document Employee Document Center")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> searchDocumentEmployeeDocumentCenter(SearchParam searchParamObj, Long categoryId);
	
	@ApiOperation(value = "Delete Filter")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> deleteFilter(Long filterId);

	@ApiOperation(value = "Save Employee Filter List")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> saveEmployeeFilterList(CompanyDocumentCenterForm companyDocumentCenterForm) throws UnsupportedEncodingException;

	@ApiOperation(value = "Short List Data")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> shortListData();

}