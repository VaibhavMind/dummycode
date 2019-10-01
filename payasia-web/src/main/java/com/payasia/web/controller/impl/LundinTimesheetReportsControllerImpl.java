/**
 * The Class LundinTimesheetReportsControllerImpl.
 * 
 * @author vivek jain
 */
package com.payasia.web.controller.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
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

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.EmployeeShortListDTO;
import com.payasia.common.dto.LundinDailyPaidTimesheetDTO;
import com.payasia.common.dto.LundinTimesheetDTO;
import com.payasia.common.dto.LundinTimesheetReportDTO;
import com.payasia.common.dto.LundinTimewritingDeptReportDTO;
import com.payasia.common.dto.LundinTimewritingReportDTO;
import com.payasia.common.dto.TimesheetFormPdfDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.EmployeeListForm;
import com.payasia.common.form.LeaveReportsResponse;
import com.payasia.common.form.LundinTimesheetReportsForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.DateUtils;
import com.payasia.logic.GeneralLogic;
import com.payasia.logic.LundinPendingTimesheetLogic;
import com.payasia.logic.LundinTimesheetPrintPDFLogic;
import com.payasia.logic.LundinTimesheetReportsLogic;
import com.payasia.web.controller.LundinTimesheetReportsController;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.jxls.exception.ParsePropertyException;
import net.sf.jxls.transformer.XLSTransformer;

@Controller
public class LundinTimesheetReportsControllerImpl implements
		LundinTimesheetReportsController {
	/** The Constant logger. */
	private static final Logger LOGGER = Logger
			.getLogger(LundinTimesheetReportsControllerImpl.class);

	/** The lundinTimesheetReportsLogic. */
	@Resource
	LundinTimesheetReportsLogic lundinTimesheetReportsLogic;

	/** The payasia temp path. */
	@Value("#{payasiaptProperties['payasia.temp.path']}")
	private String PAYASIA_TEMP_PATH;

	/** The document convertor path. */
	@Value("#{payasiaptProperties['payasia.document.converter.path']}")
	private String DOCUMENT_CONVERTOR_PATH;

	@Resource
	MessageSource messageSource;

	@Resource
	LundinPendingTimesheetLogic lundinPendingTimesheetLogic;

	@Resource
	LundinTimesheetPrintPDFLogic lundinTimesheetPrintPDFLogic;

	@Resource
	GeneralLogic generalLogic;
	
	@Autowired
	private ServletContext servletContext;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.web.controller.IngersollOTTimesheetReportsController#
	 * searchEmployee(java .lang.String, java.lang.String, int, int,
	 * java.lang.String, java.lang.String, java.lang.String,
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.web.controller.IngersollOTTimesheetReportsController#
	 * otBatchList(javax .servlet.http.HttpServletRequest)
	 */
	
	@Override
	@RequestMapping(value = "/admin/lundinTimesheetReports/otBatchList.html", method = RequestMethod.POST)
	@ResponseBody
	public String otBatchList(HttpServletRequest request,
			@RequestParam(value = "year", required = false) int year) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		List<LundinTimesheetReportsForm> otBatchList = lundinTimesheetReportsLogic
				.otBatchList(companyId, year);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(otBatchList, jsonConfig);
		return jsonObject.toString();
	}
	
	@Override
	@RequestMapping(value = "/employee/lundinTimesheetReports/otBatchList.html", method = RequestMethod.POST)
	@ResponseBody
	public String otBatchEmpList(HttpServletRequest request,
			@RequestParam(value = "year", required = false) int year) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		List<LundinTimesheetReportsForm> otBatchList = lundinTimesheetReportsLogic
				.otBatchList(companyId, year);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(otBatchList, jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/admin/lundinTimesheetReports/lundinDepartmentList.html", method = RequestMethod.POST)
	@ResponseBody
	public String lundinDepartmentList(HttpServletRequest request) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		List<LundinTimesheetReportsForm> otBatchList = lundinTimesheetReportsLogic
				.lundinDepartmentList(companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(otBatchList, jsonConfig);
		return jsonObject.toString();
	}
	
	@Override
	@RequestMapping(value = "/employee/lundinTimesheetReports/lundinDepartmentList.html", method = RequestMethod.POST)
	@ResponseBody
	public String lundinEmpDepartmentList(HttpServletRequest request) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
        List<LundinTimesheetReportsForm> otBatchList = lundinTimesheetReportsLogic
				.lundinDepartmentList(companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(otBatchList, jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/admin/lundinTimesheetReports/lundinBlockList.html", method = RequestMethod.POST)
	@ResponseBody
	public String lundinBlockList(HttpServletRequest request) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		List<LundinTimesheetReportsForm> otBatchList = lundinTimesheetReportsLogic
				.lundinBlockList(companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(otBatchList, jsonConfig);
		return jsonObject.toString();
	}
	
	@Override
	@RequestMapping(value = "/employee/lundinTimesheetReports/lundinBlockList.html", method = RequestMethod.POST)
	@ResponseBody
	public String lundinEmpBlockList(HttpServletRequest request) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
        List<LundinTimesheetReportsForm> otBatchList = lundinTimesheetReportsLogic
				.lundinBlockList(companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(otBatchList, jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/admin/lundinTimesheetReports/lundinAFEList.html", method = RequestMethod.POST)
	@ResponseBody
	public String lundinAFEList(HttpServletRequest request,
			@RequestParam(value = "blockId", required = false) Long blockId) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		List<LundinTimesheetReportsForm> otBatchList = lundinTimesheetReportsLogic
				.lundinAFEList(companyId, blockId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(otBatchList, jsonConfig);
		return jsonObject.toString();
	}
	
	@Override
	@RequestMapping(value = "/employee/lundinTimesheetReports/lundinAFEList.html", method = RequestMethod.POST)
	@ResponseBody
	public String lundinAFEEmpList(HttpServletRequest request,
			@RequestParam(value = "blockId", required = false) Long blockId) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		List<LundinTimesheetReportsForm> otBatchList = lundinTimesheetReportsLogic
				.lundinAFEList(companyId, blockId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(otBatchList, jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/admin/lundinTimesheetReports/lundinEmployeeList.html", method = RequestMethod.POST)
	@ResponseBody
	public String lundinEmployeeList(HttpServletRequest request) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		Long employeeId = Long.parseLong(UserContext.getUserId());
		List<LundinTimesheetReportsForm> otBatchList = lundinTimesheetReportsLogic
				.lundinEmployeeList(employeeId, companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(otBatchList, jsonConfig);
		return jsonObject.toString();
	}
	
	@Override
	@RequestMapping(value = "/employee/lundinTimesheetReports/lundinEmployeeList.html", method = RequestMethod.POST)
	@ResponseBody
	public String lundinEmployeList(HttpServletRequest request) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		Long employeeId = Long.parseLong(UserContext.getUserId());
		List<LundinTimesheetReportsForm> otBatchList = lundinTimesheetReportsLogic
				.lundinEmployeeList(employeeId, companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(otBatchList, jsonConfig);
		return jsonObject.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.web.controller.IngersollOTTimesheetReportsController#
	 * genOvertimeHoursAndAllowanceReportExcelFile
	 * (com.payasia.common.form.IngersollOTTimesheetReportsForm,
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/admin/lundinTimesheetReports/genTimewritingAndCostAllocationReportExcelFile.html", method = RequestMethod.POST)
	public void genTimewritingAndCostAllocationReportExcelFile(
			@ModelAttribute(value = "lundinTimesheetReportsForm") LundinTimesheetReportsForm lundinTimesheetReportsForm,
			HttpServletRequest request, HttpServletResponse response) {

		UUID uuid = UUID.randomUUID();

		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
        Long employeeId = Long.parseLong(UserContext.getUserId());

		LundinTimesheetReportDTO otTimesheetDataDTO = lundinTimesheetReportsLogic
				.genTimewritingAndCostAllocationReportExcelFile(companyId,
						employeeId, lundinTimesheetReportsForm);

		LundinTimewritingReportDTO lundinTimewritingReportDTO = lundinTimesheetReportsLogic
				.getOTBatchName(companyId,
						lundinTimesheetReportsForm.getFromBatchId(),
						lundinTimesheetReportsForm.getToBatchId());

		String batchName = lundinTimewritingReportDTO.getFileBatchName();
		String header = "Timewriting & Cost Allocation Percentage Computation For "
				+ batchName + " Payroll";
		String subHeader = "Period: From "
				+ lundinTimewritingReportDTO.getFromBatchDate() + " to "
				+ lundinTimewritingReportDTO.getToBatchDate();

		Map beans = new HashMap();
		beans.put("header", header);
		beans.put("subHeader", subHeader);
		beans.put("deptCodeTechnicalTimewritingDTOMap",
				otTimesheetDataDTO.getDeptCodeTechnicalTimewritingDTOMap());
		beans.put("deptCodeNonTechnicalTimewritingDTOMap",
				otTimesheetDataDTO.getDeptCodeNonTechnicalTimewritingDTOMap());
		beans.put("empListDepartmentWiseMap",
				otTimesheetDataDTO.getEmpListDepartmentWiseMap());
		beans.put("empTimesheetKeyValueMap",
				otTimesheetDataDTO.getEmpTimesheetKeyValueMap());
		if (otTimesheetDataDTO.getDeptCodeTechnicalTimewritingDTOMap()
				.isEmpty()) {
			beans.put("totalTechnicalManHoursDTOList",
					new ArrayList<LundinTimewritingDeptReportDTO>());
			beans.put("totalTechnicalManHoursWithEffecAllocDTOList",
					new ArrayList<LundinTimewritingDeptReportDTO>());
		} else {
			beans.put("totalTechnicalManHoursDTOList",
					otTimesheetDataDTO.getTotalTechnicalManHoursDTOList());
			beans.put("totalTechnicalManHoursWithEffecAllocDTOList",
					otTimesheetDataDTO
							.getTotalTechnicalManHoursWithEffecAllocDTOList());
		}

		beans.put("totalTechnicalManHoursValMap",
				otTimesheetDataDTO.getTotalTechnicalManHoursValMap());
		beans.put("totalTechnicalManHoursWithEffecAllocMap",
				otTimesheetDataDTO.getTotalTechnicalManHoursWithEffecAllocMap());

		beans.put("employeeSet", otTimesheetDataDTO.getEmployeeColCountList());
		beans.put("nonTechEffecAllocByDeptMap",
				otTimesheetDataDTO.getNonTechEffecAllocByDeptMap());
		beans.put("nonTechFinalEffecAllocByDeptMap",
				otTimesheetDataDTO.getNonTechFinalEffecAllocByDeptMap());
		beans.put("nonTechTotalFinalEffecAllocMap",
				otTimesheetDataDTO.getNonTechTotalFinalEffecAllocMap());
		beans.put("malyasianOpernTechMap",
				otTimesheetDataDTO.getMalyasianOpernTechMap());
		beans.put("blockAfeNTOFCDTOList",
				otTimesheetDataDTO.getBlockAfeNTOFCDTOList());
		beans.put("ntofcTotalDaysValMap",
				otTimesheetDataDTO.getNtofcTotalDaysValMap());

		beans.put("ntofcEffecAllocMap",
				otTimesheetDataDTO.getNtofcEffecAllocMap());
		beans.put("ntofcFinalEffecAllocMap",
				otTimesheetDataDTO.getNtofcFinalEffecAllocMap());
		beans.put("ntofcFinalEffecAllocTotal",
				otTimesheetDataDTO.getNtofcFinalEffecAllocTotal());

		beans.put("ntofcDepartmentCode",
				otTimesheetDataDTO.getNtofcDepartmentCode());
		beans.put("ntofcDepartmentName",
				otTimesheetDataDTO.getNtofcDepartmentName());

		beans.put("currentDate", DateUtils.timeStampToStringWithTime(DateUtils
				.getCurrentTimestampWithTime()));

		XLSTransformer transformer = new XLSTransformer();
		File tempDestFile = null;
		File tempFolder = new File(PAYASIA_TEMP_PATH + "/LundinReportTemplate/"
				+ uuid);
		tempFolder.mkdirs();

		String templateFileName = servletContext.getRealPath(
				"/resources/LundinReportTemplate/Timewriting Report.xlsx");
		String destFileName = PAYASIA_TEMP_PATH + "/LundinReportTemplate/"
				+ uuid + "/Timewriting Report" + uuid + ".xlsx";
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

			String fileName = "Timewriting for " + batchName + " Payroll";
			outputStream = response.getOutputStream();
			response.setContentType("application/octet-stream");
			response.setHeader("Cache-Control",
					"must-revalidate, post-check=0, pre-check=0");

			fileName = fileName + ".xlsx";
			response.setHeader("Content-Disposition", "attachment; filename=\""
					+ fileName + "");

			outputStream = response.getOutputStream();
			outputStream.write(data);
		} catch (IOException ioException) {
			LOGGER.error(ioException.getMessage(), ioException);
			throw new PayAsiaSystemException(ioException.getMessage(),
					ioException);

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
				throw new PayAsiaSystemException(ioException.getMessage(),
						ioException);
			}
		}

	}

	
	@Override
	@RequestMapping(value = "/employee/lundinTimesheetReports/genTimewritingAndCostAllocationReportExcelFile.html", method = RequestMethod.POST)
	public void genTimewritingAndCostAllocationEmpReportExcelFile(
			@ModelAttribute(value = "lundinTimesheetReportsForm") LundinTimesheetReportsForm lundinTimesheetReportsForm,
			HttpServletRequest request, HttpServletResponse response) {

		UUID uuid = UUID.randomUUID();

		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		Long employeeId = Long.parseLong(UserContext.getUserId());

		LundinTimesheetReportDTO otTimesheetDataDTO = lundinTimesheetReportsLogic
				.genTimewritingAndCostAllocationReportExcelFile(companyId,
						employeeId, lundinTimesheetReportsForm);

		LundinTimewritingReportDTO lundinTimewritingReportDTO = lundinTimesheetReportsLogic
				.getOTBatchName(companyId,
						lundinTimesheetReportsForm.getFromBatchId(),
						lundinTimesheetReportsForm.getToBatchId());

		String batchName = lundinTimewritingReportDTO.getFileBatchName();
		String header = "Timewriting & Cost Allocation Percentage Computation For "
				+ batchName + " Payroll";
		String subHeader = "Period: From "
				+ lundinTimewritingReportDTO.getFromBatchDate() + " to "
				+ lundinTimewritingReportDTO.getToBatchDate();

		Map beans = new HashMap();
		beans.put("header", header);
		beans.put("subHeader", subHeader);
		beans.put("deptCodeTechnicalTimewritingDTOMap",
				otTimesheetDataDTO.getDeptCodeTechnicalTimewritingDTOMap());
		beans.put("deptCodeNonTechnicalTimewritingDTOMap",
				otTimesheetDataDTO.getDeptCodeNonTechnicalTimewritingDTOMap());
		beans.put("empListDepartmentWiseMap",
				otTimesheetDataDTO.getEmpListDepartmentWiseMap());
		beans.put("empTimesheetKeyValueMap",
				otTimesheetDataDTO.getEmpTimesheetKeyValueMap());
		if (otTimesheetDataDTO.getDeptCodeTechnicalTimewritingDTOMap()
				.isEmpty()) {
			beans.put("totalTechnicalManHoursDTOList",
					new ArrayList<LundinTimewritingDeptReportDTO>());
			beans.put("totalTechnicalManHoursWithEffecAllocDTOList",
					new ArrayList<LundinTimewritingDeptReportDTO>());
		} else {
			beans.put("totalTechnicalManHoursDTOList",
					otTimesheetDataDTO.getTotalTechnicalManHoursDTOList());
			beans.put("totalTechnicalManHoursWithEffecAllocDTOList",
					otTimesheetDataDTO
							.getTotalTechnicalManHoursWithEffecAllocDTOList());
		}

		beans.put("totalTechnicalManHoursValMap",
				otTimesheetDataDTO.getTotalTechnicalManHoursValMap());
		beans.put("totalTechnicalManHoursWithEffecAllocMap",
				otTimesheetDataDTO.getTotalTechnicalManHoursWithEffecAllocMap());

		beans.put("employeeSet", otTimesheetDataDTO.getEmployeeColCountList());
		beans.put("nonTechEffecAllocByDeptMap",
				otTimesheetDataDTO.getNonTechEffecAllocByDeptMap());
		beans.put("nonTechFinalEffecAllocByDeptMap",
				otTimesheetDataDTO.getNonTechFinalEffecAllocByDeptMap());
		beans.put("nonTechTotalFinalEffecAllocMap",
				otTimesheetDataDTO.getNonTechTotalFinalEffecAllocMap());
		beans.put("malyasianOpernTechMap",
				otTimesheetDataDTO.getMalyasianOpernTechMap());
		beans.put("blockAfeNTOFCDTOList",
				otTimesheetDataDTO.getBlockAfeNTOFCDTOList());
		beans.put("ntofcTotalDaysValMap",
				otTimesheetDataDTO.getNtofcTotalDaysValMap());

		beans.put("ntofcEffecAllocMap",
				otTimesheetDataDTO.getNtofcEffecAllocMap());
		beans.put("ntofcFinalEffecAllocMap",
				otTimesheetDataDTO.getNtofcFinalEffecAllocMap());
		beans.put("ntofcFinalEffecAllocTotal",
				otTimesheetDataDTO.getNtofcFinalEffecAllocTotal());

		beans.put("ntofcDepartmentCode",
				otTimesheetDataDTO.getNtofcDepartmentCode());
		beans.put("ntofcDepartmentName",
				otTimesheetDataDTO.getNtofcDepartmentName());

		beans.put("currentDate", DateUtils.timeStampToStringWithTime(DateUtils
				.getCurrentTimestampWithTime()));

		XLSTransformer transformer = new XLSTransformer();
		File tempDestFile = null;
		File tempFolder = new File(PAYASIA_TEMP_PATH + "/LundinReportTemplate/"
				+ uuid);
		tempFolder.mkdirs();

		String templateFileName = servletContext.getRealPath(
				"/resources/LundinReportTemplate/Timewriting Report.xlsx");
		String destFileName = PAYASIA_TEMP_PATH + "/LundinReportTemplate/"
				+ uuid + "/Timewriting Report" + uuid + ".xlsx";
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

			String fileName = "Timewriting for " + batchName + " Payroll";
			outputStream = response.getOutputStream();
			response.setContentType("application/octet-stream");
			response.setHeader("Cache-Control",
					"must-revalidate, post-check=0, pre-check=0");

			fileName = fileName + ".xlsx";
			response.setHeader("Content-Disposition", "attachment; filename=\""
					+ fileName + "");

			outputStream = response.getOutputStream();
			outputStream.write(data);
		} catch (IOException ioException) {
			LOGGER.error(ioException.getMessage(), ioException);
			throw new PayAsiaSystemException(ioException.getMessage(),
					ioException);

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
				throw new PayAsiaSystemException(ioException.getMessage(),
						ioException);
			}
		}

	}
	
	@Override
	@RequestMapping(value = "/admin/lundinTimesheetReports/showDailyPaidTimesheet", method = RequestMethod.POST)
	@ResponseBody
	public String showDailyPaidTimesheet(
			@ModelAttribute(value = "lundinTimesheetReportsForm") LundinTimesheetReportsForm lundinTimesheetReportsForm,
			@RequestParam(value = "dataDictionaryIds", required = false) String[] dataDictionaryIds,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		Boolean isManager = false;
		LundinTimesheetReportDTO lundinTimesheetReportDTO = lundinTimesheetReportsLogic
				.showDailyPaidTimesheet(companyId, employeeId,
						lundinTimesheetReportsForm, isManager,
						dataDictionaryIds);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("aaData",
				lundinTimesheetReportDTO.getLundinDailyPaidTimesheetDTOs());
		jsonObject.put("aoColumns",
				lundinTimesheetReportDTO.getLundinHeaderDTOs());
		String data = jsonObject.toString();
		return data;

	}
	
	@Override
	@RequestMapping(value = "/employee/lundinTimesheetReports/showDailyPaidTimesheet", method = RequestMethod.POST)
	@ResponseBody
	public String showEmpDailyPaidTimesheet(
			@ModelAttribute(value = "lundinTimesheetReportsForm") LundinTimesheetReportsForm lundinTimesheetReportsForm,
			@RequestParam(value = "dataDictionaryIds", required = false) String[] dataDictionaryIds,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		Boolean isManager = false;
		LundinTimesheetReportDTO lundinTimesheetReportDTO = lundinTimesheetReportsLogic
				.showDailyPaidTimesheet(companyId, employeeId,
						lundinTimesheetReportsForm, isManager,
						dataDictionaryIds);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("aaData",
				lundinTimesheetReportDTO.getLundinDailyPaidTimesheetDTOs());
		jsonObject.put("aoColumns",
				lundinTimesheetReportDTO.getLundinHeaderDTOs());
		String data = jsonObject.toString();
		return data;

	}

	@RequestMapping(value = "/admin/lundinTimesheetReports/genDailyPaidTimesheetExcelFile.html", method = RequestMethod.POST)
	@Override
	public void genDailyPaidTimesheetExcelFile(
			@ModelAttribute(value = "lundinTimesheetReportsForm") LundinTimesheetReportsForm lundinTimesheetReportsForm,
			@RequestParam(value = "dataDictionaryIds", required = true) String[] dataDictionaryIds,
			HttpServletRequest request, HttpServletResponse response) {
		UUID uuid = UUID.randomUUID();
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		Boolean isManager = false;
		LundinTimesheetReportDTO lundinTimesheetReportDTO = lundinTimesheetReportsLogic
				.showDailyPaidTimesheet(companyId, employeeId,
						lundinTimesheetReportsForm, isManager,
						dataDictionaryIds);

		LundinDailyPaidTimesheetDTO dailyPaidDto = lundinTimesheetReportsLogic
				.getDailyPaidBatchName(companyId,
						lundinTimesheetReportsForm.getFromBatchId());

		String header = "Daily Paid Employees Approved Timesheet Report";
		String subHeader1 = "for " + dailyPaidDto.getFileBatchName()
				+ " Payroll ";
		String subHeader2 = "Period: From "
				+ dailyPaidDto.getFileFromBatchDate() + " to "
				+ dailyPaidDto.getFileToBatchDate();

		Map beans = new HashMap();
		beans.put("header", header);
		beans.put("subHeader1", subHeader1);
		beans.put("subHeader2", subHeader2);
		beans.put("totalEmployeeCount", "Total Employees: "
				+ lundinTimesheetReportDTO.getTotalEmployeesCount());
		beans.put(
				"currentDate",
				"Timestamp: "
						+ DateUtils.timeStampToStringWithTime(DateUtils
								.getCurrentTimestampWithTime()));
		beans.put("lundinDailyPaidReportList",
				lundinTimesheetReportDTO.getLundinDailyPaidTimesheetDTOs());

		if (lundinTimesheetReportDTO.getDataDictNameList().size() > 0) {
			beans.put("custFieldColNameList",
					lundinTimesheetReportDTO.getDataDictNameList());
		}
		beans.put("lundinCustomDataList",
				lundinTimesheetReportDTO.getLeaveCustomDataDTOs());
		XLSTransformer transformer = new XLSTransformer();
		File tempFolder = new File(PAYASIA_TEMP_PATH + "/LundinReportTemplate/"
				+ uuid);
		tempFolder.mkdirs();
		File tempDestFile = null;
		String templateFileName = "";

		if (lundinTimesheetReportDTO.getDataDictNameList().size() > 0) {
			templateFileName = servletContext
					.getRealPath(
							"/resources/LundinReportTemplate/LundinDailyPaidTimesheetReport.xlsx");
		} else {
			templateFileName =servletContext
					.getRealPath(
							"/resources/LundinReportTemplate/LundinDailyPaidTimesheetReportWOCustomFields.xlsx");
		}
		String destFileName = PAYASIA_TEMP_PATH + "/LundinReportTemplate/"
				+ uuid + "/Daily Paid Timesheet Report_"
				+ dailyPaidDto.getFileBatchName() + uuid + ".xlsx";
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

			String fileName = "Daily Paid Timesheet Report_"
					+ dailyPaidDto.getFileBatchName();
			outputStream = response.getOutputStream();
			response.setContentType("application/octet-stream");
			response.setHeader("Cache-Control",
					"must-revalidate, post-check=0, pre-check=0");

			fileName = fileName + ".xlsx";
			response.setHeader("Content-Disposition", "attachment; filename=\""
					+ fileName + "");

			outputStream = response.getOutputStream();
			outputStream.write(data);
		} catch (IOException ioException) {
			LOGGER.error(ioException.getMessage(), ioException);
			throw new PayAsiaSystemException(ioException.getMessage(),
					ioException);
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
				throw new PayAsiaSystemException(ioException.getMessage(),
						ioException);
			}
		}

	}
	
	
	
	
	@RequestMapping(value = "/employee/lundinTimesheetReports/genDailyPaidTimesheetExcelFile.html", method = RequestMethod.POST)
	@Override
	public void genDailyPaidEmpTimesheetExcelFile(
			@ModelAttribute(value = "lundinTimesheetReportsForm") LundinTimesheetReportsForm lundinTimesheetReportsForm,
			@RequestParam(value = "dataDictionaryIds", required = true) String[] dataDictionaryIds,
			HttpServletRequest request, HttpServletResponse response) {
		UUID uuid = UUID.randomUUID();
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		Boolean isManager = false;
		LundinTimesheetReportDTO lundinTimesheetReportDTO = lundinTimesheetReportsLogic
				.showDailyPaidTimesheet(companyId, employeeId,
						lundinTimesheetReportsForm, isManager,
						dataDictionaryIds);

		LundinDailyPaidTimesheetDTO dailyPaidDto = lundinTimesheetReportsLogic
				.getDailyPaidBatchName(companyId,
						lundinTimesheetReportsForm.getFromBatchId());

		String header = "Daily Paid Employees Approved Timesheet Report";
		String subHeader1 = "for " + dailyPaidDto.getFileBatchName()
				+ " Payroll ";
		String subHeader2 = "Period: From "
				+ dailyPaidDto.getFileFromBatchDate() + " to "
				+ dailyPaidDto.getFileToBatchDate();

		Map beans = new HashMap();
		beans.put("header", header);
		beans.put("subHeader1", subHeader1);
		beans.put("subHeader2", subHeader2);
		beans.put("totalEmployeeCount", "Total Employees: "
				+ lundinTimesheetReportDTO.getTotalEmployeesCount());
		beans.put(
				"currentDate",
				"Timestamp: "
						+ DateUtils.timeStampToStringWithTime(DateUtils
								.getCurrentTimestampWithTime()));
		beans.put("lundinDailyPaidReportList",
				lundinTimesheetReportDTO.getLundinDailyPaidTimesheetDTOs());

		if (lundinTimesheetReportDTO.getDataDictNameList().size() > 0) {
			beans.put("custFieldColNameList",
					lundinTimesheetReportDTO.getDataDictNameList());
		}
		beans.put("lundinCustomDataList",
				lundinTimesheetReportDTO.getLeaveCustomDataDTOs());
		XLSTransformer transformer = new XLSTransformer();
		File tempFolder = new File(PAYASIA_TEMP_PATH + "/LundinReportTemplate/"
				+ uuid);
		tempFolder.mkdirs();
		File tempDestFile = null;
		String templateFileName = "";

		if (lundinTimesheetReportDTO.getDataDictNameList().size() > 0) {
			templateFileName = servletContext
					.getRealPath(
							"/resources/LundinReportTemplate/LundinDailyPaidTimesheetReport.xlsx");
		} else {
			templateFileName = servletContext
					.getRealPath(
							"/resources/LundinReportTemplate/LundinDailyPaidTimesheetReportWOCustomFields.xlsx");
		}
		String destFileName = PAYASIA_TEMP_PATH + "/LundinReportTemplate/"
				+ uuid + "/Daily Paid Timesheet Report_"
				+ dailyPaidDto.getFileBatchName() + uuid + ".xlsx";
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

			String fileName = "Daily Paid Timesheet Report_"
					+ dailyPaidDto.getFileBatchName();
			outputStream = response.getOutputStream();
			response.setContentType("application/octet-stream");
			response.setHeader("Cache-Control",
					"must-revalidate, post-check=0, pre-check=0");

			fileName = fileName + ".xlsx";
			response.setHeader("Content-Disposition", "attachment; filename=\""
					+ fileName + "");

			outputStream = response.getOutputStream();
			outputStream.write(data);
		} catch (IOException ioException) {
			LOGGER.error(ioException.getMessage(), ioException);
			throw new PayAsiaSystemException(ioException.getMessage(),
					ioException);
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
				throw new PayAsiaSystemException(ioException.getMessage(),
						ioException);
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
	@RequestMapping(value = "/admin/lundinTimesheetReports/genDailyPaidTimesheetPDF.html", method = RequestMethod.POST)
	@Override
	public void genDailyPaidTimesheetPDF(
			@ModelAttribute(value = "lundinTimesheetReportsForm") LundinTimesheetReportsForm lundinTimesheetReportsForm,
			@RequestParam(value = "dataDictionaryIds", required = true) String[] dataDictionaryIds,
			HttpServletRequest request, HttpServletResponse response) {
		UUID uuid = UUID.randomUUID();
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		Boolean isManager = false;
		LundinTimesheetReportDTO lundinTimesheetReportDTO = lundinTimesheetReportsLogic
				.showDailyPaidTimesheet(companyId, employeeId,
						lundinTimesheetReportsForm, isManager,
						dataDictionaryIds);

		LundinDailyPaidTimesheetDTO dailyPaidDto = lundinTimesheetReportsLogic
				.getDailyPaidBatchName(companyId,
						lundinTimesheetReportsForm.getFromBatchId());

		String header = "Daily Paid Employees Approved Timesheet Report";
		String subHeader1 = "for " + dailyPaidDto.getFileBatchName()
				+ " Payroll ";
		String subHeader2 = "Period: From "
				+ dailyPaidDto.getFileFromBatchDate() + " to "
				+ dailyPaidDto.getFileToBatchDate();

		Map beans = new HashMap();
		beans.put("header", header);
		beans.put("subHeader1", subHeader1);
		beans.put("subHeader2", subHeader2);
		beans.put("totalEmployeeCount", "Total Employees: "
				+ lundinTimesheetReportDTO.getTotalEmployeesCount());
		beans.put(
				"currentDate",
				"Timestamp: "
						+ DateUtils.timeStampToStringWithTime(DateUtils
								.getCurrentTimestampWithTime()));
		beans.put("lundinDailyPaidReportList",
				lundinTimesheetReportDTO.getLundinDailyPaidTimesheetDTOs());

		if (lundinTimesheetReportDTO.getDataDictNameList().size() > 0) {
			beans.put("custFieldColNameList",
					lundinTimesheetReportDTO.getDataDictNameList());
		}
		beans.put("lundinCustomDataList",
				lundinTimesheetReportDTO.getLeaveCustomDataDTOs());
		XLSTransformer transformer = new XLSTransformer();
		File tempDestPdfFile = null;
		File tempDestExcelFile = null;
		File tempFolder = new File(PAYASIA_TEMP_PATH + "/LundinReportTemplate/"
				+ uuid);
		tempFolder.mkdirs();
		String templateFileName = "";

		if (lundinTimesheetReportDTO.getDataDictNameList().size() > 0) {
			templateFileName = servletContext
					.getRealPath(
							"/resources/LundinReportTemplate/LundinDailyPaidTimesheetReport.xlsx");
		} else {
			templateFileName = servletContext
					.getRealPath(
							"/resources/LundinReportTemplate/LundinDailyPaidTimesheetReportWOCustomFields.xlsx");
		}

		String destFileNameExcel = PAYASIA_TEMP_PATH
				+ "/DailyPaidTimesheetReport_"
				+ dailyPaidDto.getFileBatchName() + uuid + ".xlsx";
		destFileNameExcel = destFileNameExcel.replaceAll(" ", "_");
		String destFileNamePDF = PAYASIA_TEMP_PATH
				+ "/DailyPaidTimesheetReport_"
				+ dailyPaidDto.getFileBatchName() + uuid + ".pdf";
		destFileNamePDF = destFileNamePDF.replaceAll(" ", "_");
		Process process = null;
		try {
			transformer
					.transformXLS(templateFileName, beans, destFileNameExcel);

			ArrayList<String> cmdList = new ArrayList<String>();

			String file1 = destFileNameExcel.replaceAll(" ", "_");
			String file2 = destFileNamePDF.replaceAll(" ", "_");

			// cmdList.add(PYTHON_EXE_PATH);
			cmdList.add(DOCUMENT_CONVERTOR_PATH);
			cmdList.add(file1);
			cmdList.add(file2);

			ProcessBuilder processBuilder = new ProcessBuilder(cmdList);

			LOGGER.info("processBuilder.command()   "
					+ processBuilder.command());
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

			String fileName = "Daily Paid Timesheet Report_"
					+ dailyPaidDto.getFileBatchName();
			outputStream = response.getOutputStream();
			response.setContentType("application/octet-stream");
			response.setHeader("Cache-Control",
					"must-revalidate, post-check=0, pre-check=0");

			fileName = fileName.replaceAll(" ", "_") + ".pdf";
			response.setHeader("Content-Disposition", "attachment; filename=\""
					+ fileName + "");

			outputStream = response.getOutputStream();
			outputStream.write(data);
		} catch (IOException ioException) {
			LOGGER.error(ioException.getMessage(), ioException);
			throw new PayAsiaSystemException(ioException.getMessage(),
					ioException);

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
				throw new PayAsiaSystemException(ioException.getMessage(),
						ioException);
			}
		}

	}
	
	@RequestMapping(value = "/employee/lundinTimesheetReports/genDailyPaidTimesheetPDF.html", method = RequestMethod.POST)
	@Override
	public void genDailyPaidEmpTimesheetPDF(
			@ModelAttribute(value = "lundinTimesheetReportsForm") LundinTimesheetReportsForm lundinTimesheetReportsForm,
			@RequestParam(value = "dataDictionaryIds", required = true) String[] dataDictionaryIds,
			HttpServletRequest request, HttpServletResponse response) {
		UUID uuid = UUID.randomUUID();
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		Boolean isManager = false;
		LundinTimesheetReportDTO lundinTimesheetReportDTO = lundinTimesheetReportsLogic
				.showDailyPaidTimesheet(companyId, employeeId,
						lundinTimesheetReportsForm, isManager,
						dataDictionaryIds);

		LundinDailyPaidTimesheetDTO dailyPaidDto = lundinTimesheetReportsLogic
				.getDailyPaidBatchName(companyId,
						lundinTimesheetReportsForm.getFromBatchId());

		String header = "Daily Paid Employees Approved Timesheet Report";
		String subHeader1 = "for " + dailyPaidDto.getFileBatchName()
				+ " Payroll ";
		String subHeader2 = "Period: From "
				+ dailyPaidDto.getFileFromBatchDate() + " to "
				+ dailyPaidDto.getFileToBatchDate();

		Map beans = new HashMap();
		beans.put("header", header);
		beans.put("subHeader1", subHeader1);
		beans.put("subHeader2", subHeader2);
		beans.put("totalEmployeeCount", "Total Employees: "
				+ lundinTimesheetReportDTO.getTotalEmployeesCount());
		beans.put(
				"currentDate",
				"Timestamp: "
						+ DateUtils.timeStampToStringWithTime(DateUtils
								.getCurrentTimestampWithTime()));
		beans.put("lundinDailyPaidReportList",
				lundinTimesheetReportDTO.getLundinDailyPaidTimesheetDTOs());

		if (lundinTimesheetReportDTO.getDataDictNameList().size() > 0) {
			beans.put("custFieldColNameList",
					lundinTimesheetReportDTO.getDataDictNameList());
		}
		beans.put("lundinCustomDataList",
				lundinTimesheetReportDTO.getLeaveCustomDataDTOs());
		XLSTransformer transformer = new XLSTransformer();
		File tempDestPdfFile = null;
		File tempDestExcelFile = null;
		File tempFolder = new File(PAYASIA_TEMP_PATH + "/LundinReportTemplate/"
				+ uuid);
		tempFolder.mkdirs();
		String templateFileName = "";

		if (lundinTimesheetReportDTO.getDataDictNameList().size() > 0) {
			templateFileName = servletContext
					.getRealPath(
							"/resources/LundinReportTemplate/LundinDailyPaidTimesheetReport.xlsx");
		} else {
			templateFileName = servletContext
					.getRealPath(
							"/resources/LundinReportTemplate/LundinDailyPaidTimesheetReportWOCustomFields.xlsx");
		}

		String destFileNameExcel = PAYASIA_TEMP_PATH
				+ "/DailyPaidTimesheetReport_"
				+ dailyPaidDto.getFileBatchName() + uuid + ".xlsx";
		destFileNameExcel = destFileNameExcel.replaceAll(" ", "_");
		String destFileNamePDF = PAYASIA_TEMP_PATH
				+ "/DailyPaidTimesheetReport_"
				+ dailyPaidDto.getFileBatchName() + uuid + ".pdf";
		destFileNamePDF = destFileNamePDF.replaceAll(" ", "_");
		Process process = null;
		try {
			transformer
					.transformXLS(templateFileName, beans, destFileNameExcel);

			ArrayList<String> cmdList = new ArrayList<String>();

			String file1 = destFileNameExcel.replaceAll(" ", "_");
			String file2 = destFileNamePDF.replaceAll(" ", "_");

			// cmdList.add(PYTHON_EXE_PATH);
			cmdList.add(DOCUMENT_CONVERTOR_PATH);
			cmdList.add(file1);
			cmdList.add(file2);

			ProcessBuilder processBuilder = new ProcessBuilder(cmdList);

			LOGGER.info("processBuilder.command()   "
					+ processBuilder.command());
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

			String fileName = "Daily Paid Timesheet Report_"
					+ dailyPaidDto.getFileBatchName();
			outputStream = response.getOutputStream();
			response.setContentType("application/octet-stream");
			response.setHeader("Cache-Control",
					"must-revalidate, post-check=0, pre-check=0");

			fileName = fileName.replaceAll(" ", "_") + ".pdf";
			response.setHeader("Content-Disposition", "attachment; filename=\""
					+ fileName + "");

			outputStream = response.getOutputStream();
			outputStream.write(data);
		} catch (IOException ioException) {
			LOGGER.error(ioException.getMessage(), ioException);
			throw new PayAsiaSystemException(ioException.getMessage(),
					ioException);

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
				throw new PayAsiaSystemException(ioException.getMessage(),
						ioException);
			}
		}

	}

	@Override
	@RequestMapping(value = "/admin/lundinTimesheetReports/showTimesheetStatusReport.html", method = RequestMethod.POST)
	@ResponseBody
	public String showTimesheetStatusReport(
			@ModelAttribute(value = "lundinTimesheetReportsForm") LundinTimesheetReportsForm lundinTimesheetReportsForm,
			@RequestParam(value = "dataDictionaryIds", required = false) String[] dataDictionaryIds,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		Boolean isManager = false;
		LundinTimesheetReportDTO lundinTimesheetReportDTO = lundinTimesheetReportsLogic
				.showTimesheetStatusReport(companyId, employeeId,
						lundinTimesheetReportsForm, isManager,
						dataDictionaryIds);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("aaData",
				lundinTimesheetReportDTO.getLundinTimesheetStatusReportDTOs());
		jsonObject.put("aoColumns",
				lundinTimesheetReportDTO.getLundinHeaderDTOs());
		String data = jsonObject.toString();
		return data;

	}
	
	@Override
	@RequestMapping(value = "/employee/lundinTimesheetReports/showTimesheetStatusReport.html", method = RequestMethod.POST)
	@ResponseBody
	public String showTimesheetStatusEmpReport(
			@ModelAttribute(value = "lundinTimesheetReportsForm") LundinTimesheetReportsForm lundinTimesheetReportsForm,
			@RequestParam(value = "dataDictionaryIds", required = false) String[] dataDictionaryIds,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		Boolean isManager = false;
		LundinTimesheetReportDTO lundinTimesheetReportDTO = lundinTimesheetReportsLogic
				.showTimesheetStatusReport(companyId, employeeId,
						lundinTimesheetReportsForm, isManager,
						dataDictionaryIds);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("aaData",
				lundinTimesheetReportDTO.getLundinTimesheetStatusReportDTOs());
		jsonObject.put("aoColumns",
				lundinTimesheetReportDTO.getLundinHeaderDTOs());
		String data = jsonObject.toString();
		return data;

	}

	@RequestMapping(value = "/admin/lundinTimesheetReports/genTimesheetStatusReportExcelFile.html", method = RequestMethod.POST)
	@Override
	public void genTimesheetStatusReportExcelFile(
			@ModelAttribute(value = "lundinTimesheetReportsForm") LundinTimesheetReportsForm lundinTimesheetReportsForm,
			@RequestParam(value = "dataDictionaryIds", required = true) String[] dataDictionaryIds,
			HttpServletRequest request, HttpServletResponse response) {
		UUID uuid = UUID.randomUUID();
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		Boolean isManager = false;
		LundinTimesheetReportDTO lundinTimesheetReportDTO = lundinTimesheetReportsLogic
				.showTimesheetStatusReport(companyId, employeeId,
						lundinTimesheetReportsForm, isManager,
						dataDictionaryIds);

		LundinTimewritingReportDTO lundinTimewritingReportDTO = lundinTimesheetReportsLogic
				.getOTBatchName(companyId,
						lundinTimesheetReportsForm.getFromBatchId(),
						lundinTimesheetReportsForm.getToBatchId());

		String header = "Timesheet Status Report";
		String subHeader1 = "for "
				+ lundinTimewritingReportDTO.getFileBatchName() + " Payroll ";
		String subHeader2 = "Period: From "
				+ lundinTimewritingReportDTO.getFromBatchDate() + " to "
				+ lundinTimewritingReportDTO.getToBatchDate();

		Map beans = new HashMap();
		beans.put("header", header);
		beans.put("subHeader1", subHeader1);
		beans.put("subHeader2", subHeader2);
		beans.put("totalEmployeeCount", "Total Employees: "
				+ lundinTimesheetReportDTO.getTotalEmployeesCount());
		beans.put(
				"totalEmployeeApprovedCount",
				"Total Employees with Approved Timesheet: "
						+ lundinTimesheetReportDTO
								.getTotalEmployeesApprovedTimesheetCount());
		beans.put(
				"totalEmployeePendingCount",
				"Total Employees with Pending Timesheet: "
						+ lundinTimesheetReportDTO
								.getTotalEmployeesPendingTimesheetCount());
		beans.put(
				"currentDate",
				"Timestamp: "
						+ DateUtils.timeStampToStringWithTime(DateUtils
								.getCurrentTimestampWithTime()));
		beans.put("lundinTimesheetStatusList",
				lundinTimesheetReportDTO.getLundinTimesheetStatusReportDTOs());

		if (lundinTimesheetReportDTO.getDataDictNameList().size() > 0) {
			beans.put("custFieldColNameList",
					lundinTimesheetReportDTO.getDataDictNameList());
		}
		beans.put("lundinCustomDataList",
				lundinTimesheetReportDTO.getLeaveCustomDataDTOs());
		XLSTransformer transformer = new XLSTransformer();
		File tempFolder = new File(PAYASIA_TEMP_PATH + "/LundinReportTemplate/"
				+ uuid);
		tempFolder.mkdirs();
		File tempDestFile = null;
		String templateFileName = "";

		if (lundinTimesheetReportDTO.getDataDictNameList().size() > 0) {
			templateFileName = servletContext
					.getRealPath(
							"/resources/LundinReportTemplate/LundinTimesheetStatusReport.xlsx");
		} else {
			templateFileName = servletContext
					.getRealPath(
							"/resources/LundinReportTemplate/LundinTimesheetStatusReportWOCustomFields.xlsx");
		}
		String destFileName = PAYASIA_TEMP_PATH + "/LundinReportTemplate/"
				+ uuid + "/Timesheet Status Report_"
				+ lundinTimewritingReportDTO.getFileBatchName() + uuid
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

			String fileName = "Timesheet Status Report_"
					+ lundinTimewritingReportDTO.getFileBatchName();
			outputStream = response.getOutputStream();
			response.setContentType("application/octet-stream");
			response.setHeader("Cache-Control",
					"must-revalidate, post-check=0, pre-check=0");

			fileName = fileName + ".xlsx";
			response.setHeader("Content-Disposition", "attachment; filename=\""
					+ fileName + "");

			outputStream = response.getOutputStream();
			outputStream.write(data);
		} catch (IOException ioException) {
			LOGGER.error(ioException.getMessage(), ioException);
			throw new PayAsiaSystemException(ioException.getMessage(),
					ioException);
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
				throw new PayAsiaSystemException(ioException.getMessage(),
						ioException);
			}
		}
	}
	
	@RequestMapping(value = "/employee/lundinTimesheetReports/genTimesheetStatusReportExcelFile.html", method = RequestMethod.POST)
	@Override
	public void genTimesheetStatusEmpReportExcelFile(
			@ModelAttribute(value = "lundinTimesheetReportsForm") LundinTimesheetReportsForm lundinTimesheetReportsForm,
			@RequestParam(value = "dataDictionaryIds", required = true) String[] dataDictionaryIds,
			HttpServletRequest request, HttpServletResponse response) {
		UUID uuid = UUID.randomUUID();
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		Boolean isManager = false;
		LundinTimesheetReportDTO lundinTimesheetReportDTO = lundinTimesheetReportsLogic
				.showTimesheetStatusReport(companyId, employeeId,
						lundinTimesheetReportsForm, isManager,
						dataDictionaryIds);

		LundinTimewritingReportDTO lundinTimewritingReportDTO = lundinTimesheetReportsLogic
				.getOTBatchName(companyId,
						lundinTimesheetReportsForm.getFromBatchId(),
						lundinTimesheetReportsForm.getToBatchId());

		String header = "Timesheet Status Report";
		String subHeader1 = "for "
				+ lundinTimewritingReportDTO.getFileBatchName() + " Payroll ";
		String subHeader2 = "Period: From "
				+ lundinTimewritingReportDTO.getFromBatchDate() + " to "
				+ lundinTimewritingReportDTO.getToBatchDate();

		Map beans = new HashMap();
		beans.put("header", header);
		beans.put("subHeader1", subHeader1);
		beans.put("subHeader2", subHeader2);
		beans.put("totalEmployeeCount", "Total Employees: "
				+ lundinTimesheetReportDTO.getTotalEmployeesCount());
		beans.put(
				"totalEmployeeApprovedCount",
				"Total Employees with Approved Timesheet: "
						+ lundinTimesheetReportDTO
								.getTotalEmployeesApprovedTimesheetCount());
		beans.put(
				"totalEmployeePendingCount",
				"Total Employees with Pending Timesheet: "
						+ lundinTimesheetReportDTO
								.getTotalEmployeesPendingTimesheetCount());
		beans.put(
				"currentDate",
				"Timestamp: "
						+ DateUtils.timeStampToStringWithTime(DateUtils
								.getCurrentTimestampWithTime()));
		beans.put("lundinTimesheetStatusList",
				lundinTimesheetReportDTO.getLundinTimesheetStatusReportDTOs());

		if (lundinTimesheetReportDTO.getDataDictNameList().size() > 0) {
			beans.put("custFieldColNameList",
					lundinTimesheetReportDTO.getDataDictNameList());
		}
		beans.put("lundinCustomDataList",
				lundinTimesheetReportDTO.getLeaveCustomDataDTOs());
		XLSTransformer transformer = new XLSTransformer();
		File tempFolder = new File(PAYASIA_TEMP_PATH + "/LundinReportTemplate/"
				+ uuid);
		tempFolder.mkdirs();
		File tempDestFile = null;
		String templateFileName = "";

		if (lundinTimesheetReportDTO.getDataDictNameList().size() > 0) {
			templateFileName = servletContext
					.getRealPath(
							"/resources/LundinReportTemplate/LundinTimesheetStatusReport.xlsx");
		} else {
			templateFileName = servletContext
					.getRealPath(
							"/resources/LundinReportTemplate/LundinTimesheetStatusReportWOCustomFields.xlsx");
		}
		String destFileName = PAYASIA_TEMP_PATH + "/LundinReportTemplate/"
				+ uuid + "/Timesheet Status Report_"
				+ lundinTimewritingReportDTO.getFileBatchName() + uuid
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

			String fileName = "Timesheet Status Report_"
					+ lundinTimewritingReportDTO.getFileBatchName();
			outputStream = response.getOutputStream();
			response.setContentType("application/octet-stream");
			response.setHeader("Cache-Control",
					"must-revalidate, post-check=0, pre-check=0");

			fileName = fileName + ".xlsx";
			response.setHeader("Content-Disposition", "attachment; filename=\""
					+ fileName + "");

			outputStream = response.getOutputStream();
			outputStream.write(data);
		} catch (IOException ioException) {
			LOGGER.error(ioException.getMessage(), ioException);
			throw new PayAsiaSystemException(ioException.getMessage(),
					ioException);
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
				throw new PayAsiaSystemException(ioException.getMessage(),
						ioException);
			}
		}
	}

	@RequestMapping(value = "/admin/lundinTimesheetReports/genTimesheetStatusReportPDF.html", method = RequestMethod.POST)
	@Override
	public void genTimesheetStatusReportPDF(
			@ModelAttribute(value = "lundinTimesheetReportsForm") LundinTimesheetReportsForm lundinTimesheetReportsForm,
			@RequestParam(value = "dataDictionaryIds", required = true) String[] dataDictionaryIds,
			HttpServletRequest request, HttpServletResponse response) {
		UUID uuid = UUID.randomUUID();
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		Boolean isManager = false;
		LundinTimesheetReportDTO lundinTimesheetReportDTO = lundinTimesheetReportsLogic
				.showTimesheetStatusReport(companyId, employeeId,
						lundinTimesheetReportsForm, isManager,
						dataDictionaryIds);

		LundinTimewritingReportDTO lundinTimewritingReportDTO = lundinTimesheetReportsLogic
				.getOTBatchName(companyId,
						lundinTimesheetReportsForm.getFromBatchId(),
						lundinTimesheetReportsForm.getToBatchId());

		String header = "Timesheet Status Report";
		String subHeader1 = "for "
				+ lundinTimewritingReportDTO.getFileBatchName() + " Payroll ";
		String subHeader2 = "Period: From "
				+ lundinTimewritingReportDTO.getFromBatchDate() + " to "
				+ lundinTimewritingReportDTO.getToBatchDate();

		Map beans = new HashMap();
		beans.put("header", header);
		beans.put("subHeader1", subHeader1);
		beans.put("subHeader2", subHeader2);
		beans.put("totalEmployeeCount", "Total Employees: "
				+ lundinTimesheetReportDTO.getTotalEmployeesCount());
		beans.put(
				"totalEmployeeApprovedCount",
				"Total Employees with Approved Timesheet: "
						+ lundinTimesheetReportDTO
								.getTotalEmployeesApprovedTimesheetCount());
		beans.put(
				"totalEmployeePendingCount",
				"Total Employees with Pending Timesheet: "
						+ lundinTimesheetReportDTO
								.getTotalEmployeesPendingTimesheetCount());
		beans.put(
				"currentDate",
				"Timestamp: "
						+ DateUtils.timeStampToStringWithTime(DateUtils
								.getCurrentTimestampWithTime()));
		beans.put("lundinTimesheetStatusList",
				lundinTimesheetReportDTO.getLundinTimesheetStatusReportDTOs());

		if (lundinTimesheetReportDTO.getDataDictNameList().size() > 0) {
			beans.put("custFieldColNameList",
					lundinTimesheetReportDTO.getDataDictNameList());
		}
		beans.put("lundinCustomDataList",
				lundinTimesheetReportDTO.getLeaveCustomDataDTOs());
		XLSTransformer transformer = new XLSTransformer();
		File tempDestPdfFile = null;
		File tempDestExcelFile = null;
		File tempFolder = new File(PAYASIA_TEMP_PATH + "/LundinReportTemplate/"
				+ uuid);
		tempFolder.mkdirs();
		String templateFileName = "";

		if (lundinTimesheetReportDTO.getDataDictNameList().size() > 0) {
			templateFileName = servletContext
					.getRealPath(
							"/resources/LundinReportTemplate/LundinTimesheetStatusReport.xlsx");
		} else {
			templateFileName =servletContext
					.getRealPath(
							"/resources/LundinReportTemplate/LundinTimesheetStatusReportWOCustomFields.xlsx");
		}

		String destFileNameExcel = PAYASIA_TEMP_PATH
				+ "/TimesheetStatusReport_"
				+ lundinTimewritingReportDTO.getFileBatchName() + uuid
				+ ".xlsx";
		destFileNameExcel = destFileNameExcel.replaceAll(" ", "_");
		String destFileNamePDF = PAYASIA_TEMP_PATH + "/TimesheetStatusReport_"
				+ lundinTimewritingReportDTO.getFileBatchName() + uuid + ".pdf";
		destFileNamePDF = destFileNamePDF.replaceAll(" ", "_");
		Process process = null;
		try {
			transformer
					.transformXLS(templateFileName, beans, destFileNameExcel);

			ArrayList<String> cmdList = new ArrayList<String>();

			String file1 = destFileNameExcel.replaceAll(" ", "_");
			String file2 = destFileNamePDF.replaceAll(" ", "_");

			// cmdList.add(PYTHON_EXE_PATH);
			cmdList.add(DOCUMENT_CONVERTOR_PATH);
			cmdList.add(file1);
			cmdList.add(file2);

			ProcessBuilder processBuilder = new ProcessBuilder(cmdList);

			LOGGER.info("processBuilder.command()   "
					+ processBuilder.command());
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

			String fileName = "Timesheet Status Report_"
					+ lundinTimewritingReportDTO.getFileBatchName();
			outputStream = response.getOutputStream();
			response.setContentType("application/octet-stream");
			response.setHeader("Cache-Control",
					"must-revalidate, post-check=0, pre-check=0");

			fileName = fileName.replaceAll(" ", "_") + ".pdf";
			response.setHeader("Content-Disposition", "attachment; filename=\""
					+ fileName + "");

			outputStream = response.getOutputStream();
			outputStream.write(data);
		} catch (IOException ioException) {
			LOGGER.error(ioException.getMessage(), ioException);
			throw new PayAsiaSystemException(ioException.getMessage(),
					ioException);

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
				throw new PayAsiaSystemException(ioException.getMessage(),
						ioException);
			}
		}
	}
	
	@RequestMapping(value = "/employee/lundinTimesheetReports/genTimesheetStatusReportPDF.html", method = RequestMethod.POST)
	@Override
	public void genTimesheetStatusEmpReportPDF(
			@ModelAttribute(value = "lundinTimesheetReportsForm") LundinTimesheetReportsForm lundinTimesheetReportsForm,
			@RequestParam(value = "dataDictionaryIds", required = true) String[] dataDictionaryIds,
			HttpServletRequest request, HttpServletResponse response) {
		UUID uuid = UUID.randomUUID();
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		Boolean isManager = false;
		LundinTimesheetReportDTO lundinTimesheetReportDTO = lundinTimesheetReportsLogic
				.showTimesheetStatusReport(companyId, employeeId,
						lundinTimesheetReportsForm, isManager,
						dataDictionaryIds);

		LundinTimewritingReportDTO lundinTimewritingReportDTO = lundinTimesheetReportsLogic
				.getOTBatchName(companyId,
						lundinTimesheetReportsForm.getFromBatchId(),
						lundinTimesheetReportsForm.getToBatchId());

		String header = "Timesheet Status Report";
		String subHeader1 = "for "
				+ lundinTimewritingReportDTO.getFileBatchName() + " Payroll ";
		String subHeader2 = "Period: From "
				+ lundinTimewritingReportDTO.getFromBatchDate() + " to "
				+ lundinTimewritingReportDTO.getToBatchDate();

		Map beans = new HashMap();
		beans.put("header", header);
		beans.put("subHeader1", subHeader1);
		beans.put("subHeader2", subHeader2);
		beans.put("totalEmployeeCount", "Total Employees: "
				+ lundinTimesheetReportDTO.getTotalEmployeesCount());
		beans.put(
				"totalEmployeeApprovedCount",
				"Total Employees with Approved Timesheet: "
						+ lundinTimesheetReportDTO
								.getTotalEmployeesApprovedTimesheetCount());
		beans.put(
				"totalEmployeePendingCount",
				"Total Employees with Pending Timesheet: "
						+ lundinTimesheetReportDTO
								.getTotalEmployeesPendingTimesheetCount());
		beans.put(
				"currentDate",
				"Timestamp: "
						+ DateUtils.timeStampToStringWithTime(DateUtils
								.getCurrentTimestampWithTime()));
		beans.put("lundinTimesheetStatusList",
				lundinTimesheetReportDTO.getLundinTimesheetStatusReportDTOs());

		if (lundinTimesheetReportDTO.getDataDictNameList().size() > 0) {
			beans.put("custFieldColNameList",
					lundinTimesheetReportDTO.getDataDictNameList());
		}
		beans.put("lundinCustomDataList",
				lundinTimesheetReportDTO.getLeaveCustomDataDTOs());
		XLSTransformer transformer = new XLSTransformer();
		File tempDestPdfFile = null;
		File tempDestExcelFile = null;
		File tempFolder = new File(PAYASIA_TEMP_PATH + "/LundinReportTemplate/"
				+ uuid);
		tempFolder.mkdirs();
		String templateFileName = "";

		if (lundinTimesheetReportDTO.getDataDictNameList().size() > 0) {
			templateFileName = servletContext
					.getRealPath(
							"/resources/LundinReportTemplate/LundinTimesheetStatusReport.xlsx");
		} else {
			templateFileName = servletContext
					.getRealPath(
							"/resources/LundinReportTemplate/LundinTimesheetStatusReportWOCustomFields.xlsx");
		}

		String destFileNameExcel = PAYASIA_TEMP_PATH
				+ "/TimesheetStatusReport_"
				+ lundinTimewritingReportDTO.getFileBatchName() + uuid
				+ ".xlsx";
		destFileNameExcel = destFileNameExcel.replaceAll(" ", "_");
		String destFileNamePDF = PAYASIA_TEMP_PATH + "/TimesheetStatusReport_"
				+ lundinTimewritingReportDTO.getFileBatchName() + uuid + ".pdf";
		destFileNamePDF = destFileNamePDF.replaceAll(" ", "_");
		Process process = null;
		try {
			transformer
					.transformXLS(templateFileName, beans, destFileNameExcel);

			ArrayList<String> cmdList = new ArrayList<String>();

			String file1 = destFileNameExcel.replaceAll(" ", "_");
			String file2 = destFileNamePDF.replaceAll(" ", "_");

			// cmdList.add(PYTHON_EXE_PATH);
			cmdList.add(DOCUMENT_CONVERTOR_PATH);
			cmdList.add(file1);
			cmdList.add(file2);

			ProcessBuilder processBuilder = new ProcessBuilder(cmdList);

			LOGGER.info("processBuilder.command()   "
					+ processBuilder.command());
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

			String fileName = "Timesheet Status Report_"
					+ lundinTimewritingReportDTO.getFileBatchName();
			outputStream = response.getOutputStream();
			response.setContentType("application/octet-stream");
			response.setHeader("Cache-Control",
					"must-revalidate, post-check=0, pre-check=0");

			fileName = fileName.replaceAll(" ", "_") + ".pdf";
			response.setHeader("Content-Disposition", "attachment; filename=\""
					+ fileName + "");

			outputStream = response.getOutputStream();
			outputStream.write(data);
		} catch (IOException ioException) {
			LOGGER.error(ioException.getMessage(), ioException);
			throw new PayAsiaSystemException(ioException.getMessage(),
					ioException);

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
				throw new PayAsiaSystemException(ioException.getMessage(),
						ioException);
			}
		}
	}

	@Override
	@RequestMapping(value = "/admin/lundinTimesheetReports/searchEmployee", method = RequestMethod.POST)
	@ResponseBody
	public String searchEmployee(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			@RequestParam(value = "searchCondition", required = true) String searchCondition,
			@RequestParam(value = "searchText", required = true) String searchText,
			@RequestParam(value = "includeResignedEmployees", required = false) Boolean includeResignedEmployees,
			@RequestParam(value = "departmentId", required = false) long departmentId,
			HttpServletRequest request, HttpServletResponse response) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		LeaveReportsResponse leaveReportResponse = lundinTimesheetReportsLogic
				.searchEmployee(pageDTO, sortDTO, searchCondition, searchText,
						companyId, employeeId, includeResignedEmployees,
						departmentId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(leaveReportResponse,
				jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return null;
	}
	
	@Override
	@RequestMapping(value = "/employee/lundinTimesheetReports/searchEmployee", method = RequestMethod.POST)
	@ResponseBody
	public String searchEmploye(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			@RequestParam(value = "searchCondition", required = true) String searchCondition,
			@RequestParam(value = "searchText", required = true) String searchText,
			@RequestParam(value = "includeResignedEmployees", required = false) Boolean includeResignedEmployees,
			@RequestParam(value = "departmentId", required = false) long departmentId,
			HttpServletRequest request, HttpServletResponse response) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		LeaveReportsResponse leaveReportResponse = lundinTimesheetReportsLogic
				.searchEmployee(pageDTO, sortDTO, searchCondition, searchText,
						companyId, employeeId, includeResignedEmployees,
						departmentId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(leaveReportResponse,
				jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/admin/lundinTimesheetReports/searchEmployeeByManager", method = RequestMethod.POST)
	@ResponseBody
	public String searchEmployeeByManager(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			@RequestParam(value = "searchCondition", required = true) String searchCondition,
			@RequestParam(value = "searchText", required = true) String searchText,
			@RequestParam(value = "includeResignedEmployees", required = false) Boolean includeResignedEmployees,
			@RequestParam(value = "departmentId", required = false) long departmentId,
			HttpServletRequest request, HttpServletResponse response) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		LeaveReportsResponse leaveReportResponse = lundinTimesheetReportsLogic
				.searchEmployeeByManager(pageDTO, sortDTO, searchCondition,
						searchText, companyId, employeeId,
						includeResignedEmployees, departmentId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(leaveReportResponse,
				jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return null;
	}

	
	@Override
	@RequestMapping(value = "/employee/lundinTimesheetReports/searchEmployeeByManager", method = RequestMethod.POST)
	@ResponseBody
	public String searchEmployeeByEmpManager(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			@RequestParam(value = "searchCondition", required = true) String searchCondition,
			@RequestParam(value = "searchText", required = true) String searchText,
			@RequestParam(value = "includeResignedEmployees", required = false) Boolean includeResignedEmployees,
			@RequestParam(value = "departmentId", required = false) long departmentId,
			HttpServletRequest request, HttpServletResponse response) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		LeaveReportsResponse leaveReportResponse = lundinTimesheetReportsLogic
				.searchEmployeeByManager(pageDTO, sortDTO, searchCondition,
						searchText, companyId, employeeId,
						includeResignedEmployees, departmentId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(leaveReportResponse,
				jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return null;
	}
	@Override
	@RequestMapping(value = "/admin/lundinTimesheetReports/genTimesheetHistoryReportPDF.html", method = RequestMethod.POST)
	public @ResponseBody byte[] genTimesheetHistoryReportPDF(
			@ModelAttribute(value = "lundinTimesheetReportsForm") LundinTimesheetReportsForm lundinTimesheetReportsForm,
			HttpServletRequest request, HttpServletResponse response) {

		UUID uuid = UUID.randomUUID();
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		List<LundinTimesheetDTO> lundinTimesheets = lundinTimesheetPrintPDFLogic
				.generateTimesheets(lundinTimesheetReportsForm, employeeId,
						companyId);
		ByteArrayOutputStream errorPdfBytes = new ByteArrayOutputStream();
		ByteArrayOutputStream noTimesheetPdfBytes = new ByteArrayOutputStream();
		try {

			LundinTimewritingReportDTO lundinTimewritingReportDTO = lundinTimesheetReportsLogic
					.getOTBatchName(companyId,
							lundinTimesheetReportsForm.getFromBatchId(),
							lundinTimesheetReportsForm.getToBatchId());

			String batchName = lundinTimewritingReportDTO.getFileBatchName();
			response.reset();
			Document copyDocument = new Document();
			String filename = "Timesheet History Report_" + batchName + " .pdf";

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			PdfCopy copy = new PdfCopy(copyDocument, out);

			copyDocument.open();

			PdfReader copyPdfReader;
			int noOfPagesInExistPdf = 0;
			String mimeType = "";

			if (!lundinTimesheets.isEmpty()) {
				for (LundinTimesheetDTO lundinTimesheet : lundinTimesheets) {
					TimesheetFormPdfDTO timesheetFormPdfDTO = lundinPendingTimesheetLogic
							.generateTimesheetPrintPDF(companyId,
									lundinTimesheet.getEmployeeId(),
									lundinTimesheet.getTimesheetId(), true,
									lundinTimesheet);
					if (timesheetFormPdfDTO != null) {
						copyPdfReader = new PdfReader(
								timesheetFormPdfDTO.getTimesheetPdfByteFile());

						noOfPagesInExistPdf = copyPdfReader.getNumberOfPages();
						for (int page = 0; page < noOfPagesInExistPdf;) {
							copy.addPage(copy.getImportedPage(copyPdfReader,
									++page));
						}
						copy.freeReader(copyPdfReader);
						copyPdfReader.close();

						mimeType = URLConnection
								.guessContentTypeFromName(timesheetFormPdfDTO
										.getEmployeeNumber()
										+ "_"
										+ timesheetFormPdfDTO
												.getTimesheetBatchDesc()
										+ uuid
										+ ".pdf");
					}
				}
			} else {

				response.reset();
				String mimeTypes = "";
				mimeType = URLConnection
						.guessContentTypeFromName("No Timesheet Found.pdf");
				Document document = new Document();
				PdfWriter writer = PdfWriter.getInstance(document,
						noTimesheetPdfBytes);
				document.open();
				document.add(new Paragraph(
						"No Timesheet history report found for these employees"));
				document.close();
				writer.close();
				response.setContentType("application/" + mimeTypes);
				response.setContentLength(1);

				response.setHeader("Content-Disposition",
						"attachment;filename=No Timesheet Found.pdf");
				return noTimesheetPdfBytes.toByteArray();

			}

			copyDocument.close();

			response.setContentType("application/" + mimeType);
			response.setContentLength(noOfPagesInExistPdf);

			response.setHeader("Content-Disposition", "attachment;filename="
					+ filename);

			return out.toByteArray();
		} catch (Exception e) {
			try {
				response.reset();
				String mimeType = "";
				mimeType = URLConnection.guessContentTypeFromName("error.pdf");
				Document document = new Document();
				PdfWriter writer = PdfWriter.getInstance(document,
						errorPdfBytes);
				document.open();
				document.add(new Paragraph(
						"Error in generating Timesheet history report"));
				document.close();
				writer.close();
				response.setContentType("application/" + mimeType);
				response.setContentLength(1);

				response.setHeader("Content-Disposition",
						"attachment;filename=error.pdf");
			} catch (Exception e1) {
				e.printStackTrace();
			}

			return errorPdfBytes.toByteArray();
		}

	}
	
	@Override
	@RequestMapping(value = "/employee/lundinTimesheetReports/genTimesheetHistoryReportPDF.html", method = RequestMethod.POST)
	public @ResponseBody byte[] genTimesheetHistoryEmpReportPDF(
			@ModelAttribute(value = "lundinTimesheetReportsForm") LundinTimesheetReportsForm lundinTimesheetReportsForm,
			HttpServletRequest request, HttpServletResponse response) {

		UUID uuid = UUID.randomUUID();
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		List<LundinTimesheetDTO> lundinTimesheets = lundinTimesheetPrintPDFLogic
				.generateTimesheets(lundinTimesheetReportsForm, employeeId,
						companyId);
		ByteArrayOutputStream errorPdfBytes = new ByteArrayOutputStream();
		ByteArrayOutputStream noTimesheetPdfBytes = new ByteArrayOutputStream();
		try {

			LundinTimewritingReportDTO lundinTimewritingReportDTO = lundinTimesheetReportsLogic
					.getOTBatchName(companyId,
							lundinTimesheetReportsForm.getFromBatchId(),
							lundinTimesheetReportsForm.getToBatchId());

			String batchName = lundinTimewritingReportDTO.getFileBatchName();
			response.reset();
			Document copyDocument = new Document();
			String filename = "Timesheet History Report_" + batchName + " .pdf";

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			PdfCopy copy = new PdfCopy(copyDocument, out);

			copyDocument.open();

			PdfReader copyPdfReader;
			int noOfPagesInExistPdf = 0;
			String mimeType = "";

			if (!lundinTimesheets.isEmpty()) {
				for (LundinTimesheetDTO lundinTimesheet : lundinTimesheets) {
					TimesheetFormPdfDTO timesheetFormPdfDTO = lundinPendingTimesheetLogic
							.generateTimesheetPrintPDF(companyId,
									lundinTimesheet.getEmployeeId(),
									lundinTimesheet.getTimesheetId(), true,
									lundinTimesheet);
					if (timesheetFormPdfDTO != null) {
						copyPdfReader = new PdfReader(
								timesheetFormPdfDTO.getTimesheetPdfByteFile());

						noOfPagesInExistPdf = copyPdfReader.getNumberOfPages();
						for (int page = 0; page < noOfPagesInExistPdf;) {
							copy.addPage(copy.getImportedPage(copyPdfReader,
									++page));
						}
						copy.freeReader(copyPdfReader);
						copyPdfReader.close();

						mimeType = URLConnection
								.guessContentTypeFromName(timesheetFormPdfDTO
										.getEmployeeNumber()
										+ "_"
										+ timesheetFormPdfDTO
												.getTimesheetBatchDesc()
										+ uuid
										+ ".pdf");
					}
				}
			} else {

				response.reset();
				String mimeTypes = "";
				mimeType = URLConnection
						.guessContentTypeFromName("No Timesheet Found.pdf");
				Document document = new Document();
				PdfWriter writer = PdfWriter.getInstance(document,
						noTimesheetPdfBytes);
				document.open();
				document.add(new Paragraph(
						"No Timesheet history report found for these employees"));
				document.close();
				writer.close();
				response.setContentType("application/" + mimeTypes);
				response.setContentLength(1);

				response.setHeader("Content-Disposition",
						"attachment;filename=No Timesheet Found.pdf");
				return noTimesheetPdfBytes.toByteArray();

			}

			copyDocument.close();

			response.setContentType("application/" + mimeType);
			response.setContentLength(noOfPagesInExistPdf);

			response.setHeader("Content-Disposition", "attachment;filename="
					+ filename);

			return out.toByteArray();
		} catch (Exception e) {
			try {
				response.reset();
				String mimeType = "";
				mimeType = URLConnection.guessContentTypeFromName("error.pdf");
				Document document = new Document();
				PdfWriter writer = PdfWriter.getInstance(document,
						errorPdfBytes);
				document.open();
				document.add(new Paragraph(
						"Error in generating Timesheet history report"));
				document.close();
				writer.close();
				response.setContentType("application/" + mimeType);
				response.setContentLength(1);

				response.setHeader("Content-Disposition",
						"attachment;filename=error.pdf");
			} catch (Exception e1) {
				e.printStackTrace();
			}

			return errorPdfBytes.toByteArray();
		}

	}

	@Override
	@RequestMapping(value = "/admin/lundinTimesheetReports/showTimesheetHistoryReportUrl.html", method = RequestMethod.POST)
	public @ResponseBody byte[] genShortListTimesheetHistoryReportPDF(
			@ModelAttribute(value = "lundinTimesheetReportsForm") LundinTimesheetReportsForm lundinTimesheetReportsForm,
			HttpServletRequest request, HttpServletResponse response) {

		UUID uuid = UUID.randomUUID();
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		EmployeeShortListDTO reportShortList = generalLogic
				.getShortListEmployeeIdsForReports(companyId,
						lundinTimesheetReportsForm.getMetaData());

		List<BigInteger> employees = reportShortList.getShortListEmployeeIds();
        String employeeString = "";
		if (lundinTimesheetReportsForm.getDepartmentId() != null
				&& lundinTimesheetReportsForm.getDepartmentId() != 0) {
			List<EmployeeListForm> employeeListForms = lundinTimesheetReportsLogic
					.findEmployeeBasedOnDepartment(companyId,
							lundinTimesheetReportsForm.getDepartmentId(),
							employeeId);

			for (EmployeeListForm employeeListForm : employeeListForms) {

				if (employees.contains(BigInteger.valueOf(employeeListForm
						.getEmployeeID())))

					employeeString = employeeString.concat(
							String.valueOf(employeeListForm.getEmployeeID()))
							.concat(",");
			}

		}

		else {
			for (BigInteger employee : employees) {
				employeeString = employeeString
						.concat(String.valueOf(employee)).concat(",");
			}

		}

		lundinTimesheetReportsForm.setEmployeeIds(employeeString);

		List<LundinTimesheetDTO> lundinTimesheets = lundinTimesheetPrintPDFLogic
				.generateTimesheets(lundinTimesheetReportsForm, employeeId,
						companyId);
		ByteArrayOutputStream errorPdfBytes = new ByteArrayOutputStream();
		ByteArrayOutputStream noTimesheetPdfBytes = new ByteArrayOutputStream();
		try {

			response.reset();
			Document copyDocument = new Document();
			LundinTimewritingReportDTO lundinTimewritingReportDTO = lundinTimesheetReportsLogic
					.getOTBatchName(companyId,
							lundinTimesheetReportsForm.getFromBatchId(),
							lundinTimesheetReportsForm.getToBatchId());

			String batchName = lundinTimewritingReportDTO.getFileBatchName();
			String filename = "Timesheet History Report_" + batchName + " .pdf";

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			PdfCopy copy = new PdfCopy(copyDocument, out);

			copyDocument.open();

			PdfReader copyPdfReader;
			int noOfPagesInExistPdf = 0;
			String mimeType = "";

			if (!lundinTimesheets.isEmpty()) {
				for (LundinTimesheetDTO lundinTimesheet : lundinTimesheets) {
					TimesheetFormPdfDTO timesheetFormPdfDTO = lundinPendingTimesheetLogic
							.generateTimesheetPrintPDF(companyId,
									lundinTimesheet.getEmployeeId(),
									lundinTimesheet.getTimesheetId(), true,
									lundinTimesheet);

					copyPdfReader = new PdfReader(
							timesheetFormPdfDTO.getTimesheetPdfByteFile());

					noOfPagesInExistPdf = copyPdfReader.getNumberOfPages();
					for (int page = 0; page < noOfPagesInExistPdf;) {
						copy.addPage(copy
								.getImportedPage(copyPdfReader, ++page));
					}
					copy.freeReader(copyPdfReader);
					copyPdfReader.close();

					mimeType = URLConnection
							.guessContentTypeFromName(timesheetFormPdfDTO
									.getEmployeeNumber()
									+ "_"
									+ timesheetFormPdfDTO
											.getTimesheetBatchDesc()
									+ uuid
									+ ".pdf");
				}
			} else {
				response.reset();
				String mimeTypes = "";
				mimeType = URLConnection
						.guessContentTypeFromName("No Timesheet Found.pdf");
				Document document = new Document();
				PdfWriter writer = PdfWriter.getInstance(document,
						noTimesheetPdfBytes);
				document.open();
				document.add(new Paragraph(
						"No Timesheet history report found for these employees"));
				document.close();
				writer.close();
				response.setContentType("application/" + mimeTypes);
				response.setContentLength(1);

				response.setHeader("Content-Disposition",
						"attachment;filename=No Timesheet Found.pdf");
				return noTimesheetPdfBytes.toByteArray();
			}

			copyDocument.close();

			response.setContentType("application/" + mimeType);
			response.setContentLength(noOfPagesInExistPdf);

			response.setHeader("Content-Disposition", "attachment;filename="
					+ filename);

			return out.toByteArray();
		} catch (Exception e) {

			try {
				response.reset();
				String mimeType = "";
				mimeType = URLConnection.guessContentTypeFromName("error.pdf");
				Document document = new Document();
				PdfWriter writer = PdfWriter.getInstance(document,
						errorPdfBytes);
				document.open();
				document.add(new Paragraph(
						"Error in generating Timesheet history report"));
				document.close();
				writer.close();
				response.setContentType("application/" + mimeType);
				response.setContentLength(1);

				response.setHeader("Content-Disposition",
						"attachment;filename=error.pdf");
			} catch (Exception e1) {
				e.printStackTrace();
			}

			return errorPdfBytes.toByteArray();
		}

	}
	
	@Override
	@RequestMapping(value = "/employee/lundinTimesheetReports/showTimesheetHistoryReportUrl.html", method = RequestMethod.POST)
	public @ResponseBody byte[] genShortListTimesheetHistoryEmpReportPDF(
			@ModelAttribute(value = "lundinTimesheetReportsForm") LundinTimesheetReportsForm lundinTimesheetReportsForm,
			HttpServletRequest request, HttpServletResponse response) {

		UUID uuid = UUID.randomUUID();
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		EmployeeShortListDTO reportShortList = generalLogic
				.getShortListEmployeeIdsForReports(companyId,
						lundinTimesheetReportsForm.getMetaData());

		List<BigInteger> employees = reportShortList.getShortListEmployeeIds();
        String employeeString = "";
		if (lundinTimesheetReportsForm.getDepartmentId() != null
				&& lundinTimesheetReportsForm.getDepartmentId() != 0) {
			List<EmployeeListForm> employeeListForms = lundinTimesheetReportsLogic
					.findEmployeeBasedOnDepartment(companyId,
							lundinTimesheetReportsForm.getDepartmentId(),
							employeeId);

			for (EmployeeListForm employeeListForm : employeeListForms) {

				if (employees.contains(BigInteger.valueOf(employeeListForm
						.getEmployeeID())))

					employeeString = employeeString.concat(
							String.valueOf(employeeListForm.getEmployeeID()))
							.concat(",");
			}

		}

		else {
			for (BigInteger employee : employees) {
				employeeString = employeeString
						.concat(String.valueOf(employee)).concat(",");
			}

		}

		lundinTimesheetReportsForm.setEmployeeIds(employeeString);

		List<LundinTimesheetDTO> lundinTimesheets = lundinTimesheetPrintPDFLogic
				.generateTimesheets(lundinTimesheetReportsForm, employeeId,
						companyId);
		ByteArrayOutputStream errorPdfBytes = new ByteArrayOutputStream();
		ByteArrayOutputStream noTimesheetPdfBytes = new ByteArrayOutputStream();
		try {

			response.reset();
			Document copyDocument = new Document();
			LundinTimewritingReportDTO lundinTimewritingReportDTO = lundinTimesheetReportsLogic
					.getOTBatchName(companyId,
							lundinTimesheetReportsForm.getFromBatchId(),
							lundinTimesheetReportsForm.getToBatchId());

			String batchName = lundinTimewritingReportDTO.getFileBatchName();
			String filename = "Timesheet History Report_" + batchName + " .pdf";

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			PdfCopy copy = new PdfCopy(copyDocument, out);

			copyDocument.open();

			PdfReader copyPdfReader;
			int noOfPagesInExistPdf = 0;
			String mimeType = "";

			if (!lundinTimesheets.isEmpty()) {
				for (LundinTimesheetDTO lundinTimesheet : lundinTimesheets) {
					TimesheetFormPdfDTO timesheetFormPdfDTO = lundinPendingTimesheetLogic
							.generateTimesheetPrintPDF(companyId,
									lundinTimesheet.getEmployeeId(),
									lundinTimesheet.getTimesheetId(), true,
									lundinTimesheet);

					copyPdfReader = new PdfReader(
							timesheetFormPdfDTO.getTimesheetPdfByteFile());

					noOfPagesInExistPdf = copyPdfReader.getNumberOfPages();
					for (int page = 0; page < noOfPagesInExistPdf;) {
						copy.addPage(copy
								.getImportedPage(copyPdfReader, ++page));
					}
					copy.freeReader(copyPdfReader);
					copyPdfReader.close();

					mimeType = URLConnection
							.guessContentTypeFromName(timesheetFormPdfDTO
									.getEmployeeNumber()
									+ "_"
									+ timesheetFormPdfDTO
											.getTimesheetBatchDesc()
									+ uuid
									+ ".pdf");
				}
			} else {
				response.reset();
				String mimeTypes = "";
				mimeType = URLConnection
						.guessContentTypeFromName("No Timesheet Found.pdf");
				Document document = new Document();
				PdfWriter writer = PdfWriter.getInstance(document,
						noTimesheetPdfBytes);
				document.open();
				document.add(new Paragraph(
						"No Timesheet history report found for these employees"));
				document.close();
				writer.close();
				response.setContentType("application/" + mimeTypes);
				response.setContentLength(1);

				response.setHeader("Content-Disposition",
						"attachment;filename=No Timesheet Found.pdf");
				return noTimesheetPdfBytes.toByteArray();
			}

			copyDocument.close();

			response.setContentType("application/" + mimeType);
			response.setContentLength(noOfPagesInExistPdf);

			response.setHeader("Content-Disposition", "attachment;filename="
					+ filename);

			return out.toByteArray();
		} catch (Exception e) {

			try {
				response.reset();
				String mimeType = "";
				mimeType = URLConnection.guessContentTypeFromName("error.pdf");
				Document document = new Document();
				PdfWriter writer = PdfWriter.getInstance(document,
						errorPdfBytes);
				document.open();
				document.add(new Paragraph(
						"Error in generating Timesheet history report"));
				document.close();
				writer.close();
				response.setContentType("application/" + mimeType);
				response.setContentLength(1);

				response.setHeader("Content-Disposition",
						"attachment;filename=error.pdf");
			} catch (Exception e1) {
				e.printStackTrace();
			}

			return errorPdfBytes.toByteArray();
		}

	}
	
	

	@Override
	@RequestMapping(value = "/admin/lundinTimesheetReports/isEmployeeRoleAsTimesheetReviewer.html", method = RequestMethod.POST)
	@ResponseBody
	public String isEmployeeRoleAsTimesheetReviewer(HttpServletRequest request,
			HttpServletResponse response) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		String status = lundinTimesheetReportsLogic
				.isEmployeeRoleAsTimesheetReviewer(companyId, employeeId);
		return status;
	}
	
	@Override
	@RequestMapping(value = "/employee/lundinTimesheetReports/isEmployeeRoleAsTimesheetReviewer.html", method = RequestMethod.POST)
	@ResponseBody
	public String isEmployeRoleAsTimesheetReviewer(HttpServletRequest request,
			HttpServletResponse response) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		String status = lundinTimesheetReportsLogic
				.isEmployeeRoleAsTimesheetReviewer(companyId, employeeId);
		return status;
	}
}
