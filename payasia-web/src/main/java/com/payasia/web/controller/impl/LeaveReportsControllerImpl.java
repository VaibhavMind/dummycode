/**
 * The Class LeaveReportsControllerImpl.
 * 
 * @author ragulapraveen
 */
package com.payasia.web.controller.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.mail.internet.MimeUtility;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.payasia.common.dto.EmployeeLeaveSchemeTypeDTO;
import com.payasia.common.dto.LeaveReportDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.EmployeeListForm;
import com.payasia.common.form.LeavePreferenceForm;
import com.payasia.common.form.LeaveReportsForm;
import com.payasia.common.form.LeaveReportsResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.form.SwitchCompanyResponse;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.ExcelUtils;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.logic.LeaveBalanceSummaryLogic;
import com.payasia.logic.LeaveReportsLogic;
import com.payasia.web.controller.LeaveReportsController;
import com.payasia.web.util.PayAsiaSessionAttributes;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.jxls.exception.ParsePropertyException;
import net.sf.jxls.transformer.XLSTransformer;

@Controller
@RequestMapping(value = "/admin/leaveReports")
public class LeaveReportsControllerImpl implements LeaveReportsController {
	/** The Constant logger. */
	private static final Logger LOGGER = Logger.getLogger(LeaveReportsControllerImpl.class);

	/** The leave reports logic. */
	@Resource
	LeaveReportsLogic leaveReportsLogic;

	/** The payasia temp path. */
	@Value("#{payasiaptProperties['payasia.temp.path']}")
	private String PAYASIA_TEMP_PATH;

	/** The document convertor path. */
	@Value("#{payasiaptProperties['payasia.document.converter.path']}")
	private String DOCUMENT_CONVERTOR_PATH;

	@Resource
	MessageSource messageSource;

	@Resource
	LeaveBalanceSummaryLogic leaveBalanceSummaryLogic;
	
	@Autowired
	private ServletContext servletContext;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.LeaveReportsController#viewLeaveReports(java
	 * .lang.String, java.lang.String, int, int,
	 * javax.servlet.http.HttpServletRequest)
	 */
	@Override
	@RequestMapping(value = "/viewLeaveReports", method = RequestMethod.POST)
	@ResponseBody
	public String viewLeaveReports(@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows, HttpServletRequest request) {
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		LeaveReportsResponse response = leaveReportsLogic.viewLeaveReports(pageDTO, sortDTO, companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(response, jsonConfig);

		return jsonObject.toString();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.LeaveReportsController#searchEmployee(java
	 * .lang.String, java.lang.String, int, int, java.lang.String,
	 * java.lang.String, java.lang.String,
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/searchEmployee", method = RequestMethod.POST)
	@ResponseBody
	public String searchEmployee(@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			@RequestParam(value = "searchCondition", required = true) String searchCondition,
			@RequestParam(value = "searchText", required = true) String searchText,
			@RequestParam(value = "metaData", required = false) String metaData,
			@RequestParam(value = "includeResignedEmployees", required = false) Boolean includeResignedEmployees,
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		Long employeeId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		LeaveReportsResponse leaveReportResponse = leaveReportsLogic.searchEmployee(pageDTO, sortDTO, searchCondition,
				searchText, companyId, employeeId, metaData, includeResignedEmployees);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(leaveReportResponse, jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.LeaveReportsController#getLeaveTypeList(javax
	 * .servlet.http.HttpServletRequest)
	 */
	@Override
	@RequestMapping(value = "/leaveTypeList", method = RequestMethod.POST)
	@ResponseBody
	public String getLeaveTypeList(HttpServletRequest request) {
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		List<LeaveReportsForm> leaveSchemeList = leaveReportsLogic.getLeaveTypeList(companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(leaveSchemeList, jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/leaveReviewerList.html", method = RequestMethod.POST)
	@ResponseBody
	public String getLeaveReviewerList(HttpServletRequest request) {
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long employeeId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		List<EmployeeListForm> leaveReviewerList = leaveReportsLogic.getLeaveReviewerList(employeeId, companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(leaveReviewerList, jsonConfig);
		return jsonObject.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.LeaveReportsController#getLeaveTransactionList
	 * (javax.servlet.http.HttpServletRequest)
	 */
	@Override
	@RequestMapping(value = "/leaveTransactionList", method = RequestMethod.POST)
	@ResponseBody
	public String getLeaveTransactionList(HttpServletRequest request, Locale locale) {
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		List<LeaveReportsForm> leaveSchemeList = leaveReportsLogic.getLeaveTransactionList(companyId);

		for (LeaveReportsForm leaveReportsForm : leaveSchemeList) {
			leaveReportsForm.setLeaveTransactionName(
					messageSource.getMessage(leaveReportsForm.getLeaveTransactionName(), new Object[] {}, locale));
		}
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(leaveSchemeList, jsonConfig);
		return jsonObject.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.LeaveReportsController#showLeaveTranReport
	 * (com.payasia.common.form.LeaveReportsForm,
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, java.util.Locale)
	 */
	@Override
	@RequestMapping(value = "/showLeaveTranReport", method = RequestMethod.POST)
	@ResponseBody
	public String showLeaveTranReport(@ModelAttribute(value = "LeaveReportsForm") LeaveReportsForm leaveReportsForm,
			@RequestParam(value = "dataDictionaryIds", required = false) String[] dataDictionaryIds,
			HttpServletRequest request, HttpServletResponse response, Locale locale) {
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		Long employeeId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		Boolean isManager = false;
		LeaveReportDTO leaveDataDTO = leaveReportsLogic.showLeaveTranReport(companyId, employeeId, leaveReportsForm,
				isManager, dataDictionaryIds);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("aaData", leaveDataDTO.getLeaveTranReportDTOs());
		jsonObject.put("aoColumns", leaveDataDTO.getLeaveHeaderDTOs());
		String data = jsonObject.toString();
		return data;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.web.controller.LeaveReportsController#
	 * showLeaveBalanceAsOnDateReport(com.payasia.common.form.LeaveReportsForm,
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, java.util.Locale)
	 */
	@Override
	@RequestMapping(value = "/showLeaveBalanceAsOnDateReport.html", method = RequestMethod.POST)
	@ResponseBody
	public String showLeaveBalanceAsOnDateReport(
			@ModelAttribute(value = "LeaveReportsForm") LeaveReportsForm leaveReportsForm,
			@RequestParam(value = "dataDictionaryIds", required = false) String[] dataDictionaryIds,
			HttpServletRequest request, HttpServletResponse response, Locale locale) {
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		Long employeeId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		LeaveReportDTO leaveDataDTO = leaveReportsLogic.showLeaveBalanceAsOnDateReport(companyId, leaveReportsForm,
				employeeId, dataDictionaryIds);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("aaData", leaveDataDTO.getLeaveBalAsOnDayDTOs());
		jsonObject.put("aoColumns", leaveDataDTO.getLeaveHeaderDTOs());
		String data = jsonObject.toString();
		return data;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.LeaveReportsController#showLeaveReviewerReport
	 * (com.payasia.common.form.LeaveReportsForm,
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, java.util.Locale)
	 */
	@Override
	@RequestMapping(value = "/showLeaveReviewerReport.html", method = RequestMethod.POST)
	@ResponseBody
	public String showLeaveReviewerReport(@ModelAttribute(value = "LeaveReportsForm") LeaveReportsForm leaveReportsForm,
			HttpServletRequest request, HttpServletResponse response, Locale locale) {
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		Long employeeId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		LeaveReportDTO leaveDataDTO = leaveReportsLogic.showLeaveReviewerReport(companyId, leaveReportsForm,
				employeeId);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("aaData", leaveDataDTO.getLeaveReviewerReportDTOs());
		jsonObject.put("aoColumns", leaveDataDTO.getLeaveHeaderDTOs());
		String data = jsonObject.toString();
		return data;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.web.controller.LeaveReportsController#
	 * genLeaveTranReportExcelFile (com.payasia.common.form.LeaveReportsForm,
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@RequestMapping(value = "/genLeaveTeportExcelFile.html", method = RequestMethod.POST)
	@Override
	public void genLeaveTranReportExcelFile(
			@ModelAttribute(value = "LeaveReportsForm") LeaveReportsForm leaveReportsForm,
			@RequestParam(value = "dataDictionaryIds", required = true) String[] dataDictionaryIds,
			HttpServletRequest request, HttpServletResponse response) {

		UUID uuid = UUID.randomUUID();

		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		Long employeeId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		Boolean isManager = false;
		LeaveReportDTO leaveDataDTO = leaveReportsLogic.showLeaveTranReport(companyId, employeeId, leaveReportsForm,
				isManager, dataDictionaryIds);
		List<String> leaveTranList = convertArrayToList(leaveReportsForm.getMultipleLeaveTransactionName());

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

		if (leaveDataDTO.getDataDictNameList().size() > 0) {
			beans.put("custFieldColNameList", leaveDataDTO.getDataDictNameList());
		}
		beans.put("leaveCustomDataList", leaveDataDTO.getLeaveCustomDataDTOs());
		XLSTransformer transformer = new XLSTransformer();
		File tempFolder = new File(PAYASIA_TEMP_PATH + "/leavereport/" + uuid);
		tempFolder.mkdirs();
		File tempDestFile = null;
		String templateFileName = "";

		boolean isLeaveUnitDays = leaveBalanceSummaryLogic.isLeaveUnitDays(companyId);

		if (leaveReportsForm.isMultipleRecord()) {
			beans.put("custFieldColNameList", leaveDataDTO.getDataDictNameList());
			templateFileName = servletContext
					.getRealPath("/resources/LeaveReportTemplate/LeaveTransactionReportMultipleRecord.xlsx");
		} else {
			if (isLeaveUnitDays) {
				// Leave In Days
				if (leaveTranList.contains(PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_APPROVED)
						|| leaveTranList.contains(PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_APPROVED_CANCEL)) {
					if (leaveDataDTO.getDataDictNameList().size() > 0) {
						templateFileName = servletContext.getRealPath(
								"/resources/LeaveReportTemplate/LeaveTransactionReportWithApprovedDate.xlsx");
					} else {
						templateFileName = servletContext.getRealPath(
								"/resources/LeaveReportTemplate/LeaveTranReportWithAppDateWithoutCustomFields.xlsx");
					}
				} else {
					if (leaveDataDTO.getDataDictNameList().size() > 0) {
						templateFileName = servletContext
								.getRealPath("/resources/LeaveReportTemplate/LeaveTransactionReport.xlsx");
					} else {
						templateFileName = servletContext.getRealPath(
								"/resources/LeaveReportTemplate/LeaveTransactionReportWithoutCustomFields.xlsx");
					}
				}
			} else {
				// Leave In Hours
				if (leaveTranList.contains(PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_APPROVED)
						|| leaveTranList.contains(PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_APPROVED_CANCEL)) {
					if (leaveDataDTO.getDataDictNameList().size() > 0) {
						templateFileName = servletContext.getRealPath(
								"/resources/LeaveReportTemplate/LeaveTransactionReportWithApprovedDateNHours.xlsx");
					} else {
						templateFileName = servletContext.getRealPath(
								"/resources/LeaveReportTemplate/LeaveTranReportWithAppDateNHoursaWOCustomFields.xlsx");
					}
				} else {
					if (leaveDataDTO.getDataDictNameList().size() > 0) {
						templateFileName = servletContext
								.getRealPath("/resources/LeaveReportTemplate/LeaveTransactionReportWithHours.xlsx");
					} else {
						templateFileName = servletContext.getRealPath(
								"/resources/LeaveReportTemplate/LeaveTransactionReportWithHoursWOCustomFields.xlsx");
					}
				}
			}
		}
		String destFileName = PAYASIA_TEMP_PATH + "/leavereport/" + uuid + "/Leave TransactionReport" + uuid + ".xlsx";
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

			String fileName = "Leave Transaction Report";
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.LeaveReportsController#genLeaveTranReportPDF
	 * (com.payasia.common.form.LeaveReportsForm,
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@RequestMapping(value = "/genLeaveTranReportPDF.html", method = RequestMethod.POST)
	@Override
	public void genLeaveTranReportPDF(@ModelAttribute(value = "LeaveReportsForm") LeaveReportsForm leaveReportsForm,
			@RequestParam(value = "dataDictionaryIds", required = true) String[] dataDictionaryIds,
			HttpServletRequest request, HttpServletResponse response) {
		UUID uuid = UUID.randomUUID();

		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		Long employeeId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		Boolean isManager = false;
		LeaveReportDTO leaveDataDTO = leaveReportsLogic.showLeaveTranReport(companyId, employeeId, leaveReportsForm,
				isManager, dataDictionaryIds);
		List<String> leaveTranList = convertArrayToList(leaveReportsForm.getMultipleLeaveTransactionName());

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

		if (leaveDataDTO.getDataDictNameList().size() > 0) {
			beans.put("custFieldColNameList", leaveDataDTO.getDataDictNameList());
		}
		beans.put("leaveCustomDataList", leaveDataDTO.getLeaveCustomDataDTOs());

		XLSTransformer transformer = new XLSTransformer();
		File tempDestPdfFile = null;
		File tempDestExcelFile = null;
		File tempFolder = new File(PAYASIA_TEMP_PATH + "/leavereport/" + uuid);
		tempFolder.mkdirs();
		String templateFileName = "";

		boolean isLeaveUnitDays = leaveBalanceSummaryLogic.isLeaveUnitDays(companyId);

		if (leaveReportsForm.isMultipleRecord()) {
			beans.put("custFieldColNameList", leaveDataDTO.getDataDictNameList());
			templateFileName = servletContext
					.getRealPath("/resources/LeaveReportTemplate/LeaveTransactionReportMultipleRecord.xlsx");
		} else {
			if (isLeaveUnitDays) {
				// Leave In Days
				if (leaveTranList.contains(PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_APPROVED)
						|| leaveTranList.contains(PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_APPROVED_CANCEL)) {

					if (leaveDataDTO.getDataDictNameList().size() > 0) {
						templateFileName = servletContext.getRealPath(
								"/resources/LeaveReportTemplate/LeaveTransactionReportWithApprovedDate.xlsx");

					} else {
						templateFileName = servletContext.getRealPath(
								"/resources/LeaveReportTemplate/LeaveTranReportWithAppDateWithoutCustomFields.xlsx");
					}

				} else {
					if (leaveDataDTO.getDataDictNameList().size() > 0) {
						templateFileName = servletContext
								.getRealPath("/resources/LeaveReportTemplate/LeaveTransactionReport.xlsx");
					} else {
						templateFileName = servletContext.getRealPath(
								"/resources/LeaveReportTemplate/LeaveTransactionReportWithoutCustomFields.xlsx");
					}
				}
			} else {
				// Leave In Hours
				if (leaveTranList.contains(PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_APPROVED)
						|| leaveTranList.contains(PayAsiaConstants.LEAVE_TRANSACTION_TYPE_LEAVE_APPROVED_CANCEL)) {
					if (leaveDataDTO.getDataDictNameList().size() > 0) {
						templateFileName = servletContext.getRealPath(
								"/resources/LeaveReportTemplate/LeaveTransactionReportWithApprovedDateNHours.xlsx");
					} else {
						templateFileName = servletContext.getRealPath(
								"/resources/LeaveReportTemplate/LeaveTranReportWithAppDateNHoursaWOCustomFields.xlsx");
					}
				} else {
					if (leaveDataDTO.getDataDictNameList().size() > 0) {
						templateFileName = servletContext
								.getRealPath("/resources/LeaveReportTemplate/LeaveTransactionReportWithHours.xlsx");
					} else {
						templateFileName = servletContext.getRealPath(
								"/resources/LeaveReportTemplate/LeaveTransactionReportWithHoursWOCustomFields.xlsx");
					}
				}
			}

		}

		String destFileNameExcel = PAYASIA_TEMP_PATH + "/LeaveTransactionReport" + uuid + ".xlsx";
		String destFileNamePDF = PAYASIA_TEMP_PATH + "/LeaveTransactionReport" + uuid + ".pdf";
		Process process = null;
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
			process = processBuilder.start();
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

			String fileName = "Leave Transaction Report";
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

	private List<String> convertArrayToList(String[] stringArr) {
		List<String> leaveTranList = new ArrayList<String>();
		for (String leaveTransaction : stringArr) {
			if (!leaveTransaction.equalsIgnoreCase("0")) {
				leaveTranList.add(leaveTransaction);
			}
		}
		return leaveTranList;
	}

	@RequestMapping(value = "/genDayWiseLeaveTeportExcelFile.html", method = RequestMethod.POST)
	@Override
	public void genDayWiseLeaveTranReportExcelFile(
			@ModelAttribute(value = "LeaveReportsForm") LeaveReportsForm leaveReportsForm,
			@RequestParam(value = "dataDictionaryIds", required = true) String[] dataDictionaryIds,
			HttpServletRequest request, HttpServletResponse response) {

		UUID uuid = UUID.randomUUID();

		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		Long employeeId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		Boolean isManager = false;
		LeaveReportDTO leaveDataDTO = leaveReportsLogic.showDayWiseLeaveTranReport(companyId, employeeId,
				leaveReportsForm, isManager, dataDictionaryIds);

		String header = "Day Wise Leave Transaction Report Between " + leaveReportsForm.getStartDate() + " And "
				+ leaveReportsForm.getEndDate();

		Map<String, Object> beans = new HashMap<String, Object>();
		beans.put("header", header);
		beans.put("dayWiseLeaveTranMap", leaveDataDTO.getDayWiseLeaveTranMap());
		if (leaveDataDTO.isLeavePreferencePreApproval()) {
			beans.put("dayWiseLeaveTranMap", leaveDataDTO.getDayWiseLeaveTranMap());
		}
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

		beans.put("leaveCustomDataList", leaveDataDTO.getLeaveCustomDataDTOs());
		XLSTransformer transformer = new XLSTransformer();
		File tempDestFile = null;
		File tempFolder = new File(PAYASIA_TEMP_PATH + "/leavereport/" + uuid);
		tempFolder.mkdirs();

		String templateFileName = "";
		if (leaveDataDTO.getDataDictNameList().size() > 0) {
			templateFileName = servletContext
					.getRealPath("/resources/LeaveReportTemplate/DayWiseLeaveTransactionReport.xlsx");

		} else {
			templateFileName = servletContext.getRealPath(
					"/resources/LeaveReportTemplate/DayWiseLeaveTransactionReportWithoutCustomFields.xlsx");
		}
		String destFileName = PAYASIA_TEMP_PATH + "/leavereport/" + uuid + "/Day Wise Leave TransactionReport" + uuid
				+ ".xlsx";
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

			String fileName = "Day Wise Leave Transaction Report";
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

	@RequestMapping(value = "/genDayWiseLeaveTranReportPDF.html", method = RequestMethod.POST)
	@Override
	public void genDayWiseLeaveTranReportPDF(
			@ModelAttribute(value = "LeaveReportsForm") LeaveReportsForm leaveReportsForm,
			@RequestParam(value = "dataDictionaryIds", required = true) String[] dataDictionaryIds,
			HttpServletRequest request, HttpServletResponse response) {
		UUID uuid = UUID.randomUUID();

		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		Long employeeId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		Boolean isManager = false;
		LeaveReportDTO leaveDataDTO = leaveReportsLogic.showDayWiseLeaveTranReport(companyId, employeeId,
				leaveReportsForm, isManager, dataDictionaryIds);

		String header = "Day Wise Leave Transaction Report Between " + leaveReportsForm.getStartDate() + " And "
				+ leaveReportsForm.getEndDate();

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
		XLSTransformer transformer = new XLSTransformer();
		File tempDestPdfFile = null;
		File tempDestExcelFile = null;
		File tempFolder = new File(PAYASIA_TEMP_PATH + "/leavereport/" + uuid);
		tempFolder.mkdirs();
		String templateFileName = "";
		if (leaveDataDTO.getDataDictNameList().size() > 0) {
			templateFileName = servletContext
					.getRealPath("/resources/LeaveReportTemplate/DayWiseLeaveTransactionReport.xlsx");

		} else {
			templateFileName = servletContext.getRealPath(
					"/resources/LeaveReportTemplate/DayWiseLeaveTransactionReportWithoutCustomFields.xlsx");
		}

		String destFileNameExcel = PAYASIA_TEMP_PATH + "/DayWiseLeaveTransactionReport" + uuid + ".xlsx";
		String destFileNamePDF = PAYASIA_TEMP_PATH + "/DayWiseLeaveTransactionReport" + uuid + ".pdf";
		Process process = null;
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
			process = processBuilder.start();
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

			String fileName = "Day Wise Leave Transaction Report";
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.web.controller.LeaveReportsController#
	 * genLeaveBalAsOnDayReportExcelFile
	 * (com.payasia.common.form.LeaveReportsForm,
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@RequestMapping(value = "/genLeaveBalAsOnDayReportExcelFile.html", method = RequestMethod.POST)
	@Override
	public void genLeaveBalAsOnDayReportExcelFile(
			@ModelAttribute(value = "LeaveReportsForm") LeaveReportsForm leaveReportsForm,
			@RequestParam(value = "dataDictionaryIds", required = true) String[] dataDictionaryIds,
			HttpServletRequest request, HttpServletResponse response) {
		UUID uuid = UUID.randomUUID();

		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		Long employeeId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		LeaveReportDTO leaveDataDTO = leaveReportsLogic.showLeaveBalanceAsOnDateReport(companyId, leaveReportsForm,
				employeeId, dataDictionaryIds);

		Map<String, Object> beans = new HashMap<String, Object>();
		beans.put("leaveBalAsOnDayReportList", leaveDataDTO.getLeaveBalAsOnDayDTOs());
		beans.put("custFieldColNameList", leaveDataDTO.getDataDictNameList());
		beans.put("leaveTypeNameList", leaveDataDTO.getLeaveTypeNames());
		XLSTransformer transformer = new XLSTransformer();
		File tempDestFile = null;
		File tempFolder = new File(PAYASIA_TEMP_PATH + "/leavereport/" + uuid);
		tempFolder.mkdirs();
		String templateFileName = servletContext
				.getRealPath("/resources/LeaveReportTemplate/LeaveBalanceAsOnDayReport.xlsx");
		String destFileName = PAYASIA_TEMP_PATH + "/leavereport/" + uuid + "/Leave Reviewer Report" + uuid + ".xlsx";
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

			String fileName = "Leave Balance As On Day Report";
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.web.controller.LeaveReportsController#
	 * genLeaveBalAsOnDayReportPDFFile(com.payasia.common.form.LeaveReportsForm,
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@RequestMapping(value = "/genLeaveBalAsOnDayReportPDFFile.html", method = RequestMethod.POST)
	@Override
	public void genLeaveBalAsOnDayReportPDFFile(
			@ModelAttribute(value = "LeaveReportsForm") LeaveReportsForm leaveReportsForm,
			@RequestParam(value = "dataDictionaryIds", required = true) String[] dataDictionaryIds,
			HttpServletRequest request, HttpServletResponse response) {
		UUID uuid = UUID.randomUUID();

		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		Long employeeId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		LeaveReportDTO leaveDataDTO = leaveReportsLogic.showLeaveBalanceAsOnDateReport(companyId, leaveReportsForm,
				employeeId, dataDictionaryIds);

		Map<String, Object> beans = new HashMap<String, Object>();
		beans.put("leaveBalAsOnDayReportList", leaveDataDTO.getLeaveBalAsOnDayDTOs());
		beans.put("custFieldColNameList", leaveDataDTO.getDataDictNameList());
		beans.put("leaveTypeNameList", leaveDataDTO.getLeaveTypeNames());
		XLSTransformer transformer = new XLSTransformer();
		File tempDestPdfFile = null;
		File tempDestExcelFile = null;
		File tempFolder = new File(PAYASIA_TEMP_PATH + "/leavereport/" + uuid);
		tempFolder.mkdirs();
		String templateFileName = servletContext
				.getRealPath("/resources/LeaveReportTemplate/LeaveBalanceAsOnDayReport.xlsx");
		String destFileNameExcel = PAYASIA_TEMP_PATH + "/LeaveReviewerReport" + uuid + ".xlsx";

		String destFileNamePDF = PAYASIA_TEMP_PATH + "/LeaveReviewerReport" + uuid + ".pdf";

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

			String fileName = "Leave Balance As On Day Report";
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.web.controller.LeaveReportsController#
	 * genLeaveReviewerReportExcelFile(com.payasia.common.form.LeaveReportsForm,
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@RequestMapping(value = "/genLeaveReviewerReportExcelFile.html", method = RequestMethod.POST)
	@Override
	public void genLeaveReviewerReportExcelFile(
			@ModelAttribute(value = "LeaveReportsForm") LeaveReportsForm leaveReportsForm, HttpServletRequest request,
			HttpServletResponse response) {
		UUID uuid = UUID.randomUUID();

		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		Long employeeId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		LeaveReportDTO leaveDataDTO = leaveReportsLogic.showLeaveReviewerReport(companyId, leaveReportsForm,
				employeeId);

		Map<String, Object> beans = new HashMap<String, Object>();
		beans.put("leaveReviewerReportList", leaveDataDTO.getLeaveReviewerReportDTOs());
		XLSTransformer transformer = new XLSTransformer();
		File tempDestFile = null;
		File tempFolder = new File(PAYASIA_TEMP_PATH + "/leavereport/" + uuid);
		tempFolder.mkdirs();
		String templateFileName = servletContext
				.getRealPath("/resources/LeaveReportTemplate/LeaveReviewerReport.xlsx");
		String destFileName = PAYASIA_TEMP_PATH + "/leavereport/" + uuid + "/Leave Reviewer Report" + uuid + ".xlsx";

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

			String fileName = "Leave Reviewer Report";
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.web.controller.LeaveReportsController#
	 * genLeaveReviewerReportPDFFile(com.payasia.common.form.LeaveReportsForm,
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@RequestMapping(value = "/genLeaveReviewerReportPDFFile.html", method = RequestMethod.POST)
	@Override
	public void genLeaveReviewerReportPDFFile(
			@ModelAttribute(value = "LeaveReportsForm") LeaveReportsForm leaveReportsForm, HttpServletRequest request,
			HttpServletResponse response) {
		UUID uuid = UUID.randomUUID();

		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		Long employeeId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		LeaveReportDTO leaveDataDTO = leaveReportsLogic.showLeaveReviewerReport(companyId, leaveReportsForm,
				employeeId);

		Map<String, Object> beans = new HashMap<String, Object>();
		beans.put("leaveReviewerReportList", leaveDataDTO.getLeaveReviewerReportDTOs());
		XLSTransformer transformer = new XLSTransformer();
		File tempDestPdfFile = null;
		File tempDestExcelFile = null;
		File tempFolder = new File(PAYASIA_TEMP_PATH + "/leavereport/" + uuid);
		tempFolder.mkdirs();
		String templateFileName = servletContext
				.getRealPath("/resources/LeaveReportTemplate/LeaveReviewerReport.xlsx");
		String destFileNameExcel = PAYASIA_TEMP_PATH + "/LeaveReviewerReport" + uuid + ".xlsx";

		String destFileNamePDF = PAYASIA_TEMP_PATH + "/LeaveReviewerReport" + uuid + ".pdf";

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

			String fileName = "Leave Reviewer Report";
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.web.controller.LeaveReportsController#
	 * showYearWiseSummaryReport (com.payasia.common.form.LeaveReportsForm,
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, java.util.Locale)
	 */
	@Override
	@RequestMapping(value = "/showYearWiseSummaryReport.html", method = RequestMethod.POST)
	@ResponseBody
	public String showYearWiseSummaryReport(
			@ModelAttribute(value = "LeaveReportsForm") LeaveReportsForm leaveReportsForm,
			@RequestParam(value = "dataDictionaryIds", required = false) String[] dataDictionaryIds,
			HttpServletRequest request, HttpServletResponse response, Locale locale) {
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		Long employeeId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		Boolean isManager = false;
		LeaveReportDTO leaveDataDTO = leaveReportsLogic.showYearWiseSummaryReport(employeeId, companyId,
				leaveReportsForm, isManager, dataDictionaryIds);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("aaData", leaveDataDTO.getSummarryDTOs());
		jsonObject.put("aoColumns", leaveDataDTO.getLeaveTypeNames());
		jsonObject.put("dataDictionaryColumns", leaveDataDTO.getDataDictNameList());
		String data = jsonObject.toString();
		return data;

	}

	private void genYearWiseSummarryReportExcelFile(LeaveReportsForm leaveReportsForm, HttpServletRequest request,
			HttpServletResponse response, Locale locale, String[] dataDictionaryIds) {
		UUID uuid = UUID.randomUUID();
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		Long employeeId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		Boolean isManager = false;
		LeaveReportDTO leaveDataDTO = leaveReportsLogic.showYearWiseSummaryReport(employeeId, companyId,
				leaveReportsForm, isManager, dataDictionaryIds);

		Map<String, Object> beans = new HashMap<String, Object>();
		beans.put("yearWiseSummaryList", leaveDataDTO.getSummarryDTOs());
		beans.put("leaveTypesList", leaveDataDTO.getLeaveTypeNames());
		beans.put("customFieldsList", leaveDataDTO.getDataDictNameList());

		beans.put("carriedForward", messageSource.getMessage("payasia.carried.forward", new Object[] {}, locale));
		beans.put("credited", messageSource.getMessage("payasia.credited", new Object[] {}, locale));
		beans.put("enchased", messageSource.getMessage("payasia.encashed", new Object[] {}, locale));
		beans.put("forfeited", messageSource.getMessage("payasia.forfeited", new Object[] {}, locale));
		beans.put("approved", messageSource.getMessage("payasia.approved", new Object[] {}, locale));
		beans.put("closingBalance", messageSource.getMessage("payasia.closing.balance", new Object[] {}, locale));

		XLSTransformer transformer = new XLSTransformer();
		File tempDestFile = null;
		File tempFolder = new File(PAYASIA_TEMP_PATH + "/leavereport/" + uuid);
		tempFolder.mkdirs();
		String templateFileName = servletContext
				.getRealPath("/resources/LeaveReportTemplate/YearWiseSummaryReport.xlsx");
		String destFileName = PAYASIA_TEMP_PATH + "/leavereport/" + uuid + "/Year Wise Summary Report" + uuid + ".xlsx";

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

			String fileName = "Year Wise Summary Report";
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.web.controller.LeaveReportsController#
	 * genYearWiseSummarryReportPDFFile
	 * (com.payasia.common.form.LeaveReportsForm,
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@RequestMapping(value = "/genYearWiseSummarryReportPDFFile.html", method = RequestMethod.POST)
	@Override
	public void genYearWiseSummarryReportPDFFile(
			@ModelAttribute(value = "LeaveReportsForm") LeaveReportsForm leaveReportsForm,
			@RequestParam(value = "dataDictionaryIds", required = true) String[] dataDictionaryIds,
			HttpServletRequest request, HttpServletResponse response, Locale locale) {
		UUID uuid = UUID.randomUUID();

		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		Long employeeId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		Boolean isManager = false;
		LeaveReportDTO leaveDataDTO = leaveReportsLogic.showYearWiseSummaryReport(employeeId, companyId,
				leaveReportsForm, isManager, dataDictionaryIds);

		Map<String, Object> beans = new HashMap<String, Object>();
		beans.put("yearWiseSummaryList", leaveDataDTO.getSummarryDTOs());
		beans.put("leaveTypesList", leaveDataDTO.getLeaveTypeNames());
		beans.put("customFieldsList", leaveDataDTO.getDataDictNameList());
		beans.put("carriedForward", messageSource.getMessage("payasia.carried.forward", new Object[] {}, locale));
		beans.put("credited", messageSource.getMessage("payasia.credited", new Object[] {}, locale));
		beans.put("enchased", messageSource.getMessage("payasia.encashed", new Object[] {}, locale));
		beans.put("forfeited", messageSource.getMessage("payasia.forfeited", new Object[] {}, locale));
		beans.put("approved", messageSource.getMessage("payasia.approved", new Object[] {}, locale));
		beans.put("closingBalance", messageSource.getMessage("payasia.closing.balance", new Object[] {}, locale));

		XLSTransformer transformer = new XLSTransformer();
		File tempDestPdfFile = null;
		File tempDestExcelFile = null;
		File tempFolder = new File(PAYASIA_TEMP_PATH + "/leavereport/" + uuid);
		tempFolder.mkdirs();
		String templateFileName = servletContext
				.getRealPath("/resources/LeaveReportTemplate/YearWiseSummaryReport.xlsx");
		String destFileNameExcel = PAYASIA_TEMP_PATH + "/YearWiseSummaryReport" + uuid + ".xlsx";

		String destFileNamePDF = PAYASIA_TEMP_PATH + "/YearWiseSummaryReport" + uuid + ".pdf";

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

			String fileName = "Year Wise Summary Report";
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

	private void genYearWiseSummarryReportExcelFileInMultipleSheets(LeaveReportsForm leaveReportsForm,
			HttpServletRequest request, HttpServletResponse response, Locale locale, String[] dataDictionaryIds) {
		UUID uuid = UUID.randomUUID();

		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		Long employeeId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		Boolean isManager = false;
		LeaveReportDTO leaveDataDTO = leaveReportsLogic.showYearWiseSummaryReport(employeeId, companyId,
				leaveReportsForm, isManager, dataDictionaryIds);

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

		File tempFolder = new File(PAYASIA_TEMP_PATH + "/leavereport/" + uuid);
		tempFolder.mkdirs();
		String templateFileName = "";
		if (leaveDataDTO.getDataDictNameList().size() > 0) {
			templateFileName = servletContext
					.getRealPath("/resources/LeaveReportTemplate/YearWiseSummaryReportMultipleSheets.xlsx");
		} else {
			templateFileName = servletContext.getRealPath(
					"/resources/LeaveReportTemplate/YearWiseSummaryReportMultipleSheetsWithOutCustField.xlsx");
		}

		InputStream is = null;
		Workbook resultWorkbook = null;
		List<String> sheetNames = new ArrayList<>();

		try {
			is = new BufferedInputStream(new FileInputStream(templateFileName));
			for (String leaveTypeName : leaveDataDTO.getLeaveTypeNames()) {
				sheetNames.add(ExcelUtils.getSheetSafeName(leaveTypeName));
			}

			resultWorkbook = transformer.transformMultipleSheetsList(is, leaveDataDTO.getLeaveTypeNames(), sheetNames,
					"sheetName", beans, 0);

			String fileName = "Year Wise Summary Report.xlsx";
			ServletOutputStream outputStream = null;
			outputStream = response.getOutputStream();
			response.setContentType("application/octet-stream");
			response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");

			String user_agent = request.getHeader("user-agent");
			boolean isInternetExplorer = (user_agent.indexOf("MSIE") > -1);

			if (isInternetExplorer) {
				response.setHeader("Content-disposition",
						"attachment; filename=\"" + URLEncoder.encode(fileName, "utf-8").replaceAll("\\+", " ") + "\"");

			} else {
				response.setHeader("Content-disposition",
						"attachment; filename=\"" + MimeUtility.encodeWord(fileName, "utf-8", "Q") + "\"");

			}

			resultWorkbook.write(outputStream);
			if (outputStream != null) {
				outputStream.flush();
				outputStream.close();
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
		tempFolder.delete();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.web.controller.LeaveReportsController#
	 * genYearWiseSummarryReportExcelFile
	 * (com.payasia.common.form.LeaveReportsForm,
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@RequestMapping(value = "/genYearWiseSummarryExcelReport.html", method = RequestMethod.POST)
	@Override
	public void genYearWiseSummarryExcelReport(
			@ModelAttribute(value = "LeaveReportsForm") LeaveReportsForm leaveReportsForm,
			@RequestParam(value = "dataDictionaryIds", required = true) String[] dataDictionaryIds,
			HttpServletRequest request, HttpServletResponse response, Locale locale) {

		if ("checked".equals(leaveReportsForm.getMultiSheet())) {
			genYearWiseSummarryReportExcelFileInMultipleSheets(leaveReportsForm, request, response, locale,
					dataDictionaryIds);
		} else {
			genYearWiseSummarryReportExcelFile(leaveReportsForm, request, response, locale, dataDictionaryIds);
		}

	}

	@Override
	@RequestMapping(value = "/genLeaveBalAsOnDayCustReport.html", method = RequestMethod.POST)
	public @ResponseBody byte[] genLeaveBalAsOnDayCustReport(
			@ModelAttribute(value = "LeaveReportsForm") LeaveReportsForm leaveReportsForm, HttpServletResponse response,
			HttpServletRequest request) {
		UUID uuid = UUID.randomUUID();
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		Long employeeId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		LeaveReportDTO leaveReportDTO = leaveReportsLogic.genLeaveBalAsOnDayCustReportPDF(companyId, employeeId,
				leaveReportsForm, false);

		response.reset();
		String mimeType = URLConnection.guessContentTypeFromName("LeaveBalanceAsOnDayCustomEmpPerPage" + uuid + ".pdf");
		response.setContentType("application/" + mimeType);
		response.setContentLength(leaveReportDTO.getLeaveBalAsOnDayCustReportByteFile().length);
		String filename = "LeaveBalanceAsOnDayCustomEmpPerPage" + uuid + ".pdf";

		response.setHeader("Content-Disposition", "attachment;filename=" + filename);

		return leaveReportDTO.getLeaveBalAsOnDayCustReportByteFile();
	}

	@Override
	@RequestMapping(value = "/getLeavePreferenceDetail.html", method = RequestMethod.POST)
	@ResponseBody
	public String getLeavePreferenceDetail(HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		String companyCode = (String) request.getSession().getAttribute(PayAsiaSessionAttributes.EMPLOYEE_COMPANY_CODE);
		boolean isAdmin = true;
		LeavePreferenceForm leavePreferenceForm = leaveReportsLogic.getLeavePreferenceDetail(companyId, companyCode,
				isAdmin);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(leavePreferenceForm, jsonConfig);
		return jsonObject.toString();

	}

	@Override
	@RequestMapping(value = "/getYearList.html", method = RequestMethod.POST)
	@ResponseBody
	public String getYearList(HttpServletRequest request, Locale locale) {
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		boolean isManager = false;
		List<EmployeeLeaveSchemeTypeDTO> yearList = leaveReportsLogic.getDistinctYears(companyId, isManager);

		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(yearList, jsonConfig);
		return jsonObject.toString();
	}

	@RequestMapping(value = "/genLeaveHeadcountExcelReport.html", method = RequestMethod.POST)
	@Override
	public void genLeaveHeadcountExcelReport(
			@ModelAttribute(value = "LeaveReportsForm") LeaveReportsForm leaveReportsForm, HttpServletRequest request,
			HttpServletResponse response, Locale locale) {
		UUID uuid = UUID.randomUUID();
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long employeeId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		Boolean isManager = false;
		LeaveReportDTO leaveDataDTO = leaveReportsLogic.genLeaveHeadcountReport(employeeId, companyId, leaveReportsForm,
				isManager);
		String header = "Leave Headcount Report";

		Map<String, Object> beans = new HashMap<String, Object>();
		beans.put("header", header);
		beans.put("leaveHeadCountHeaderDTOMap", leaveDataDTO.getLeaveHeadCountHeaderDTOMap());
		beans.put("leaveHeadCountEmpDataListMap", leaveDataDTO.getLeaveHeadCountEmpDataListMap());
		beans.put("companyCodeList", leaveDataDTO.getCompanyCodeList());
		beans.put("currentDate", DateUtils.timeStampToStringWithTime(DateUtils.getCurrentTimestampWithTime()));

		XLSTransformer transformer = new XLSTransformer();
		File tempFolder = new File(PAYASIA_TEMP_PATH + "/leavereport/" + uuid);
		tempFolder.mkdirs();
		String templateFileName = servletContext
				.getRealPath("/resources/LeaveReportTemplate/LeaveHeadcountReportMultipleSheets.xlsx");

		InputStream is = null;
		Workbook resultWorkbook = null;
		List<String> sheetNames = new ArrayList<>();

		try {
			is = new BufferedInputStream(new FileInputStream(templateFileName));
			for (String companyCode : leaveDataDTO.getCompanyCodeList()) {
				sheetNames.add(ExcelUtils.getSheetSafeName(companyCode));
			}

			resultWorkbook = transformer.transformMultipleSheetsList(is, leaveDataDTO.getCompanyCodeList(), sheetNames,
					"sheetName", beans, 0);

			String fileName = "Leave Headcount Report.xlsx";
			ServletOutputStream outputStream = null;
			outputStream = response.getOutputStream();
			response.setContentType("application/octet-stream");
			response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");

			String user_agent = request.getHeader("user-agent");
			boolean isInternetExplorer = (user_agent.indexOf("MSIE") > -1);

			if (isInternetExplorer) {
				response.setHeader("Content-disposition",
						"attachment; filename=\"" + URLEncoder.encode(fileName, "utf-8").replaceAll("\\+", " ") + "\"");

			} else {
				response.setHeader("Content-disposition",
						"attachment; filename=\"" + MimeUtility.encodeWord(fileName, "utf-8", "Q") + "\"");

			}

			resultWorkbook.write(outputStream);
			if (outputStream != null) {
				outputStream.flush();
				outputStream.close();
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
		tempFolder.delete();
	}

	@RequestMapping(value = "/genLeaveHeadcountPdfReport.html", method = RequestMethod.POST)
	@Override
	public void genLeaveHeadcountPdfReport(
			@ModelAttribute(value = "LeaveReportsForm") LeaveReportsForm leaveReportsForm, HttpServletRequest request,
			HttpServletResponse response, Locale locale) {
		UUID uuid = UUID.randomUUID();
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long employeeId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		Boolean isManager = false;
		LeaveReportDTO leaveDataDTO = leaveReportsLogic.genLeaveHeadcountReport(employeeId, companyId, leaveReportsForm,
				isManager);
		String header = "Leave Headcount Report";

		Map<String, Object> beans = new HashMap<String, Object>();
		beans.put("header", header);
		beans.put("leaveHeadCountHeaderDTOMap", leaveDataDTO.getLeaveHeadCountHeaderDTOMap());
		beans.put("leaveHeadCountEmpDataListMap", leaveDataDTO.getLeaveHeadCountEmpDataListMap());
		beans.put("companyCodeList", leaveDataDTO.getCompanyCodeList());
		beans.put("currentDate", DateUtils.timeStampToStringWithTime(DateUtils.getCurrentTimestampWithTime()));

		XLSTransformer transformer = new XLSTransformer();
		File tempDestPdfFile = null;
		File tempDestExcelFile = null;
		File tempFolder = new File(PAYASIA_TEMP_PATH + "/leavereport/" + uuid);
		tempFolder.mkdirs();
		String templateFileName = servletContext
				.getRealPath("/resources/LeaveReportTemplate/LeaveHeadcountReportMultipleSheets For PDF.xlsx");
		String destFileNameExcel = PAYASIA_TEMP_PATH + "/LeaveHeadCountReport" + uuid + ".xlsx";

		String destFileNamePDF = PAYASIA_TEMP_PATH + "/LeaveHeadCountReport" + uuid + ".pdf";

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

			String fileName = "Leave HeadCount Report";
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

	@Override
	@RequestMapping(value = "/switchCompanyList.html", method = RequestMethod.POST)
	@ResponseBody
	public String getSwitchCompanyList(
			@RequestParam(value = "searchCondition", required = false) String searchCondition,
			@RequestParam(value = "searchText", required = false) String searchText,
			@RequestParam(value = "groupName", required = false) String groupName, HttpServletRequest request) {
		Long employeeId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		PageRequest pageDTO = new PageRequest();
		SortCondition sortDTO = new SortCondition();

		SwitchCompanyResponse response = leaveReportsLogic.getSwitchCompanyList(pageDTO, sortDTO, employeeId,
				searchCondition, searchText, groupName);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(response, jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return null;

	}

	@Override
	@RequestMapping(value = "/orderedCompanyList.html", method = RequestMethod.POST)
	@ResponseBody
	public String getOrderedCompanyList(
			@RequestParam(value = "searchCondition", required = false) String searchCondition,
			@RequestParam(value = "searchText", required = false) String searchText,
			@RequestParam(value = "groupName", required = false) String groupName, HttpServletRequest request) {
		Long employeeId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		PageRequest pageDTO = new PageRequest();
		SortCondition sortDTO = new SortCondition();

		SwitchCompanyResponse response = leaveReportsLogic.getOrderedCompanyList(pageDTO, sortDTO, employeeId,
				searchCondition, searchText, groupName);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(response, jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return null;

	}

}
