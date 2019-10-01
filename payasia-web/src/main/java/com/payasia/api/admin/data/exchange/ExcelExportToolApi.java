package com.payasia.api.admin.data.exchange;

import org.springframework.http.ResponseEntity;

import com.payasia.api.hris.model.SearchParam;
import com.payasia.api.swagger.SwaggerTags;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

@Api(tags="Excel Export Tool")
public interface ExcelExportToolApi extends SwaggerTags{

	@ApiOperation(value = "Excel Export Tool", notes = "Excel Export Tool can be searched and displayed.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> findExcelExportTemplate(SearchParam searchParamObj);

	@ApiOperation(value = "Excel Export Tool Get Table Mapping", notes = "Display Mapping.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> findExistingMapping(Long entityId);

	@ApiOperation(value = "Excel Export Tool Get Child Table Mapping", notes = "Display child Mapping.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> findExistingChildTableMapping(Long entityId, Long formId, int tablePosition);

	@ApiOperation(value = "Excel Export Tool Get templates", notes = "Display templates.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> getTemplates(Long templateId);

	@ApiOperation(value = "Excel Export Tool Delete templates", notes = "Delete templates.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> deleteTemplates(Long templateId);

	@ApiOperation(value = "Excel Export Tool Add templates", notes = "Add templates.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> saveExportTemplate(String metaData);

	@ApiOperation(value = "Excel Export Tool Edit templates", notes = "Edit templates.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> saveEditedExportTemplate(Long templateId, String metaData);

	@ApiOperation(value = "Excel Export Tool Filter for templates Group", notes = "Filter for templates Group.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> filtersForTemplateGroup(Long templateId);

	@ApiOperation(value = "Excel Export Tool Generate Excel", notes = "Generate Excel.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> generateExcelExport(String jsonStr);

	/*@ApiOperation(value = "Excel Export Tool Data Dictionay Static List", notes = "Data Dictionay Static List.")*/
	/*@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> getDataDictionaryEmpStaticList();*/

	@ApiOperation(value = "Excel Export Tool Data Advance Filter List", notes = "Advance Filter List.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> getAdvanceFilterComboList();

	@ApiOperation(value = "Excel Export Tool Get Table Mapping For Group", notes = "Display Mapping For Group.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> findExistingMappingForGroup(Long entityId);

	@ApiOperation(value = "Excel Export Tool Filter for templates", notes = "Filter for templates.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> filtersForTemplate(Long templateId);

	@ApiOperation(value = "Excel Export Tool Filter for Export Companies", notes = "Display Export Companies.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> findExportCompanies(SearchParam searchParamObj);

	

}
