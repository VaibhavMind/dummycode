package com.payasia.web.controller.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.payasia.common.dto.AppCodeDTO;
import com.payasia.common.dto.LundinEmployeeTimesheetReviewerDTO;
import com.payasia.common.dto.LundinOTBatchDTO;
import com.payasia.common.dto.LundinTimesheetDetailDTO;
import com.payasia.common.dto.LundinTimesheetSaveDTO;
import com.payasia.common.dto.ResponseObjectDTO;
import com.payasia.common.form.AddClaimFormResponse;
import com.payasia.common.form.LionTimesheetForm;
import com.payasia.common.form.LundinMyRequestForm;
import com.payasia.common.form.LundinTimesheetForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.form.WorkFlowDelegateResponse;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.logic.AddClaimLogic;
import com.payasia.logic.LionEmployeeTimesheetApplicationDetailLogic;
import com.payasia.logic.LionReviewPendingTimesheetLogic;
import com.payasia.logic.LionTimesheetApplicationReviewerLogic;
import com.payasia.logic.LionTimesheetPreferenceLogic;
import com.payasia.logic.LionTimesheetRequestLogic;
import com.payasia.logic.LundinPendingTimesheetLogic;
import com.payasia.logic.LundinTimesheetPreferenceLogic;
import com.payasia.logic.LundinTimesheetRequestLogic;
import com.payasia.logic.LundinTimesheetReviewerLogic;
import com.payasia.web.controller.LionTimesheetController;
import com.payasia.web.util.PayAsiaSessionAttributes;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

@Controller
/*@RequestMapping(value = { "/employee/lion/ts/", "/admin/lion/ts/" })*/
public class LionTimesheetControllerImpl implements LionTimesheetController {

	private static final Logger LOGGER = Logger
			.getLogger(LundinTimesheetControllerImpl.class);

	@Resource
	AddClaimLogic addClaimLogic;

	@Resource
	LundinTimesheetRequestLogic lundinRequestLogic;

	@Resource
	LionTimesheetRequestLogic lionRequestLogic;

	@Resource
	LundinTimesheetPreferenceLogic lundinPreferenceLogic;

	@Resource
	LionTimesheetPreferenceLogic lionPreferenceLogic;

	@Resource
	LundinTimesheetReviewerLogic lundinTimesheetReviewerLogic;

	@Resource
	LundinPendingTimesheetLogic lundinPendingTimesheetLogic;

	@Resource
	LionTimesheetPreferenceLogic lionTimesheetPreferenceLogic;

	@Resource
	LionEmployeeTimesheetApplicationDetailLogic lionEmployeeTimesheetApplicationDetailLogic;

	@Resource
	LionTimesheetApplicationReviewerLogic lionEmployeeApplicationReviewerLogic;

	@Resource
	LionReviewPendingTimesheetLogic lionReviewPendingTimesheetLogic;

	@Override
	@RequestMapping(value ={"/employee/lion/ts/search/getPendingTimesheet.html"}, method = RequestMethod.POST)
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
		Long empId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);
		String pageContextPath = request.getContextPath();
		AddClaimFormResponse addClaimFormResponse = null;
		String transactionType = new String();
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		addClaimFormResponse = lionTimesheetPreferenceLogic
				.getPendingTimesheet(fromDate, toDate, empId, pageDTO, sortDTO,
						pageContextPath, transactionType, companyId,
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
	@RequestMapping(value ="/employee/lion/ts/search/getSubmittedTimesheet.html", method = RequestMethod.POST)
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
		Long empId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);
		String pageContextPath = request.getContextPath();
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		AddClaimFormResponse addClaimFormResponse = null;
		String transactionType = new String();
		addClaimFormResponse = lionTimesheetPreferenceLogic
				.getSubmittedTimesheet(fromDate, toDate, empId, pageDTO,
						sortDTO, pageContextPath, transactionType, companyId,
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
	@RequestMapping(value = "/employee/lion/ts/search/getApprovedTimesheet.html", method = RequestMethod.POST)
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
		Long empId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);
		String pageContextPath = request.getContextPath();
		AddClaimFormResponse addClaimFormResponse = null;
		String transactionType = new String();
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		addClaimFormResponse = lionTimesheetPreferenceLogic
				.getApprovedTimesheet(fromDate, toDate, empId, pageDTO,
						sortDTO, pageContextPath, transactionType, companyId,
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
	@RequestMapping(value = "/employee/lion/ts/search/getRejectedTimesheet.html", method = RequestMethod.POST)
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
		Long empId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

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
//TODO remove
	@Override
	@RequestMapping(value = "/search/getWithdrawnTimesheet.html", method = RequestMethod.POST)
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
		Long empId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

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
	@RequestMapping(value = "/employee/lion/ts/search/getAllTimesheet.html", method = RequestMethod.POST)
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
		Long empId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

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
		addClaimFormResponse = lionTimesheetPreferenceLogic.getAllTimesheet(
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
//TODO not calling
	@Override
	@RequestMapping(value = "/getDataForTimesheetReviewWorkflow.html", method = RequestMethod.POST)
	@ResponseBody
	public String getDataForTimesheetReview(
			@RequestParam(value = "otTimesheetId") Long otTimesheetId,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		Long empId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

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
	@RequestMapping(value = "/employee/lion/ts/getBatches.html", method = RequestMethod.POST)
	@ResponseBody
	public String getBatches(HttpServletRequest request,
			HttpServletResponse response) {
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		List<LundinOTBatchDTO> lionOTBatchDTOs = lionPreferenceLogic
				.getOTBacthesByCompanyId(companyId, employeeId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray
				.fromObject(lionOTBatchDTOs, jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/employee/lion/ts/generateAndGetBatches.html", method = RequestMethod.POST)
	@ResponseBody
	public String generateAndGetBatches(HttpServletRequest request,
			HttpServletResponse response) {
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		List<LundinOTBatchDTO> lundinBatches = new ArrayList<LundinOTBatchDTO>();
		try {
			lionPreferenceLogic.createOTBatches(lionPreferenceLogic
					.getByCompanyId(companyId),
					Calendar.getInstance().get(Calendar.YEAR), companyId);
			/*
			 * lundinBatches = lundinPreferenceLogic.getOTBacthesByCompanyId(
			 * companyId, employeeId);
			 */

			List<LundinOTBatchDTO> lionOTBatchDTOs = lionPreferenceLogic
					.getOTBacthesByCompanyId(companyId, employeeId);

			JsonConfig jsonConfig = new JsonConfig();
			JSONArray jsonObject = JSONArray.fromObject(lionOTBatchDTOs,
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
	@RequestMapping(value = "/employee/lion/ts/getBatchesByRev.html", method = RequestMethod.POST)
	@ResponseBody
	public String getBatchesByRev(
			@RequestParam(value = "employeeNumber") String employeeNumber,
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long employeeId = lionPreferenceLogic.getEmployeeIdByNumber(
				employeeNumber, companyId);

		List<LundinOTBatchDTO> lionOTBatchDTOs = lionPreferenceLogic
				.getOTBacthesByCompanyId(companyId, employeeId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray
				.fromObject(lionOTBatchDTOs, jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/employee/lion/ts/generateAndGetBatchesByRev.html", method = RequestMethod.POST)
	@ResponseBody
	public String generateAndGetBatchesByRev(
			@RequestParam(value = "employeeNumber") String employeeNumber,
			HttpServletRequest request, HttpServletResponse response) {

		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long employeeId = lionPreferenceLogic.getEmployeeIdByNumber(
				employeeNumber, companyId);
		List<LundinOTBatchDTO> lundinBatches = new ArrayList<LundinOTBatchDTO>();
		try {
			lionPreferenceLogic.createOTBatches(lionPreferenceLogic
					.getByCompanyId(companyId),
					Calendar.getInstance().get(Calendar.YEAR), companyId);

			List<LundinOTBatchDTO> lionOTBatchDTOs = lionPreferenceLogic
					.getOTBacthesByCompanyId(companyId, employeeId);

			JsonConfig jsonConfig = new JsonConfig();
			JSONArray jsonObject = JSONArray.fromObject(lionOTBatchDTOs,
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
	@RequestMapping(value = "/employee/lion/ts/submitToWorkflow.html", method = RequestMethod.POST)
	@ResponseBody
	public String submitToWorkflow(
			@ModelAttribute(value = "lionTimesheetForm") LionTimesheetForm lionTimesheetForm,
			@RequestParam(value = "batchIdToUse") Long batchIdToUse,
			HttpServletRequest request, HttpServletResponse response) {

		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		/*
		 * lionEmployeeTimesheetApplicationDetailLogic.acceptTimesheet(
		 * lionTimesheetForm, companyId, employeeId, batchIdToUse);
		 */

		ResponseObjectDTO responseDto = new ResponseObjectDTO();
		try {
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
//TODO not calling
	@Override
	@RequestMapping(value = "/saveAsDraftWorkflow.html", method = RequestMethod.POST)
	@ResponseBody
	public String saveAsDraftWorkflow(
			@RequestParam("timesheetId") long timesheetId,
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

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
//TODO remiove
	@Override
	@RequestMapping(value = "/editTimesheet.html", method = RequestMethod.POST)
	@ResponseBody
	public String editTimesheet(
			@RequestBody LundinTimesheetSaveDTO lundinTimesheetSaveDTO,
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

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
//TODO remove
	@Override
	@RequestMapping(value = "/getValueCodes.html", method = RequestMethod.POST)
	@ResponseBody
	public String getValueAndCodes(HttpServletRequest request,
			HttpServletResponse response) {
		List<AppCodeDTO> appCodeDTO = lundinRequestLogic.getValueAndCodes();

		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(appCodeDTO, jsonConfig);
		return jsonObject.toString();

	}
	
	@Override
	@RequestMapping(value = "/employee/lion/ts/getTimesheet.html", method = RequestMethod.POST)
	@ResponseBody
	public String getTimesheetForBatch(
			@RequestParam(value = "batchId") long batchId,
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "page", required = false) Integer page,
			@RequestParam(value = "rows", required = false) Integer rows,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {

		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		return lionEmployeeTimesheetApplicationDetailLogic
				.getLionTimesheetJSON(batchId, companyId, employeeId);

	}
	
	@Override
	@RequestMapping(value = "/employee/lion/ts/getTimesheetByRev.html", method = RequestMethod.POST)
	@ResponseBody
	public String getTimesheetForBatchByRev(
			@RequestParam(value = "employeeNumber") String employeeNumber,
			@RequestParam(value = "batchId") long batchId,
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "page", required = false) Integer page,
			@RequestParam(value = "rows", required = false) Integer rows,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {

		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		Long loggedInEmployeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		Long employeeId = lionPreferenceLogic.getEmployeeIdByNumber(
				employeeNumber, companyId);

		return lionEmployeeTimesheetApplicationDetailLogic
				.getLionTimesheetJSON(batchId, companyId, employeeId);

	}

	@Override
	@RequestMapping(value = "/employee/lion/ts/updateTimesheetRowEmployee.html", method = RequestMethod.POST)
	@ResponseBody
	
	public String updateTimesheetRowEmployee(
			@RequestParam(value = "timesheetDetailId") long employeeTimesheetDetailId,
			@RequestParam(value = "inTime") String inTime,
			@RequestParam(value = "outTime") String outTime,
			@RequestParam(value = "breakTime") String breakTime,
			@RequestParam(value = "totalHours") String totalHours,
			@RequestParam(value = "grandTotalHours") String grandTotalHours,
			@RequestParam(value = "excessHours") String excessHours,
			@RequestParam(value = "remarks") String remarks,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		/*ID DECRYPT*/
		employeeTimesheetDetailId = FormatPreserveCryptoUtil.decrypt(employeeTimesheetDetailId);
		Map<String, String> statusMap = lionEmployeeTimesheetApplicationDetailLogic
				.updateLionEmployeeTimesheetApplicationDetailEmployee(
						employeeTimesheetDetailId, inTime, outTime, breakTime,
						totalHours, grandTotalHours, excessHours, remarks);

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("success", true);
		jsonObject.put("inTimeStatus", statusMap.get("inTimeStatus"));
		jsonObject.put("outTimeStatus", statusMap.get("outTimeStatus"));
		jsonObject.put("breakTimeStatus", statusMap.get("breakTimeStatus"));
		return jsonObject.toString();

	}

	@Override
	@RequestMapping(value = "/employee/lion/ts/updateTimesheetRowReviewer.html", method = RequestMethod.POST)
	@ResponseBody
	public String updateTimesheetRowReviewer(
			@RequestParam(value = "timesheetDetailId") long employeeTimesheetDetailId,
			@RequestParam(value = "inTime") String inTime,
			@RequestParam(value = "outTime") String outTime,
			@RequestParam(value = "breakTime") String breakTime,
			@RequestParam(value = "totalHours") String totalHours,
			@RequestParam(value = "grandTotalHours") String grandTotalHours,
			@RequestParam(value = "excessHours") String excessHours,
			@RequestParam(value = "remarks") String remarks,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		/*ID DECRYPT*/
		employeeTimesheetDetailId = FormatPreserveCryptoUtil.decrypt(employeeTimesheetDetailId);
		Map<String, String> statusMap = lionEmployeeTimesheetApplicationDetailLogic
				.updateLionEmployeeTimesheetApplicationDetailReviewer(
						employeeTimesheetDetailId, inTime, outTime, breakTime,
						totalHours, grandTotalHours, excessHours, remarks);

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("success", true);
		jsonObject.put("inTimeStatus", statusMap.get("inTimeStatus"));
		jsonObject.put("outTimeStatus", statusMap.get("outTimeStatus"));
		jsonObject.put("breakTimeStatus", statusMap.get("breakTimeStatus"));
		return jsonObject.toString();

	}

	@Override
	@RequestMapping(value = "/employee/lion/ts/submitTimesheetRow.html", method = RequestMethod.POST)
	@ResponseBody
	public String submitTimesheetRow(
			@RequestParam(value = "timesheetDetailId") long employeeTimesheetDetailId,
			@RequestParam(value = "inTime") String inTime,
			@RequestParam(value = "outTime") String outTime,
			@RequestParam(value = "breakTime") String breakTime,
			@RequestParam(value = "totalHours") String totalHours,
			@RequestParam(value = "grandTotalHours") String grandTotalHours,
			@RequestParam(value = "excessHours") String excessHours,
			@RequestParam(value = "remarks") String remarks,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		/*ID DECRYPT*/
		employeeTimesheetDetailId = FormatPreserveCryptoUtil.decrypt(employeeTimesheetDetailId);
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		lionEmployeeTimesheetApplicationDetailLogic
				.submitLionEmployeeTimesheetApplicationDetail(employeeId,
						employeeTimesheetDetailId, inTime, outTime, breakTime,
						totalHours, grandTotalHours, excessHours,remarks);

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("success", true);
		return jsonObject.toString();

	}

	@Override
	@RequestMapping(value = "/employee/lion/ts/submitEmpTimesheetRowByRev.html", method = RequestMethod.POST)
	@ResponseBody
	public String submitEmpTimesheetRowByRev(
			@RequestParam(value = "timesheetDetailId") long employeeTimesheetDetailId,
			@RequestParam(value = "inTime") String inTime,
			@RequestParam(value = "outTime") String outTime,
			@RequestParam(value = "breakTime") String breakTime,
			@RequestParam(value = "totalHours") String totalHours,
			@RequestParam(value = "grandTotalHours") String grandTotalHours,
			@RequestParam(value = "excessHours") String excessHours,
			@RequestParam(value = "remarks") String remarks,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		/*ID DECRYPT*/
		employeeTimesheetDetailId=FormatPreserveCryptoUtil.decrypt(employeeTimesheetDetailId);
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		lionEmployeeTimesheetApplicationDetailLogic.submitEmpTimesheetRowByRev(
				employeeId, employeeTimesheetDetailId, inTime, outTime,
				breakTime, totalHours, grandTotalHours, excessHours, remarks);

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("success", true);
		return jsonObject.toString();

	}

	@Override
	@RequestMapping(value = "/employee/lion/ts/approveTimesheetRow.html", method = RequestMethod.POST)
	@ResponseBody
	public String approveTimesheetRow(
			@RequestParam(value = "timesheetDetailId") long employeeTimesheetDetailId,
			@RequestParam(value = "inTime") String inTime,
			@RequestParam(value = "outTime") String outTime,
			@RequestParam(value = "breakTime") String breakTime,
			@RequestParam(value = "totalHours") String totalHours,
			@RequestParam(value = "grandTotalHours") String grandTotalHours,
			@RequestParam(value = "excessHours") String excessHours,
			@RequestParam(value = "remarks") String remarks,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		/*ID DECRYPT*/
		employeeTimesheetDetailId = FormatPreserveCryptoUtil.decrypt(employeeTimesheetDetailId);
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		Map<String, String> statusMap = lionEmployeeTimesheetApplicationDetailLogic
				.approveLionEmployeeTimesheetApplicationDetail(employeeId,
						employeeTimesheetDetailId, inTime, outTime, breakTime,
						totalHours, grandTotalHours, excessHours, remarks);

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("success", true);
		jsonObject.put("inTimeStatus", statusMap.get("inTimeStatus"));
		jsonObject.put("outTimeStatus", statusMap.get("outTimeStatus"));
		jsonObject.put("breakTimeStatus", statusMap.get("breakTimeStatus"));
		return jsonObject.toString();

	}

	@Override
	@RequestMapping(value = "/employee/lion/ts/submitAndApproveTimesheetByRev.html", method = RequestMethod.POST)
	@ResponseBody
	public String submitAndApproveTimesheetByRev(
			@RequestParam(value = "timesheetDetailId") long employeeTimesheetDetailId,
			@RequestParam(value = "inTime") String inTime,
			@RequestParam(value = "outTime") String outTime,
			@RequestParam(value = "breakTime") String breakTime,
			@RequestParam(value = "totalHours") String totalHours,
			@RequestParam(value = "grandTotalHours") String grandTotalHours,
			@RequestParam(value = "excessHours") String excessHours,
			@RequestParam(value = "remarks") String remarks,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		/*ID DECRYPT*/
		employeeTimesheetDetailId=FormatPreserveCryptoUtil.decrypt(employeeTimesheetDetailId);
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		Map<String, String> statusMap = lionEmployeeTimesheetApplicationDetailLogic
				.submitAndApproveEmpTimesheetAppDetailByRev(employeeId,
						employeeTimesheetDetailId, inTime, outTime, breakTime,
						totalHours, grandTotalHours, excessHours, remarks);

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("success", true);
		jsonObject.put("inTimeStatus", statusMap.get("inTimeStatus"));
		jsonObject.put("outTimeStatus", statusMap.get("outTimeStatus"));
		jsonObject.put("breakTimeStatus", statusMap.get("breakTimeStatus"));
		return jsonObject.toString();

	}

	@Override
	@RequestMapping(value = "/employee/lion/ts/openTimesheet.html", method = RequestMethod.POST)
	@ResponseBody
	public String openTimesheetForEditing(
			@RequestParam("timesheetId") long timesheetId,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		/*ID DECRYPT*/
		timesheetId = FormatPreserveCryptoUtil.decrypt(timesheetId);
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		return lionEmployeeTimesheetApplicationDetailLogic
				.getTimesheetApplications(timesheetId, employeeId, companyId);

	}

	@Override
	@RequestMapping(value = "/employee/lion/ts/openTimesheetReviewer.html", method = RequestMethod.POST)
	@ResponseBody
	public String openTimesheetForEditingReviewer(
			@RequestParam("timesheetId") long timesheetId,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		/*ID DECRYPT*/
		timesheetId = FormatPreserveCryptoUtil.decrypt(timesheetId);
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		String returnJSON = lionEmployeeApplicationReviewerLogic
				.getTimesheetApplications(timesheetId, employeeId, companyId);
		return returnJSON;
	}
//TODO remove
	@Override
	@RequestMapping(value = "/rev/openTimesheet.html", method = RequestMethod.POST)
	@ResponseBody
	public String openTimesheet(@RequestParam("timesheetId") long timesheetId,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

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
	//TODO remove
	@Override
	@RequestMapping(value = "/getReviewers.html", method = RequestMethod.POST)
	@ResponseBody
	public String getReviewers(HttpServletRequest request,
			HttpServletResponse response) {
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

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
	//TODO remove
	@Override
	@RequestMapping(value = "/withdrawRequest.html", method = RequestMethod.POST)
	@ResponseBody
	public String withdrawRequest(
			@RequestParam("timesheetId") long timesheetId,
			HttpServletRequest request, HttpServletResponse response) {
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

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

	@Override
	@RequestMapping(value = "/employee/lion/ts/searchEmployee.html", method = RequestMethod.POST)
	@ResponseBody
	public String searchEmployee(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			@RequestParam(value = "empName", required = true) String empName,
			@RequestParam(value = "empNumber", required = true) String empNumber,
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		WorkFlowDelegateResponse employeeResponse = lionReviewPendingTimesheetLogic
				.searchEmployee(pageDTO, sortDTO, empName, empNumber,
						companyId, employeeId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(employeeResponse,
				jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return null;
	}

}
