package com.payasia.web.controller.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.mail.internet.MimeUtility;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.LundinEmployeeTimesheetReviewerDTO;
import com.payasia.common.dto.LundinOTBatchDTO;
import com.payasia.common.dto.ResponseObjectDTO;
import com.payasia.common.dto.TimesheetFormPdfDTO;
import com.payasia.common.form.AddClaimFormResponse;
import com.payasia.common.form.DataExportForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.logic.AddClaimLogic;
import com.payasia.logic.CoherentMyOvertimeLogic;
import com.payasia.logic.CoherentMyShiftLogic;
import com.payasia.logic.LionPendingTimesheetLogic;
import com.payasia.logic.LionTimesheetPreferenceLogic;
import com.payasia.logic.LundinPendingTimesheetLogic;
import com.payasia.logic.LundinTimesheetPreferenceLogic;
import com.payasia.logic.LundinTimesheetRequestLogic;
import com.payasia.logic.LundinTimesheetReviewerLogic;
import com.payasia.web.controller.CoherentMyShiftController;
import com.payasia.web.util.PayAsiaSessionAttributes;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

@Controller
//@RequestMapping(value = { "/employee/coherent/ms/", "/admin/coherent/ms/" })
public class CoherentMyShiftControllerImpl implements CoherentMyShiftController {
	private static final Logger LOGGER = Logger
			.getLogger(CoherentMyShiftControllerImpl.class);

	@Resource
	AddClaimLogic addClaimLogic;

	@Resource
	LundinTimesheetRequestLogic lundinRequestLogic;

	@Resource
	LundinTimesheetPreferenceLogic lundinPreferenceLogic;

	@Resource
	LundinTimesheetReviewerLogic lundinTimesheetReviewerLogic;

	@Resource
	LundinPendingTimesheetLogic lundinPendingTimesheetLogic;

	@Resource
	CoherentMyShiftLogic coherentMyShiftLogic;

	@Resource
	LionTimesheetPreferenceLogic lionPreferenceLogic;

	@Resource
	CoherentMyOvertimeLogic coherentMyOvertimeLogic;

	@Resource
	LionPendingTimesheetLogic lionPendingTimesheetLogic;

	@Override
	@RequestMapping(value = "/employee/coherent/ms/search/getAllShift.html", method = RequestMethod.POST)
	@ResponseBody
	public String getAllShift(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "searchCondition", required = false) String searchCondition,
			@RequestParam(value = "searchText", required = false) String searchText,
			@RequestParam(value = "transactionType", required = false) String transactionType,
			@RequestParam(value = "fromDate", required = true) String fromDate,
			@RequestParam(value = "toDate", required = true) String toDate,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		Long empId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		PageRequest pageDTO = new PageRequest();
		if (page == 0)
			page = 1;
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);
		String pageContextPath = request.getContextPath();
		AddClaimFormResponse addClaimFormResponse = null;
		addClaimFormResponse = coherentMyShiftLogic.getAllCoherentShifts(
				fromDate, toDate, empId, pageDTO, sortDTO, pageContextPath,
				transactionType, companyId, searchCondition, searchText);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(addClaimFormResponse,
				jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/employee/coherent/ms/search/getPendingTimesheet.html", method = RequestMethod.POST)
	@ResponseBody
	public String getPendingMysheet(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "fromDate", required = true) String fromDate,
			@RequestParam(value = "toDate", required = true) String toDate,
			@RequestParam(value = "searchCondition", required = false) String searchCondition,
			@RequestParam(value = "searchText", required = false) String searchText,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		Long empId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		AddClaimFormResponse addClaimFormResponse = null;
		addClaimFormResponse = coherentMyShiftLogic.getPendingShift(fromDate,
				toDate, empId, pageDTO, sortDTO, searchCondition, searchText,
				companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(addClaimFormResponse,
				jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/employee/coherent/ms/search/getSubmittedTimesheet.html", method = RequestMethod.POST)
	@ResponseBody
	public String getSubmittedOTTimesheet(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "fromDate", required = true) String fromDate,
			@RequestParam(value = "toDate", required = true) String toDate,
			@RequestParam(value = "searchCondition", required = false) String searchCondition,
			@RequestParam(value = "searchText", required = false) String searchText,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		Long empId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);
		String pageContextPath = request.getContextPath();
		AddClaimFormResponse addClaimFormResponse = null;
		addClaimFormResponse = coherentMyShiftLogic.getSubmittedShift(fromDate,
				toDate, empId, pageDTO, sortDTO, pageContextPath,
				searchCondition, searchText, companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(addClaimFormResponse,
				jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/employee/coherent/ms/search/getApprovedTimesheet.html", method = RequestMethod.POST)
	@ResponseBody
	public String getApprovedMyshift(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "fromDate", required = true) String fromDate,
			@RequestParam(value = "toDate", required = true) String toDate,
			@RequestParam(value = "searchCondition", required = false) String searchCondition,
			@RequestParam(value = "searchText", required = false) String searchText,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		Long empId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);
		String pageContextPath = request.getContextPath();
		AddClaimFormResponse addClaimFormResponse = null;
		addClaimFormResponse = coherentMyShiftLogic.getApprovedShift(fromDate,
				toDate, empId, pageDTO, sortDTO, pageContextPath,
				searchCondition, searchText, companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(addClaimFormResponse,
				jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/employee/coherent/ms/search/getRejectedTimesheet.html", method = RequestMethod.POST)
	@ResponseBody
	public String getRejectedShift(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "fromDate", required = true) String fromDate,
			@RequestParam(value = "toDate", required = true) String toDate,
			@RequestParam(value = "searchCondition", required = false) String searchCondition,
			@RequestParam(value = "searchText", required = false) String searchText,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		Long empId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);
		String pageContextPath = request.getContextPath();
		AddClaimFormResponse addClaimFormResponse = null;
		addClaimFormResponse = coherentMyShiftLogic.getRejectedShift(fromDate,
				toDate, empId, pageDTO, sortDTO, pageContextPath,
				searchCondition, searchText, companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(addClaimFormResponse,
				jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/employee/coherent/ms/getShiftForBatch.html", method = RequestMethod.POST)
	@ResponseBody
	public String getShiftForBatch(
			@RequestParam(value = "batchId") long batchId,
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "page", required = false) Integer page,
			@RequestParam(value = "rows", required = false) Integer rows,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {

		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		return coherentMyShiftLogic.getCoherentShiftJSON(batchId, companyId,
				employeeId);

	}

	@Override
	@RequestMapping(value = "/employee/coherent/ms/updateShiftForBatch.html", method = RequestMethod.POST)
	@ResponseBody
	public String updateShiftForBatch(
			@RequestParam(value = "shiftId") long shiftId,
			@RequestParam(value = "totalShift") String totalShift,
			@RequestParam(value = "remarks") String remarks,
			@RequestParam(value = "isShift") String isShift,
			@RequestParam(value = "coherentShiftType") String coherentShiftType,
			@RequestParam(value = "shiftTypePerDate") String shiftTypePerDate,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		/*ID DECRYPT*/
		shiftId = FormatPreserveCryptoUtil.decrypt(shiftId);
		Map<String, String> statusMap = coherentMyShiftLogic
				.updateCoherentShiftApplicationDetailEmployee(
						String.valueOf(shiftId), totalShift, remarks, isShift,
						coherentShiftType, shiftTypePerDate);

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("success", true);
		jsonObject.put("totShifts", statusMap.get("totShifts"));
		jsonObject.put("isShft", statusMap.get("isShft"));
		jsonObject.put("shiftType", statusMap.get("shiftType"));
		jsonObject.put("coherentShiftType", statusMap.get("coherentShiftType"));
		jsonObject.put("remarks", statusMap.get("remarks"));
		return jsonObject.toString();

	}

	@Override
	@RequestMapping(value = "/employee/coherent/ms/submitToWorkflow.html", method = RequestMethod.POST)
	@ResponseBody
	public String submitShiftToWorkflow(
			@RequestParam("shiftApplicationId") long shiftApplicationId,
			HttpServletRequest request, HttpServletResponse response) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		/*ID DECRYPT*/
		shiftApplicationId = FormatPreserveCryptoUtil.decrypt(shiftApplicationId);

		List<LundinEmployeeTimesheetReviewerDTO> employeeOTReviewerDTOs = lundinTimesheetReviewerLogic
				.getWorkFlowRuleList(employeeId, companyId);
		ResponseObjectDTO responseDto = new ResponseObjectDTO();
		try {
			if (employeeOTReviewerDTOs != null
					&& employeeOTReviewerDTOs.isEmpty()) {
				responseDto.setKey(1);
				responseDto.setSuccess(false);
			} else {
				coherentMyShiftLogic.submitToWorkFlow(employeeOTReviewerDTOs,
						shiftApplicationId, companyId, employeeId);
				responseDto.setKey(1);
				responseDto.setSuccess(true);
			}

			JsonConfig jsonConfig = new JsonConfig();
			JSONObject jsonObject = JSONObject.fromObject(responseDto,
					jsonConfig);
			return jsonObject.toString();
		} catch (Exception e) {

			LOGGER.error(e.getMessage(), e);
			responseDto.setKey(1);
			responseDto.setSuccess(false);
			JsonConfig jsonConfig = new JsonConfig();
			JSONObject jsonObject = JSONObject.fromObject(responseDto,
					jsonConfig);
			return jsonObject.toString();
		}

	}

	@Override
	@RequestMapping(value = "/employee/coherent/ms/openSubmittedShift.html", method = RequestMethod.POST)
	@ResponseBody
	public String openSubmittedShift(
			@RequestParam("shiftApplicationId") long shiftApplicationId,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		/*ID DECRYPT*/
		shiftApplicationId = FormatPreserveCryptoUtil.decrypt(shiftApplicationId);

		return coherentMyShiftLogic.shiftApplications(shiftApplicationId,
				employeeId, companyId);

	}

	@Override
	@RequestMapping(value = "/employee/coherent/ms/withdrawRequest.html", method = RequestMethod.POST)
	@ResponseBody
	public String withdrawRequest(
			@RequestParam("shiftApplicationId") long shiftApplicationId,
			HttpServletRequest request, HttpServletResponse response) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		/*ID DECRYPT*/
		shiftApplicationId = FormatPreserveCryptoUtil.decrypt(shiftApplicationId);

		ResponseObjectDTO responseDto = new ResponseObjectDTO();
		try {
			if (coherentMyShiftLogic.withdrawShiftRequest(shiftApplicationId,
					employeeId, companyId)) {
				responseDto.setKey(1);
				responseDto.setSuccess(true);

			} else {
				responseDto.setKey(1);
				responseDto.setSuccess(false);

			}
			JsonConfig jsonConfig = new JsonConfig();
			JSONObject jsonObject = JSONObject.fromObject(responseDto,
					jsonConfig);
			return jsonObject.toString();
		} catch (Exception e) {

			LOGGER.error(e.getMessage(), e);
			responseDto.setKey(1);
			responseDto.setSuccess(false);
			JsonConfig jsonConfig = new JsonConfig();
			JSONObject jsonObject = JSONObject.fromObject(responseDto,
					jsonConfig);
			return jsonObject.toString();
		}
	}

	@Override
	@RequestMapping(value = "/employee/coherent/ms/printShiftDetail.html", method = RequestMethod.GET)
	public @ResponseBody byte[] printShiftDetail(
			@RequestParam(value = "ShiftApplicationId", required = true) Long ShiftApplicationId,
			HttpServletResponse response, HttpServletRequest request) {
		UUID uuid = UUID.randomUUID();
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		/*ID DECRYPT*/
		ShiftApplicationId = FormatPreserveCryptoUtil.decrypt(ShiftApplicationId);

		boolean hasLundinTimesheetModule = (boolean) request
				.getSession()
				.getAttribute(
						PayAsiaSessionAttributes.COMPANY_HAS_LUNDIN_TIMESHEET_MODULE);
		TimesheetFormPdfDTO timesheetFormPdfDTO = null;
		// timesheetFormPdfDTO = lundinPendingTimesheetLogic
		// .generateTimesheetPrintPDF(companyId, employeeId,
		// ShiftApplicationId, hasLundinTimesheetModule);

		response.reset();
		String mimeType = URLConnection
				.guessContentTypeFromName(timesheetFormPdfDTO
						.getEmployeeNumber()
						+ "_"
						+ timesheetFormPdfDTO.getTimesheetBatchDesc()
						+ uuid
						+ ".pdf");
		response.setContentType("application/" + mimeType);
		response.setContentLength(timesheetFormPdfDTO.getTimesheetPdfByteFile().length);
		String filename = timesheetFormPdfDTO.getEmployeeNumber() + "_"
				+ timesheetFormPdfDTO.getTimesheetBatchDesc() + uuid + ".pdf";

		response.setHeader("Content-Disposition", "attachment;filename="
				+ filename);

		return timesheetFormPdfDTO.getTimesheetPdfByteFile();
	}

	@Override
	@RequestMapping(value = "/employee/coherent/ms/getBatches.html", method = RequestMethod.POST)
	@ResponseBody
	public String getBatches(HttpServletRequest request,
			HttpServletResponse response) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());


		List<LundinOTBatchDTO> coherentOTBatchDTOs = coherentMyShiftLogic
				.getOTBacthesByCompanyId(companyId, employeeId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(coherentOTBatchDTOs,
				jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/employee/coherent/ms/generateAndGetBatches.html", method = RequestMethod.POST)
	@ResponseBody
	public String generateAndGetBatches(HttpServletRequest request,
			HttpServletResponse response) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());


		try {
			coherentMyOvertimeLogic.createOTBatches(
					Calendar.getInstance().get(Calendar.YEAR), companyId);
			List<LundinOTBatchDTO> coherentBatches = null;
			coherentBatches = coherentMyShiftLogic.getOTBacthesByCompanyId(
					companyId, employeeId);
			if (coherentBatches == null || coherentBatches.isEmpty()) {
				coherentMyOvertimeLogic.createOTBatches(Calendar.getInstance()
						.get(Calendar.YEAR), companyId);
				coherentBatches = coherentMyShiftLogic.getOTBacthesByCompanyId(
						companyId, employeeId);
			}

			JsonConfig jsonConfig = new JsonConfig();
			JSONArray jsonObject = JSONArray.fromObject(coherentBatches,
					jsonConfig);
			return jsonObject.toString();
		} catch (Exception e) {
			JSONObject obj = new JSONObject();
			obj.put("success", "yes");
			LOGGER.error(e.getMessage(), e);
			return obj.toString();

		}

	}

	@Override
	@RequestMapping(value = "/employee/coherent/ms/saveAsDraftWorkflow.html", method = RequestMethod.POST)
	@ResponseBody
	public String saveAsDraftWorkflow(
			@RequestParam("shiftApplicationId") long shiftApplicationId,
			HttpServletRequest request, HttpServletResponse response) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		/*ID DECRYPT*/
		shiftApplicationId = FormatPreserveCryptoUtil.decrypt(shiftApplicationId);

		ResponseObjectDTO responseDto = new ResponseObjectDTO();
		try {
			coherentMyShiftLogic.saveAsDraftTimesheet(shiftApplicationId,
					employeeId, companyId);
			responseDto.setKey(1);
			responseDto.setSuccess(true);

			JsonConfig jsonConfig = new JsonConfig();
			JSONObject jsonObject = JSONObject.fromObject(responseDto,
					jsonConfig);
			return jsonObject.toString();
		} catch (Exception e) {

			LOGGER.error(e.getMessage(), e);
			responseDto.setKey(1);
			responseDto.setSuccess(false);
			JsonConfig jsonConfig = new JsonConfig();
			JSONObject jsonObject = JSONObject.fromObject(responseDto,
					jsonConfig);
			return jsonObject.toString();
		}

	}

	@Override
	@RequestMapping(value = "/employee/coherent/ms/exportShiftDetail.html", method = RequestMethod.GET)
	public void exportShiftDetail(
			@RequestParam(value = "timesheetId", required = true) Long timesheetId,
			HttpServletResponse response, HttpServletRequest request)
			throws IOException {
		UUID uuid = UUID.randomUUID();
		/*ID DECRYPT*/
		timesheetId = FormatPreserveCryptoUtil.decrypt(timesheetId);
		DataExportForm exportForm = coherentMyShiftLogic
				.generateShiftExcel(timesheetId);

		Workbook excelFile = exportForm.getWorkbook();

		String fileName = exportForm.getFinalFileName() + "_" + uuid + ".xls";

		ServletOutputStream outputStream = response.getOutputStream();
		response.setContentType("application/octet-stream");
		response.setHeader("Cache-Control",
				"must-revalidate, post-check=0, pre-check=0");

		String user_agent = request.getHeader("user-agent");
		boolean isInternetExplorer = (user_agent.indexOf("MSIE") > -1);
		if (isInternetExplorer) {
			response.setHeader(
					"Content-disposition",
					"attachment; filename=\""
							+ URLEncoder.encode(fileName, "utf-8").replaceAll(
									"\\+", " ") + "\"");
		} else {
			response.setHeader("Content-disposition", "attachment; filename=\""
					+ MimeUtility.encodeWord(fileName, "utf-8", "Q") + "\"");
		}

		excelFile.write(outputStream);
		outputStream.flush();
		outputStream.close();
	}

}
