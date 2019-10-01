package com.payasia.api.leave.impl;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.ServletContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.payasia.api.hris.model.Filters;
import com.payasia.api.hris.model.SearchParam;
import com.payasia.api.leave.EmployeeLeaveReportsApi;
import com.payasia.api.utils.ApiUtils;
import com.payasia.api.utils.SearchSortUtils;
import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.EmployeeLeaveSchemeTypeDTO;
import com.payasia.common.dto.LeaveReportDTO;
import com.payasia.common.dto.SearchSortDTO;
import com.payasia.common.dto.SelectOptionDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.EmployeeFilterListForm;
import com.payasia.common.form.LeavePreferenceForm;
import com.payasia.common.form.LeaveReportsForm;
import com.payasia.common.form.LeaveReportsResponse;
import com.payasia.common.util.ExcelUtils;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.logic.LeaveBalanceSummaryLogic;
import com.payasia.logic.LeaveReportsLogic;
import com.payasia.logic.ManageUserLogic;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.jxls.exception.ParsePropertyException;
import net.sf.jxls.transformer.XLSTransformer;

/**
 * @author gaurav
 * @param : This class is used to build LeaveReprt related APIs.
*/
@RestController
@RequestMapping(value = ApiUtils.API_ROOT_PATH_FOR_EMPLOYEE_LEAVE+"/leaveReports" ,  produces = {MediaType.APPLICATION_JSON_UTF8_VALUE} )
public class EmployeeLeaveReportsApiImpl implements EmployeeLeaveReportsApi{

	private static final Logger LOGGER = Logger.getLogger(EmployeeLeaveReportsApiImpl.class);
	
	@Resource
	private LeaveReportsLogic leaveReportsLogic;
	
	@Value("#{payasiaptProperties['payasia.temp.path']}")
	private String PAYASIA_TEMP_PATH;

	@Value("#{payasiaptProperties['payasia.document.converter.path']}")
	private String DOCUMENT_CONVERTOR_PATH;
	
	@Autowired
	private ServletContext servletContext;
	
	@Resource
	private LeaveBalanceSummaryLogic leaveBalanceSummaryLogic;
	
	@Autowired
	private MessageSource messageSource;
	
	@Resource
	private ManageUserLogic manageUserLogic;
	
	@Autowired
	private SearchSortUtils searchSortUtils;
	
	@Override
	@GetMapping(value = "leave-type-list")
	public ResponseEntity<?> getLeaveTypeList() {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		List<LeaveReportsForm> leaveSchemeList = leaveReportsLogic.getLeaveTypeList(companyId);
		return new ResponseEntity<>(leaveSchemeList, HttpStatus.OK);
	}

	@Override
	@GetMapping(value = "leave-report-list")
	public ResponseEntity<?> getLeaveReportList() {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		String companyCode =  UserContext.getCompanyCode();
		boolean isAdmin = false;
		List<SelectOptionDTO> leaveReportList = leaveReportsLogic.getLeaveReportList(companyId, companyCode, isAdmin);
		return new ResponseEntity<>(leaveReportList, HttpStatus.OK);
	}
	
	@Override
	@GetMapping(value = "leave-preference-detail")
	public ResponseEntity<?> getLeavePreferenceDetail() {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		String companyCode =  UserContext.getCompanyCode();
		boolean isAdmin = false;
		LeavePreferenceForm leavePreferenceForm = leaveReportsLogic.getLeavePreferenceDetail(companyId, companyCode, isAdmin);
		return new ResponseEntity<>(leavePreferenceForm, HttpStatus.OK);
	}
	
	@Override
	@GetMapping(value = "leave-transaction-list")
	public ResponseEntity<?> getLeaveTransactionList() {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		
		List<LeaveReportsForm> leaveSchemeList = leaveReportsLogic.getLeaveTransactionList(companyId);
		for (LeaveReportsForm leaveReportsForm : leaveSchemeList) {
			leaveReportsForm.setLeaveTransactionName(messageSource.getMessage(leaveReportsForm.getLeaveTransactionName(), new Object[] {}, UserContext.getLocale()));
		}

		return new ResponseEntity<>(leaveSchemeList, HttpStatus.OK);
	}
	
	@Override
	@GetMapping(value = "employee-filter-list")
	public ResponseEntity<?> getEmployeeFilterList() {
		List<EmployeeFilterListForm> empFilterList = manageUserLogic.getEmployeeFilterList(Long.parseLong(UserContext.getWorkingCompanyId()));
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("empFilterList", empFilterList);
		return new ResponseEntity<>(empFilterList, HttpStatus.OK);
	}
	
	@Override
	@PostMapping(value = "search-employee-for-manager")
	public ResponseEntity<?> searchEmployeeForManager(@RequestBody SearchParam searchParamObj, @RequestParam(value = "metaData", required = false) String metaData) {
		Long empId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		
		SearchSortDTO searchSortDTO = searchSortUtils.getSearchSortObject(searchParamObj);

		Boolean includeResignedEmployees = null;
		String searchCondition = null;
		String searchText = null;
		
		final List<Filters> filterllist = Arrays.asList(searchParamObj.getFilters());
		
		if ((filterllist != null && !filterllist.isEmpty())) {
			switch(filterllist.size()){
			
			case 1:
				if(StringUtils.equalsIgnoreCase(filterllist.get(0).getField(), "includeResignedEmployees")){
					includeResignedEmployees = Boolean.parseBoolean(filterllist.get(0).getValue());
				}
				else{
					switch(filterllist.get(0).getField().toUpperCase()){
					case "EMPNO":
						searchCondition = "empno";
						searchText = filterllist.get(0).getValue();
						break;
						
					case "FIRSTNAME":
						searchCondition = "firstName";
						searchText = filterllist.get(0).getValue();
						break;
						
					case "LASTNAME":
						searchCondition = "lastName";
						searchText = filterllist.get(0).getValue();
						break;
					}
				}
				break;

			case 2:
				if(StringUtils.equalsIgnoreCase(filterllist.get(0).getField(), "includeResignedEmployees")){
					includeResignedEmployees = Boolean.parseBoolean(filterllist.get(0).getValue());
					switch(filterllist.get(1).getField().toUpperCase()){
					case "EMPNO":
						searchCondition = "empno";
						searchText = filterllist.get(1).getValue();
						break;
						
					case "FIRSTNAME":
						searchCondition = "firstName";
						searchText = filterllist.get(1).getValue();
						break;
						
					case "LASTNAME":
						searchCondition = "lastName";
						searchText = filterllist.get(1).getValue();
						break;
					}
				}
				else{
					switch(filterllist.get(0).getField().toUpperCase()){
					case "EMPNO":
						searchCondition = "empno";
						searchText = filterllist.get(0).getValue();
						break;
						
					case "FIRSTNAME":
						searchCondition = "firstName";
						searchText = filterllist.get(0).getValue();
						break;
						
					case "LASTNAME":
						searchCondition = "lastName";
						searchText = filterllist.get(0).getValue();
						break;
					}
					includeResignedEmployees = Boolean.parseBoolean(filterllist.get(1).getValue());
				}
				break;
			}
		}
		
		LeaveReportsResponse leaveReportResponse = leaveReportsLogic.searchEmployeeForManager(searchSortDTO.getPageRequest(), searchSortDTO.getSortCondition(), searchCondition, searchText, 
				companyId, empId, metaData, includeResignedEmployees);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(leaveReportResponse, jsonConfig);
		return new ResponseEntity<>(jsonObject.toString(), HttpStatus.OK);
	}

	@Override
	@PostMapping(value = "show-report-data")
	public ResponseEntity<?> showReportData(@RequestParam (value="requestReportType", required=true) String requestReportType, @RequestBody LeaveReportsForm leaveReportsForm){
	
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		Boolean isManager = true;
		JSONObject jsonObject = new JSONObject();
		LeaveReportDTO leaveDataDTO = new LeaveReportDTO();
		switch (requestReportType.toUpperCase()) {
		case "LBD":
			leaveDataDTO = leaveReportsLogic.showLeaveBalanceAsOnDateReport(companyId, leaveReportsForm, employeeId, leaveReportsForm.getDataDictionaryIds());
			jsonObject.put("aaData", leaveDataDTO.getLeaveBalAsOnDayDTOs());
			jsonObject.put("aoColumns", leaveDataDTO.getLeaveHeaderDTOs());
			break;
			
		case "LTR":
			leaveDataDTO = leaveReportsLogic.showLeaveTranReport(companyId, employeeId, leaveReportsForm, isManager, leaveReportsForm.getDataDictionaryIds());
			jsonObject.put("aaData", leaveDataDTO.getLeaveTranReportDTOs());
			jsonObject.put("aoColumns", leaveDataDTO.getLeaveHeaderDTOs());
			break;
		
		case "YSR":
			leaveDataDTO = leaveReportsLogic.showYearWiseSummaryReport(employeeId, companyId, leaveReportsForm, isManager, leaveReportsForm.getDataDictionaryIds());
			jsonObject.put("aaData", leaveDataDTO.getSummarryDTOs());
			jsonObject.put("aoColumns", leaveDataDTO.getLeaveTypeNames());
			jsonObject.put("dataDictionaryColumns", leaveDataDTO.getDataDictNameList());
			break;
		}
		return new ResponseEntity<>(jsonObject.toString(), HttpStatus.OK);
	}
	
	@Override
	@GetMapping(value = "year-list")
	public ResponseEntity<?> getYearList() {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		boolean isManager = true;
		List<EmployeeLeaveSchemeTypeDTO> yearList = leaveReportsLogic.getDistinctYears(companyId, isManager);
		return new ResponseEntity<>(yearList, HttpStatus.OK);
	}
	
	@Override
	@PostMapping(value = "generate-report-data")
	public ResponseEntity<?> generateReportDataPdfOrExcel(@RequestParam (value="requestReportType", required=true) String requestReportType, @RequestBody LeaveReportsForm leaveReportsForm){
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		Map<String, Object> reportDataMap = new HashMap<>();
		String fileName = null;
		byte[] data = null;
		Boolean isManager = true;
		UUID uuid = UUID.randomUUID();
		LeaveReportDTO leaveReportDTO = new LeaveReportDTO();
		
		switch (requestReportType.toUpperCase()) {
		case "LBD":
			leaveReportDTO = leaveReportsLogic.showLeaveBalanceAsOnDateReport(companyId, leaveReportsForm, employeeId, leaveReportsForm.getDataDictionaryIds());
			data = leaveBalAsOnDayReportPdfOrExcelFileData(leaveReportDTO, leaveReportsForm.getFileType());
			fileName = "Leave Balance As On Day Report";
			break;
			
		case "LTR":
			leaveReportDTO = leaveReportsLogic.showLeaveTranReport(companyId, employeeId, leaveReportsForm, isManager, leaveReportsForm.getDataDictionaryIds());
			List<String> leaveTranList = convertArrayToList(leaveReportsForm.getMultipleLeaveTransactionName());
			data = leaveTranReportPdfOrExcelFileData(companyId, leaveReportDTO,leaveReportsForm, leaveTranList);
			fileName = "Leave Transaction Report";
			break;
		
		case "DLTR":
			leaveReportDTO = leaveReportsLogic.showDayWiseLeaveTranReport(companyId, employeeId, leaveReportsForm, isManager, leaveReportsForm.getDataDictionaryIds());
			data = dayWiseleaveTranReportPdfOrExcelFileData(leaveReportDTO, leaveReportsForm);
			fileName = "Day Wise Leave Transaction Report";
			break;
		
		case "YSR":
			if (!(StringUtils.equalsIgnoreCase("pdf", leaveReportsForm.getFileType())) && StringUtils.isNotBlank(leaveReportsForm.getMultiSheet()) && (leaveReportsForm.getMultiSheet().equals("checked"))) {
				data = genYearWiseSummarryReportExcelFileInMultipleSheets1(leaveReportsForm, leaveReportsForm.getDataDictionaryIds());
				fileName = "Year Wise Summary Report";
			} else {
				leaveReportDTO = leaveReportsLogic.showYearWiseSummaryReport(employeeId, companyId, leaveReportsForm, isManager, leaveReportsForm.getDataDictionaryIds());
				data = yearWiseSummaryReportPDFFileData(leaveReportDTO, leaveReportsForm.getFileType());
				fileName = "Year Wise Summary Report";
			}
			break;
			
		case "LBDC1":
			if(StringUtils.equalsIgnoreCase(leaveReportsForm.getFileType(), "pdf")){
				leaveReportDTO = leaveReportsLogic.genLeaveBalAsOnDayCustReportPDF(companyId, employeeId, leaveReportsForm, isManager);
				data = leaveReportDTO.getLeaveBalAsOnDayCustReportByteFile();
				fileName = "LeaveBalanceAsOnDayCustomEmpPerPage" + uuid;
			}
			break;
		}

		if(StringUtils.equalsIgnoreCase(leaveReportsForm.getFileType(),"pdf")){
			fileName = fileName + ".pdf";
		}
		else{
			fileName = fileName + ".xlsx";			
		}
		reportDataMap.put("data", data);
		reportDataMap.put("fileName", fileName);
		return new ResponseEntity<>(reportDataMap, HttpStatus.OK);
	}
	
	// 1.) LBD PDF data
	private byte[] leaveBalAsOnDayReportPdfOrExcelFileData(LeaveReportDTO leaveDataDTO, String fileType){
		UUID uuid = UUID.randomUUID();
		String destFileNamePDF = null;
		Map<String, Object> beans = new HashMap<String, Object>();
		beans.put("leaveBalAsOnDayReportList", leaveDataDTO.getLeaveBalAsOnDayDTOs());
		beans.put("custFieldColNameList", leaveDataDTO.getDataDictNameList());
		beans.put("leaveTypeNameList", leaveDataDTO.getLeaveTypeNames());
		
		File tempFolder = new File(PAYASIA_TEMP_PATH + "/leavereport/" + uuid);
		tempFolder.mkdirs();
		String templateFileName = servletContext.getRealPath("/resources/LeaveReportTemplate/LeaveBalanceAsOnDayReport.xlsx");
		String destFileNameExcel = PAYASIA_TEMP_PATH + "/LeaveReviewerReport" + uuid + ".xlsx";
		if(StringUtils.equalsIgnoreCase(fileType, "pdf")){
			destFileNamePDF = PAYASIA_TEMP_PATH + "/LeaveReviewerReport" + uuid + ".pdf";
		}

		return commonDataPdfOrExcel(templateFileName, beans, destFileNameExcel, destFileNamePDF, tempFolder);
	}
	
	// 2.) LTR PDF data
	private byte[] leaveTranReportPdfOrExcelFileData(Long companyId, LeaveReportDTO leaveDataDTO, LeaveReportsForm leaveReportsForm, List<String> leaveTranList){
		UUID uuid = UUID.randomUUID();
		String destFileNamePDF = null;
		Map<String, Object> beans = new HashMap<String, Object>();
		beans.put("leaveTransactionReportList", leaveDataDTO.getLeaveTranReportDTOs());

		if (leaveDataDTO.isLeavePreferencePreApproval()) {
			beans.put("leavePreferencePreApproval", 1);
		} else {
			beans.put("leavePreferencePreApproval", 0);
		}
		if (leaveDataDTO.isLeaveExtensionPreference()) {
			beans.put("leaveExtension", 1);
		} else {
			beans.put("leaveExtension", 0);
		}

		if (leaveDataDTO.getDataDictNameList()!=null && leaveDataDTO.getDataDictNameList().size() > 0) {
			beans.put("custFieldColNameList", leaveDataDTO.getDataDictNameList());
		}
		
		File tempFolder = new File(PAYASIA_TEMP_PATH + "/leavereport/" + uuid);
		tempFolder.mkdirs();
		String templateFileName;

		boolean isLeaveUnitDays = leaveBalanceSummaryLogic.isLeaveUnitDays(companyId);
		if (leaveReportsForm.isMultipleRecord()) {
			beans.put("custFieldColNameList", leaveDataDTO.getDataDictNameList());
			templateFileName = servletContext.getRealPath("/resources/LeaveReportTemplate/LeaveTransactionReportMultipleRecord.xlsx");
		} else {

			if (isLeaveUnitDays) {
				// Leave In Days
				if (leaveTranList.contains(PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_APPROVED) || leaveTranList.contains(PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_APPROVED_CANCEL)) {

					if (leaveDataDTO.getDataDictNameList().size() > 0) {
						templateFileName = servletContext.getRealPath("/resources/LeaveReportTemplate/LeaveTransactionReportWithApprovedDate.xlsx");

					} else {
						templateFileName = servletContext.getRealPath("/resources/LeaveReportTemplate/LeaveTranReportWithAppDateWithoutCustomFields.xlsx");
					}

				} else {
					if (leaveDataDTO.getDataDictNameList().size() > 0) {
						templateFileName = servletContext.getRealPath("/resources/LeaveReportTemplate/LeaveTransactionReport.xlsx");
					} else {
						templateFileName = servletContext.getRealPath("/resources/LeaveReportTemplate/LeaveTransactionReportWithoutCustomFields.xlsx");
					}
				}
			} else {
				// Leave In Hours
				if (leaveTranList.contains(PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_APPROVED) || leaveTranList.contains(PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_APPROVED_CANCEL)) {
					if (leaveDataDTO.getDataDictNameList().size() > 0) {
						templateFileName = servletContext.getRealPath("/resources/LeaveReportTemplate/LeaveTransactionReportWithApprovedDateNHours.xlsx");
					} else {
						templateFileName = servletContext.getRealPath("/resources/LeaveReportTemplate/LeaveTranReportWithAppDateNHoursaWOCustomFields.xlsx");
					}
				} else {
					if (leaveDataDTO.getDataDictNameList().size() > 0) {
						templateFileName = servletContext.getRealPath("/resources/LeaveReportTemplate/LeaveTransactionReportWithHours.xlsx");
					} else {
						templateFileName = servletContext.getRealPath("/resources/LeaveReportTemplate/LeaveTransactionReportWithHoursWOCustomFields.xlsx");
					}
				}
			}
		}
		String destFileNameExcel = PAYASIA_TEMP_PATH + "/LeaveTransactionReport" + uuid + ".xlsx";
		if(StringUtils.equalsIgnoreCase(leaveReportsForm.getFileType(), "pdf")){
			destFileNamePDF = PAYASIA_TEMP_PATH + "/LeaveTransactionReport" + uuid + ".pdf";
		}
		
		return commonDataPdfOrExcel(templateFileName, beans, destFileNameExcel, destFileNamePDF, tempFolder);
	}
	
	private List<String> convertArrayToList(String[] stringArr) {
		List<String> leaveTranList = new ArrayList<String>();
		for (String leaveTransaction : stringArr) {
			if (!leaveTransaction.equalsIgnoreCase("0")) {
				leaveTranList.add(leaveTransaction);
			}
		}
		return leaveTranList;
	}
	
	// 3.) DLTR PDF data
	private byte[] dayWiseleaveTranReportPdfOrExcelFileData(LeaveReportDTO leaveDataDTO, LeaveReportsForm leaveReportsForm) {

		UUID uuid = UUID.randomUUID();
		String destFileNamePDF = null;
		String header = "Day Wise Leave Transaction Report Between " + leaveReportsForm.getStartDate() + " And "+ leaveReportsForm.getEndDate();

		Map<String, Object> beans = new HashMap<String, Object>();
		beans.put("header", header);
		beans.put("dayWiseLeaveTranMap", leaveDataDTO.getDayWiseLeaveTranMap());

		if (leaveDataDTO.isLeavePreferencePreApproval()) {
			beans.put("leavePreferencePreApproval", 1);
		} else {
			beans.put("leavePreferencePreApproval", 0);
		}
		if (leaveDataDTO.isLeaveExtensionPreference()) {
			beans.put("leaveExtension", 1);
		} else {
			beans.put("leaveExtension", 0);
		}

		if (leaveDataDTO.getDataDictNameList().size() > 0) {
			beans.put("custFieldColNameList", leaveDataDTO.getDataDictNameList());
		}
		beans.put("leaveCustomDataList", leaveDataDTO.getLeaveCustomDataDTOs());
		
		File tempFolder = new File(PAYASIA_TEMP_PATH + "/leavereport/" + uuid);
		tempFolder.mkdirs();
		String templateFileName = "";
		if (leaveDataDTO.getDataDictNameList().size() > 0) {
			templateFileName = servletContext.getRealPath("/resources/LeaveReportTemplate/DayWiseLeaveTransactionReport.xlsx");

		} else {
			templateFileName = servletContext.getRealPath("/resources/LeaveReportTemplate/DayWiseLeaveTransactionReportWithoutCustomFields.xlsx");
		}

		String destFileNameExcel = PAYASIA_TEMP_PATH + "/DayWiseLeaveTransactionReport" + uuid + ".xlsx";
		if(StringUtils.equalsIgnoreCase(leaveReportsForm.getFileType(), "pdf")){
			destFileNamePDF = PAYASIA_TEMP_PATH + "/DayWiseLeaveTransactionReport" + uuid + ".pdf";
		}
		
		return commonDataPdfOrExcel(templateFileName, beans, destFileNameExcel, destFileNamePDF, tempFolder);
	}
	

	// 4.) YSR PDF data
	private byte[] yearWiseSummaryReportPDFFileData(LeaveReportDTO leaveDataDTO, String fileType){
		UUID uuid = UUID.randomUUID();
		String destFileNamePDF = null;
		Map<String, Object> beans = new HashMap<String, Object>();
		
		beans.put("yearWiseSummaryList", leaveDataDTO.getSummarryDTOs());
		beans.put("leaveTypesList", leaveDataDTO.getLeaveTypeNames());
		beans.put("customFieldsList", leaveDataDTO.getDataDictNameList());
		beans.put("carriedForward", messageSource.getMessage("payasia.carried.forward", new Object[] {}, UserContext.getLocale()));
		beans.put("credited", messageSource.getMessage("payasia.credited", new Object[] {}, UserContext.getLocale()));
		beans.put("enchased", messageSource.getMessage("payasia.encashed", new Object[] {}, UserContext.getLocale()));
		beans.put("forfeited", messageSource.getMessage("payasia.forfeited", new Object[] {}, UserContext.getLocale()));
		beans.put("approved", messageSource.getMessage("payasia.approved", new Object[] {}, UserContext.getLocale()));
		beans.put("closingBalance", messageSource.getMessage("payasia.closing.balance", new Object[] {}, UserContext.getLocale()));
		
		File tempFolder = new File(PAYASIA_TEMP_PATH + "/leavereport/" + uuid);
		tempFolder.mkdirs();
		String templateFileName = servletContext.getRealPath("/resources/LeaveReportTemplate/YearWiseSummaryReport.xlsx");
		String destFileNameExcel = PAYASIA_TEMP_PATH + "/YearWiseSummaryReport" + uuid + ".xlsx";
		if(StringUtils.equalsIgnoreCase(fileType, "pdf")){
			destFileNamePDF = PAYASIA_TEMP_PATH + "/YearWiseSummaryReport" + uuid + ".pdf";
		}
		
		return commonDataPdfOrExcel(templateFileName, beans, destFileNameExcel, destFileNamePDF, tempFolder);
	}
	
	// COMMON METHOD FOR ALL TYPES OF REPORTS(Except LBDC1) FOR GENERARING PDF
	private byte[] commonDataPdfOrExcel(String templateFileName, Map<String, Object> beans, String destFileNameExcel, String destFileNamePDF, File tempFolder){
		XLSTransformer transformer = new XLSTransformer();
		byte[] data;
		File tempDestPdfFile = null;
		File tempDestExcelFile = null;
		
		String destPathFile = destFileNameExcel;
		
		try {
			transformer.transformXLS(templateFileName, beans, destFileNameExcel);
			if (StringUtils.isNotBlank(destFileNamePDF)) {
				ArrayList<String> cmdList = new ArrayList<String>();
				destPathFile = destFileNamePDF;

				String file1 = destFileNameExcel;
				String file2 = destFileNamePDF;

				// cmdList.add(PYTHON_EXE_PATH);
				cmdList.add(DOCUMENT_CONVERTOR_PATH);
				cmdList.add(file1);
				cmdList.add(file2);

				ProcessBuilder processBuilder = new ProcessBuilder(cmdList);

				LOGGER.info("processBuilder.command()   " + processBuilder.command());
				processBuilder.redirectErrorStream(true);
				Process process = processBuilder.start();
				process.waitFor();
			}

		} catch (ParsePropertyException e) {
			LOGGER.error(e);
		} catch (InvalidFormatException e) {
			LOGGER.error(e);
		} catch (IOException e) {
			LOGGER.error(e);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}

		try {
			Path path = Paths.get(destPathFile);
			data = Files.readAllBytes(path);
			
		} catch (IOException ioException) {
			LOGGER.error(ioException.getMessage(), ioException);
			throw new PayAsiaSystemException(ioException.getMessage(), ioException);

		} finally {
			if (StringUtils.isNotBlank(destFileNamePDF)) {
				tempDestPdfFile = new File(destFileNamePDF);
				if (tempDestPdfFile != null) {
					tempDestPdfFile.delete();
				}
			}
			tempDestExcelFile = new File(destFileNameExcel);
			if (tempDestExcelFile != null) {
				tempDestExcelFile.delete();
			}
			tempFolder.delete();
		}
		return data;
	}
	
	/*
	  YSR --> (Excel + MultiCheck)
	 */
	private byte[] genYearWiseSummarryReportExcelFileInMultipleSheets1(LeaveReportsForm leaveReportsForm, String[] dataDictionaryIds) {
		SimpleDateFormat simpledateFormat = new SimpleDateFormat("yyyyMMdd_hhmm");

		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		Boolean isManager = true;
		Locale locale = UserContext.getLocale();
		LeaveReportDTO leaveDataDTO = leaveReportsLogic.showYearWiseSummaryReport(employeeId, companyId, leaveReportsForm, isManager, dataDictionaryIds);

		Map<String, Object> beans = new HashMap<String, Object>();
		beans.put("yearWiseSummaryList", leaveDataDTO.getSummarryDTOs());
		beans.put("leaveTypesList", leaveDataDTO.getLeaveTypeNames());
		if (leaveDataDTO.getDataDictNameList().size() > 0) {
			beans.put("customFieldsList", leaveDataDTO.getDataDictNameList());
		}
		beans.put("carriedForward", messageSource.getMessage("payasia.carried.forward", new Object[] {}, locale));
		beans.put("credited", messageSource.getMessage("payasia.credited", new Object[] {}, locale));
		beans.put("enchased", messageSource.getMessage("payasia.encashed", new Object[] {}, locale));
		beans.put("forfeited", messageSource.getMessage("payasia.forfeited", new Object[] {}, locale));
		beans.put("approved", messageSource.getMessage("payasia.approved", new Object[] {}, locale));
		beans.put("closingBalance", messageSource.getMessage("payasia.closing.balance", new Object[] {}, locale));

		XLSTransformer transformer = new XLSTransformer();
		Calendar calendar = Calendar.getInstance();
		String currentDate = simpledateFormat.format(calendar.getTime());
		File tempFolder = new File(PAYASIA_TEMP_PATH + "/leavereport/" + currentDate);
		tempFolder.mkdirs();

		String templateFileName = "";
		if (leaveDataDTO.getDataDictNameList().size() > 0) {
			templateFileName = servletContext.getRealPath("/resources/LeaveReportTemplate/YearWiseSummaryReportMultipleSheets.xlsx");
		} else {
			templateFileName = servletContext.getRealPath("/resources/LeaveReportTemplate/YearWiseSummaryReportMultipleSheetsWithOutCustField.xlsx");
		}

		InputStream is = null;
		Workbook resultWorkbook = null;
		List<String> sheetNames = new ArrayList<>();
		byte[] bytes = null;
		
		try {
			is = new BufferedInputStream(new FileInputStream(templateFileName));
			for (String leaveTypeName : leaveDataDTO.getLeaveTypeNames()) {
				sheetNames.add(ExcelUtils.getSheetSafeName(leaveTypeName));
			}
			resultWorkbook = transformer.transformMultipleSheetsList(is, leaveDataDTO.getLeaveTypeNames(), sheetNames, "sheetName", beans, 0);
			
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			try {
				resultWorkbook.write(bos);
			} finally {
			    bos.close();
			}
			bytes = bos.toByteArray();

		} catch (ParsePropertyException e) {
			LOGGER.error(e);
		} catch (InvalidFormatException e) {
			LOGGER.error(e);
		} catch (IOException e) {
			LOGGER.error(e);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		tempFolder.delete();
		return bytes;
	}
}
