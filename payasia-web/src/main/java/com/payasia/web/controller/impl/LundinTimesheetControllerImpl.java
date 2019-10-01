package com.payasia.web.controller.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.MutableDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.AppCodeDTO;
import com.payasia.common.dto.LundinEmployeeTimesheetReviewerDTO;
import com.payasia.common.dto.LundinOTBatchDTO;
import com.payasia.common.dto.LundinTimesheetDTO;
import com.payasia.common.dto.LundinTimesheetDetailDTO;
import com.payasia.common.dto.LundinTimesheetSaveDTO;
import com.payasia.common.dto.ResponseObjectDTO;
import com.payasia.common.form.AddClaimFormResponse;
import com.payasia.common.form.LundinMyRequestForm;
import com.payasia.common.form.LundinTimesheetForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.logic.AddClaimLogic;
import com.payasia.logic.LundinTimesheetPreferenceLogic;
import com.payasia.logic.LundinTimesheetRequestLogic;
import com.payasia.logic.LundinTimesheetReviewerLogic;
import com.payasia.web.controller.LundinTimesheetController;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

@Controller
public class LundinTimesheetControllerImpl implements LundinTimesheetController {

	private static final Logger LOGGER = Logger
			.getLogger(LundinTimesheetControllerImpl.class);

	@Resource
	AddClaimLogic addClaimLogic;

	@Resource
	LundinTimesheetRequestLogic lundinRequestLogic;

	@Resource
	LundinTimesheetPreferenceLogic lundinPreferenceLogic;

	@Resource
	LundinTimesheetReviewerLogic lundinTimesheetReviewerLogic;

	@Override
	@RequestMapping(value = "/employee/lundin/ts/getPendingTimesheet.html", method = RequestMethod.POST)
	@ResponseBody
	public String getPendingOTTimesheet(
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

		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		AddClaimFormResponse addClaimFormResponse = null;
		addClaimFormResponse = lundinRequestLogic.getPendingTimesheet(fromDate,
				toDate, empId, pageDTO, sortDTO, searchCondition, searchText);

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
	@RequestMapping(value = "/employee/lundin/ts/getSubmittedTimesheet.html", method = RequestMethod.POST)
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

		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);
		String pageContextPath = request.getContextPath();
		AddClaimFormResponse addClaimFormResponse = null;
		addClaimFormResponse = lundinRequestLogic.getSubmittedTimesheet(
				fromDate, toDate, empId, pageDTO, sortDTO, pageContextPath,
				searchCondition, searchText);

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
	@RequestMapping(value = "/employee/lundin/ts/getApprovedTimesheet.html", method = RequestMethod.POST)
	@ResponseBody
	public String getApprovedOTTimesheet(
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

		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);
		String pageContextPath = request.getContextPath();
		AddClaimFormResponse addClaimFormResponse = null;
		addClaimFormResponse = lundinRequestLogic.getApprovedTimesheet(
				fromDate, toDate, empId, pageDTO, sortDTO, pageContextPath,
				searchCondition, searchText);

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
	@RequestMapping(value = "/employee/lundin/ts/getRejectedTimesheet.html", method = RequestMethod.POST)
	@ResponseBody
	public String getRejectedOTTimesheet(
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

		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);
		String pageContextPath = request.getContextPath();
		AddClaimFormResponse addClaimFormResponse = null;
		addClaimFormResponse = lundinRequestLogic.getRejectedTimesheet(
				fromDate, toDate, empId, pageDTO, sortDTO, pageContextPath,
				searchCondition, searchText);

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
	@RequestMapping(value = "/employee/lundin/ts/getWithdrawnTimesheet.html", method = RequestMethod.POST)
	@ResponseBody
	public String getWithdrawnOTTimesheet(
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

		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);
		String pageContextPath = request.getContextPath();
		AddClaimFormResponse addClaimFormResponse = null;
		addClaimFormResponse = lundinRequestLogic.getWithdrawnTimesheet(
				fromDate, toDate, empId, pageDTO, sortDTO, pageContextPath,
				searchCondition, searchText);

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
	@RequestMapping(value = "/employee/lundin/ts/getAllTimesheet.html", method = RequestMethod.POST)
	@ResponseBody
	public String getAllOTTimesheet(
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
		addClaimFormResponse = lundinRequestLogic.getAllTimesheet(fromDate,
				toDate, empId, pageDTO, sortDTO, pageContextPath,
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
	@RequestMapping(value = "/employee/lundin/ts/getDataForTimesheetReviewWorkflow.html", method = RequestMethod.POST)
	@ResponseBody
	public String getDataForTimesheetReview(
			@RequestParam(value = "otTimesheetId") Long otTimesheetId,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		
		/*ID DECRYPT*/
		otTimesheetId = FormatPreserveCryptoUtil.decrypt(otTimesheetId);
		
		Long empId = Long.parseLong(UserContext.getUserId());

		LundinMyRequestForm otMyRequestForm = lundinRequestLogic
				.getotTimesheetReviewWorkflow(otTimesheetId, empId);

		// PendingClaimsForm claimsForm = pendingClaimsLogic
		// .getDataForClaimReview(claimApplicationreviewerId, empId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(otMyRequestForm,
				jsonConfig);

		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/employee/lundin/ts/getBatches.html", method = RequestMethod.POST)
	@ResponseBody
	public String getBatches(HttpServletRequest request,
			HttpServletResponse response) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		List<LundinOTBatchDTO> lundinOTBatchDTOs = lundinPreferenceLogic
				.getOTBacthesByCompanyId(companyId, employeeId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(lundinOTBatchDTOs,
				jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/employee/lundin/ts/generateAndGetBatches.html", method = RequestMethod.POST)
	@ResponseBody
	public String generateAndGetBatches(HttpServletRequest request,
			HttpServletResponse response) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		List<LundinOTBatchDTO> lundinBatches = new ArrayList<LundinOTBatchDTO>();
		try {
			lundinBatches = lundinPreferenceLogic.getOTBacthesByCompanyId(
					companyId, employeeId);
			JsonConfig jsonConfig = new JsonConfig();
			JSONArray jsonObject = JSONArray.fromObject(lundinBatches,
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
	@RequestMapping(value = "/employee/lundin/ts/submitToWorkflow.html", method = RequestMethod.POST)
	@ResponseBody
	public String submitToWorkflow(
			@RequestParam("timesheetId") long timesheetId,
			HttpServletRequest request, HttpServletResponse response) {
		
		/*ID DECRYPT*/
		timesheetId = FormatPreserveCryptoUtil.decrypt(timesheetId);
		
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		List<LundinEmployeeTimesheetReviewerDTO> lundinEmployeeOTReviewerDTOs = lundinTimesheetReviewerLogic
				.getWorkFlowRuleList(employeeId, companyId);
		ResponseObjectDTO responseDto = new ResponseObjectDTO();
		try {
			if (lundinEmployeeOTReviewerDTOs != null
					&& lundinEmployeeOTReviewerDTOs.isEmpty()) {
				responseDto.setKey(1);
				responseDto.setSuccess(false);
			} else {
				lundinRequestLogic.submitToWorkFlow(
						lundinEmployeeOTReviewerDTOs, timesheetId, companyId,
						employeeId);
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
	@RequestMapping(value = "/employee/lundin/ts/saveAsDraftWorkflow.html", method = RequestMethod.POST)
	@ResponseBody
	public String saveAsDraftWorkflow(
			@RequestParam("timesheetId") long timesheetId,
			HttpServletRequest request, HttpServletResponse response) {
		/*ID DECRYPT*/
		timesheetId = FormatPreserveCryptoUtil.decrypt(timesheetId);
	    Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		ResponseObjectDTO responseDto = new ResponseObjectDTO();
		try {
			lundinRequestLogic.saveAsDraftTimesheet(timesheetId, employeeId,
					companyId);
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
	@RequestMapping(value = "/employee/lundin/ts/editTimesheet.html", method = RequestMethod.POST, consumes = "application/json; charset=UTF-8")
	@ResponseBody
	public String editTimesheet(
			@RequestBody LundinTimesheetSaveDTO lundinTimesheetSaveDTO,
			HttpServletRequest request, HttpServletResponse response) {
		
		/*ID DECRYPT*/
		lundinTimesheetSaveDTO.setTimesheetId(FormatPreserveCryptoUtil.decrypt(lundinTimesheetSaveDTO.getTimesheetId()));
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		ResponseObjectDTO responseDto = new ResponseObjectDTO();
		try {
			lundinRequestLogic.saveTimeSheetDetail(lundinTimesheetSaveDTO,
					companyId);
			lundinRequestLogic.deleteTimesheetRecords(lundinTimesheetSaveDTO
					.getDelPostData());
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
	@RequestMapping(value = "/employee/lundin/ts/getValueCodes.html", method = RequestMethod.POST)
	@ResponseBody
	public String getValueAndCodes(HttpServletRequest request,
			HttpServletResponse response) {
		List<AppCodeDTO> appCodeDTO = lundinRequestLogic.getValueAndCodes();

		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(appCodeDTO, jsonConfig);
		return jsonObject.toString();

	}

	@Override
	@RequestMapping(value = "/employee/lundin/ts/getTimesheet.html", method = RequestMethod.POST)
	@ResponseBody
	public String getTimesheetForBatch(
			@RequestParam(value = "batchId") long batchId,
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "page", required = false) Integer page,
			@RequestParam(value = "rows", required = false) Integer rows,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		
		/*ID DECRYPT*/
		batchId =  FormatPreserveCryptoUtil.decrypt(batchId);
		
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		LundinOTBatchDTO lundinBatchDto = lundinPreferenceLogic
				.getOTBatchById(batchId,companyId,employeeId);

		if (lundinBatchDto != null) {
			long otbatchId = lundinBatchDto.getOtBatchId();
			LundinTimesheetDTO timesheetDTO = new LundinTimesheetDTO();
			timesheetDTO.setCompanyId(companyId);
			timesheetDTO.setEmployeeId(employeeId);
			timesheetDTO.setLundinBatchId(otbatchId);

			long timesheetId = lundinRequestLogic
					.saveTimeSheetAndReturn(timesheetDTO);
			LundinOTBatchDTO lundinOTBatch = lundinRequestLogic
					.getOTBatch(timesheetId);
			LundinTimesheetForm timesheetForm = new LundinTimesheetForm();
			LundinTimesheetSaveDTO saveDto = new LundinTimesheetSaveDTO();
			/*ID ENCRYPT*/
			saveDto.setTimesheetId(FormatPreserveCryptoUtil.encrypt(timesheetId));
			timesheetForm.setTimesheetDTO(saveDto);
			// timesheetForm.setLundinTimesheetDetailDTOs(getNewTimesheet(
			// lundinOTBatch, employeeId, null));
			List<String> holidays = lundinRequestLogic.getHolidaysFor(
					employeeId, lundinOTBatch.getStartDate(),
					lundinOTBatch.getEndDate());
			timesheetForm.setPublicHolidays(holidays);
			List<String> blockedDaysList = lundinRequestLogic
					.getEmpResignedAndNewHiresDates(employeeId,
							lundinOTBatch.getStartDate(),
							lundinOTBatch.getEndDate());
			timesheetForm.setBlockedDays(blockedDaysList);

			DateTimeFormatter dtfOut = DateTimeFormat.forPattern("MM/dd/yyyy");

			DateTime sdt = new DateTime(lundinOTBatch.getStartDate());
			DateTime edt = new DateTime(lundinOTBatch.getEndDate());
			timesheetForm.setStartDate(dtfOut.print(sdt));
			timesheetForm.setEndDate(dtfOut.print(edt));
			JsonConfig jsonConfig = new JsonConfig();
			JSONObject jsonObject = JSONObject.fromObject(timesheetForm,
					jsonConfig);
			return jsonObject.toString();
		} else {
			return "error";
		}

	}

	@Override
	@RequestMapping(value = "/employee/lundin/ts/openTimesheet.html", method = RequestMethod.POST)
	@ResponseBody
	public String openTimesheetForEditing(
			@RequestParam("timesheetId") long timesheetId,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		
		/*ID DECRYPT*/
		timesheetId = FormatPreserveCryptoUtil.decrypt(timesheetId);
		
		Long employeeId = Long.parseLong(UserContext.getUserId());
        LundinTimesheetForm sheetResponse = new LundinTimesheetForm();
		LundinTimesheetSaveDTO lundinTimesheetDTO = lundinRequestLogic
				.getTimesheetResponseForm(timesheetId);

		LundinOTBatchDTO lundinOTBatch = lundinRequestLogic
				.getOTBatch(timesheetId);
		sheetResponse.setCanWithdraw(lundinRequestLogic
				.getCanWithdraw(timesheetId));

		sheetResponse.setTimesheetDTO(lundinTimesheetDTO);

		DateTimeFormatter dtfOut = DateTimeFormat.forPattern("MM/dd/yyyy");
		DateTime sdt = new DateTime(lundinOTBatch.getStartDate());
		DateTime edt = new DateTime(lundinOTBatch.getEndDate());

		sheetResponse.setStartDate(dtfOut.print(sdt));
		sheetResponse.setEndDate(dtfOut.print(edt));
		List<String> holidays = lundinRequestLogic.getHolidaysFor(employeeId,
				lundinOTBatch.getStartDate(), lundinOTBatch.getEndDate());
		sheetResponse.setPublicHolidays(holidays);

		List<String> blockedDaysList = lundinRequestLogic
				.getEmpResignedAndNewHiresDates(employeeId,
						lundinOTBatch.getStartDate(),
						lundinOTBatch.getEndDate());
		sheetResponse.setBlockedDays(blockedDaysList);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject
				.fromObject(sheetResponse, jsonConfig);
		return jsonObject.toString();

	}

	@Override
	@RequestMapping(value = "/employee/lundin/ts/rev/openTimesheet.html", method = RequestMethod.POST)
	@ResponseBody
	public String openTimesheet(@RequestParam("timesheetId") long timesheetId,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		
		/*ID DECRYPT*/
		timesheetId=FormatPreserveCryptoUtil.decrypt(timesheetId);
		Long employeeId = Long.parseLong(UserContext.getUserId());
		LundinTimesheetForm sheetResponse = new LundinTimesheetForm();
		LundinTimesheetSaveDTO lundinTimesheetDTO = lundinRequestLogic
				.getTimesheetResponseForm(timesheetId);

		LundinOTBatchDTO lundinOTBatch = lundinRequestLogic
				.getOTBatch(timesheetId);

		sheetResponse.setCanWithdraw(lundinRequestLogic
				.getCanWithdraw(timesheetId));

		sheetResponse.setTimesheetDTO(lundinTimesheetDTO);

		DateTimeFormatter dtfOut = DateTimeFormat.forPattern("MM/dd/yyyy");
		DateTime sdt = new DateTime(lundinOTBatch.getStartDate());
		DateTime edt = new DateTime(lundinOTBatch.getEndDate());

		sheetResponse.setStartDate(dtfOut.print(sdt));
		sheetResponse.setEndDate(dtfOut.print(edt));
		List<String> holidays = lundinRequestLogic.getHolidaysFor(
				lundinRequestLogic.findById(timesheetId).getEmployeeId(),
				lundinOTBatch.getStartDate(), lundinOTBatch.getEndDate());

		sheetResponse.setPublicHolidays(holidays);
		List<String> blockedDaysList = lundinRequestLogic
				.getEmpResignedAndNewHiresDates(employeeId,
						lundinOTBatch.getStartDate(),
						lundinOTBatch.getEndDate());
		sheetResponse.setBlockedDays(blockedDaysList);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject
				.fromObject(sheetResponse, jsonConfig);
		return jsonObject.toString();

	}

	@Override
	@RequestMapping(value = "/employee/lundin/ts/getReviewers.html", method = RequestMethod.POST)
	@ResponseBody
	public String getReviewers(HttpServletRequest request,
			HttpServletResponse response) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		List<LundinEmployeeTimesheetReviewerDTO> lundinEmployeeTimesheetReviewerDTOs = lundinRequestLogic
				.getReviewersFor(employeeId, companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(
				lundinEmployeeTimesheetReviewerDTOs, jsonConfig);
		return jsonObject.toString();
	}

	private List<LundinTimesheetDetailDTO> getNewTimesheet(
			LundinOTBatchDTO lundinOTBatch, long employeeId,
			List<LundinTimesheetDetailDTO> oldTimesheetDetailsList) {

		// LundinOTBatchDTO lundinOTBatch = lundinRequestLogic
		// .getOTBatch(timesheetId);
		List<LundinTimesheetDetailDTO> toReturnList = new ArrayList<LundinTimesheetDetailDTO>();
		Timestamp startdt = lundinOTBatch.getStartDate();
		Timestamp enddt = lundinOTBatch.getEndDate();

		DateTime fromDt = new DateTime(startdt.getTime());
		DateTime toDt = new DateTime(enddt.getTime());

		int daysInBetween = Days.daysBetween(fromDt, toDt).getDays();
		int counter = 0;

		if (oldTimesheetDetailsList != null) {

			Collections.sort(oldTimesheetDetailsList,
					new Comparator<LundinTimesheetDetailDTO>() {

						@Override
						public int compare(LundinTimesheetDetailDTO arg0,
								LundinTimesheetDetailDTO arg1) {
							DateTime dt1 = new DateTime(arg0.getTimesheetDate());
							DateTime dt2 = new DateTime(arg1.getTimesheetDate());

							if (dt1.isAfter(dt2)) {
								return 1;
							}
							return -1;
						}

					});
		}

		for (int i = 0; i <= daysInBetween; i++) {
			MutableDateTime tempDt = fromDt.plusDays(i).toMutableDateTime();
			tempDt.setTime(0, 0, 0, 0);

			if (oldTimesheetDetailsList != null
					&& counter < oldTimesheetDetailsList.size()) {

				MutableDateTime oldDt = new MutableDateTime(
						oldTimesheetDetailsList.get(counter).getTimesheetDate());
				if (oldDt.compareTo(tempDt) == 0) {
					LundinTimesheetDetailDTO old = oldTimesheetDetailsList
							.get(counter);
					old.setAlready(true);
					toReturnList.add(old);
					counter++;
					continue;
				}

			}
			LundinTimesheetDetailDTO newSheet = new LundinTimesheetDetailDTO();
			newSheet.setAlready(false);
			toReturnList.add(newSheet);

		}
		return toReturnList;
	}

	@Override
	@RequestMapping(value = "/employee/lundin/ts/withdrawRequest.html", method = RequestMethod.POST)
	@ResponseBody
	public String withdrawRequest(
			@RequestParam("timesheetId") long timesheetId,
			HttpServletRequest request, HttpServletResponse response) {
		
		/*ID DECRYPT*/
		timesheetId = FormatPreserveCryptoUtil.decrypt(timesheetId);
		
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		ResponseObjectDTO responseDto = new ResponseObjectDTO();
		try {
			if (lundinRequestLogic.withdrawTimesheetRequest(timesheetId,
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

}
