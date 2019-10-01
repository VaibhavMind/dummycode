package com.payasia.api.admin.data.exchange;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.xml.sax.SAXException;

import com.itextpdf.text.DocumentException;
import com.payasia.api.hris.model.SearchParam;
import com.payasia.api.swagger.SwaggerTags;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

@Api(tags="Excel Import Tool")
public interface ExcelImportToolApi extends SwaggerTags{

	@ApiOperation(value = "Excel Import Tool", notes = "Excel Import Tool can be searched and displayed.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> findExcelImportTemplate(SearchParam searchParamObj);

	@ApiOperation(value = "Excel Import Tool Get Template", notes = "Display Template.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> findDataForTemplate(Long templateId);

	@ApiOperation(value = "Excel Import Tool Get Table Mapping", notes = "Display Mapping.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> findExistingMapping(Long entityId);

	@ApiOperation(value = "Excel Import Tool Get Child Table Mapping", notes = "Display Child Mapping.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> findExistingChildTableMapping(Long entityId, Long formId, int tablePosition);

	@ApiOperation(value = "Excel Import Tool Add Template", notes = "Add Data Import Template.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> saveImportTemplate(String metaData);

	@ApiOperation(value = "Excel Import Tool Generate Excel Template", notes = "Generate Excel Import Template.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> generateExcelImport(Long templateId)
			throws DocumentException, IOException, JAXBException, SAXException;
	
	@ApiOperation(value = "Excel Import Tool Get Playslip Format", notes = "Display Playslip Format.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> getPlaySlipFormat();

	@ApiOperation(value = "Excel Import Tool Get Company Frequency", notes = "Display Company Frequency.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> getCompanyFrequency();

	@ApiOperation(value = "Excel Import Tool Get Template List", notes = "Display Template List.")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> getTemplateList(Long entityId);

	@ApiOperation(value = "Excel Import Tool Get History", notes = "Display History .")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> getHistory();

	@ApiOperation(value = "Excel Import Tool Get Company Address", notes = "Display Company Address .")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> getCompanyAddress();

	@ApiOperation(value = "Excel Import Tool Get Company Address Mapping", notes = "Display Company Address Mapping .")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> getCompanyAddressMapping();

	@ApiOperation(value = "Excel Import Tool Get Company Filter List", notes = "Display Company Filter List .")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> getCompanyFilterList();

	@ApiOperation(value = "Excel Import Tool Add Company Address", notes = "Add Company Address .")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> configureCompanyAddress(String[] dataDictionaryIds);

	@ApiOperation(value = "Excel Import Tool Preview Text PlaySlip", notes = "Preview Text PlaySlip .")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> previewTextPayslipPdf(String jsonStr, CommonsMultipartFile files);

	@ApiOperation(value = "Excel Import Tool Upload PlaySlip", notes = "Upload PlaySlip .")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> uploadPayslip(String jsonStr, CommonsMultipartFile files);

	@ApiOperation(value = "Excel Import Tool Data Import Status", notes = "Data Import Status .")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> dataImportStatus(String files);

	@ApiOperation(value = "Excel Import Tool Data Add Edited Template", notes = "Add Edited Template .")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> saveEditedImportTemplate(Long templateId, String metaData);

	@ApiOperation(value = "Excel Import Tool Data Get Company Part", notes = "Display Company Part .")
	@ApiImplicitParam(name = "Authorization", value = "Bearer Authorization token value", required = true, dataType = "string", paramType = "header")
	ResponseEntity<?> getCompanyPart();


}
