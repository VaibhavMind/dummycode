package com.payasia.web.controller.claim.impl;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.AddClaimDTO;
import com.payasia.common.dto.ClaimDetailsReportDTO;
import com.payasia.common.dto.ClaimFormPdfDTO;
import com.payasia.common.dto.ClaimReportHeaderDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.AddClaimForm;
import com.payasia.common.form.AddClaimFormResponse;
import com.payasia.common.form.ClaimApplicationItemAttach;
import com.payasia.common.form.ClaimPreferenceForm;
import com.payasia.common.form.ClaimReportsForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaURLConstant;
import com.payasia.common.util.ResponseDataConverter;
import com.payasia.logic.AddClaimLogic;
import com.payasia.logic.ClaimReportsLogic;
import com.payasia.logic.GeneralLogic;
import com.payasia.logic.MyClaimLogic;
import com.payasia.web.controller.claim.MyClaimController;
import com.payasia.web.util.PayAsiaSessionAttributes;

import net.sf.json.JSONObject;
import net.sf.jxls.exception.ParsePropertyException;
import net.sf.jxls.transformer.XLSTransformer;

@Controller
@RequestMapping(value = PayAsiaURLConstant.EMPLOYEE_MY_CLAIM)
public class MyClaimControllerImpl implements MyClaimController {

	/** The Constant logger. */
	private static final Logger LOGGER = Logger.getLogger(MyClaimControllerImpl.class);
	@Resource
	MyClaimLogic myClaimLogic;

	@Resource
	AddClaimLogic addClaimLogic;
	
	@Resource
	ClaimReportsLogic claimReportsLogic;
	@Resource
	GeneralLogic generalLogic;
	
	@Autowired
	private ServletContext servletContext;
	
	/** The payasia temp path. */
	@Value("#{payasiaptProperties['payasia.temp.path']}")
	private String PAYASIA_TEMP_PATH;

	@Value("#{payasiaptProperties['payasia.document.converter.path']}")
	private String DOCUMENT_CONVERTOR_PATH;

	@Override
	@RequestMapping(value = PayAsiaURLConstant.GET_PENDING_CLAIMS, method = RequestMethod.POST)
	@ResponseBody
	public String getPendingClaims(@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "fromDate", required = true) String fromDate,
			@RequestParam(value = "toDate", required = true) String toDate,
			@RequestParam(value = "searchCondition", required = false) String searchCondition,
			@RequestParam(value = "searchText", required = false) String searchText,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows, Locale locale) {
		
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		AddClaimDTO claimDTO = new AddClaimDTO();
		claimDTO.setCompanyId(companyId);
		claimDTO.setEmployeeId(employeeId);
		claimDTO.setFromDate(fromDate);
		claimDTO.setToDate(toDate);
		claimDTO.setSearchCondition(searchCondition);
		claimDTO.setSearchText(searchText);
		claimDTO.setLocale(locale);
		
		AddClaimFormResponse addClaimFormResponse = myClaimLogic.getPendingClaims(claimDTO, pageDTO, sortDTO);

		return ResponseDataConverter.getJsonURLEncodedData(addClaimFormResponse);
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.GET_SUBMITTED_CLAIMS, method = RequestMethod.POST)
	@ResponseBody
	public String getSubmittedClaims(@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "fromDate", required = true) String fromDate,
			@RequestParam(value = "toDate", required = true) String toDate,
			@RequestParam(value = "searchCondition", required = false) String searchCondition,
			@RequestParam(value = "searchText", required = false) String searchText,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows, HttpServletRequest request) {

		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);
		
		AddClaimDTO claimDTO = new AddClaimDTO();
		claimDTO.setCompanyId(companyId);
		claimDTO.setEmployeeId(employeeId);
		claimDTO.setFromDate(fromDate);
		claimDTO.setToDate(toDate);
		claimDTO.setSearchCondition(searchCondition);
		claimDTO.setSearchText(searchText);
		claimDTO.setPageContextPath(request.getContextPath());
		
		AddClaimFormResponse addClaimFormResponse = myClaimLogic.getSubmittedClaims(claimDTO, pageDTO, sortDTO);

		return ResponseDataConverter.getJsonURLEncodedData(addClaimFormResponse);
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.GET_APPROVED_CLAIMS, method = RequestMethod.POST)
	@ResponseBody
	public String getApprovedClaims(@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "fromDate", required = true) String fromDate,
			@RequestParam(value = "toDate", required = true) String toDate,
			@RequestParam(value = "searchCondition", required = false) String searchCondition,
			@RequestParam(value = "searchText", required = false) String searchText,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows, HttpServletRequest request) {
		
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);
		
		AddClaimDTO claimDTO = new AddClaimDTO();
		claimDTO.setCompanyId(companyId);
		claimDTO.setEmployeeId(employeeId);
		claimDTO.setFromDate(fromDate);
		claimDTO.setToDate(toDate);
		claimDTO.setSearchCondition(searchCondition);
		claimDTO.setSearchText(searchText);
		claimDTO.setPageContextPath(request.getContextPath());
		
		AddClaimFormResponse addClaimFormResponse = myClaimLogic.getApprovedClaims(claimDTO, pageDTO, sortDTO);

		return ResponseDataConverter.getJsonURLEncodedData(addClaimFormResponse);
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.GET_REJECTED_CLAIMS, method = RequestMethod.POST)
	@ResponseBody
	public String getRejectedClaims(@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "fromDate", required = true) String fromDate,
			@RequestParam(value = "toDate", required = true) String toDate,
			@RequestParam(value = "searchCondition", required = false) String searchCondition,
			@RequestParam(value = "searchText", required = false) String searchText,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows, HttpServletRequest request) {

		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);
		
		AddClaimDTO claimDTO = new AddClaimDTO();
		claimDTO.setCompanyId(companyId);
		claimDTO.setEmployeeId(employeeId);
		claimDTO.setFromDate(fromDate);
		claimDTO.setToDate(toDate);
		claimDTO.setSearchCondition(searchCondition);
		claimDTO.setSearchText(searchText);
		claimDTO.setPageContextPath(request.getContextPath());
		
		AddClaimFormResponse addClaimFormResponse = myClaimLogic.getRejectedClaims(claimDTO, pageDTO,
				sortDTO);

		return ResponseDataConverter.getJsonURLEncodedData(addClaimFormResponse);
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.GET_WITHDRAWN_CLAIMS, method = RequestMethod.POST)
	@ResponseBody
	public String getWithdrawnClaims(@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "fromDate", required = true) String fromDate,
			@RequestParam(value = "toDate", required = true) String toDate,
			@RequestParam(value = "searchCondition", required = false) String searchCondition,
			@RequestParam(value = "searchText", required = false) String searchText,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows, HttpServletRequest request) {
		
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		AddClaimDTO claimDTO = new AddClaimDTO();
		claimDTO.setCompanyId(companyId);
		claimDTO.setEmployeeId(employeeId);
		claimDTO.setFromDate(fromDate);
		claimDTO.setToDate(toDate);
		claimDTO.setSearchCondition(searchCondition);
		claimDTO.setSearchText(searchText);
		claimDTO.setPageContextPath(request.getContextPath());
		
		AddClaimFormResponse addClaimFormResponse = myClaimLogic.getWithdrawnClaims(claimDTO, pageDTO, sortDTO);

		return ResponseDataConverter.getJsonURLEncodedData(addClaimFormResponse);
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.GET_ALL_CLAIMS, method = RequestMethod.POST)
	@ResponseBody
	public String getAllClaims(@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "searchCondition", required = false) String searchCondition,
			@RequestParam(value = "searchText", required = false) String searchText,
			@RequestParam(value = "transactionType", required = false) String transactionType,
			@RequestParam(value = "fromDate", required = true) String fromDate,
			@RequestParam(value = "toDate", required = true) String toDate,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows, HttpServletRequest request, Locale locale) {
		
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);
		String pageContextPath = request.getContextPath();
		
		AddClaimDTO claimDTO = new AddClaimDTO();
		claimDTO.setCompanyId(companyId);
		claimDTO.setEmployeeId(employeeId);
		claimDTO.setFromDate(fromDate);
		claimDTO.setToDate(toDate);
		claimDTO.setSearchCondition(searchCondition);
		claimDTO.setSearchText(searchText);
		claimDTO.setLocale(locale);
		claimDTO.setTransactionType(transactionType);
		claimDTO.setPageContextPath(pageContextPath);
		
		AddClaimFormResponse addClaimFormResponse = myClaimLogic.getAllClaims(claimDTO, pageDTO, sortDTO);

		return ResponseDataConverter.getJsonURLEncodedData(addClaimFormResponse);
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.GET_CLAIMS_APPLICATION_DATA, method = RequestMethod.POST)
	@ResponseBody
	public String getClaimApplicationData(@ModelAttribute("claimApplicationId") Long claimApplicationId, HttpServletRequest request, Locale locale) {

		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		
		AddClaimDTO claimDTO = new AddClaimDTO();
		claimDTO.setCompanyId(companyId);
		claimDTO.setEmployeeId(employeeId);
		claimDTO.setClaimApplicationId(FormatPreserveCryptoUtil.decrypt(claimApplicationId));
		claimDTO.setLocale(locale);
		claimDTO.setAdmin(false);
	
		AddClaimForm addClaimForm = myClaimLogic.getClaimApplicationData(claimDTO);
		if (addClaimForm == null) {
			return null;
		}
		return ResponseDataConverter.getJsonURLEncodedData(addClaimForm);
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.SEARCH_CLAIM_TEMPLATE_ITEMS, method = RequestMethod.POST)
	@ResponseBody
	public String searchClaimTemplateItems(@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "claimApplicationId", required = false) Long claimApplicationId,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows, HttpServletRequest request,
			Locale locale) {

		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);
		
		AddClaimDTO claimDTO = new AddClaimDTO();
		claimDTO.setCompanyId(companyId);
		claimDTO.setEmployeeId(employeeId);
		claimDTO.setClaimApplicationId(FormatPreserveCryptoUtil.decrypt(claimApplicationId));
		claimDTO.setLocale(locale);
		claimDTO.setAdmin(false);
	
		AddClaimFormResponse addClaimFormResponse = myClaimLogic.searchClaimTemplateItems(claimDTO
				,pageDTO, sortDTO, null);

		return ResponseDataConverter.getJsonURLEncodedData(addClaimFormResponse);
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.GET_CLAIM_PREFERENCES, method = RequestMethod.POST)
	@ResponseBody
	public String getClaimPreferences(HttpServletRequest request) {
		
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		
		ClaimPreferenceForm claimPreferenceForm = myClaimLogic.getClaimPreferences(companyId);

		return ResponseDataConverter.getObjectToJson(claimPreferenceForm);

	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.PRINT_CLAIM_APPLICATION_FORM, method = RequestMethod.GET)
	public @ResponseBody byte[] printClaimApplicationFormEmp(
			@RequestParam(value = "claimApplicationId", required = true) Long claimApplicationId,
			HttpServletResponse response, HttpServletRequest request) {
		UUID uuid = UUID.randomUUID();
		
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		
		AddClaimDTO claimDTO = new AddClaimDTO();
		claimDTO.setCompanyId(companyId);
		claimDTO.setEmployeeId(employeeId);
		claimDTO.setClaimApplicationId(FormatPreserveCryptoUtil.decrypt(claimApplicationId));
		claimDTO.setAdmin(false);
		claimDTO.setHasLundinTimesheetModule((boolean)request.getSession()
				.getAttribute(PayAsiaSessionAttributes.COMPANY_HAS_LUNDIN_TIMESHEET_MODULE));
	
		ClaimFormPdfDTO claimFormPdfDTO = myClaimLogic.generateClaimFormPrintPDF(claimDTO);

		response.reset();
		String mimeType = URLConnection.guessContentTypeFromName(
				claimFormPdfDTO.getEmployeeNumber() + "_" + claimFormPdfDTO.getClaimTemplateName() + uuid + ".pdf");
		response.setContentType("application/" + mimeType);
		response.setContentLength(claimFormPdfDTO.getClaimFormPdfByteFile().length);
		String filename = claimFormPdfDTO.getEmployeeNumber() + "_" + claimFormPdfDTO.getClaimTemplateName() + uuid
				+ ".pdf";

		response.setHeader("Content-Disposition", "attachment;filename=" + filename);

		return claimFormPdfDTO.getClaimFormPdfByteFile();
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.VIEW_ATTACHMENT, method = RequestMethod.GET)
	public @ResponseBody byte[] viewAttachment(
			@RequestParam(value = "attachmentId", required = true) long claimApplicationItemAttachmentId,
			HttpServletResponse response, HttpServletRequest request) {

		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		
		AddClaimDTO addClaimDTO = new AddClaimDTO();
		addClaimDTO.setClaimApplicationItemAttachmentId(FormatPreserveCryptoUtil.decrypt(claimApplicationItemAttachmentId));
		addClaimDTO.setCompanyId(companyId);
		addClaimDTO.setEmployeeId(employeeId);
		addClaimDTO.setAdmin(false);

		ClaimApplicationItemAttach claimApplicationItemAttach = myClaimLogic.viewAttachment(addClaimDTO);

		byte[] byteFile = claimApplicationItemAttach.getAttachmentBytes();

		response.reset();
		String mimeType = URLConnection.guessContentTypeFromName(claimApplicationItemAttach.getFileName());
		response.setContentType("application/" + mimeType);
		response.setContentLength(byteFile.length);
		String filename = claimApplicationItemAttach.getFileName();

		response.setHeader("Content-Disposition", "attachment;filename=" + filename);

		return byteFile;
	}
	
	@Override
	@RequestMapping(value = PayAsiaURLConstant.SHOW_CLAIM_TRANSACTION_REPORT, method = RequestMethod.POST)
	@ResponseBody
	public String showClaimTransactionReport(@ModelAttribute(value = "ClaimReportsForm") ClaimReportsForm claimReportsForm,
			@RequestParam(value = "dataDictionaryIds", required = false) String[] dataDictionaryIds,
			@RequestParam(value = "isCheckedFromCreatedDate", required = false)Boolean isCheckedFromCreatedDate,
			HttpServletRequest request, HttpServletResponse response, Locale locale) {
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long employeeId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		boolean hasLundinTimesheetModule = (boolean) request.getSession()
				.getAttribute(PayAsiaSessionAttributes.COMPANY_HAS_LUNDIN_TIMESHEET_MODULE);
		
		if(claimReportsForm.getStatusName()==null && isCheckedFromCreatedDate == false){
			claimReportsForm.setStatusName("Approved");
		}
		
		ClaimDetailsReportDTO claimDetailsReportDTO = myClaimLogic.showClaimTransactionReport(companyId,
				claimReportsForm, employeeId, dataDictionaryIds, hasLundinTimesheetModule, locale,isCheckedFromCreatedDate);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("aaData", claimDetailsReportDTO.getClaimDetailsDataDTOs());
		jsonObject.put("aoColumns", claimDetailsReportDTO.getClaimHeaderDTOs());
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;

	}
	
	@RequestMapping(value = PayAsiaURLConstant.GEN_EMPLOYEE_CLAIM_TRANSACTION_REPORT_EXCEL_FILE, method = RequestMethod.POST)
	@Override
	public void genEmployeeClaimTranasactionReportExcelFile(	
			@ModelAttribute(value = "ClaimReportsForm") ClaimReportsForm claimReportsForm,
			@RequestParam(value = "dataDictionaryIds", required = false) String[] dataDictionaryIds,
			@RequestParam(value = "isCheckedFromCreatedDate", required = false)Boolean isCheckedFromCreatedDate,
			HttpServletRequest request, HttpServletResponse response, Locale locale) {
		UUID uuid = UUID.randomUUID();

		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long employeeId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		boolean hasLundinTimesheetModule = (boolean) request.getSession()
				.getAttribute(PayAsiaSessionAttributes.COMPANY_HAS_LUNDIN_TIMESHEET_MODULE);
		
		if(claimReportsForm.getStatusName()==null && isCheckedFromCreatedDate == false){
			claimReportsForm.setStatusName("Approved");
		}
		
		ClaimDetailsReportDTO claimDetailsReportDTO = myClaimLogic.showClaimTransactionReport(companyId,
				claimReportsForm, employeeId, dataDictionaryIds, hasLundinTimesheetModule, locale, isCheckedFromCreatedDate);
		ClaimReportHeaderDTO claimantName = new ClaimReportHeaderDTO();
		claimantName.setmDataProp("claimantName");
		claimantName.setsTitle("Claimaint Name");
		
		boolean toShowClaimant = claimDetailsReportDTO.getClaimHeaderDTOs().contains(claimantName);
		Map beans = new HashMap();
		beans.put("claimDetailsReportList", claimDetailsReportDTO.getClaimDetailsDataDTOs());
		if (claimDetailsReportDTO.getDataDictNameList().size() > 0) {
			beans.put("custFieldColNameList", claimDetailsReportDTO.getDataDictNameList());
		}
		beans.put("claimDetailsReportCustomDataList", claimDetailsReportDTO.getClaimDetailsCustomDataDTOs());
		beans.put("claimIsSubordinateEnable", claimDetailsReportDTO.isSubordinateCompanyEmployee());
		XLSTransformer transformer = new XLSTransformer();
		File tempDestFile = null;
		File tempFolder = new File(PAYASIA_TEMP_PATH + "/claimreport/" + uuid);
		tempFolder.mkdirs();
		String templateFileName;

		if (hasLundinTimesheetModule) {
			if (toShowClaimant) {
				if (claimDetailsReportDTO.getDataDictNameList().size() > 0) {
					templateFileName = servletContext
							.getRealPath("/resources/ClaimReportTemplate/ClaimTransactionReportWithClaimantLundin.xlsx");
				} else {
					templateFileName = servletContext.getRealPath(
							"/resources/ClaimReportTemplate/ClaimTransactionReportWithClaimantWOCustomLundin.xlsx");
				}

			} else {
				if (claimDetailsReportDTO.getDataDictNameList().size() > 0) {
					templateFileName = servletContext
							.getRealPath("/resources/ClaimReportTemplate/ClaimTransactionReportLundin.xlsx");
				} else {
					templateFileName = servletContext
							.getRealPath("/resources/ClaimReportTemplate/ClaimTransactionReportWOCustomLundin.xlsx");
				}

			}
		} else {
			if (toShowClaimant) {
				if (claimDetailsReportDTO.getDataDictNameList().size() > 0) {
					templateFileName = servletContext
							.getRealPath("/resources/ClaimReportTemplate/ClaimTransactionReportWithClaimant.xlsx");
				} else {
					templateFileName = servletContext
							.getRealPath("/resources/ClaimReportTemplate/ClaimTransactionReportWithClaimantWOCustom.xlsx");
				}

			} else {
				if (claimDetailsReportDTO.getDataDictNameList().size() > 0) {
					templateFileName = servletContext
							.getRealPath("/resources/ClaimReportTemplate/ClaimTransactionReport.xlsx");
				} else {
					templateFileName = servletContext
							.getRealPath("/resources/ClaimReportTemplate/ClaimTransactionReportWOCustom.xlsx");
				}

			}
		}

		String destFileName = PAYASIA_TEMP_PATH + "/claimreport/" + uuid + "/Claim Transaction Report" + uuid + ".xlsx";

		try {
			transformer.transformXLS(templateFileName, beans, destFileName);
		} catch (ParsePropertyException e) {
			LOGGER.error(e);
		} catch (InvalidFormatException e) {
			LOGGER.error(e);
		} catch (IOException e) {
			LOGGER.error(e);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		ServletOutputStream outputStream = null;
		try {

			Path path = Paths.get(destFileName);
			byte[] data = Files.readAllBytes(path);

			String fileName = "Claim Transaction Report";
			outputStream = response.getOutputStream();
			response.setContentType("application/octet-stream");
			response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");

			fileName = fileName + ".xlsx";
			response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "");

			outputStream = response.getOutputStream();
			outputStream.write(data);

		} catch (IOException ioException) {
			LOGGER.error(ioException.getMessage(), ioException);
			throw new PayAsiaSystemException(ioException.getMessage(), ioException);

		} finally {
			tempDestFile = new File(destFileName);
			if (tempDestFile != null) {
				tempDestFile.delete();
			}
			tempFolder.delete();
			try {

				if (outputStream != null) {
					outputStream.flush();
					outputStream.close();
				}
			} catch (IOException ioException) {
				LOGGER.error(ioException.getMessage(), ioException);
				throw new PayAsiaSystemException(ioException.getMessage(), ioException);
			}
		}

	}
	
	@RequestMapping(value = PayAsiaURLConstant.GEN_EMPLOYEE_CLAIM_TRANSACTION_REPORT_PDF_FILE, method = RequestMethod.POST)
	@Override
	public void genEmployeeClaimTranasactionReportPdfFile(
			@ModelAttribute(value = "ClaimReportsForm") ClaimReportsForm claimReportsForm,
			@RequestParam(value = "dataDictionaryIds", required = false) String[] dataDictionaryIds,
			@RequestParam(value = "isCheckedFromCreatedDate", required = false)Boolean isCheckedFromCreatedDate,
			HttpServletRequest request, HttpServletResponse response, Locale locale) {
		UUID uuid = UUID.randomUUID();

		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long employeeId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		boolean hasLundinTimesheetModule = (boolean) request.getSession()
				.getAttribute(PayAsiaSessionAttributes.COMPANY_HAS_LUNDIN_TIMESHEET_MODULE);
		
		if(claimReportsForm.getStatusName()==null && isCheckedFromCreatedDate == false){
			claimReportsForm.setStatusName("Approved");
		}
		
		ClaimDetailsReportDTO claimDetailsReportDTO = myClaimLogic.showClaimTransactionReport(companyId,
				claimReportsForm, employeeId, dataDictionaryIds, hasLundinTimesheetModule, locale, isCheckedFromCreatedDate);

		ClaimReportHeaderDTO claimantName = new ClaimReportHeaderDTO();
		claimantName.setmDataProp("claimantName");
		claimantName.setsTitle("Claimaint Name");
		boolean toShowClaimant = claimDetailsReportDTO.getClaimHeaderDTOs().contains(claimantName);
		Map beans = new HashMap();
		beans.put("claimDetailsReportList", claimDetailsReportDTO.getClaimDetailsDataDTOs());
		if (claimDetailsReportDTO.getDataDictNameList().size() > 0) {
			beans.put("custFieldColNameList", claimDetailsReportDTO.getDataDictNameList());
		}
		beans.put("claimDetailsReportCustomDataList", claimDetailsReportDTO.getClaimDetailsCustomDataDTOs());
		beans.put("claimIsSubordinateEnable", claimDetailsReportDTO.isSubordinateCompanyEmployee());
		XLSTransformer transformer = new XLSTransformer();
		File tempDestPdfFile = null;
		File tempDestExcelFile = null;
		File tempFolder = new File(PAYASIA_TEMP_PATH + "/claimreport/" + uuid);
		tempFolder.mkdirs();
		String templateFileName;
		if (hasLundinTimesheetModule) {
			if (toShowClaimant) {
				if (claimDetailsReportDTO.getDataDictNameList().size() > 0) {
					templateFileName = servletContext
							.getRealPath("/resources/ClaimReportTemplate/ClaimTransactionReportWithClaimantLundin.xlsx");
				} else {
					templateFileName = servletContext.getRealPath(
							"/resources/ClaimReportTemplate/ClaimTransactionReportWithClaimantWOCustomLundin.xlsx");
				}

			} else {
				if (claimDetailsReportDTO.getDataDictNameList().size() > 0) {
					templateFileName = servletContext
							.getRealPath("/resources/ClaimReportTemplate/ClaimTransactionReportLundin.xlsx");
				} else {
					templateFileName = servletContext
							.getRealPath("/resources/ClaimReportTemplate/ClaimTransactionReportWOCustomLundin.xlsx");
				}

			}
		} else {
			if (toShowClaimant) {
				if (claimDetailsReportDTO.getDataDictNameList().size() > 0) {
					templateFileName = servletContext
							.getRealPath("/resources/ClaimReportTemplate/ClaimTransactionReportWithClaimant.xlsx");
				} else {
					templateFileName = servletContext
							.getRealPath("/resources/ClaimReportTemplate/ClaimTransactionReportWithClaimantWOCustom.xlsx");
				}

			} else {
				if (claimDetailsReportDTO.getDataDictNameList().size() > 0) {
					templateFileName = servletContext
							.getRealPath("/resources/ClaimReportTemplate/ClaimTransactionReport.xlsx");
				} else {
					templateFileName = servletContext
							.getRealPath("/resources/ClaimReportTemplate/ClaimTransactionReportWOCustom.xlsx");
				}

			}
		}

		String destFileNameExcel = PAYASIA_TEMP_PATH + "/ClaimTransactionReport" + uuid + ".xlsx";

		String destFileNamePDF = PAYASIA_TEMP_PATH + "/ClaimTransactionReport" + uuid + ".pdf";

		try {
			transformer.transformXLS(templateFileName, beans, destFileNameExcel);

			ArrayList<String> cmdList = new ArrayList<String>();

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

		} catch (ParsePropertyException e) {
			LOGGER.error(e);
		} catch (InvalidFormatException e) {
			LOGGER.error(e);
		} catch (IOException e) {
			LOGGER.error(e);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		ServletOutputStream outputStream = null;
		try {

			Path path = Paths.get(destFileNamePDF);
			byte[] data = Files.readAllBytes(path);

			String fileName = "Claim Transaction Report";
			outputStream = response.getOutputStream();
			response.setContentType("application/octet-stream");
			response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");

			fileName = fileName + ".pdf";
			response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "");

			outputStream = response.getOutputStream();
			outputStream.write(data);

		} catch (IOException ioException) {
			LOGGER.error(ioException.getMessage(), ioException);
			throw new PayAsiaSystemException(ioException.getMessage(), ioException);

		} finally {
			tempDestPdfFile = new File(destFileNamePDF);
			if (tempDestPdfFile != null) {
				tempDestPdfFile.delete();
			}
			tempDestExcelFile = new File(destFileNameExcel);
			if (tempDestExcelFile != null) {
				tempDestExcelFile.delete();
			}
			tempFolder.delete();
			try {

				if (outputStream != null) {
					outputStream.flush();
					outputStream.close();
				}
			} catch (IOException ioException) {
				LOGGER.error(ioException.getMessage(), ioException);
				throw new PayAsiaSystemException(ioException.getMessage(), ioException);
			}
		}

	}
	
}
