package com.payasia.api.admin.data.exchange.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.payasia.api.admin.data.exchange.ExcelExportToolApi;
import com.payasia.api.hris.model.Filters;
import com.payasia.api.hris.model.SearchParam;
import com.payasia.api.utils.ApiMessageHandler;
import com.payasia.api.utils.ApiMessageHandler.Type;
import com.payasia.api.utils.ApiUtils;
import com.payasia.api.utils.SearchSortUtils;
import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.SearchSortDTO;
import com.payasia.common.form.DataExportForm;
import com.payasia.common.form.ExcelExportFiltersForm;
import com.payasia.common.form.ExcelExportToolForm;
import com.payasia.common.form.ExcelExportToolFormResponse;
import com.payasia.common.form.SwitchCompanyResponse;
import com.payasia.common.util.FileUtils;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.dao.DataDictionaryDAO;
import com.payasia.dao.bean.DataDictionary;
import com.payasia.logic.AWSS3Logic;
import com.payasia.logic.DataExportLogic;
import com.payasia.logic.DataImportLogic;
import com.payasia.logic.DataImportUtils;
import com.payasia.logic.ExcelExportToolLogic;
import com.payasia.logic.GeneralLogic;
import com.payasia.logic.PaySlipDynamicFormLogic;
import com.payasia.logic.SwitchCompanyLogic;
import com.payasia.logic.TextPayslipUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * @author vaibhav Dhawan
 * @param :This class used for Excel Import Tool API
 */
@RestController
@RequestMapping(value = ApiUtils.API_ROOT_PATH_FOR_ADMIN+"/excel-export")
public class ExcelExportToolApiImpl implements ExcelExportToolApi{

	@Resource
	private ExcelExportToolLogic excelExportToolLogic;
	
	@Resource
	private PaySlipDynamicFormLogic paySlipDynamicFormLogic;
	
	@Resource
	private DataImportLogic dataImportLogic;
	
	@Resource
	TextPayslipUtils textPayslipUtils;
	
	@Resource
	DataImportUtils dataImportUtils;
	
	@Resource
	FileUtils fileUtils;
	
	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	private SearchSortUtils searchSortUtils;
	
	@Autowired
	private DataDictionaryDAO dataDictionaryDAO;
	
	
	@Autowired
	@Qualifier("AWSS3Logic")
	AWSS3Logic awss3LogicImpl;
	
	@Value("#{payasiaptProperties['payasia.application.deployment.location']}")
	private String appDeployLocation;
	
	
	@Value("#{payasiaptProperties['payasia.home']}")
	private String downloadPath;
	
	
	@Value("#{payasiaptProperties['payasia.document.root.directory']}")
	private String rootDirectoryName;
	
	
	private String status="Success";
	
	@Resource
	DataExportLogic dataExportLogic;
	
	@Resource
	GeneralLogic generalLogic;
	
	@Resource
	SwitchCompanyLogic switchCompanyLogic;

	
	private static final Logger LOGGER = Logger.getLogger(ExcelExportToolApiImpl.class);
	
	@Override
	@PostMapping(value = "search-export-template")
	public ResponseEntity<?> findExcelExportTemplate(@RequestBody SearchParam searchParamObj) {
		
		Long companyId = UserContext.getClientAdminId();
		final List<Filters> filterllist = Arrays.asList(searchParamObj.getFilters());
    	SearchSortDTO searchSortDTO = searchSortUtils.getSearchSortObject(searchParamObj);
				
		String searchValue = "";
		String searchCondition = "";
		Long entityId = 0L;
		String scope = "0";
		if ((filterllist != null && !filterllist.isEmpty())) {
			searchValue = filterllist.get(0).getValue();
			searchCondition = filterllist.get(0).getField();
			
		}
		
		if (searchCondition
				.equalsIgnoreCase(PayAsiaConstants.SEARCH_CONDITION_CATEGORY)) {
			entityId = Long.parseLong(searchValue);
		} else if (searchCondition
				.equalsIgnoreCase(PayAsiaConstants.SEARCH_CONDITION_SCOPE)) {
			scope = searchValue;
		}
			
		ExcelExportToolFormResponse formResponse = excelExportToolLogic.getExistImportTempDef(searchSortDTO.getPageRequest(), searchSortDTO.getSortCondition(), companyId,
						searchCondition, searchValue, entityId, scope);
		
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(formResponse, jsonConfig);
		
		return new ResponseEntity<>(jsonObject.toString(), HttpStatus.OK);	
	}
	
	
	@Override
	@GetMapping(value = "existing-mapping")
	public ResponseEntity<?> findExistingMapping(@RequestParam (value = "entityId", required=true) Long entityId) {
		
		Long companyId = UserContext.getClientAdminId();
		ExcelExportToolForm excelExportToolForm = excelExportToolLogic
				.getExistMapping(companyId, entityId, UserContext.getLanguageId());
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(excelExportToolForm, jsonConfig);
		return new ResponseEntity<>(jsonObject.toString(), HttpStatus.OK);	
	}

	@Override
	@GetMapping(value = "existing-child-mapping")
	public ResponseEntity<?> findExistingChildTableMapping(
			@RequestParam (value = "entityId", required=true) Long entityId,
			@RequestParam (value = "formId", required=true) Long formId,
			@RequestParam (value = "tablePosition", required=true) int tablePosition) {
		
		Long companyId = UserContext.getClientAdminId();
		Long languageId = UserContext.getLanguageId();
		ExcelExportToolForm excelImportToolForm = excelExportToolLogic
				.getExistTableMapping(companyId, entityId, formId,
						tablePosition, languageId);
		
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(excelImportToolForm, jsonConfig);
		return new ResponseEntity<>(jsonObject.toString(), HttpStatus.OK);	
	}
	
	@Override
	@GetMapping(value = "templates")
	public ResponseEntity<?> getTemplates(@RequestParam(value="templateId", required=true) Long templateId)
	{
		/*ID DECRYPT*/
		templateId = FormatPreserveCryptoUtil.decrypt(templateId);
		ExcelExportToolForm excelExportToolForm = excelExportToolLogic
				.getDataForTemplate(templateId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(excelExportToolForm, jsonConfig);
		return new ResponseEntity<>(jsonObject.toString(), HttpStatus.OK);	
		
	}
	
	
	@Override
	@PostMapping(value = "addExportTemplate")
	public ResponseEntity<?> saveExportTemplate(@RequestBody String metaData)
	{
		Long companyId = UserContext.getClientAdminId();
		String responseString = null;
		Map<String, String> messageMap = new HashMap<String, String>();
		try
		{
			if(metaData != null && !metaData.isEmpty())
			{
				String decodeMetaData = URLDecoder.decode(metaData, "UTF-8");	
				responseString = excelExportToolLogic.saveTemplate(companyId,
						decodeMetaData, UserContext.getLanguageId());
				
				responseString = messageSource.getMessage(
						responseString, new Object[] {}, UserContext.getLocale());
				messageMap.put("message", responseString);
			}
		}
		catch (UnsupportedEncodingException unsupportedEncodingException) {
			LOGGER.error(unsupportedEncodingException.getMessage(),
					unsupportedEncodingException);
		} catch (NoSuchMessageException noSuchMessageException) {
			LOGGER.error(noSuchMessageException.getMessage(),
					noSuchMessageException);
		}
		return new ResponseEntity<>(messageMap, HttpStatus.OK);
	}
	
	
	
	
	@Override
	@DeleteMapping(value = "templates")
	public ResponseEntity<?> deleteTemplates(@RequestParam (value = "templateId", required=true) Long templateId)
	{
		/*ID DECRYPT*/
		templateId = FormatPreserveCryptoUtil.decrypt(templateId);
		Long companyId = UserContext.getClientAdminId();
		boolean deleteStatus =	excelExportToolLogic.isAdminAuthorizedForComTemplate(templateId, companyId);
		 if(deleteStatus){
		     excelExportToolLogic.deleteTemplate(templateId);
		     return new ResponseEntity<>(new ApiMessageHandler(Type.SUCCESS, HttpStatus.OK.toString(), messageSource.getMessage("payasia.dataexport.deleted.successfully", new Object[]{}, UserContext.getLocale())), HttpStatus.OK);
		 }
		 return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.dataexport.deleted.error", new Object[]{}, UserContext.getLocale())), HttpStatus.NOT_FOUND);
		
	}
	
	

	@Override
	@PostMapping(value = "add-edited-exportTemplate", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
	public ResponseEntity<?> saveEditedExportTemplate(@RequestParam (value = "templateId", required=true) Long templateId,
			@RequestBody String metaData)
	{
		/*ID DECRYPT*/
		templateId = FormatPreserveCryptoUtil.decrypt(templateId);
		Long companyId = UserContext.getClientAdminId();
		String responseString = null;
		Map<String, String> messageMap = new HashMap<String, String>();
		try {
			if (metaData != null && !metaData.isEmpty()) {
				String decodemetadata = URLDecoder.decode(metaData, "UTF-8");
				responseString = excelExportToolLogic.editTemplate(templateId , decodemetadata, companyId ,
						UserContext.getLanguageId());

				responseString = messageSource.getMessage(responseString, new Object[] {}, UserContext.getLocale());
				messageMap.put("message", responseString);
			}
		} catch (UnsupportedEncodingException unsupportedEncodingException) {
			LOGGER.error(unsupportedEncodingException.getMessage(),
					unsupportedEncodingException);
		} catch (NoSuchMessageException noSuchMessageException) {
			LOGGER.error(noSuchMessageException.getMessage(),
					noSuchMessageException);
		}
		return new ResponseEntity<>(messageMap, HttpStatus.OK);
	}
	
	@Override
	@GetMapping(value = "filter-templates-group")
	public ResponseEntity<?> filtersForTemplateGroup(@RequestParam (value = "templateId", required=true) Long templateId)
	{
		/*ID DECRYPT*/
		templateId = FormatPreserveCryptoUtil.decrypt(templateId);
		
		Long employeeId = Long.parseLong(UserContext.getUserId());

		Long languageId = UserContext.getLanguageId();
		DataExportForm dataExportForm = dataExportLogic.getDataForTemplateGroup(templateId, languageId, null,employeeId, "edit");

	   // JsonConfig jsonConfig = new JsonConfig();
		//JSONObject jsonObject = JSONObject.fromObject(dataExportForm.getExcelExportFilterList(), jsonConfig);
		
		return new ResponseEntity<>(dataExportForm.getExcelExportFilterList(), HttpStatus.OK);	
		
	}
	
	
	@Override
	@PostMapping(value = "generate-excel")
	public ResponseEntity<?> generateExcelExport(@RequestParam("DataExportForm") String jsonStr)
	{
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObj = JSONObject.fromObject(jsonStr, jsonConfig);
		Long languageId = UserContext.getLanguageId();
		Long companyId = UserContext.getClientAdminId();
		Long employeeId = Long.parseLong(UserContext.getUserId());
		DataExportForm dataExportForm = new DataExportForm();
		DataExportForm exportForm;
		String scope =jsonObj.getString("scope");
		/*ID DECRYPT*/
		dataExportForm.setTemplateId(FormatPreserveCryptoUtil.decrypt(jsonObj.getLong("templateId")));
		dataExportForm.setEntityId(jsonObj.getLong("entityId"));
		
		JSONArray jsonArray = jsonObj.getJSONArray("selectedComapnyIds");
		String[] arry = new String[jsonArray.size()];
		
		for (int i = 0; i < jsonArray.size(); i++) {
              arry[i] = jsonArray.getString(i);
		}
		
		dataExportForm.setSelectedComapnyIds(arry);
		List<ExcelExportFiltersForm> excelExportFilterList  = new ArrayList<>();
		JSONArray excelExportFilterArray = jsonObj.getJSONArray("excelExportFilterList");
		if (!excelExportFilterArray.isEmpty()) {
			for (int i = 0; i < excelExportFilterArray.size(); i++) {
				
				ExcelExportFiltersForm excelExportFiltersForm = new ExcelExportFiltersForm();
				excelExportFiltersForm.setExportFilterId(jsonObj.getJSONArray("excelExportFilterList").getJSONObject(i).getLong("filterId"));
				excelExportFiltersForm.setDataDictionaryName(jsonObj.getJSONArray("excelExportFilterList").getJSONObject(i).getString("filterName"));
				excelExportFiltersForm.setEqualityOperator(jsonObj.getJSONArray("excelExportFilterList").getJSONObject(i).getString("equalityOperator"));
				excelExportFiltersForm.setLogicalOperator(jsonObj.getJSONArray("excelExportFilterList").getJSONObject(i).getString("logicalOperator"));
				excelExportFiltersForm.setValue(jsonObj.getJSONArray("excelExportFilterList").getJSONObject(i).getString("value"));
				excelExportFiltersForm.setOpenBracket(jsonObj.getJSONArray("excelExportFilterList").getJSONObject(i).getString("openBracket"));
				excelExportFiltersForm.setCloseBracket(jsonObj.getJSONArray("excelExportFilterList").getJSONObject(i).getString("closeBracket"));
				DataDictionary dataDictionary = dataDictionaryDAO.findById(jsonObj.getJSONArray("excelExportFilterList").getJSONObject(i).getLong("dictionaryId"));
				excelExportFiltersForm.setDictionaryId(dataDictionary.getDataDictionaryId());
				excelExportFilterList.add(excelExportFiltersForm);
			}
		}
			
		dataExportForm.setExcelExportFilterList(excelExportFilterList);

		if (scope.equalsIgnoreCase(PayAsiaConstants.PAYASIA_SCOPE_GROUP
				.toLowerCase())) {

			exportForm = dataExportLogic.generateExcelGroup(
					dataExportForm.getTemplateId(), companyId, employeeId,
					dataExportForm.getExcelExportFilterList(), languageId,
					dataExportForm.getSelectedComapnyIds());

		} else {
			exportForm = dataExportLogic.generateExcel(
					dataExportForm.getTemplateId(), companyId, employeeId,
					dataExportForm.getExcelExportFilterList(), languageId);
		}
		Workbook excelFile = exportForm.getWorkbook();
		String fileName = exportForm.getFinalFileName();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] bytes = null;
		try {
			excelFile.write(bos);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		bytes = bos.toByteArray();
		Map<String, Object> generateDataExcelMap = new HashMap<>();
		generateDataExcelMap.put("generateExcelbody", bytes);
		generateDataExcelMap.put("excelName", fileName + ".xls");
	    return new ResponseEntity<>(generateDataExcelMap, HttpStatus.OK);
	}
	
		
	@Override
	@GetMapping(value = "advance-filterComboList")
	public ResponseEntity<?> getAdvanceFilterComboList()
	{
		Long companyId = UserContext.getClientAdminId();
		Map<String, String> advanceFilterCombosHashMap = generalLogic.getEmployeeFilterComboList(companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(
				advanceFilterCombosHashMap, jsonConfig);
		return new ResponseEntity<>(jsonObject.toString(), HttpStatus.OK);
	}
	
	@Override
	@GetMapping(value = "existing-mapping-group")
	public ResponseEntity<?> findExistingMappingForGroup(@RequestParam (value = "entityId", required=true) Long entityId) {
		
		Long companyId = UserContext.getClientAdminId();
		ExcelExportToolForm excelExportToolForm = excelExportToolLogic
				.getExistMappingForGroup(companyId, entityId, UserContext.getLanguageId());
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(excelExportToolForm, jsonConfig);
		return new ResponseEntity<>(jsonObject.toString(), HttpStatus.OK);	
	}
	
	@Override
	@GetMapping(value = "filter-templates")
	public ResponseEntity<?> filtersForTemplate(@RequestParam (value = "templateId", required=true) Long templateId)
	{
		/*ID DECRYPT*/
		templateId = FormatPreserveCryptoUtil.decrypt(templateId);
		DataExportForm dataExportForm =null;
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = UserContext.getClientAdminId();
	    boolean status =	dataExportLogic.isAdminAuthorizedForComTemplate(templateId, companyId);
		if(status)
		{
		 dataExportForm = dataExportLogic.getDataForTemplate(templateId, UserContext.getLanguageId(), employeeId, "edit");
		}
		else
		{
			return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("data.info.nodata", new Object[]{}, UserContext.getLocale())), HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(dataExportForm.getExcelExportFilterList(), HttpStatus.OK);	
		
	}
	
	
	@Override
	@PostMapping(value = "export-companies")
	public ResponseEntity<?> findExportCompanies(@RequestBody SearchParam searchParamObj) {
		
		Long companyId = UserContext.getClientAdminId();
		Long employeeId = Long.parseLong(UserContext.getUserId());
		SearchSortDTO searchSortDTO = searchSortUtils.getSearchSortObject(searchParamObj);
		SwitchCompanyResponse response = switchCompanyLogic.getExportCompanies(
				searchSortDTO.getPageRequest(), searchSortDTO.getSortCondition(), employeeId, companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(response, jsonConfig);
		
		return new ResponseEntity<>(jsonObject.toString(), HttpStatus.OK);	
	}
	
	
}
