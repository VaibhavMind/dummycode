package com.payasia.api.admin.data.exchange.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
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
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.payasia.api.admin.data.exchange.ExcelImportToolApi;
import com.payasia.api.hris.model.Filters;
import com.payasia.api.hris.model.SearchParam;
import com.payasia.api.utils.ApiMessageHandler;
import com.payasia.api.utils.ApiMessageHandler.Type;
import com.payasia.api.utils.ApiUtils;
import com.payasia.api.utils.SearchSortUtils;
import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.CompanyDocumentLogDTO;
import com.payasia.common.dto.DataImportLogDTO;
import com.payasia.common.dto.FilePathGeneratorDTO;
import com.payasia.common.dto.PayslipFrequencyDTO;
import com.payasia.common.dto.SearchSortDTO;
import com.payasia.common.dto.TextPaySlipListDTO;
import com.payasia.common.exception.PayAsiaRollBackDataException;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.DataImportForm;
import com.payasia.common.form.EmployeeFilterListForm;
import com.payasia.common.form.ExcelImportToolForm;
import com.payasia.common.form.ExcelImportToolFormResponse;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.ExcelUtils;
import com.payasia.common.util.FileUtils;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.bean.Company;
import com.payasia.logic.AWSS3Logic;
import com.payasia.logic.DataImportLogic;
import com.payasia.logic.DataImportUtils;
import com.payasia.logic.ExcelImportToolLogic;
import com.payasia.logic.PaySlipDynamicFormLogic;
import com.payasia.logic.TextPayslipUtils;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * @author vaibhav Dhawan
 * @param :This class used for Excel Import Tool API
 */
@RestController
@RequestMapping(value = ApiUtils.API_ROOT_PATH_FOR_ADMIN+"/excel-import")
public class ExcelImportToolApiImpl implements ExcelImportToolApi{

	@Resource
	private ExcelImportToolLogic excelImportToolLogic;
	
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
	
	@Resource
	CompanyDAO companyDAO;
	
	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	private SearchSortUtils searchSortUtils;
	
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
	
	private static final Logger LOGGER = Logger.getLogger(ExcelImportToolApiImpl.class);
	
	@Override
	@PostMapping(value = "search-import-template")
	public ResponseEntity<?> findExcelImportTemplate(@RequestBody SearchParam searchParamObj) {
		
		Long companyId = UserContext.getClientAdminId();
		final List<Filters> filterllist = Arrays.asList(searchParamObj.getFilters());
    	SearchSortDTO searchSortDTO = searchSortUtils.getSearchSortObject(searchParamObj);
				
		String searchValue = "";
		String searchCondition = "";
		if ((filterllist != null && !filterllist.isEmpty())) {
			searchValue = filterllist.get(0).getValue();
			searchCondition = filterllist.get(0).getField();
		}
		ExcelImportToolFormResponse formResponse  = excelImportToolLogic.getExistImportTempDef(searchSortDTO.getPageRequest(), searchSortDTO.getSortCondition(), companyId,
							searchCondition, searchValue);
		

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(formResponse, jsonConfig);
		
		return new ResponseEntity<>(jsonObject.toString(), HttpStatus.OK);	
	}
	
	@Override
	@GetMapping(value = "get-data-template", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
	public ResponseEntity<?> findDataForTemplate(@RequestParam (value = "templateId", required=true) Long templateId) {
		 templateId=FormatPreserveCryptoUtil.decrypt(templateId);
	   ExcelImportToolForm excelExportToolForm = excelImportToolLogic.getDataForTemplate(templateId);
		
	    JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(excelExportToolForm, jsonConfig);
		
		return new ResponseEntity<>(jsonObject.toString(), HttpStatus.OK);	
		
	}
	
	@Override
	@GetMapping(value = "get-existing-mapping", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
	public ResponseEntity<?> findExistingMapping(@RequestParam (value = "entityId", required=true) Long entityId) {
		
		Long companyId = UserContext.getClientAdminId();
		ExcelImportToolForm excelImportToolForm = excelImportToolLogic
				.getExistMapping(companyId, entityId, UserContext.getLanguageId());
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(excelImportToolForm, jsonConfig);
		return new ResponseEntity<>(jsonObject.toString(), HttpStatus.OK);	
	}
	
	
	@Override
	@GetMapping(value = "get-existing-child-mapping", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
	public ResponseEntity<?> findExistingChildTableMapping(
			@RequestParam (value = "entityId", required=true) Long entityId,
			@RequestParam (value = "formId", required=true) Long formId,
			@RequestParam (value = "tablePosition", required=true) int tablePosition) {
		
		Long companyId = UserContext.getClientAdminId();
		Long languageId = UserContext.getLanguageId();
		ExcelImportToolForm excelImportToolForm = excelImportToolLogic
				.getExistTableMapping(companyId, entityId, formId,
						tablePosition, languageId);
		
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(excelImportToolForm, jsonConfig);
		return new ResponseEntity<>(jsonObject.toString(), HttpStatus.OK);	
	}

	
	@Override
	@PostMapping(value = "addImportTemplate", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
	public ResponseEntity<?> saveImportTemplate(@RequestBody String metaData)
	{
		Long companyId = UserContext.getClientAdminId();
		String responseString = null;
		Map<String, String> messageMap = new HashMap<String, String>();
			if(metaData != null && !metaData.isEmpty())
			{
				
				responseString = excelImportToolLogic.saveTemplate(companyId,
						metaData, UserContext.getLanguageId());
				
				responseString = messageSource.getMessage(
						responseString, new Object[] {}, UserContext.getLocale());
				
				if(responseString.equalsIgnoreCase("Duplicate Template Name"))
				{
					return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.excel.export.duplicate.template.name", new Object[]{}, UserContext.getLocale())), HttpStatus.NOT_FOUND);
				}
				
				if(responseString.equalsIgnoreCase("Error in saving Import Template"))
				{
					return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.excel.export.error.in.saving.import.template", new Object[]{}, UserContext.getLocale())), HttpStatus.NOT_FOUND);
				}
				
			}
			messageMap.put("message", responseString);
			return new ResponseEntity<>(messageMap, HttpStatus.OK);
		
	}
	
	
	
	@Override
	@PostMapping(value = "add-edited-importTemplate", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
	public ResponseEntity<?> saveEditedImportTemplate(@RequestParam (value = "templateId", required=true) Long templateId,
			@RequestBody String metaData)
	{
		/*ID DECRYPT*/
		templateId = FormatPreserveCryptoUtil.decrypt(templateId);
		Long companyId = UserContext.getClientAdminId();
		String responseString = null;
		Map<String, String> messageMap = new HashMap<String, String>();
		
			if(metaData != null && !metaData.isEmpty())
			{
	            responseString = excelImportToolLogic.editTemplate(companyId,
	            		metaData, templateId, UserContext.getLanguageId());
	
				responseString = messageSource.getMessage(
						responseString, new Object[] {}, UserContext.getLocale());
				if(responseString.equalsIgnoreCase("Duplicate Template Name"))
				{
					return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.excel.export.duplicate.template.name", new Object[]{}, UserContext.getLocale())), HttpStatus.NOT_FOUND);
				}
				if(responseString.equalsIgnoreCase("Error in saving Import Template"))
				{
					return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.excel.export.error.in.saving.import.template", new Object[]{}, UserContext.getLocale())), HttpStatus.NOT_FOUND);
				}
				
			}
		messageMap.put("message", responseString);
		return new ResponseEntity<>(messageMap, HttpStatus.OK);
	}
	
	
	
	
		
	@Override
	@PostMapping(value = "generate-excel")
	public ResponseEntity<?> generateExcelImport(@RequestParam (value = "templateId", required=true) Long templateId) {
//		UUID uuid = UUID.randomUUID();
		Long templateIdDecr = FormatPreserveCryptoUtil.decrypt(templateId);
		ExcelImportToolForm importToolForm = excelImportToolLogic
				.generateExcel(templateIdDecr);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] bytes = null;
		if(importToolForm.getWorkbook() == null)
		{
			return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("data.info.nodata", new Object[]{}, UserContext.getLocale())), HttpStatus.NOT_FOUND);
		}
		else
		{
			try {
				 importToolForm.getWorkbook().write(bos);
			} catch (IOException e) {
			     e.printStackTrace();
			}
			bytes = bos.toByteArray();
		}
		Map<String, Object> generateDataExcelMap = new HashMap<>();
		generateDataExcelMap.put("generateExcelbody", bytes);
		generateDataExcelMap.put("excelName", importToolForm.getTemplateName() + ".xls");
	    return new ResponseEntity<>(generateDataExcelMap, HttpStatus.OK);
	}
	
	@DeleteMapping(value = "delete-template")
	public ResponseEntity<?> deleteImportTemplate(@RequestParam (value = "templateId", required=true) Long templateId) {
		  Long templateIdDecr = FormatPreserveCryptoUtil.decrypt(templateId);
	      String deleteStatus = excelImportToolLogic.deleteTemplate(templateIdDecr);	
		  if(StringUtils.equalsIgnoreCase(deleteStatus,status)){
				return new ResponseEntity<>(new ApiMessageHandler(Type.SUCCESS, HttpStatus.OK.toString(), messageSource.getMessage("payasia.dataimport.deleted.successfully", new Object[]{}, UserContext.getLocale())), HttpStatus.OK);
		    }
		  return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("payasia.dataimport.deleted.error", new Object[]{}, UserContext.getLocale())), HttpStatus.NOT_FOUND);
	}
	
	
	@Override
	@GetMapping(value = "playslip-format")
	public ResponseEntity<?> getPlaySlipFormat() {
		
		Long companyId = UserContext.getClientAdminId();
		String paySlipFormat = dataImportLogic.getPayslipFormatforCompany(companyId);
		Map<String, String> paySlipFormatMap = new HashMap<String, String>();
		paySlipFormatMap.put("paySlipFormat", paySlipFormat);
		return new ResponseEntity<>(paySlipFormatMap, HttpStatus.OK);
	}
	
	
	@Override
	@GetMapping(value = "company-frequency")
	public ResponseEntity<?> getCompanyFrequency() {
		
		Long companyId = UserContext.getClientAdminId();
		List<PayslipFrequencyDTO> paySlipFreq = dataImportLogic.getPayslipFrequency(companyId);
		if(paySlipFreq.isEmpty())
		{
			return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("data.info.nodata", new Object[] {}, UserContext.getLocale()).toString()),HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(paySlipFreq, HttpStatus.OK);
	}
	
	@Override
	@GetMapping(value = "templates")
	public ResponseEntity<?> getTemplateList(@RequestParam (value = "entityId", required=true) Long entityId) {
		Long companyId = UserContext.getClientAdminId();
		List<ExcelImportToolForm> templateList = dataImportLogic.getTemplateList(entityId, companyId);
		if(templateList == null && templateList.isEmpty())
		{
			return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("data.info.nodata", new Object[] {}, UserContext.getLocale()).toString()),HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(templateList, HttpStatus.OK);
	}
	
	@Override
	@GetMapping(value = "history")
	public ResponseEntity<?> getHistory() {
		Long companyId = UserContext.getClientAdminId();
		DataImportForm importHistory = dataImportLogic.getImportHistory(companyId);
		if(importHistory.getDataImportHistoryList()!=null && !importHistory.getDataImportHistoryList().isEmpty())
		{
			return new ResponseEntity<>(importHistory.getDataImportHistoryList(), HttpStatus.OK);
		}
		return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("data.info.nodata", new Object[] {}, UserContext.getLocale()).toString()),HttpStatus.NOT_FOUND);
	}
	
	
	@Override
	@GetMapping(value = "company-address")
	public ResponseEntity<?> getCompanyAddress() {
		Long companyId = UserContext.getClientAdminId();
		String companyAddress = dataImportLogic.getCompanyAddress(companyId);
		Map<String, String> companyAddMap = new HashMap<String, String>();
		companyAddMap.put("companyAddress", companyAddress);
		return new ResponseEntity<>(companyAddMap, HttpStatus.OK);
	}
	
	
	@Override
	@GetMapping(value = "company-address-mapping")
	public ResponseEntity<?> getCompanyAddressMapping() {
		Long companyId = UserContext.getClientAdminId();
		List<EmployeeFilterListForm> filterList = dataImportLogic
				.getCompanyAddressMappingList(companyId);
		return new ResponseEntity<>(filterList, HttpStatus.OK);
	}
	
	
	@Override
	@GetMapping(value = "company-filter")
	public ResponseEntity<?> getCompanyFilterList() {
		Long companyId = UserContext.getClientAdminId();
		Long employeeId = Long.parseLong(UserContext.getUserId());
		List<EmployeeFilterListForm> filterList = dataImportLogic
				.getCompanyFilterList(companyId, employeeId);
		if(filterList !=null && !filterList.isEmpty())
		{
			return new ResponseEntity<>(filterList, HttpStatus.OK);
		}
		return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("data.info.nodata", new Object[] {}, UserContext.getLocale()).toString()),HttpStatus.NOT_FOUND);
	}
	
	@Override
	@PostMapping(value = "configure-company-address")
	public ResponseEntity<?> configureCompanyAddress(@RequestParam(value = "dataDictionaryIds", required = false) String[] dataDictionaryIds) {
		
		Long companyId = UserContext.getClientAdminId();
		String companyAddress = dataImportLogic.configureCompanyAddress(companyId, dataDictionaryIds);
		Map<String, String> companyAddressMap = new HashMap<String, String>();
		companyAddressMap.put("confCompanyAddress", companyAddress);
		return new ResponseEntity<>(companyAddressMap, HttpStatus.OK);
	}
	
	@Override
	@PostMapping(value = "preview-payslip")
	public ResponseEntity<?> previewTextPayslipPdf(@RequestParam("DataImportForm") String jsonStr,
			@RequestParam("files") CommonsMultipartFile files) {
		Long companyId = UserContext.getClientAdminId();
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObj = JSONObject.fromObject(jsonStr, jsonConfig);
		final String fileExt = files.getOriginalFilename().substring(files.getOriginalFilename().lastIndexOf(".") + 1, files.getOriginalFilename().length());
		TextPaySlipListDTO textPaySlipListDTO = null;
		
		String previewPdfFilePath = null;
		if (fileExt.toLowerCase().equals(PayAsiaConstants.FILE_TYPE_TXT)) {

			try {
				if (StringUtils.equalsIgnoreCase(jsonObj.getString("payslipTextFormat"), PayAsiaConstants.PAYSLIP_TEXT_FORMAT_FOR_MALAYSIA)) {
					textPaySlipListDTO = textPayslipUtils
							.readMalaysiaTextPayslip(files);
				} else if (StringUtils.equalsIgnoreCase(jsonObj.getString("payslipTextFormat"),PayAsiaConstants.PAYSLIP_TEXT_FORMAT_FOR_HONGKONG)) {
					textPaySlipListDTO = textPayslipUtils
							.readHongKongTextPayslip(files);
				} else {
					textPaySlipListDTO = textPayslipUtils
							.readSingaporeTextPayslip(files);
				}
				
				DataImportForm dataImportForm = new DataImportForm();
				dataImportForm.setTemplateId(jsonObj.getLong("templateId"));
				dataImportForm.setPayslipFrequencyName(jsonObj.getString("payslipFrequencyName"));
				dataImportForm.setYear(jsonObj.getInt("year"));
				dataImportForm.setMonthId(jsonObj.getLong("monthId"));
				dataImportForm.setPart(jsonObj.getInt("part"));
				dataImportForm.setReleasePayslipWithImport(jsonObj.getBoolean("releasePayslipWithImport"));
				dataImportForm.setPayslipTextFormat(jsonObj.getString("payslipTextFormat"));
				dataImportForm.setCompanyAddress(jsonObj.getString("companyAddress"));
				dataImportForm.setEntityId(jsonObj.getLong("entityId"));
				previewPdfFilePath = dataImportLogic
						.generateSamplePaySlipPDFForPreview(textPaySlipListDTO,
								dataImportForm, companyId);
			} catch (ParseException | IOException e) {
				LOGGER.error(e.getMessage(), e);
			}
						
		}
		return new ResponseEntity<>(previewPdfFilePath, HttpStatus.OK);
	}
	
	
	@Override
	@PostMapping(value = "data-import-status")
	public ResponseEntity<?> dataImportStatus(@RequestParam("files") String files) {
		DataImportForm importLog = null;

		String key = files;
		String status = dataImportUtils.dataImportStatusMap.get(key);

		if ((status != null) && (status != "")
				&& ("SUCCESS".equals(status.toUpperCase()))) {

			dataImportUtils.dataImportStatusMap.put(key, "");

			importLog = dataImportUtils.dataImportLogMap.get(key);
			if (importLog.getDataImportLogList() != null) {
				for (DataImportLogDTO dataImportLogDTO : importLog
						.getDataImportLogList()) {
					try {
						dataImportLogDTO.setFailureType(URLEncoder.encode(
								messageSource.getMessage(
										dataImportLogDTO.getFailureType(),
										new Object[] {}, UserContext.getLocale()), "UTF-8"));
						if (dataImportLogDTO.isFromMessageSource()) {

							if (dataImportLogDTO.getPostParameter() != null) {
								dataImportLogDTO.setRemarks(URLEncoder.encode(
										messageSource.getMessage(
												dataImportLogDTO.getRemarks(),
												dataImportLogDTO
														.getPostParameter(),
														UserContext.getLocale()), "UTF-8"));
							} else {
								dataImportLogDTO.setRemarks(URLEncoder.encode(
										messageSource.getMessage(
												dataImportLogDTO.getRemarks(),
												new Object[] {}, UserContext.getLocale()),
										"UTF-8"));
							}

						}
					} catch (UnsupportedEncodingException
							| NoSuchMessageException exception) {
						LOGGER.error(exception.getMessage(), exception);
						throw new PayAsiaSystemException(
								exception.getMessage(), exception);
					}
				}
			}
			JsonConfig jsonConfig = new JsonConfig();
			JSONObject jsonObject = JSONObject.fromObject(importLog, jsonConfig);
			return new ResponseEntity<>(jsonObject.toString(), HttpStatus.OK);
		}

		return new ResponseEntity<>(new ApiMessageHandler(Type.INFO, HttpStatus.NOT_FOUND.toString(), messageSource.getMessage("data.info.nodata", new Object[] {}, UserContext.getLocale()).toString()),HttpStatus.NOT_FOUND);
	}
	
	
	
	@Override
	@PostMapping(value = "upload-payslip")
	public ResponseEntity<?> uploadPayslip(@RequestParam("dataImportForm") String jsonStr,
			@RequestParam("files") CommonsMultipartFile files) {
		   
		final  Long companyId = UserContext.getClientAdminId();
			JsonConfig jsonConfig = new JsonConfig();
			JSONObject jsonObj = JSONObject.fromObject(jsonStr, jsonConfig);
		
			final DataImportForm dataImportForm = new DataImportForm();
			dataImportForm.setTemplateId(jsonObj.getLong("templateId"));
			dataImportForm.setPayslipFrequencyName(jsonObj.getString("payslipFrequency"));
			dataImportForm.setYear(jsonObj.getInt("year"));
			dataImportForm.setMonthId(jsonObj.getLong("monthId"));
			dataImportForm.setPart(jsonObj.getInt("part"));
			dataImportForm.setReleasePayslipWithImport(jsonObj.getBoolean("releasePayslipWithImport"));
			dataImportForm.setPayslipTextFormat(jsonObj.getString("payslipTextFormat"));
			dataImportForm.setCompanyAddress(jsonObj.getString("companyAddress"));
			dataImportForm.setAddCompanyLogoInPdf(jsonObj.getBoolean("addCompanyLogoInPdf"));
			dataImportForm.setLogoAlignment(jsonObj.getString("logoAlignment"));
			dataImportForm.setFileNameContainsCompNameForPdf(jsonObj.getBoolean("fileNameContainsCompNameForPdf"));
			dataImportForm.setEntityId(jsonObj.getLong("entityId"));
			dataImportForm.setCompanyAddressBelowLogo(jsonObj.getBoolean("companyAddressBelowLogo"));
			dataImportForm.setScheduleDate(jsonObj.getString("scheduleDate"));
			dataImportForm.setScheduleTime(jsonObj.getString("scheduleTime"));
			
			
			String excelFileName = files.getOriginalFilename();
			dataImportForm.setFileName(files);
			final String fileExt = excelFileName.substring(
					excelFileName.lastIndexOf(".") + 1, excelFileName.length());

			final String modifiedfileName = new String(
					Base64.encodeBase64((files.getOriginalFilename() + DateUtils
							.convertCurrentDateTimeWithMilliSecToString(PayAsiaConstants.TEMP_FILE_TIMESTAMP_WITH_MILLISEC_FORMAT))
							.getBytes()));
			boolean isValidFile  = FileUtils.isValidFile(dataImportForm.getFileName(), excelFileName,PayAsiaConstants.ALLOWED_STANDARD_UPLOAD_FILE_EXT, PayAsiaConstants.ALLOWED_STANDARD_UPLOAD_FILE_MINE_TYPE, PayAsiaConstants.FILE_SIZE);
		
			if(!isValidFile){
				List<DataImportLogDTO> errors = new ArrayList<DataImportLogDTO>();
				DataImportForm importLogs = new DataImportForm();
				DataImportLogDTO error = new DataImportLogDTO();
				error.setFailureType("payasia.data.import.invalid.template");
				error.setRemarks("Please verify the template");
				error.setFromMessageSource(false);
				errors.add(error);
				importLogs.setDataImportLogList(errors);
				dataImportUtils.dataImportStatusMap.put(modifiedfileName,"error");
				dataImportUtils.dataImportStatusMap.put(modifiedfileName,"success");
				dataImportUtils.dataImportLogMap.put(modifiedfileName,importLogs);
				
				return new ResponseEntity<>(modifiedfileName, HttpStatus.OK);
			}
			
			boolean payslipReleasedStatus = paySlipDynamicFormLogic
					.getPayslipReleasedStatus(companyId,
							dataImportForm.getMonthId(), dataImportForm.getYear(),
							dataImportForm.getPart());
			String entityName = dataImportLogic.getEntityName(dataImportForm
					.getEntityId());
			if (payslipReleasedStatus
					&& entityName
							.equalsIgnoreCase(PayAsiaConstants.PAY_SLIP_FORM_ENTITY_NAME)) {

				List<DataImportLogDTO> errors = new ArrayList<DataImportLogDTO>();
				DataImportForm importLogs = new DataImportForm();
				DataImportLogDTO error = new DataImportLogDTO();

				error.setFailureType("payasia.employee.payslip");
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.MONTH, Integer.parseInt(String
						.valueOf(dataImportForm.getMonthId())) - 1);
				String month = calendar.getDisplayName(Calendar.MONTH,
						Calendar.LONG, Locale.getDefault());
				Object[] payslipDetailsArr = new Object[3];
				payslipDetailsArr[0] = month;
				payslipDetailsArr[1] = dataImportForm.getYear();
				payslipDetailsArr[2] = dataImportForm.getPart();
				error.setPostParameter(payslipDetailsArr);
				error.setRemarks("payasia.data.import.payslip.released.status.msg");
				error.setFromMessageSource(true);

				errors.add(error);

				importLogs.setDataImportLogList(errors);
				dataImportUtils.dataImportStatusMap.put(modifiedfileName, "error");
				dataImportUtils.dataImportStatusMap
						.put(modifiedfileName, "success");

				dataImportUtils.dataImportLogMap.put(modifiedfileName, importLogs);
				return new ResponseEntity<>(modifiedfileName,HttpStatus.OK);
			}

			DataImportForm readData = new DataImportForm();
			TextPaySlipListDTO textPaySlipListDTO = null;
			List<String> zipFileNames = null;
			DataImportLogDTO zipFileDataImportSummaryDTO = null;

			if (fileExt.toLowerCase().equals(PayAsiaConstants.FILE_TYPE_XLS)) {
				readData = ExcelUtils.getImportedData(dataImportForm.getFileName());
			} else if (fileExt.toLowerCase()
					.equals(PayAsiaConstants.FILE_TYPE_XLSX)) {
				readData = ExcelUtils.getImportedDataForXLSX(dataImportForm
						.getFileName());
			} else if (fileExt.toLowerCase().equals(PayAsiaConstants.FILE_TYPE_TXT)) {

				try {

					if (dataImportForm.getPayslipTextFormat().equalsIgnoreCase(
							PayAsiaConstants.PAYSLIP_TEXT_FORMAT_FOR_MALAYSIA)) {
						textPaySlipListDTO = textPayslipUtils
								.readMalaysiaTextPayslip(dataImportForm
										.getFileName());
					} else if (dataImportForm
							.getPayslipTextFormat()
							.equalsIgnoreCase(
									PayAsiaConstants.PAYSLIP_TEXT_FORMAT_FOR_HONGKONG)) {
						textPaySlipListDTO = textPayslipUtils
								.readHongKongTextPayslip(dataImportForm
										.getFileName());
					} else if (dataImportForm
							.getPayslipTextFormat()
							.equalsIgnoreCase(
									PayAsiaConstants.PAYSLIP_TEXT_FORMAT_FOR_INDONESIA)) {
						textPaySlipListDTO = textPayslipUtils
								.readIndonesiaTextPayslip(dataImportForm
										.getFileName());
					} else if (dataImportForm
							.getPayslipTextFormat()
							.equalsIgnoreCase(
									PayAsiaConstants.PAYSLIP_TEXT_FORMAT_FOR_SINGAPORE_PAYMENT_MODE_CASH)) {
						textPaySlipListDTO = textPayslipUtils
								.readSingaporeTextPayslipPaymentModeCash(dataImportForm
										.getFileName());
					} else {
						textPaySlipListDTO = textPayslipUtils
								.readSingaporeTextPayslip(dataImportForm
										.getFileName());
					}
				} catch (Exception e) {
					LOGGER.error(e.getMessage(), e);
					List<DataImportLogDTO> errors = new ArrayList<DataImportLogDTO>();
					DataImportForm importLogs = new DataImportForm();
					DataImportLogDTO error = new DataImportLogDTO();

					error.setFailureType("payasia.data.import.invalid.template");
					error.setRemarks("Please verify the template");
					error.setFromMessageSource(false);

					errors.add(error);

					importLogs.setDataImportLogList(errors);
					dataImportUtils.dataImportStatusMap.put(modifiedfileName,
							"error");
					dataImportUtils.dataImportStatusMap.put(modifiedfileName,
							"success");

					dataImportUtils.dataImportLogMap.put(modifiedfileName,
							importLogs);
					return new ResponseEntity<>(modifiedfileName, HttpStatus.OK);
				}
			} else if (fileExt.toLowerCase().equals(PayAsiaConstants.FILE_TYPE_ZIP)) {
				/*
				 * String filePath = downloadPath + "/company/" + companyId + "/" +
				 * PayAsiaConstants.COMPANY_DOCUMENT_PAYSLIP + "/" +
				 * dataImportForm.getYear() + "/";
				 */

				FilePathGeneratorDTO filePathGenerator = fileUtils
						.getFileCommonPath(downloadPath, rootDirectoryName,
								companyId,
								PayAsiaConstants.COMPANY_DOCUMENT_PAYSLIP, null,
								String.valueOf(dataImportForm.getYear()), null,
								null, PayAsiaConstants.PAYASIA_DOC_UPLOAD_EVENT, 0);
				String filePath = fileUtils.getGeneratedFilePath(filePathGenerator);

				List<CompanyDocumentLogDTO> documentLogs = new ArrayList<CompanyDocumentLogDTO>();
				zipFileDataImportSummaryDTO = dataImportLogic
						.uploadPDFZipFileForPayslips(dataImportForm.getFileName(),
								filePath, fileExt.toLowerCase(), companyId,
								documentLogs, dataImportForm);

			}

			final TextPaySlipListDTO textPaySlipListDTOCopy = textPaySlipListDTO;
			final DataImportForm readDataCopy = readData;
			final DataImportLogDTO zipFileDataImportSummaryDTOCopy = zipFileDataImportSummaryDTO;

			Runnable runnable = new Runnable() {
				@Override
				public void run() {

					DataImportForm importLogs = new DataImportForm();

					dataImportUtils.dataImportStatusMap.put(modifiedfileName,
							"processing");

					if (fileExt.toLowerCase()
							.equals(PayAsiaConstants.FILE_TYPE_XLS)
							|| fileExt.toLowerCase().equals(
									PayAsiaConstants.FILE_TYPE_XLSX)) {
						try {
							importLogs = dataImportLogic.importFile(dataImportForm,
									companyId, readDataCopy);
						} catch (PayAsiaRollBackDataException ex) {
							LOGGER.error(ex.getMessage(), ex);
							importLogs.setDataImportLogList(ex.getErrors());
						} catch (Exception e) {
							LOGGER.error(e.getMessage(), e);
							List<DataImportLogDTO> errors = new ArrayList<DataImportLogDTO>();

							DataImportLogDTO error = new DataImportLogDTO();

							error.setFailureType("payasia.record.error");
							error.setRemarks("payasia.record.error");
							error.setFromMessageSource(false);

							errors.add(error);

							importLogs.setDataImportLogList(errors);
							dataImportUtils.dataImportStatusMap.put(
									modifiedfileName, "error");
						}
					} else if (fileExt.toLowerCase().equals(
							PayAsiaConstants.FILE_TYPE_TXT)) {

						try {
							DataImportLogDTO dataImportLogDTO = dataImportLogic
									.generateTextPaySlipPDF(textPaySlipListDTOCopy,
											dataImportForm, companyId);
							importLogs.setDataImportLogSummaryDTO(dataImportLogDTO);
							importLogs.setImportTypeTextPdfFile(true);
							dataImportUtils.dataImportStatusMap.put(
									modifiedfileName, "success");
						} catch (Exception e) {
							LOGGER.error(e.getMessage(), e);
							List<DataImportLogDTO> errors = new ArrayList<DataImportLogDTO>();
							DataImportLogDTO error = new DataImportLogDTO();

							error.setFailureType("payasia.data.import.invalid.template");
							error.setRemarks("Please verify the template");
							error.setFromMessageSource(false);

							errors.add(error);

							importLogs.setDataImportLogList(errors);
							importLogs.setImportTypeTextPdfFile(true);
							dataImportUtils.dataImportStatusMap.put(
									modifiedfileName, "error");
						}

					} else if (fileExt.toLowerCase().equals(
							PayAsiaConstants.FILE_TYPE_ZIP)) {

						try {
							dataImportLogic.uploadPaySlipPDF(dataImportForm,
									zipFileDataImportSummaryDTOCopy
											.getZipFileNamesList(), companyId);
							DataImportLogDTO dataImportLogDTO = new DataImportLogDTO();
							dataImportLogDTO.setImportTypeTextPdfFile(true);
							dataImportLogDTO
									.setTotalRecords(zipFileDataImportSummaryDTOCopy
											.getTotalRecords());
							dataImportLogDTO
									.setTotalSuccessRecords(zipFileDataImportSummaryDTOCopy
											.getZipFileNamesList().size());
							dataImportLogDTO
									.setTotalFailedRecords(zipFileDataImportSummaryDTOCopy
											.getInvalidEmployeeNumbersList().size());
							dataImportLogDTO
									.setInvalidEmployeeNumbersList(zipFileDataImportSummaryDTOCopy
											.getInvalidEmployeeNumbersList());
							importLogs.setDataImportLogSummaryDTO(dataImportLogDTO);
							importLogs.setImportTypeTextPdfFile(true);
							dataImportUtils.dataImportStatusMap.put(
									modifiedfileName, "success");
						} catch (Exception e) {
							LOGGER.error(e.getMessage(), e);
							List<DataImportLogDTO> errors = new ArrayList<DataImportLogDTO>();
							DataImportLogDTO error = new DataImportLogDTO();

							error.setFailureType("payasia.data.import.invalid.file");
							error.setRemarks("Please verify the uploaded file");
							error.setFromMessageSource(false);

							errors.add(error);

							importLogs.setDataImportLogList(errors);
							importLogs.setImportTypeTextPdfFile(true);
							DataImportUtils.dataImportStatusMap.put(
									modifiedfileName, "error");
						}

					}

					DataImportUtils.dataImportStatusMap.put(modifiedfileName,
							"success");
					DataImportUtils.dataImportLogMap.put(modifiedfileName,
							importLogs);
					String statusMsg = DataImportUtils.dataImportStatusMap.get(modifiedfileName);
					
					if(statusMsg!=null && statusMsg.equalsIgnoreCase("success"))
					{
						//validImportMsg();
						DataImportUtils.dataImportStatusMap.get(modifiedfileName);
					}
															
				}
			};
			Thread thread = new Thread(runnable, modifiedfileName);
			thread.start();
			return new ResponseEntity<>(modifiedfileName, HttpStatus.OK);
			
			
	}
	
		
	
	@Override
	@GetMapping(value = "company-part")
	public ResponseEntity<?> getCompanyPart() {
		Long companyId = UserContext.getClientAdminId();
		 Company company = companyDAO.findById(companyId);
		Map<String, Integer> companyPartMap = new HashMap<String, Integer>();
		companyPartMap.put("companyPart", company.getPart());
		return new ResponseEntity<>(companyPartMap, HttpStatus.OK);
	}
}
