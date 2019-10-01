package com.payasia.web.controller.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.LundinEmployeeTimesheetReviewerDTO;
import com.payasia.common.dto.LundinOTBatchDTO;
import com.payasia.common.dto.ResponseObjectDTO;
import com.payasia.common.form.AddClaimFormResponse;
import com.payasia.common.form.CoherentTimesheetDTO;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.logic.CoherentMyOvertimeLogic;
import com.payasia.logic.LundinTimesheetReviewerLogic;
import com.payasia.web.controller.CoherentMyOvertimeController;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

@Controller
// @RequestMapping(value = { "/employee/coherent/ts/", "/admin/coherent/ts/" })
public class CoherentMyOvertimeControllerImpl implements CoherentMyOvertimeController {

	private static final Logger LOGGER = Logger.getLogger(LundinTimesheetControllerImpl.class);

	@Resource
	CoherentMyOvertimeLogic coherentMyOvertimeLogic;

	@Resource
	LundinTimesheetReviewerLogic lundinTimesheetReviewerLogic;

	@Override
	@RequestMapping(value = "/employee/coherent/ts/search/getPendingTimesheet.html", method = RequestMethod.POST)
	@ResponseBody
	public String getPendingOTTimesheet(@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "fromDate", required = true) String fromDate,
			@RequestParam(value = "toDate", required = true) String toDate,
			@RequestParam(value = "searchCondition", required = false) String searchCondition,
			@RequestParam(value = "searchText", required = false) String searchText,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows, HttpServletRequest request,
			HttpServletResponse response, HttpSession session) {
		Long empId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		AddClaimFormResponse addClaimFormResponse = null;
		addClaimFormResponse = coherentMyOvertimeLogic.getPendingTimesheet(fromDate, toDate, empId, pageDTO, sortDTO,
				searchCondition, searchText, companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(addClaimFormResponse, jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/employee/coherent/ts/search/getSubmittedTimesheet.html", method = RequestMethod.POST)
	@ResponseBody
	public String getSubmittedOTTimesheet(@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "fromDate", required = true) String fromDate,
			@RequestParam(value = "toDate", required = true) String toDate,
			@RequestParam(value = "searchCondition", required = false) String searchCondition,
			@RequestParam(value = "searchText", required = false) String searchText,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows, HttpServletRequest request,
			HttpServletResponse response, HttpSession session) {
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
		addClaimFormResponse = coherentMyOvertimeLogic.getSubmittedTimesheet(fromDate, toDate, empId, pageDTO, sortDTO,
				pageContextPath, searchCondition, searchText, companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(addClaimFormResponse, jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/employee/coherent/ts/search/getApprovedTimesheet.html", method = RequestMethod.POST)
	@ResponseBody
	public String getApprovedOTTimesheet(@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "fromDate", required = true) String fromDate,
			@RequestParam(value = "toDate", required = true) String toDate,
			@RequestParam(value = "searchCondition", required = false) String searchCondition,
			@RequestParam(value = "searchText", required = false) String searchText,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows, HttpServletRequest request,
			HttpServletResponse response, HttpSession session) {
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
		addClaimFormResponse = coherentMyOvertimeLogic.getApprovedTimesheet(fromDate, toDate, empId, pageDTO, sortDTO,
				pageContextPath, searchCondition, searchText, companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(addClaimFormResponse, jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/employee/coherent/ts/search/getRejectedTimesheet.html", method = RequestMethod.POST)
	@ResponseBody
	public String getRejectedOTTimesheet(@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "fromDate", required = true) String fromDate,
			@RequestParam(value = "toDate", required = true) String toDate,
			@RequestParam(value = "searchCondition", required = false) String searchCondition,
			@RequestParam(value = "searchText", required = false) String searchText,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows, HttpServletRequest request,
			HttpServletResponse response, HttpSession session) {
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
		addClaimFormResponse = coherentMyOvertimeLogic.getRejectedTimesheet(fromDate, toDate, empId, pageDTO, sortDTO,
				pageContextPath, searchCondition, searchText, companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(addClaimFormResponse, jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/employee/coherent/ts/search/getWithdrawnTimesheet.html", method = RequestMethod.POST)
	@ResponseBody
	public String getWithdrawnOTTimesheet(@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "fromDate", required = true) String fromDate,
			@RequestParam(value = "toDate", required = true) String toDate,
			@RequestParam(value = "searchCondition", required = false) String searchCondition,
			@RequestParam(value = "searchText", required = false) String searchText,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows, HttpServletRequest request,
			HttpServletResponse response, HttpSession session) {
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
		addClaimFormResponse = coherentMyOvertimeLogic.getWithdrawnTimesheet(fromDate, toDate, empId, pageDTO, sortDTO,
				pageContextPath, searchCondition, searchText, companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(addClaimFormResponse, jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/employee/coherent/ts/search/getAllTimesheet.html", method = RequestMethod.POST)
	@ResponseBody
	public String getAllOTTimesheet(@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "searchCondition", required = false) String searchCondition,
			@RequestParam(value = "searchText", required = false) String searchText,
			@RequestParam(value = "transactionType", required = false) String transactionType,
			@RequestParam(value = "fromDate", required = true) String fromDate,
			@RequestParam(value = "toDate", required = true) String toDate,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows, HttpServletRequest request,
			HttpServletResponse response, HttpSession session) {
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
		addClaimFormResponse = coherentMyOvertimeLogic.getAllTimesheet(fromDate, toDate, empId, pageDTO, sortDTO,
				pageContextPath, transactionType, companyId, searchCondition, searchText);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(addClaimFormResponse, jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/employee/coherent/ts/getBatches.html", method = RequestMethod.POST)
	@ResponseBody
	public String getBatches(HttpServletRequest request, HttpServletResponse response) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		List<LundinOTBatchDTO> coherentOTBatchDTOs = coherentMyOvertimeLogic.getOTBacthesByCompanyId(companyId,
				employeeId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(coherentOTBatchDTOs, jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/employee/coherent/ts/generateAndGetBatches.html", method = RequestMethod.POST)
	@ResponseBody
	public String generateAndGetBatches(HttpServletRequest request, HttpServletResponse response) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		try {
			coherentMyOvertimeLogic.createOTBatches(Calendar.getInstance().get(Calendar.YEAR), companyId);
			List<LundinOTBatchDTO> coherentBatches = null;
			coherentBatches = coherentMyOvertimeLogic.getOTBacthesByCompanyId(companyId, employeeId);
			if (coherentBatches == null || coherentBatches.isEmpty()) {
				coherentMyOvertimeLogic.createOTBatches(Calendar.getInstance().get(Calendar.YEAR), companyId);
				coherentBatches = coherentMyOvertimeLogic.getOTBacthesByCompanyId(companyId, employeeId);
			}

			JsonConfig jsonConfig = new JsonConfig();
			JSONArray jsonObject = JSONArray.fromObject(coherentBatches, jsonConfig);
			return jsonObject.toString();
		} catch (Exception e) {
			JSONObject obj = new JSONObject();
			obj.put("success", "yes");
			LOGGER.error(e.getMessage(), e);
			return obj.toString();

		}

	}

	@Override
	@RequestMapping(value = "/employee/coherent/ts/submitToWorkflow.html", method = RequestMethod.POST)
	@ResponseBody
	public String submitToWorkflow(@RequestParam("timesheetId") long timesheetId,
			@RequestParam("remarks") String remarks, HttpServletRequest request, HttpServletResponse response) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		/*ID DECRYPT*/
		timesheetId =  FormatPreserveCryptoUtil.decrypt(timesheetId);
		List<LundinEmployeeTimesheetReviewerDTO> coherentEmployeeOTReviewerDTOs = lundinTimesheetReviewerLogic
				.getWorkFlowRuleList(employeeId, companyId);
		ResponseObjectDTO responseDto = new ResponseObjectDTO();
		try {
			if (coherentEmployeeOTReviewerDTOs != null && coherentEmployeeOTReviewerDTOs.isEmpty()) {
				responseDto.setKey(1);
				responseDto.setSuccess(false);
			} else {
				String status = coherentMyOvertimeLogic.submitToWorkFlow(coherentEmployeeOTReviewerDTOs, timesheetId,
						remarks, companyId, employeeId);
				if (status.equalsIgnoreCase(PayAsiaConstants.COHERENT_EXCEEDED_TOTAL_HOURS)) {
					responseDto.setMessage(status);
					responseDto.setKey(1);
					responseDto.setSuccess(false);
				} else if (status.equalsIgnoreCase(PayAsiaConstants.COHERENT_ZERO_TOTAL_HOURS)) {
					responseDto.setMessage(status);
					responseDto.setKey(1);
					responseDto.setSuccess(false);
				} else {
					responseDto.setMessage(status);
					responseDto.setKey(1);
					responseDto.setSuccess(true);
				}
			}

			JsonConfig jsonConfig = new JsonConfig();
			JSONObject jsonObject = JSONObject.fromObject(responseDto, jsonConfig);
			return jsonObject.toString();
		} catch (Exception e) {

			LOGGER.error(e.getMessage(), e);
			responseDto.setKey(1);
			responseDto.setSuccess(false);
			JsonConfig jsonConfig = new JsonConfig();
			JSONObject jsonObject = JSONObject.fromObject(responseDto, jsonConfig);
			return jsonObject.toString();
		}

	}

	@Override
	@RequestMapping(value = "/employee/coherent/ts/saveAsDraftWorkflow.html", method = RequestMethod.POST)
	@ResponseBody
	public String saveAsDraftWorkflow(@RequestParam("timesheetId") long timesheetId, HttpServletRequest request,
			HttpServletResponse response) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		/*ID DECRYPT*/
		timesheetId = FormatPreserveCryptoUtil.decrypt(timesheetId);
		List<LundinEmployeeTimesheetReviewerDTO> coherentEmployeeOTReviewerDTOs = lundinTimesheetReviewerLogic
				.getWorkFlowRuleList(employeeId, companyId);
		ResponseObjectDTO responseDto = new ResponseObjectDTO();
		try {
			if (coherentEmployeeOTReviewerDTOs != null && coherentEmployeeOTReviewerDTOs.isEmpty()) {
				responseDto.setKey(1);
				responseDto.setSuccess(false);
			} else {
				coherentMyOvertimeLogic.saveAsDraftTimesheet(timesheetId, employeeId, companyId);
				responseDto.setKey(1);
				responseDto.setSuccess(true);
			}

			JsonConfig jsonConfig = new JsonConfig();
			JSONObject jsonObject = JSONObject.fromObject(responseDto, jsonConfig);
			return jsonObject.toString();
		} catch (Exception e) {

			LOGGER.error(e.getMessage(), e);
			responseDto.setKey(1);
			responseDto.setSuccess(false);
			JsonConfig jsonConfig = new JsonConfig();
			JSONObject jsonObject = JSONObject.fromObject(responseDto, jsonConfig);
			return jsonObject.toString();
		}

	}

	@Override
	@RequestMapping(value = "/employee/coherent/ts/getTimesheet.html", method = RequestMethod.POST)
	@ResponseBody
	public String getTimesheetForBatch(@RequestParam(value = "batchId") long batchId,
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "page", required = false) Integer page,
			@RequestParam(value = "rows", required = false) Integer rows, HttpServletRequest request,
			HttpServletResponse response, HttpSession session) {

		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		return coherentMyOvertimeLogic.getCoherentTimesheetJSON(batchId, companyId, employeeId);

	}

	@Override
	@RequestMapping(value = "/employee/coherent/ts/openTimesheet.html", method = RequestMethod.POST)
	@ResponseBody
	public String openTimesheetForEditing(@RequestParam("timesheetId") long timesheetId, HttpServletRequest request,
			HttpServletResponse response, HttpSession session) {
		/*ID DECRYPT*/
		timesheetId = FormatPreserveCryptoUtil.decrypt(timesheetId);
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		return coherentMyOvertimeLogic.getTimesheetApplications(timesheetId, employeeId, companyId);

	}

	@Override
	@RequestMapping(value = "/employee/coherent/ts/updateTimesheetRowEmployee.html", method = RequestMethod.POST)
	@ResponseBody
	public String updateTimesheetRowEmployee(@RequestParam(value = "timesheetDetailId") long employeeTimesheetDetailId,
			@RequestParam(value = "inTime") String inTime, @RequestParam(value = "outTime") String outTime,
			@RequestParam(value = "breakTime") String breakTime, @RequestParam(value = "dayType") String dayType,
			@RequestParam(value = "totalHours") String totalHours, @RequestParam(value = "ot15hours") String ot15hours,
			@RequestParam(value = "ot10day") String ot10day, @RequestParam(value = "ot20day") String ot20day,
			@RequestParam(value = "remarks") String remarks, @RequestParam(value = "grandot10day") String grandot10day,
			@RequestParam(value = "grandot15hours") String grandot15hours,
			@RequestParam(value = "grandot20day") String grandot20day,
			@RequestParam(value = "grandTotalHours") String grandTotalHours, HttpServletRequest request,
			HttpServletResponse response, HttpSession session) {
		CoherentTimesheetDTO coherentTimesheetDTO = new CoherentTimesheetDTO();
		coherentTimesheetDTO.setInTime(inTime);
		coherentTimesheetDTO.setOutTime(outTime);
		coherentTimesheetDTO.setEmployeeTimesheetDetailId(employeeTimesheetDetailId);
		coherentTimesheetDTO.setBreakTime(breakTime);
		coherentTimesheetDTO.setDayType(dayType);
		coherentTimesheetDTO.setTotalHours(totalHours);
		coherentTimesheetDTO.setOt15hours(ot15hours);
		coherentTimesheetDTO.setOt10day(ot10day);
		coherentTimesheetDTO.setOt20day(ot20day);
		coherentTimesheetDTO.setRemarks(remarks);
		coherentTimesheetDTO.setGrandot10day(grandot10day);
		coherentTimesheetDTO.setGrandot15hours(grandot15hours);
		coherentTimesheetDTO.setGrandot20day(grandot20day);
		coherentTimesheetDTO.setGrandtotalhours(grandTotalHours);
		Map<String, String> statusMap = coherentMyOvertimeLogic
				.updateCoherentEmployeeTimesheetApplicationDetailEmployee(coherentTimesheetDTO);

		JSONObject jsonObject = new JSONObject();

		if (statusMap.get(PayAsiaConstants.COHERENT_EXCEEDED_TOTAL_HOURS)
				.equalsIgnoreCase(PayAsiaConstants.COHERENT_EXCEEDED_TOTAL_HOURS)) {
			jsonObject.put("success", false);
			jsonObject.put(PayAsiaConstants.COHERENT_EXCEEDED_TOTAL_HOURS,
					PayAsiaConstants.COHERENT_EXCEEDED_TOTAL_HOURS);
		} else {
			jsonObject.put("success", true);
		}

		jsonObject.put("inTimeStatus", statusMap.get("inTimeStatus"));
		jsonObject.put("outTimeStatus", statusMap.get("outTimeStatus"));
		jsonObject.put("breakTimeStatus", statusMap.get("breakTimeStatus"));
		return jsonObject.toString();

	}

	@Override
	@RequestMapping(value = "/employee/coherent/ts/withdrawRequest.html", method = RequestMethod.POST)
	@ResponseBody
	public String withdrawRequest(@RequestParam("timesheetId") long timesheetId, HttpServletRequest request,
			HttpServletResponse response) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		/*ID DECRYPT*/
		timesheetId = FormatPreserveCryptoUtil.decrypt(timesheetId);
		ResponseObjectDTO responseDto = new ResponseObjectDTO();
		try {
			if (coherentMyOvertimeLogic.withdrawTimesheetRequest(timesheetId, employeeId, companyId)) {
				responseDto.setKey(1);
				responseDto.setSuccess(true);

			} else {
				responseDto.setKey(1);
				responseDto.setSuccess(false);

			}
			JsonConfig jsonConfig = new JsonConfig();
			JSONObject jsonObject = JSONObject.fromObject(responseDto, jsonConfig);
			return jsonObject.toString();
		} catch (Exception e) {

			LOGGER.error(e.getMessage(), e);
			responseDto.setKey(1);
			responseDto.setSuccess(false);
			JsonConfig jsonConfig = new JsonConfig();
			JSONObject jsonObject = JSONObject.fromObject(responseDto, jsonConfig);
			return jsonObject.toString();
		}
	}

}
