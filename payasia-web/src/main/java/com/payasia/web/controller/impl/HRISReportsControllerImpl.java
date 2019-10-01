package com.payasia.web.controller.impl;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
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

import com.payasia.common.dto.EmployeeLoginHistoryReportDTO;
import com.payasia.common.dto.HRISReportDTO;
import com.payasia.common.dto.RolePrivilegeReportDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.ClaimReportsForm;
import com.payasia.common.form.EmployeeDetailsResponse;
import com.payasia.common.form.HRISReportsForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.form.SwitchCompanyResponse;
import com.payasia.common.util.DateUtils;
import com.payasia.logic.HRISReportsLogic;
import com.payasia.web.controller.HRISReportsController;
import com.payasia.web.util.PayAsiaSessionAttributes;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.jxls.exception.ParsePropertyException;
import net.sf.jxls.transformer.XLSTransformer;

@Controller
@RequestMapping(value = "/admin/hrisReports")
public class HRISReportsControllerImpl implements HRISReportsController {
	/** The Constant logger. */
	private static final Logger LOGGER = Logger
			.getLogger(HRISReportsControllerImpl.class);

	@Resource
	HRISReportsLogic hrisReportsLogic;

	/** The payasia temp path. */
	@Value("#{payasiaptProperties['payasia.temp.path']}")
	private String PAYASIA_TEMP_PATH;

	/** The document convertor path. */
	@Value("#{payasiaptProperties['payasia.document.converter.path']}")
	private String DOCUMENT_CONVERTOR_PATH;
	
	@Autowired
	private ServletContext servletContext;

	@Resource
	MessageSource messageSource;

	@RequestMapping(value = "/genHRISHeadcountExcelReport.html", method = RequestMethod.POST)
	@Override
	public void genHRISHeadcountExcelReport(
			@ModelAttribute(value = "HRISReportsForm") HRISReportsForm hrisReportsForm,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) {
		UUID uuid = UUID.randomUUID();
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		Boolean isManager = false;
		HRISReportDTO hrisDataDTO = hrisReportsLogic.genHRISHeadcountReport(
				employeeId, companyId, hrisReportsForm, isManager);
		String header = "PayAsia Headcount Report";

		Map beans = new HashMap();
		beans.put("header", header);
		beans.put("companyStatisticReportDTOs",
				hrisDataDTO.getHrisHeadCountEmpDataList());
		beans.put("generatedBy", hrisDataDTO.getCreatedBy());
		beans.put("currentDate", DateUtils.timeStampToStringWithTime(DateUtils
				.getCurrentTimestampWithTime()));

		XLSTransformer transformer = new XLSTransformer();
		File tempFolder = new File(PAYASIA_TEMP_PATH + "/hrisreport/" + uuid);
		tempFolder.mkdirs();
		File tempDestFile = null;
		String templateFileName = servletContext
				.getRealPath(
						"/resources/HRISReportTemplate/PayAsiaHeadcountReportMultipleSheets.xlsx");

		String destFileName = PAYASIA_TEMP_PATH + "/hrisreport/" + uuid
				+ "PayAsia Headcount Report" + uuid + ".xlsx";

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

			String fileName = "PayAsia Headcount Report";
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

	@RequestMapping(value = "/genHRISHeadcountPdfReport.html", method = RequestMethod.POST)
	@Override
	public void genHRISHeadcountPdfReport(
			@ModelAttribute(value = "HRISReportsForm") HRISReportsForm hrisReportsForm,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) {
		UUID uuid = UUID.randomUUID();
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		Boolean isManager = false;
		HRISReportDTO hrisDataDTO = hrisReportsLogic.genHRISHeadcountReport(
				employeeId, companyId, hrisReportsForm, isManager);
		String header = "PayAsia Headcount Report";

		Map beans = new HashMap();
		beans.put("header", header);
		beans.put("companyStatisticReportDTOs",
				hrisDataDTO.getHrisHeadCountEmpDataList());
		beans.put("generatedBy", hrisDataDTO.getCreatedBy());
		beans.put("currentDate", DateUtils.timeStampToStringWithTime(DateUtils
				.getCurrentTimestampWithTime()));

		XLSTransformer transformer = new XLSTransformer();
		File tempDestPdfFile = null;
		File tempDestExcelFile = null;
		File tempFolder = new File(PAYASIA_TEMP_PATH + "/hrisreport/" + uuid);
		tempFolder.mkdirs();
		String templateFileName = servletContext
				.getRealPath(
						"/resources/HRISReportTemplate/PayAsiaHeadcountReportMultipleSheets.xlsx");
		String destFileNameExcel = PAYASIA_TEMP_PATH
				+ "/PayAsiaHeadcountReport" + uuid + ".xlsx";

		String destFileNamePDF = PAYASIA_TEMP_PATH + "/PayAsiaHeadcountReport"
				+ uuid + ".pdf";

		try {
			transformer
					.transformXLS(templateFileName, beans, destFileNameExcel);

			ArrayList<String> cmdList = new ArrayList<String>();

			String file1 = destFileNameExcel;
			String file2 = destFileNamePDF;

			// cmdList.add(PYTHON_EXE_PATH);
			cmdList.add(DOCUMENT_CONVERTOR_PATH);
			cmdList.add(file1);
			cmdList.add(file2);

			ProcessBuilder processBuilder = new ProcessBuilder(cmdList);

			LOGGER.info("processBuilder.command()   "
					+ processBuilder.command());
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

			String fileName = "PayAsia Headcount Report";
			outputStream = response.getOutputStream();
			response.setContentType("application/octet-stream");
			response.setHeader("Cache-Control",
					"must-revalidate, post-check=0, pre-check=0");

			fileName = fileName + ".pdf";
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

	@RequestMapping(value = "/genLoginDetailsExcelReport.html", method = RequestMethod.POST)
	@Override
	public void genLoginDetailseExcelReport(
			@ModelAttribute(value = "ClaimReportsForm") ClaimReportsForm claimReportsForm,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) {
		UUID uuid = UUID.randomUUID();
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		List<EmployeeLoginHistoryReportDTO> employeeLoginHistoryReportDTOs = hrisReportsLogic
				.genLoginDetailsReport(claimReportsForm, companyId);

		Map beans = new HashMap();
		beans.put("employeeLoginHistoryReportDTOs",
				employeeLoginHistoryReportDTOs);

		XLSTransformer transformer = new XLSTransformer();
		File tempFolder = new File(PAYASIA_TEMP_PATH + "/hrisreport/" + uuid);
		tempFolder.mkdirs();
		File tempDestFile = null;
		String templateFileName = servletContext.getRealPath(
				"/resources/HRISReportTemplate/Employee Login Details.xlsx");

		String destFileName = PAYASIA_TEMP_PATH + "/hrisreport/" + uuid
				+ "Employee Login Details" + uuid + ".xlsx";

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

			String fileName = "Employee Login Details";
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

	@RequestMapping(value = "/genLoginDetailsPdfReport.html", method = RequestMethod.POST)
	@Override
	public void genLoginDetailsPdfReport(
			@ModelAttribute(value = "ClaimReportsForm") ClaimReportsForm claimReportsForm,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) {
		UUID uuid = UUID.randomUUID();
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		List<EmployeeLoginHistoryReportDTO> employeeLoginHistoryReportDTOs = hrisReportsLogic
				.genLoginDetailsReport(claimReportsForm, companyId);

		Map beans = new HashMap();
		beans.put("employeeLoginHistoryReportDTOs",
				employeeLoginHistoryReportDTOs);

		XLSTransformer transformer = new XLSTransformer();
		File tempDestPdfFile = null;
		File tempDestExcelFile = null;
		File tempFolder = new File(PAYASIA_TEMP_PATH + "/hrisreport/" + uuid);
		tempFolder.mkdirs();
		String templateFileName = servletContext.getRealPath(
				"/resources/HRISReportTemplate/Employee Login Details.xlsx");
		String destFileNameExcel = PAYASIA_TEMP_PATH + "/EmployeeLoginDetails"
				+ uuid + ".xlsx";

		String destFileNamePDF = PAYASIA_TEMP_PATH + "/EmployeeLoginDetails"
				+ uuid + ".pdf";

		try {
			transformer
					.transformXLS(templateFileName, beans, destFileNameExcel);

			ArrayList<String> cmdList = new ArrayList<String>();

			String file1 = destFileNameExcel;
			String file2 = destFileNamePDF;

			// cmdList.add(PYTHON_EXE_PATH);
			cmdList.add(DOCUMENT_CONVERTOR_PATH);
			cmdList.add(file1);
			cmdList.add(file2);

			ProcessBuilder processBuilder = new ProcessBuilder(cmdList);

			LOGGER.info("processBuilder.command()   "
					+ processBuilder.command());
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

			String fileName = "Employee Login Details";
			outputStream = response.getOutputStream();
			response.setContentType("application/octet-stream");
			response.setHeader("Cache-Control",
					"must-revalidate, post-check=0, pre-check=0");

			fileName = fileName + ".pdf";
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
	@RequestMapping(value = "/getEmployeeList.html", method = RequestMethod.POST)
	@ResponseBody
	public String getEmployeeList(
			@RequestParam(value = "searchCondition", required = false) String searchCondition,
			@RequestParam(value = "searchText", required = false) String searchText,
			@RequestParam(value = "companyName", required = false) String companyName,
			@RequestParam(value = "fromDate", required = false) String fromDate,
			@RequestParam(value = "toDate", required = false) String toDate,
			HttpServletRequest request) {
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		PageRequest pageDTO = new PageRequest();
		SortCondition sortDTO = new SortCondition();

		EmployeeDetailsResponse response = hrisReportsLogic.getEmployeeList(
				searchCondition, searchText, companyName);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(response, jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return null;

	}

	@RequestMapping(value = "/genRolePrivilegeExcelReport.html", method = RequestMethod.POST)
	@Override
	public void genRolePrivilegeExcelReport(
			@ModelAttribute(value = "ClaimReportsForm") ClaimReportsForm claimReportsForm,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) {
		UUID uuid = UUID.randomUUID();
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		Boolean isManager = false;
		List<RolePrivilegeReportDTO> rolePrivilegeDTOs = hrisReportsLogic
				.genRolePrivilegeReport(employeeId, companyId,
						claimReportsForm, isManager);

		Map beans = new HashMap();
		beans.put("rolePrivilegeDTOs", rolePrivilegeDTOs);

		XLSTransformer transformer = new XLSTransformer();
		File tempFolder = new File(PAYASIA_TEMP_PATH + "/hrisreport/" + uuid);
		tempFolder.mkdirs();
		File tempDestFile = null;
		String templateFileName = servletContext
				.getRealPath(
						"/resources/HRISReportTemplate/Employee List With Role and Privilege.xlsx");

		String destFileName = PAYASIA_TEMP_PATH + "/hrisreport/" + uuid
				+ "Employee List With Role and Privilege" + uuid + ".xlsx";

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

			String fileName = "Employee List With Role and Privilege";
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

	@RequestMapping(value = "/genRolePrivilegePdfReport.html", method = RequestMethod.POST)
	@Override
	public void genRolePrivilegePdfReport(
			@ModelAttribute(value = "ClaimReportsForm") ClaimReportsForm claimReportsForm,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) {
		UUID uuid = UUID.randomUUID();
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		Boolean isManager = false;

		List<RolePrivilegeReportDTO> rolePrivilegeDTOs = hrisReportsLogic
				.genRolePrivilegeReport(employeeId, companyId,
						claimReportsForm, isManager);

		Map beans = new HashMap();
		beans.put("rolePrivilegeDTOs", rolePrivilegeDTOs);

		XLSTransformer transformer = new XLSTransformer();
		File tempDestPdfFile = null;
		File tempDestExcelFile = null;
		File tempFolder = new File(PAYASIA_TEMP_PATH + "/hrisreport/" + uuid);
		tempFolder.mkdirs();
		String templateFileName = servletContext
				.getRealPath(
						"/resources/HRISReportTemplate/Employee List With Role and Privilege.xlsx");
		String destFileNameExcel = PAYASIA_TEMP_PATH
				+ "/EmployeeListWithRoleandPrivilege" + uuid + ".xlsx";

		String destFileNamePDF = PAYASIA_TEMP_PATH + "/hrisreport/" + uuid
				+ "/EmployeeListWithRoleandPrivilege" + uuid + ".pdf";

		try {
			transformer
					.transformXLS(templateFileName, beans, destFileNameExcel);

			ArrayList<String> cmdList = new ArrayList<String>();

			String file1 = destFileNameExcel;
			String file2 = destFileNamePDF;

			// cmdList.add(PYTHON_EXE_PATH);
			cmdList.add(DOCUMENT_CONVERTOR_PATH);
			cmdList.add(file1);
			cmdList.add(file2);

			ProcessBuilder processBuilder = new ProcessBuilder(cmdList);

			LOGGER.info("processBuilder.command()   "
					+ processBuilder.command());
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

			String fileName = "Employee List With Role and Privilege";
			outputStream = response.getOutputStream();
			response.setContentType("application/octet-stream");
			response.setHeader("Cache-Control",
					"must-revalidate, post-check=0, pre-check=0");

			fileName = fileName + ".pdf";
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
	@RequestMapping(value = "/switchCompanyList.html", method = RequestMethod.POST)
	@ResponseBody
	public String getSwitchCompanyList(
			@RequestParam(value = "searchCondition", required = false) String searchCondition,
			@RequestParam(value = "searchText", required = false) String searchText,
			@RequestParam(value = "groupName", required = false) String groupName,
			@RequestParam(value = "includeInactiveCompany", required = false) Boolean includeInactiveCompany,
			HttpServletRequest request) {
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		PageRequest pageDTO = new PageRequest();
		SortCondition sortDTO = new SortCondition();

		SwitchCompanyResponse response = hrisReportsLogic.getSwitchCompanyList(
				pageDTO, sortDTO, employeeId, searchCondition, searchText,
				groupName, includeInactiveCompany);
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
