package com.payasia.web.controller.impl;

import java.io.File;
import java.io.IOException;
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
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.payasia.common.dto.LionTimesheetReportDTO;
import com.payasia.common.dto.LionTimesheetSummaryReportDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.LionTimesheetReportsForm;
import com.payasia.common.util.DateUtils;
import com.payasia.logic.LionTimesheetReportsLogic;
import com.payasia.web.controller.LionTimesheetReportsController;
import com.payasia.web.util.PayAsiaSessionAttributes;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.jxls.exception.ParsePropertyException;
import net.sf.jxls.transformer.XLSTransformer;

@Controller
/*@RequestMapping(value = { "/employee/lionTimesheetReports", "/admin/lionTimesheetReports" })*/
public class LionTimesheetReportsControllerImpl implements LionTimesheetReportsController {
	/** The Constant logger. */
	private static final Logger LOGGER = Logger.getLogger(LionTimesheetReportsControllerImpl.class);

	/** The lionTimesheetReportsLogic. */
	@Resource
	LionTimesheetReportsLogic lionTimesheetReportsLogic;

	/** The payasia temp path. */
	@Value("#{payasiaptProperties['payasia.temp.path']}")
	private String PAYASIA_TEMP_PATH;

	/** The document convertor path. */
	@Value("#{payasiaptProperties['payasia.document.converter.path']}")
	private String DOCUMENT_CONVERTOR_PATH;

	@Resource
	MessageSource messageSource;
	
	@Autowired
	private ServletContext servletContext;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.web.controller.IngersollOTTimesheetReportsController#
	 * otBatchList(javax .servlet.http.HttpServletRequest)
	 */
	@Override
	@RequestMapping(value = {"/employee/lionTimesheetReports/otBatchList.html", "/admin/lionTimesheetReports/otBatchList.html"}, method = RequestMethod.POST)
	@ResponseBody
	public String otBatchList(HttpServletRequest request, @RequestParam(value = "year", required = false) int year) {
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		List<LionTimesheetReportsForm> otBatchList = lionTimesheetReportsLogic.otBatchList(companyId, year);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(otBatchList, jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value =  {"/employee/lionTimesheetReports/locationFieldList.html", "/admin/lionTimesheetReports/locationFieldList.html"}, method = RequestMethod.POST)
	@ResponseBody
	public String locationFieldList(HttpServletRequest request) {
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		List<LionTimesheetReportsForm> locationFieldList = lionTimesheetReportsLogic
				.getEmpDynlocationFieldList(companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(locationFieldList, jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/admin/lionTimesheetReports/lionEmployeeList.html", method = RequestMethod.POST)
	@ResponseBody
	public String lionEmployeeList(HttpServletRequest request) {
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long employeeId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		List<LionTimesheetReportsForm> lionEmployeeList = lionTimesheetReportsLogic
				.lionTimesheetEmployeeList(employeeId, companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(lionEmployeeList, jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/admin/lionTimesheetReports/lionTimesheetReviewerList.html", method = RequestMethod.POST)
	@ResponseBody
	public String lionTimesheetReviewerList(HttpServletRequest request) {
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long employeeId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		List<LionTimesheetReportsForm> lionEmployeeList = lionTimesheetReportsLogic
				.lionTimesheetReviewerList(employeeId, companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(lionEmployeeList, jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/employee/lionTimesheetReports/lionTimesheetEmpListForManager.html", method = RequestMethod.POST)
	@ResponseBody
	public String lionTimesheetEmpListForManager(HttpServletRequest request) {
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long employeeId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		List<LionTimesheetReportsForm> lionEmployeeList = lionTimesheetReportsLogic
				.lionTimesheetEmpListForManager(employeeId, companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(lionEmployeeList, jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = {"/employee/lionTimesheetReports/showTimesheetSummaryReport.html", "/admin/lionTimesheetReports/showTimesheetSummaryReport.html"} , method = RequestMethod.POST)
	@ResponseBody
	public String showTimesheetSummaryReport(
			@ModelAttribute(value = "lionTimesheetReportsForm") LionTimesheetReportsForm lionTimesheetReportsForm,
			@RequestParam(value = "dataDictionaryIds", required = false) String[] dataDictionaryIds,
			HttpServletRequest request, HttpServletResponse response, Locale locale) {
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		Long employeeId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		Boolean isManager = false;
		if (lionTimesheetReportsForm.isManagerRole()) {
			isManager = true;
		}
		LionTimesheetReportDTO lionTimesheetReportDTO = lionTimesheetReportsLogic.showTimesheetSummaryReport(companyId,
				employeeId, lionTimesheetReportsForm, isManager, dataDictionaryIds);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("aaData", lionTimesheetReportDTO.getLionTimesheetStatusReportDTOs());
		jsonObject.put("aoColumns", lionTimesheetReportDTO.getTimesheetHeaderDTOs());
		String data = jsonObject.toString();
		return data;

	}

	@RequestMapping(value = {"/employee/lionTimesheetReports/genTimesheetSummaryReportExcelFile.html", "/admin/lionTimesheetReports/genTimesheetSummaryReportExcelFile.html"}, method = RequestMethod.POST)
	@Override
	public void genTimesheetSummaryReportExcelFile(
			@ModelAttribute(value = "lionTimesheetReportsForm") LionTimesheetReportsForm lionTimesheetReportsForm,
			@RequestParam(value = "dataDictionaryIds", required = true) String[] dataDictionaryIds,
			HttpServletRequest request, HttpServletResponse response) {
		UUID uuid = UUID.randomUUID();
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long employeeId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		Boolean isManager = false;
		if (lionTimesheetReportsForm.isManagerRole()) {
			isManager = true;
		}
		LionTimesheetReportDTO lionTimesheetReportDTO = lionTimesheetReportsLogic.showTimesheetSummaryReport(companyId,
				employeeId, lionTimesheetReportsForm, isManager, dataDictionaryIds);

		LionTimesheetSummaryReportDTO lionBatchReportDTO = lionTimesheetReportsLogic.getTimesheetBatchName(companyId,
				lionTimesheetReportsForm.getFromBatchId());

		String header = "eTimesheet Summary Report";
		String subHeader = "Period: From " + lionBatchReportDTO.getFileFromBatchDate() + " to "
				+ lionBatchReportDTO.getFileToBatchDate();

		Map<String, Object> beans = new HashMap<String, Object>();
		beans.put("header", header);
		beans.put("subHeader1", subHeader);
		beans.put("totalEmployeeCount", "Total Employees: " + lionTimesheetReportDTO.getTotalEmployeesCount());
		beans.put("currentDate",
				"Timestamp: " + DateUtils.timeStampToStringWithTime(DateUtils.getCurrentTimestampWithTime()));
		beans.put("timesheetStatusList", lionTimesheetReportDTO.getLionTimesheetStatusReportDTOs());

		if (lionTimesheetReportDTO.getDataDictNameList() != null
				&& lionTimesheetReportDTO.getDataDictNameList().size() > 0) {
			beans.put("custFieldColNameList", lionTimesheetReportDTO.getDataDictNameList());
		}
		beans.put("lundinCustomDataList", lionTimesheetReportDTO.getLeaveCustomDataDTOs());
		XLSTransformer transformer = new XLSTransformer();
		File tempFolder = new File(PAYASIA_TEMP_PATH + "/LionReportTemplate/" + uuid);
		tempFolder.mkdirs();
		File tempDestFile = null;
		String templateFileName = "";

		if (lionTimesheetReportDTO.getDataDictNameList() != null
				&& lionTimesheetReportDTO.getDataDictNameList().size() > 0) {
			templateFileName = servletContext
					.getRealPath("/resources/LionReportTemplate/LionTimesheetStatusReport.xlsx");
		} else {
			templateFileName = servletContext
					.getRealPath("/resources/LionReportTemplate/LionTimesheetStatusReportWOCustomFields.xlsx");
		}
		String destFileName = PAYASIA_TEMP_PATH + "/LionReportTemplate/" + uuid + "/eTimesheet Summary Report_"
				+ lionBatchReportDTO.getFileBatchName() + uuid + ".xlsx";
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

			String fileName = "eTimesheet Summary Report_" + lionBatchReportDTO.getFileBatchName();
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

	@RequestMapping(value ={"/employee/lionTimesheetReports/genTimesheetSummaryReportPDF.html", "/admin/lionTimesheetReports/genTimesheetSummaryReportPDF.html"} , method = RequestMethod.POST)
	@Override
	public void genTimesheetSummaryReportPDF(
			@ModelAttribute(value = "lionTimesheetReportsForm") LionTimesheetReportsForm lionTimesheetReportsForm,
			@RequestParam(value = "dataDictionaryIds", required = true) String[] dataDictionaryIds,
			HttpServletRequest request, HttpServletResponse response) {
		UUID uuid = UUID.randomUUID();
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long employeeId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		Boolean isManager = false;
		if (lionTimesheetReportsForm.isManagerRole()) {
			isManager = true;
		}
		LionTimesheetReportDTO lionTimesheetReportDTO = lionTimesheetReportsLogic.showTimesheetSummaryReport(companyId,
				employeeId, lionTimesheetReportsForm, isManager, dataDictionaryIds);

		LionTimesheetSummaryReportDTO lionBatchReportDTO = lionTimesheetReportsLogic.getTimesheetBatchName(companyId,
				lionTimesheetReportsForm.getFromBatchId());

		String header = "eTimesheet Summary Report";
		String subHeader = "Period: From " + lionBatchReportDTO.getFileFromBatchDate() + " to "
				+ lionBatchReportDTO.getFileToBatchDate();

		Map<String, Object> beans = new HashMap<String, Object>();
		beans.put("header", header);
		beans.put("subHeader1", subHeader);
		beans.put("totalEmployeeCount", "Total Employees: " + lionTimesheetReportDTO.getTotalEmployeesCount());
		beans.put("currentDate",
				"Timestamp: " + DateUtils.timeStampToStringWithTime(DateUtils.getCurrentTimestampWithTime()));
		beans.put("timesheetStatusList", lionTimesheetReportDTO.getLionTimesheetStatusReportDTOs());

		if (lionTimesheetReportDTO.getDataDictNameList() != null
				&& lionTimesheetReportDTO.getDataDictNameList().size() > 0) {
			beans.put("custFieldColNameList", lionTimesheetReportDTO.getDataDictNameList());
		}
		beans.put("lundinCustomDataList", lionTimesheetReportDTO.getLeaveCustomDataDTOs());
		XLSTransformer transformer = new XLSTransformer();

		File tempFolder = new File(PAYASIA_TEMP_PATH + "/LionReportTemplate/" + uuid);
		tempFolder.mkdirs();
		String templateFileName = "";

		if (lionTimesheetReportDTO.getDataDictNameList() != null
				&& lionTimesheetReportDTO.getDataDictNameList().size() > 0) {
			templateFileName = servletContext
					.getRealPath("/resources/LionReportTemplate/LionTimesheetStatusReport.xlsx");
		} else {
			templateFileName = servletContext
					.getRealPath("/resources/LionReportTemplate/LionTimesheetStatusReportWOCustomFields.xlsx");
		}

		String destFileNameExcel = PAYASIA_TEMP_PATH + "/eTimesheetSummaryReport-"
				+ lionBatchReportDTO.getFileBatchName() + uuid + ".xlsx";
		String destFileNamePDF = PAYASIA_TEMP_PATH + "/eTimesheetSummaryReport-" + lionBatchReportDTO.getFileBatchName()
				+ uuid + ".pdf";
		Process process = null;
		try {
			transformer.transformXLS(templateFileName, beans, destFileNameExcel.replaceAll(" ", "-"));

			ArrayList<String> cmdList = new ArrayList<String>();

			String file1 = destFileNameExcel.replaceAll(" ", "-");
			String file2 = destFileNamePDF.replaceAll(" ", "-");

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
			Path path = Paths.get(destFileNamePDF.replaceAll(" ", "-"));
			byte[] data = Files.readAllBytes(path);

			String fileName = "/eTimesheet Summary Report-" + lionBatchReportDTO.getFileBatchName();
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
			File tempDestPdfFile = new File(destFileNamePDF.replaceAll(" ", "-"));
			if (tempDestPdfFile != null && tempDestPdfFile.exists()) {
				tempDestPdfFile.delete();
			}
			File tempDestExcelFile = new File(destFileNameExcel.replaceAll(" ", "-"));
			if (tempDestExcelFile != null && tempDestPdfFile.exists()) {
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
	@RequestMapping(value = {"/employee/lionTimesheetReports/showLionTimesheetDetailsSummaryReport.html", "/admin/lionTimesheetReports/showLionTimesheetDetailsSummaryReport.html"} , method = RequestMethod.POST)
	@ResponseBody
	public String showLionTimesheetDetailsSummaryReport(
			@ModelAttribute(value = "lionTimesheetDetailListingReportForm") LionTimesheetReportsForm lionTimesheetDetailListingReportForm,
			@RequestParam(value = "dataDictionaryIds", required = false) String[] dataDictionaryIds,
			HttpServletRequest request, HttpServletResponse response, Locale locale) {
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		Long employeeId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		Boolean isManager = false;
		if (lionTimesheetDetailListingReportForm.isManagerRole()) {
			isManager = true;
		}
		LionTimesheetReportDTO lionTimesheetReportDTO = lionTimesheetReportsLogic.showTimesheetSummaryReportDetails(
				companyId, employeeId, lionTimesheetDetailListingReportForm, isManager, dataDictionaryIds);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("aaData", lionTimesheetReportDTO.getLionTimesheetStatusReportDTOs());
		jsonObject.put("aoColumns", lionTimesheetReportDTO.getTimesheetHeaderDTOs());
		String data = jsonObject.toString();
		return data;

	}

	@RequestMapping(value = {"/employee/lionTimesheetReports/genTimesheetSummaryReportExcelFileDetails.html", "/admin/lionTimesheetReports/genTimesheetSummaryReportExcelFileDetails.html"} , method = RequestMethod.POST)
	@Override
	public void genTimesheetSummaryReportExcelFileDetails(
			@ModelAttribute(value = "lionTimesheetDetailListingReportForm") LionTimesheetReportsForm lionTimesheetDetailListingReportForm,
			@RequestParam(value = "dataDictionaryIds", required = true) String[] dataDictionaryIds,
			HttpServletRequest request, HttpServletResponse response) {
		UUID uuid = UUID.randomUUID();
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long employeeId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		Boolean isManager = false;
		String fromBatchDate = null;
		String toBatchDate = null;
		String header = null;
		String fileName = null;
		if (lionTimesheetDetailListingReportForm.isManagerRole()) {
			isManager = true;
		}
		LionTimesheetReportDTO lionTimesheetReportDTO = lionTimesheetReportsLogic.showTimesheetSummaryReportDetails(
				companyId, employeeId, lionTimesheetDetailListingReportForm, isManager, dataDictionaryIds);
		
		if(lionTimesheetDetailListingReportForm.getApprovedFromDate().isEmpty() || lionTimesheetDetailListingReportForm.getApprovedToDate().isEmpty()){
			LionTimesheetSummaryReportDTO lionBatchReportDTO = lionTimesheetReportsLogic.getTimesheetBatchName(companyId,
					lionTimesheetDetailListingReportForm.getFromBatchId());
			fromBatchDate =  lionBatchReportDTO.getFileFromBatchDate();
			toBatchDate = lionBatchReportDTO.getFileToBatchDate();
			 header = "eTimesheet Detail List Report_" + fromBatchDate + "_To_"+ toBatchDate;
			 fileName = "eTimesheet Detail List Report_" + fromBatchDate+ "_To_" + toBatchDate;
		}else{
			 header = "eTimesheet Detail List Report_" + lionTimesheetDetailListingReportForm.getApprovedFromDate() + "_To_"
						+ lionTimesheetDetailListingReportForm.getApprovedToDate();
			 fileName = "eTimesheet Detail List Report_" + lionTimesheetDetailListingReportForm.getApprovedFromDate()
				+ "_To_" + lionTimesheetDetailListingReportForm.getApprovedToDate();
		}
		

		Map<String, Object> beans = new HashMap<String, Object>();
		beans.put("header", header);

		beans.put("currentDate",
				"Timestamp: " + DateUtils.timeStampToStringWithTime(DateUtils.getCurrentTimestampWithTime()));
		beans.put("timesheetStatusList", lionTimesheetReportDTO.getLionTimesheetStatusReportDTOs());

		if (lionTimesheetReportDTO.getDataDictNameList() != null
				&& lionTimesheetReportDTO.getDataDictNameList().size() > 0) {
			beans.put("custFieldColNameList", lionTimesheetReportDTO.getDataDictNameList());
		}
		beans.put("lundinCustomDataList", lionTimesheetReportDTO.getTimesheetHeaderDTOs());
		XLSTransformer transformer = new XLSTransformer();
		File tempFolder = new File(PAYASIA_TEMP_PATH + "/LionReportTemplate/" + uuid);
		tempFolder.mkdirs();
		File tempDestFile = null;
		String templateFileName = "";

		if (lionTimesheetReportDTO.getDataDictNameList() != null
				&& lionTimesheetReportDTO.getDataDictNameList().size() > 0) {
			templateFileName = servletContext
					.getRealPath("/resources/LionReportTemplate/LionTimesheetStatusReportDetails.xlsx");
		} else {
			templateFileName = servletContext
					.getRealPath("/resources/LionReportTemplate/LionTimesheetStatusReportWOCustomFieldsDetails.xlsx");
		}
		String destFileName = PAYASIA_TEMP_PATH + "/LionReportTemplate/" + uuid + "/eTimesheet Summary Report_"
				+ lionTimesheetDetailListingReportForm.getApprovedFromDate() + "_"
				+ lionTimesheetDetailListingReportForm.getApprovedToDate() + uuid + ".xlsx";
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

	@RequestMapping(value = "/admin/lionTimesheetReports/genTimesheetSummaryReportPDFDetails.html", method = RequestMethod.POST)
	@Override
	public void genTimesheetSummaryReportPDFDetails(
			@ModelAttribute(value = "lionTimesheetDetailListingReportForm") LionTimesheetReportsForm lionTimesheetDetailListingReportForm,
			@RequestParam(value = "dataDictionaryIds", required = true) String[] dataDictionaryIds,
			HttpServletRequest request, HttpServletResponse response) {
		UUID uuid = UUID.randomUUID();
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long employeeId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		Boolean isManager = false;
		if (lionTimesheetDetailListingReportForm.isManagerRole()) {
			isManager = true;
		}
		LionTimesheetReportDTO lionTimesheetReportDTO = lionTimesheetReportsLogic.showTimesheetSummaryReportDetails(
				companyId, employeeId, lionTimesheetDetailListingReportForm, isManager, dataDictionaryIds);

		String header = "eTimesheet Detail List Report_" + lionTimesheetDetailListingReportForm.getApprovedFromDate() + "_"
				+ lionTimesheetDetailListingReportForm.getApprovedToDate();

		Map<String, Object> beans = new HashMap<String, Object>();
		beans.put("header", header);

		beans.put("currentDate",
				"Timestamp: " + DateUtils.timeStampToStringWithTime(DateUtils.getCurrentTimestampWithTime()));
		beans.put("timesheetStatusList", lionTimesheetReportDTO.getLionTimesheetStatusReportDTOs());

		if (lionTimesheetReportDTO.getDataDictNameList() != null
				&& lionTimesheetReportDTO.getDataDictNameList().size() > 0) {
			beans.put("custFieldColNameList", lionTimesheetReportDTO.getDataDictNameList());
		}
		beans.put("lundinCustomDataList", lionTimesheetReportDTO.getLeaveCustomDataDTOs());
		XLSTransformer transformer = new XLSTransformer();

		File tempFolder = new File(PAYASIA_TEMP_PATH + "/LionReportTemplate/" + uuid);
		tempFolder.mkdirs();
		String templateFileName = "";

		if (lionTimesheetReportDTO.getDataDictNameList() != null
				&& lionTimesheetReportDTO.getDataDictNameList().size() > 0) {
			templateFileName = servletContext
					.getRealPath("/resources/LionReportTemplate/LionTimesheetStatusReportDetails.xlsx");
		} else {
			templateFileName = servletContext
					.getRealPath("/resources/LionReportTemplate/LionTimesheetStatusReportWOCustomFieldsDetails.xlsx");
		}

		String destFileNameExcel = PAYASIA_TEMP_PATH + "/eTimesheetSummaryReport-"
				+ lionTimesheetDetailListingReportForm.getApprovedFromDate() + "-"
				+ lionTimesheetDetailListingReportForm.getApprovedToDate() + uuid + ".xlsx";
		String destFileNamePDF = PAYASIA_TEMP_PATH + "/eTimesheetSummaryReport-"
				+ lionTimesheetDetailListingReportForm.getApprovedFromDate() + "-"
				+ lionTimesheetDetailListingReportForm.getApprovedToDate() + uuid + ".pdf";
		Process process = null;
		try {
			transformer.transformXLS(templateFileName, beans, destFileNameExcel.replaceAll(" ", "-"));

			ArrayList<String> cmdList = new ArrayList<String>();

			String file1 = destFileNameExcel.replaceAll(" ", "-");
			String file2 = destFileNamePDF.replaceAll(" ", "-");

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

			Path path = Paths.get(destFileNamePDF.replaceAll(" ", "-"));
			byte[] data = Files.readAllBytes(path);

			String fileName = "/eTimesheet Detail List Report-" + lionTimesheetDetailListingReportForm.getApprovedFromDate()
					+ "-" + lionTimesheetDetailListingReportForm.getApprovedToDate();
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
			File tempDestPdfFile = new File(destFileNamePDF.replaceAll(" ", "-"));
			if (tempDestPdfFile != null) {
				tempDestPdfFile.delete();
			}
			File tempDestExcelFile = new File(destFileNameExcel.replaceAll(" ", "-"));
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
