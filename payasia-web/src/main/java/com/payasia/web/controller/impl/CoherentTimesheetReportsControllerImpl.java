package com.payasia.web.controller.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
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

import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.CoherentTimesheetReportDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.CoherentTimesheetReportsForm;
import com.payasia.common.util.DateUtils;
import com.payasia.logic.CoherentTimesheetReportsLogic;
import com.payasia.web.controller.CoherentTimesheetReportsController;

import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;
import net.sf.jxls.exception.ParsePropertyException;
import net.sf.jxls.transformer.XLSTransformer;

@Controller
public class CoherentTimesheetReportsControllerImpl implements
		CoherentTimesheetReportsController {
	/** The Constant logger. */
	private static final Logger LOGGER = Logger
			.getLogger(CoherentTimesheetReportsControllerImpl.class);

	/** The coherentTimesheetReportsLogic. */
	@Resource
	CoherentTimesheetReportsLogic coherentTimesheetReportsLogic;

	/** The payasia temp path. */
	@Value("#{payasiaptProperties['payasia.temp.path']}")
	private String PAYASIA_TEMP_PATH;

	/** The document convertor path. */
	@Value("#{payasiaptProperties['payasia.python.DocumentConverter.path']}")
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
	@RequestMapping(value = "/admin/coherentTimesheetReports/otBatchList.html", method = RequestMethod.POST)
	@ResponseBody
	public String otBatchList(HttpServletRequest request,
			@RequestParam(value = "year", required = false) int year) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		List<CoherentTimesheetReportsForm> otBatchList = coherentTimesheetReportsLogic
				.otBatchList(companyId, year);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(otBatchList, jsonConfig);
		return jsonObject.toString();
	}
	
	@Override
	@RequestMapping(value = "/employee/coherentTimesheetReports/otBatchList.html", method = RequestMethod.POST)
	@ResponseBody
	public String otBatchEmpList(HttpServletRequest request,
			@RequestParam(value = "year", required = false) int year) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		List<CoherentTimesheetReportsForm> otBatchList = coherentTimesheetReportsLogic
				.otBatchList(companyId, year);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(otBatchList, jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/admin/coherentTimesheetReports/costCentreFieldList.html", method = RequestMethod.POST)
	@ResponseBody
	public String costCentreFieldList(HttpServletRequest request) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
        List<CoherentTimesheetReportsForm> costCentreFieldList = coherentTimesheetReportsLogic
				.getEmpDynCostCentreFieldList(companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(costCentreFieldList,
				jsonConfig);
		return jsonObject.toString();
	}
	
	@Override
	@RequestMapping(value = "/employee/coherentTimesheetReports/costCentreFieldList.html", method = RequestMethod.POST)
	@ResponseBody
	public String costCentreFieldEmpList(HttpServletRequest request) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
        List<CoherentTimesheetReportsForm> costCentreFieldList = coherentTimesheetReportsLogic
				.getEmpDynCostCentreFieldList(companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(costCentreFieldList,
				jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/admin/coherentTimesheetReports/coherentEmployeeList.html", method = RequestMethod.POST)
	@ResponseBody
	public String coherentEmployeeList(HttpServletRequest request) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		List<CoherentTimesheetReportsForm> lionEmployeeList = coherentTimesheetReportsLogic
				.coherentEmployeeList(employeeId, companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(lionEmployeeList,
				jsonConfig);
		return jsonObject.toString();
	}
	
	@Override
	@RequestMapping(value = "/employee/coherentTimesheetReports/coherentEmployeeList.html", method = RequestMethod.POST)
	@ResponseBody
	public String coherentEmployeeeList(HttpServletRequest request) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		List<CoherentTimesheetReportsForm> lionEmployeeList = coherentTimesheetReportsLogic
				.coherentEmployeeList(employeeId, companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(lionEmployeeList,
				jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/admin/coherentTimesheetReports/coherentTimesheetReviewerList.html", method = RequestMethod.POST)
	@ResponseBody
	public String coherentTimesheetReviewerList(HttpServletRequest request) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		List<CoherentTimesheetReportsForm> lionEmployeeList = coherentTimesheetReportsLogic
				.coherentTimesheetReviewerList(employeeId, companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(lionEmployeeList,
				jsonConfig);
		return jsonObject.toString();
	}
	
	@Override
	@RequestMapping(value = "/employee/coherentTimesheetReports/coherentTimesheetReviewerList.html", method = RequestMethod.POST)
	@ResponseBody
	public String coherentTimesheetReviewerEmpList(HttpServletRequest request) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		List<CoherentTimesheetReportsForm> lionEmployeeList = coherentTimesheetReportsLogic
				.coherentTimesheetReviewerList(employeeId, companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(lionEmployeeList,
				jsonConfig);
		return jsonObject.toString();
	}


	@Override
	@RequestMapping(value = "/admin/coherentTimesheetReports/coherentTimesheetEmpListForManager.html", method = RequestMethod.POST)
	@ResponseBody
	public String coherentTimesheetEmpListForManager(HttpServletRequest request) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		List<CoherentTimesheetReportsForm> lionEmployeeList = coherentTimesheetReportsLogic
				.coherentTimesheetEmpListForManager(employeeId, companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(lionEmployeeList,
				jsonConfig);
		return jsonObject.toString();
	}
	
	@Override
	@RequestMapping(value = "/employee/coherentTimesheetReports/coherentTimesheetEmpListForManager.html", method = RequestMethod.POST)
	@ResponseBody
	public String coherentTimesheetEmployeeListForManager(HttpServletRequest request) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		List<CoherentTimesheetReportsForm> lionEmployeeList = coherentTimesheetReportsLogic
				.coherentTimesheetEmpListForManager(employeeId, companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(lionEmployeeList,
				jsonConfig);
		return jsonObject.toString();
	}


	@RequestMapping(value = "/admin/coherentTimesheetReports/genEmpOvertimeDetailRepExcelFile.html", method = RequestMethod.POST)
	@Override
	public void genEmpOvertimeDetailRepExcelFile(
			@ModelAttribute(value = "coherentTimesheetReportsForm") CoherentTimesheetReportsForm coherentTimesheetReportsForm,
			@RequestParam(value = "dataDictionaryIds", required = true) String[] dataDictionaryIds,
			HttpServletRequest request, HttpServletResponse response) {
		UUID uuid = UUID.randomUUID();
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		Boolean isManager = false;
		if (coherentTimesheetReportsForm.isManagerRole()) {
			isManager = true;
		}
		CoherentTimesheetReportDTO coherentTimesheetReportDTO = coherentTimesheetReportsLogic
				.genEmpOvertimeDetailRepExcelFile(companyId, employeeId,
						coherentTimesheetReportsForm, isManager,
						dataDictionaryIds);

		CoherentTimesheetReportDTO coherentBatchReportDTO = new CoherentTimesheetReportDTO();
		if (coherentTimesheetReportDTO.getFromBatchId() != null) {
			coherentBatchReportDTO = coherentTimesheetReportsLogic
					.getOTBatchName(companyId,
							coherentTimesheetReportDTO.getFromBatchId(),
							coherentTimesheetReportDTO.getToBatchId());
		} else if (coherentTimesheetReportsForm.getFromBatchId() != null) {
			coherentBatchReportDTO = coherentTimesheetReportsLogic
					.getOTBatchName(companyId,
							coherentTimesheetReportsForm.getFromBatchId(),
							coherentTimesheetReportsForm.getToBatchId());
		}

		String header = coherentTimesheetReportDTO.getCompanyName();
		String subHeader2 = "Over Time Report ";
		String fileBatchName = "";
		if (coherentBatchReportDTO.getFromBatchDate() != null) {
			subHeader2 += "From " + coherentBatchReportDTO.getFromBatchDate()
					+ " To " + coherentBatchReportDTO.getToBatchDate();
			fileBatchName = coherentBatchReportDTO.getFileBatchName();
		}

		Map beans = new HashMap();
		beans.put("header", header);
		beans.put("subHeader2", subHeader2);
		beans.put("totalEmployeeCount", "Total Employees: "
				+ coherentTimesheetReportDTO.getTotalEmployeesCount());
		beans.put(
				"currentDate",
				"Timestamp: "
						+ DateUtils.timeStampToStringWithTime(DateUtils
								.getCurrentTimestampWithTime()));

		if (coherentTimesheetReportDTO.getDataDictNameList() != null
				&& coherentTimesheetReportDTO.getDataDictNameList().size() > 0) {
			beans.put("custFieldColNameList",
					coherentTimesheetReportDTO.getDataDictNameList());
		}
		beans.put("lundinCustomDataList",
				coherentTimesheetReportDTO.getLeaveCustomDataDTOs());

		beans.put("empTimesheetList",
				coherentTimesheetReportDTO.getCoherentOvertimeReportDTOs());

		XLSTransformer transformer = new XLSTransformer();
		File tempFolder = new File(PAYASIA_TEMP_PATH
				+ "/CoherentReportTemplate/" + uuid);
		tempFolder.mkdirs();
		File tempDestFile = null;
		String templateFileName = "";

		if (coherentTimesheetReportDTO.getDataDictNameList() != null
				&& coherentTimesheetReportDTO.getDataDictNameList().size() > 0) {
			templateFileName = servletContext
					.getRealPath(
							"/resources/CoherentReportTemplate/Employee Overtime Detail Report.xlsx");
		} else {
			templateFileName = servletContext
					.getRealPath(
							"/resources/CoherentReportTemplate/Employee Overtime Detail ReportWO CustomField.xlsx");
		}
		String destFileName = PAYASIA_TEMP_PATH + "/CoherentReportTemplate/"
				+ uuid + "/eOvertime Report_" + fileBatchName + uuid + ".xlsx";
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

			String fileName = "eOvertime Report_" + fileBatchName;
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
	
	@RequestMapping(value = "/employee/coherentTimesheetReports/genEmpOvertimeDetailRepExcelFile.html", method = RequestMethod.POST)
	@Override
	public void genEmployeeOvertimeDetailRepExcelFile(
			@ModelAttribute(value = "coherentTimesheetReportsForm") CoherentTimesheetReportsForm coherentTimesheetReportsForm,
			@RequestParam(value = "dataDictionaryIds", required = true) String[] dataDictionaryIds,
			HttpServletRequest request, HttpServletResponse response) {
		UUID uuid = UUID.randomUUID();
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		Boolean isManager = false;
		if (coherentTimesheetReportsForm.isManagerRole()) {
			isManager = true;
		}
		CoherentTimesheetReportDTO coherentTimesheetReportDTO = coherentTimesheetReportsLogic
				.genEmpOvertimeDetailRepExcelFile(companyId, employeeId,
						coherentTimesheetReportsForm, isManager,
						dataDictionaryIds);

		CoherentTimesheetReportDTO coherentBatchReportDTO = new CoherentTimesheetReportDTO();
		if (coherentTimesheetReportDTO.getFromBatchId() != null) {
			coherentBatchReportDTO = coherentTimesheetReportsLogic
					.getOTBatchName(companyId,
							coherentTimesheetReportDTO.getFromBatchId(),
							coherentTimesheetReportDTO.getToBatchId());
		} else if (coherentTimesheetReportsForm.getFromBatchId() != null) {
			coherentBatchReportDTO = coherentTimesheetReportsLogic
					.getOTBatchName(companyId,
							coherentTimesheetReportsForm.getFromBatchId(),
							coherentTimesheetReportsForm.getToBatchId());
		}

		String header = coherentTimesheetReportDTO.getCompanyName();
		String subHeader2 = "Over Time Report ";
		String fileBatchName = "";
		if (coherentBatchReportDTO.getFromBatchDate() != null) {
			subHeader2 += "From " + coherentBatchReportDTO.getFromBatchDate()
					+ " To " + coherentBatchReportDTO.getToBatchDate();
			fileBatchName = coherentBatchReportDTO.getFileBatchName();
		}

		Map beans = new HashMap();
		beans.put("header", header);
		beans.put("subHeader2", subHeader2);
		beans.put("totalEmployeeCount", "Total Employees: "
				+ coherentTimesheetReportDTO.getTotalEmployeesCount());
		beans.put(
				"currentDate",
				"Timestamp: "
						+ DateUtils.timeStampToStringWithTime(DateUtils
								.getCurrentTimestampWithTime()));

		if (coherentTimesheetReportDTO.getDataDictNameList() != null
				&& coherentTimesheetReportDTO.getDataDictNameList().size() > 0) {
			beans.put("custFieldColNameList",
					coherentTimesheetReportDTO.getDataDictNameList());
		}
		beans.put("lundinCustomDataList",
				coherentTimesheetReportDTO.getLeaveCustomDataDTOs());

		beans.put("empTimesheetList",
				coherentTimesheetReportDTO.getCoherentOvertimeReportDTOs());

		XLSTransformer transformer = new XLSTransformer();
		File tempFolder = new File(PAYASIA_TEMP_PATH
				+ "/CoherentReportTemplate/" + uuid);
		tempFolder.mkdirs();
		File tempDestFile = null;
		String templateFileName = "";

		if (coherentTimesheetReportDTO.getDataDictNameList() != null
				&& coherentTimesheetReportDTO.getDataDictNameList().size() > 0) {
			templateFileName = servletContext
					.getRealPath(
							"/resources/CoherentReportTemplate/Employee Overtime Detail Report.xlsx");
		} else {
			templateFileName =servletContext
					.getRealPath(
							"/resources/CoherentReportTemplate/Employee Overtime Detail ReportWO CustomField.xlsx");
		}
		String destFileName = PAYASIA_TEMP_PATH + "/CoherentReportTemplate/"
				+ uuid + "/eOvertime Report_" + fileBatchName + uuid + ".xlsx";
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

			String fileName = "eOvertime Report_" + fileBatchName;
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

	@RequestMapping(value = "/admin/coherentTimesheetReports/genEmpShiftDetailRepExcelFile.html", method = RequestMethod.POST)
	@Override
	public void genEmpShiftDetailRepExcelFile(
			@ModelAttribute(value = "coherentTimesheetReportsForm") CoherentTimesheetReportsForm coherentTimesheetReportsForm,
			@RequestParam(value = "dataDictionaryIds", required = true) String[] dataDictionaryIds,
			HttpServletRequest request, HttpServletResponse response) {
		UUID uuid = UUID.randomUUID();
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		Boolean isManager = false;
		if (coherentTimesheetReportsForm.isManagerRole()) {
			isManager = true;
		}
		CoherentTimesheetReportDTO coherentTimesheetReportDTO = coherentTimesheetReportsLogic
				.genEmpShiftDetailRepExcelFile(companyId, employeeId,
						coherentTimesheetReportsForm, isManager,
						dataDictionaryIds);

		CoherentTimesheetReportDTO coherentBatchReportDTO = new CoherentTimesheetReportDTO();
		if (coherentTimesheetReportDTO.getFromBatchId() != null) {
			coherentBatchReportDTO = coherentTimesheetReportsLogic
					.getOTBatchName(companyId,
							coherentTimesheetReportDTO.getFromBatchId(),
							coherentTimesheetReportDTO.getToBatchId());
		} else if (coherentTimesheetReportsForm.getFromBatchId() != null) {
			coherentBatchReportDTO = coherentTimesheetReportsLogic
					.getOTBatchName(companyId,
							coherentTimesheetReportsForm.getFromBatchId(),
							coherentTimesheetReportsForm.getToBatchId());
		}

		String header = coherentTimesheetReportDTO.getCompanyName();
		String subHeader2 = "Shift Report ";
		String fileBatchName = "";
		if (coherentBatchReportDTO.getFromBatchDate() != null) {
			subHeader2 += "From " + coherentBatchReportDTO.getFromBatchDate()
					+ " To " + coherentBatchReportDTO.getToBatchDate();
			fileBatchName = coherentBatchReportDTO.getFileBatchName();
		}

		Map beans = new HashMap();
		beans.put("header", header);
		beans.put("subHeader2", subHeader2);
		beans.put("totalEmployeeCount", "Total Employees: "
				+ coherentTimesheetReportDTO.getTotalEmployeesCount());
		beans.put(
				"currentDate",
				"Timestamp: "
						+ DateUtils.timeStampToStringWithTime(DateUtils
								.getCurrentTimestampWithTime()));

		if (coherentTimesheetReportDTO.getDataDictNameList() != null
				&& coherentTimesheetReportDTO.getDataDictNameList().size() > 0) {
			beans.put("custFieldColNameList",
					coherentTimesheetReportDTO.getDataDictNameList());
		}
		beans.put("lundinCustomDataList",
				coherentTimesheetReportDTO.getLeaveCustomDataDTOs());

		beans.put("empTimesheetList",
				coherentTimesheetReportDTO.getCoherentShjiftReportDTOs());

		XLSTransformer transformer = new XLSTransformer();
		File tempFolder = new File(PAYASIA_TEMP_PATH
				+ "/CoherentReportTemplate/" + uuid);
		tempFolder.mkdirs();
		File tempDestFile = null;
		String templateFileName = "";

		if (coherentTimesheetReportDTO.getDataDictNameList() != null
				&& coherentTimesheetReportDTO.getDataDictNameList().size() > 0) {
			templateFileName = servletContext
					.getRealPath(
							"/resources/CoherentReportTemplate/EmployeeShiftDetailReport.xlsx");
		} else {
			templateFileName = servletContext
					.getRealPath(
							"/resources/CoherentReportTemplate/EmployeeShiftCustomreport.xlsx");
		}
		String destFileName = PAYASIA_TEMP_PATH + "/CoherentReportTemplate/"
				+ uuid + "/Shift Report_" + fileBatchName + uuid + ".xlsx";
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

			String fileName = "Shift Report_" + fileBatchName;
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
	@RequestMapping(value = "/employee/coherentTimesheetReports/genEmpShiftDetailRepExcelFile.html", method = RequestMethod.POST)
	@Override
	public void genEmployeeShiftDetailRepExcelFile(
			@ModelAttribute(value = "coherentTimesheetReportsForm") CoherentTimesheetReportsForm coherentTimesheetReportsForm,
			@RequestParam(value = "dataDictionaryIds", required = true) String[] dataDictionaryIds,
			HttpServletRequest request, HttpServletResponse response) {
		UUID uuid = UUID.randomUUID();
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		Boolean isManager = false;
		if (coherentTimesheetReportsForm.isManagerRole()) {
			isManager = true;
		}
		CoherentTimesheetReportDTO coherentTimesheetReportDTO = coherentTimesheetReportsLogic
				.genEmpShiftDetailRepExcelFile(companyId, employeeId,
						coherentTimesheetReportsForm, isManager,
						dataDictionaryIds);

		CoherentTimesheetReportDTO coherentBatchReportDTO = new CoherentTimesheetReportDTO();
		if (coherentTimesheetReportDTO.getFromBatchId() != null) {
			coherentBatchReportDTO = coherentTimesheetReportsLogic
					.getOTBatchName(companyId,
							coherentTimesheetReportDTO.getFromBatchId(),
							coherentTimesheetReportDTO.getToBatchId());
		} else if (coherentTimesheetReportsForm.getFromBatchId() != null) {
			coherentBatchReportDTO = coherentTimesheetReportsLogic
					.getOTBatchName(companyId,
							coherentTimesheetReportsForm.getFromBatchId(),
							coherentTimesheetReportsForm.getToBatchId());
		}

		String header = coherentTimesheetReportDTO.getCompanyName();
		String subHeader2 = "Shift Report ";
		String fileBatchName = "";
		if (coherentBatchReportDTO.getFromBatchDate() != null) {
			subHeader2 += "From " + coherentBatchReportDTO.getFromBatchDate()
					+ " To " + coherentBatchReportDTO.getToBatchDate();
			fileBatchName = coherentBatchReportDTO.getFileBatchName();
		}

		Map beans = new HashMap();
		beans.put("header", header);
		beans.put("subHeader2", subHeader2);
		beans.put("totalEmployeeCount", "Total Employees: "
				+ coherentTimesheetReportDTO.getTotalEmployeesCount());
		beans.put(
				"currentDate",
				"Timestamp: "
						+ DateUtils.timeStampToStringWithTime(DateUtils
								.getCurrentTimestampWithTime()));

		if (coherentTimesheetReportDTO.getDataDictNameList() != null
				&& coherentTimesheetReportDTO.getDataDictNameList().size() > 0) {
			beans.put("custFieldColNameList",
					coherentTimesheetReportDTO.getDataDictNameList());
		}
		beans.put("lundinCustomDataList",
				coherentTimesheetReportDTO.getLeaveCustomDataDTOs());

		beans.put("empTimesheetList",
				coherentTimesheetReportDTO.getCoherentShjiftReportDTOs());

		XLSTransformer transformer = new XLSTransformer();
		File tempFolder = new File(PAYASIA_TEMP_PATH
				+ "/CoherentReportTemplate/" + uuid);
		tempFolder.mkdirs();
		File tempDestFile = null;
		String templateFileName = "";

		if (coherentTimesheetReportDTO.getDataDictNameList() != null
				&& coherentTimesheetReportDTO.getDataDictNameList().size() > 0) {
			templateFileName = servletContext
					.getRealPath(
							"/resources/CoherentReportTemplate/EmployeeShiftDetailReport.xlsx");
		} else {
			templateFileName = servletContext
					.getRealPath(
							"/resources/CoherentReportTemplate/EmployeeShiftCustomreport.xlsx");
		}
		String destFileName = PAYASIA_TEMP_PATH + "/CoherentReportTemplate/"
				+ uuid + "/Shift Report_" + fileBatchName + uuid + ".xlsx";
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

			String fileName = "Shift Report_" + fileBatchName;
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
}
