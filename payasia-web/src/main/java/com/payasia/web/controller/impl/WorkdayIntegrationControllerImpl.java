package com.payasia.web.controller.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;

import javax.annotation.Resource;
import javax.mail.internet.MimeUtility;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.WorkDayGenerateReportDTO;
import com.payasia.common.dto.WorkdayFieldMappingDataTransformationDTO;
import com.payasia.common.dto.WorkdayFtpImportHistoryDTO;
import com.payasia.common.dto.WorkdayPaygroupBatchDTO;
import com.payasia.common.dto.WorkdayReportMasterDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.CalculatoryFieldFormResponse;
import com.payasia.common.form.FTPImportHistoryFormResponse;
import com.payasia.common.form.FieldDataTransformationForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.form.WorkdayFtpConfigForm;
import com.payasia.common.form.WorkdayGenerateReportForm;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.ResponseDataConverter;
import com.payasia.logic.WorkdayFtpIntegrationLogic;
import com.payasia.logic.WorkdayReportLogic;
import com.payasia.web.controller.WorkdayIntegrationController;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

@Controller
@RequestMapping(value = { "/admin/WorkdayFTPIntegration" })
public class WorkdayIntegrationControllerImpl implements WorkdayIntegrationController {

	@Value("#{payasiaptProperties['payasia.temp.path']}")
	private String PAYASIA_TEMP_PATH;

	@Resource
	WorkdayFtpIntegrationLogic workdayFtpIntegrationLogic;

	@Resource
	WorkdayReportLogic workdayReportLogic;

	private static final Logger LOGGER = Logger.getLogger(WorkdayIntegrationControllerImpl.class);

	@Override
	@RequestMapping(value = "/saveFTPConfigData.html", method = RequestMethod.POST)
	@ResponseBody
	public String saveFTPConfigData(@ModelAttribute(value = "ftpConfigForm") WorkdayFtpConfigForm ftpConfigForm) {

		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		try {
			workdayFtpIntegrationLogic.saveftpConfigData(ftpConfigForm, companyId);
		} catch (PayAsiaSystemException e) {
			return e.getKey();
		}
		return PayAsiaConstants.PAYASIA_SUCCESS;
	}

	@Override
	@RequestMapping(value = { "/getEmpDataDictionaryFields.html",
			"/getPayDataDictionaryFields.html" }, method = RequestMethod.POST)
	@ResponseBody
	public String getDataDictionaryFields(
			@RequestParam(value = "isTableField", required = false) boolean isTableField) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		CalculatoryFieldFormResponse calculatoryFieldFormResponse = workdayFtpIntegrationLogic
				.getDataDictionaryFields(companyId, isTableField, PayAsiaConstants.EMPLOYEE_ENTITY_NAME);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(calculatoryFieldFormResponse, jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/saveUpdateHROField.html", method = RequestMethod.POST)
	@ResponseBody
	public String saveUpdateHROField(@RequestParam(value = "workdayFieldId", required = true) Long workdayFieldId,
			@RequestParam(value = "hroDictionaryId", required = true) Long hroDictionaryId) {

		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		return "" + workdayFtpIntegrationLogic.saveUpdateFieldMapping(workdayFieldId, hroDictionaryId, companyId);
	}

	@Override
	@RequestMapping(value = "/deleteHROField.html", method = RequestMethod.POST)
	@ResponseBody
	public String deleteHROField(@RequestParam(value = "fieldMappingId", required = true) Long fieldMappingId) {
		workdayFtpIntegrationLogic.deleteHROField(fieldMappingId);
		return "success";
	}

	@Override
	@RequestMapping(value = "/getTransformationData.html", method = RequestMethod.POST)
	@ResponseBody
	public String getDataTransformationData(@RequestParam(value = "fieldMappingId") Long fieldMappingId) {

		List<WorkdayFieldMappingDataTransformationDTO> fieldMappingDataTransformationList = workdayFtpIntegrationLogic
				.getTransformationData(fieldMappingId);

		JSONArray jsonObject = JSONArray.fromObject(fieldMappingDataTransformationList, new JsonConfig());
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/saveUpdateDataTransformationData.html", method = RequestMethod.POST)
	@ResponseBody
	public String saveUpdateDataTransformationData(
			@ModelAttribute(value = "dataTransformationFieldForm") FieldDataTransformationForm dataTransformationFieldForm) {

		workdayFtpIntegrationLogic.saveFieldDataTransformation(dataTransformationFieldForm);
		return "success";
	}

	@Override
	@RequestMapping(value = "/runFTPImport.html", method = RequestMethod.GET)
	@ResponseBody
	public String runFtpImport(@RequestParam boolean isEmployeeData) {

		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		return workdayFtpIntegrationLogic.runFtpImport(companyId, isEmployeeData, employeeId);
	}

	@Override
	@RequestMapping(value = "/ftpImportHistory.html", method = RequestMethod.GET)
	@ResponseBody
	public String getImportHistory(@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "searchCondition", required = true) String searchCondition,
			@RequestParam(value = "searchText", required = true) String searchText,
			@RequestParam(value = "fromDate", required = true) String fromDate,
			@RequestParam(value = "toDate", required = true) String toDate,
			@RequestParam(value = "importType", required = true) String importType) {

		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		WorkdayFtpImportHistoryDTO conditionDTO = new WorkdayFtpImportHistoryDTO();
		if (searchCondition.equals(PayAsiaConstants.FTP_IMPORT_HISTORY_FILENAME)
				&& StringUtils.isNotBlank(searchText)) {
			try {
				conditionDTO.setImportFileName("%" + URLDecoder.decode(searchText, "UTF-8") + "%");
			} catch (UnsupportedEncodingException ex) {
				LOGGER.error(ex.getMessage(), ex);
			}
		}
		if (searchCondition.equals(PayAsiaConstants.FTP_IMPORT_HISTORY_STATUS) && StringUtils.isNotBlank(searchText)) {
			conditionDTO.setImportStatus(searchText);
		}

		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		FTPImportHistoryFormResponse ftpImportHistoryFormResponse = workdayFtpIntegrationLogic
				.getImportHistory(fromDate, toDate, companyId, conditionDTO, pageDTO, sortDTO, importType);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(ftpImportHistoryFormResponse, jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/viewImportLog.html", method = RequestMethod.GET)
	public void viewImportLog(@RequestParam(value = "logFileId", required = true) Long logFileId, @RequestParam Boolean isEmployeeData, 
			HttpServletRequest request, HttpServletResponse response) throws IOException {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		Workbook excelFile = null;
		if(isEmployeeData)
			excelFile = workdayFtpIntegrationLogic.getEmfImportLog(logFileId, companyId);
		else
			excelFile = workdayFtpIntegrationLogic.getPtrxImportLog(logFileId, companyId);
		String time = DateUtils
				.convertCurrentDateTimeWithMilliSecToString(PayAsiaConstants.TEMP_FILE_TIMESTAMP_WITH_MILLISEC_FORMAT);
		String fileName = "ImportLog" + "_" + time + ".xls";

		ServletOutputStream outputStream = response.getOutputStream();
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

		excelFile.write(outputStream);
		outputStream.flush();
		outputStream.close();
	}

	@RequestMapping(value = "/hroWorkdayGererateReport.html", method = RequestMethod.POST)
	@Override
	public void hroWorkdayGererateReport(@RequestParam(value = "batchType") String batchType,
			@RequestParam(value = "batchYear") String batchYear,
			@RequestParam(value = "batchPeriod") String batchPeriod,
			@RequestParam(value = "workDayReport") String workDayReport, HttpServletRequest request,
			HttpServletResponse response) {

		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		WorkdayGenerateReportForm workdayGererateReportDTO = new WorkdayGenerateReportForm();
		workdayGererateReportDTO.setCompanyId(companyId);
		workdayGererateReportDTO.setBatchPeriod(FormatPreserveCryptoUtil.decrypt(Long.parseLong(batchPeriod)));
		workdayGererateReportDTO.setBatchYear(batchYear);
		workdayGererateReportDTO.setBatchType(""+FormatPreserveCryptoUtil.decrypt(Long.parseLong(batchType)));
		workdayGererateReportDTO.setWorkDayReport(FormatPreserveCryptoUtil.decrypt(Long.parseLong(workDayReport)));

		WorkDayGenerateReportDTO workDayGenerateReportDTO = workdayReportLogic
				.generateWorkDayReport(workdayGererateReportDTO);

		ServletOutputStream outputStream = null;

		try {
			outputStream = response.getOutputStream();

			response.setContentType("application/octet-stream");
			response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
			response.setHeader("Content-Disposition", "attachment; filename=\"" + workDayGenerateReportDTO.getEntiryName()+"-"+workDayGenerateReportDTO.getFileName()
					+ " - " + workDayGenerateReportDTO.getFileDatePeriod()+"-"+workDayGenerateReportDTO.getPayGroup() + ".xlsx");

			outputStream = response.getOutputStream();
			outputStream.write(workDayGenerateReportDTO.getWorkBookByteArr());

		} catch (IOException ioException) {
			LOGGER.error(ioException.getMessage(), ioException);
			throw new PayAsiaSystemException(ioException.getMessage(), ioException);

		} finally {
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

	@RequestMapping(value = "/getBatchPeriod.html", method = RequestMethod.POST)
	@ResponseBody
	@Override
	public String getBatchPeriod(@RequestParam(value = "batchYear") String batchYear,
			@RequestParam(value = "workDayReport") String workDayReport,
			@RequestParam(value = "batchType") String batchType) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		Long batchYearVal = 0l, workDayReportVal = 0l, batchTypeVal = 0l;
		if (batchYear != null && !batchYear.equals("")) {
			batchYearVal = Long.parseLong(batchYear);
		}
		if (batchYear != null && !batchYear.equals("")) {
			workDayReportVal = FormatPreserveCryptoUtil.decrypt(Long.parseLong(workDayReport));
		}
		if (batchYear != null && !batchYear.equals("")) {
			batchTypeVal = FormatPreserveCryptoUtil.decrypt(Long.parseLong(batchType));
		}

		List<WorkdayPaygroupBatchDTO> workdayPaygroupBatchDTO = workdayReportLogic.getBatchPeriod(companyId,
				batchYearVal, workDayReportVal, batchTypeVal);
		return ResponseDataConverter.getListToJson(workdayPaygroupBatchDTO);
	}

	@RequestMapping(value = "/getBatchYear.html", method = RequestMethod.POST)
	@ResponseBody
	@Override
	public String getBatchYear() {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		List<WorkdayPaygroupBatchDTO> workdayPaygroupBatchDTO = workdayReportLogic.getBatchYear(companyId);
		return ResponseDataConverter.getListToJson(workdayPaygroupBatchDTO);
	}

	@RequestMapping(value = "/getWorkDayReportMaster.html", method = RequestMethod.POST)
	@ResponseBody
	@Override
	public String getWorkDayReportMaster() {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		List<WorkdayReportMasterDTO> workdayPaygroupBatchDTO = workdayReportLogic.getWorkDayReportMaster(companyId);
		return ResponseDataConverter.getListToJson(workdayPaygroupBatchDTO);
	}
}
