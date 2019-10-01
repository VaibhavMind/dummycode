package com.payasia.web.controller.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.mail.internet.MimeUtility;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.TimesheetFormPdfDTO;
import com.payasia.common.form.CoherentTimesheetDTO;
import com.payasia.common.form.DataExportForm;
import com.payasia.common.form.LundinPendingItemsForm;
import com.payasia.common.form.OTPendingTimesheetForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.PendingOTTimesheetForm;
import com.payasia.common.form.PendingOTTimesheetResponseForm;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.logic.CoherentEmployeeOvertimeLogic;
import com.payasia.web.controller.CoherentEmployeeOvertimeController;
import com.payasia.web.util.PayAsiaSessionAttributes;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

@Controller
/*@RequestMapping(value = { "/employee/coherentEmployeeOvertime",
		"/admin/coherentEmployeeOvertimeAdmin" })*/
public class CoherentEmployeeOvertimeControllerImpl implements
		CoherentEmployeeOvertimeController {
	private static final Logger LOGGER = Logger
			.getLogger(CoherentEmployeeOvertimeControllerImpl.class);

	@Resource
	CoherentEmployeeOvertimeLogic coherentEmployeeOvertimeLogic;

	@Autowired
	MessageSource messageSource;

	@Override
	@RequestMapping(value = "/admin/coherentEmployeeOvertimeAdmin/search/getPendingTimesheet.html", method = RequestMethod.POST)
	@ResponseBody
	public String getPendingTimesheet(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
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

		PendingOTTimesheetResponseForm pendingOTTimesheetResponseForm = coherentEmployeeOvertimeLogic
				.getPendingTimesheet(empId, companyId, pageDTO, sortDTO,
						searchCondition, searchText);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(
				pendingOTTimesheetResponseForm, jsonConfig);
		return jsonObject.toString();
	}
	
	@Override
	@RequestMapping(value = "/employee/coherentEmployeeOvertime/search/getPendingTimesheet.html", method = RequestMethod.POST)
	@ResponseBody
	public String getPendingEmpTimesheet(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
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

		PendingOTTimesheetResponseForm pendingOTTimesheetResponseForm = coherentEmployeeOvertimeLogic
				.getPendingTimesheet(empId, companyId, pageDTO, sortDTO,
						searchCondition, searchText);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(
				pendingOTTimesheetResponseForm, jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/admin/coherentEmployeeOvertimeAdmin/search/getApprovedTimesheet.html", method = RequestMethod.POST)
	@ResponseBody
	public String getApprovedOTTimesheet(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
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

		List<String> StatusNameList = new ArrayList<>();
		StatusNameList.add(PayAsiaConstants.CLAIM_STATUS_COMPLETED);
		PendingOTTimesheetResponseForm addClaimFormResponse = coherentEmployeeOvertimeLogic
				.getTimesheet(empId, companyId, pageDTO, sortDTO,
						searchCondition, searchText, StatusNameList);

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
	@RequestMapping(value = "/employee/coherentEmployeeOvertime/search/getApprovedTimesheet.html", method = RequestMethod.POST)
	@ResponseBody
	public String getApprovedOTEmpTimesheet(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
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

		List<String> StatusNameList = new ArrayList<>();
		StatusNameList.add(PayAsiaConstants.CLAIM_STATUS_COMPLETED);
		PendingOTTimesheetResponseForm addClaimFormResponse = coherentEmployeeOvertimeLogic
				.getTimesheet(empId, companyId, pageDTO, sortDTO,
						searchCondition, searchText, StatusNameList);

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
	@RequestMapping(value = "/admin/coherentEmployeeOvertimeAdmin/search/getRejectedTimesheet.html", method = RequestMethod.POST)
	@ResponseBody
	public String getRejectedOTTimesheet(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
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

		List<String> StatusNameList = new ArrayList<>();
		StatusNameList.add(PayAsiaConstants.CLAIM_STATUS_REJECTED);
		PendingOTTimesheetResponseForm addClaimFormResponse = coherentEmployeeOvertimeLogic
				.getTimesheet(empId, companyId, pageDTO, sortDTO,
						searchCondition, searchText, StatusNameList);

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
	@RequestMapping(value = "/employee/coherentEmployeeOvertime/search/getRejectedTimesheet.html", method = RequestMethod.POST)
	@ResponseBody
	public String getRejectedOTEmpTimesheet(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
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

		List<String> StatusNameList = new ArrayList<>();
		StatusNameList.add(PayAsiaConstants.CLAIM_STATUS_REJECTED);
		PendingOTTimesheetResponseForm addClaimFormResponse = coherentEmployeeOvertimeLogic
				.getTimesheet(empId, companyId, pageDTO, sortDTO,
						searchCondition, searchText, StatusNameList);

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
	@RequestMapping(value = "/admin/coherentEmployeeOvertimeAdmin/search/getAllTimesheet.html", method = RequestMethod.POST)
	@ResponseBody
	public String getAllOTTimesheet(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "searchCondition", required = false) String searchCondition,
			@RequestParam(value = "searchText", required = false) String searchText,
			@RequestParam(value = "transactionType", required = false) String transactionType,
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

		List<String> StatusNameList = new ArrayList<>();
		StatusNameList.add(PayAsiaConstants.CLAIM_STATUS_REJECTED);
		StatusNameList.add(PayAsiaConstants.CLAIM_STATUS_COMPLETED);
		PendingOTTimesheetResponseForm addClaimFormResponse = coherentEmployeeOvertimeLogic
				.getAllTimesheet(empId, companyId, pageDTO, sortDTO,
						searchCondition, searchText, StatusNameList);

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
	@RequestMapping(value = "/employee/coherentEmployeeOvertime/search/getAllTimesheet.html", method = RequestMethod.POST)
	@ResponseBody
	public String getAllOTEmpTimesheet(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "searchCondition", required = false) String searchCondition,
			@RequestParam(value = "searchText", required = false) String searchText,
			@RequestParam(value = "transactionType", required = false) String transactionType,
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

		List<String> StatusNameList = new ArrayList<>();
		StatusNameList.add(PayAsiaConstants.CLAIM_STATUS_REJECTED);
		StatusNameList.add(PayAsiaConstants.CLAIM_STATUS_COMPLETED);
		PendingOTTimesheetResponseForm addClaimFormResponse = coherentEmployeeOvertimeLogic
				.getAllTimesheet(empId, companyId, pageDTO, sortDTO,
						searchCondition, searchText, StatusNameList);

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
	@RequestMapping(value = "/admin/coherentEmployeeOvertimeAdmin/openTimesheet.html", method = RequestMethod.POST)
	@ResponseBody
	public String openTimesheetForEditing(
			@RequestParam("timesheetId") long timesheetId,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		/*ID DECRYPT*/
		timesheetId = FormatPreserveCryptoUtil.decrypt(timesheetId);
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
        return coherentEmployeeOvertimeLogic.getTimesheetApplications(
				timesheetId, companyId);

	}
	
	@Override
	@RequestMapping(value = "/employee/coherentEmployeeOvertime/openTimesheet.html", method = RequestMethod.POST)
	@ResponseBody
	public String openTimesheetEmpForEditing(
			@RequestParam("timesheetId") long timesheetId,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		/*ID DECRYPT*/
		timesheetId = FormatPreserveCryptoUtil.decrypt(timesheetId);
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
        return coherentEmployeeOvertimeLogic.getTimesheetApplications(
				timesheetId, companyId);

	}


	@Override
	@RequestMapping(value = "/admin/coherentEmployeeOvertimeAdmin/getDataForTimesheetReview.html", method = RequestMethod.POST)
	@ResponseBody
	public String getDataForTimesheetReview(HttpServletRequest request,
			HttpServletResponse response, HttpSession session) {
		long timesheetId = Long.parseLong(request.getParameter("timesheetId"));
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		/*ID DECRYPT*/
		timesheetId = FormatPreserveCryptoUtil.decrypt(timesheetId);
		LundinPendingItemsForm pendingTimesheetForm = coherentEmployeeOvertimeLogic
				.getPendingItemForReview(timesheetId, employeeId, companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(pendingTimesheetForm,
				jsonConfig);
		return jsonObject.toString();

	}
	
	@Override
	@RequestMapping(value = "/employee/coherentEmployeeOvertime/getDataForTimesheetReview.html", method = RequestMethod.POST)
	@ResponseBody
	public String getDataForTimesheetEmpReview(HttpServletRequest request,
			HttpServletResponse response, HttpSession session) {
		long timesheetId = Long.parseLong(request.getParameter("timesheetId"));
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		/*ID DECRYPT*/
		timesheetId = FormatPreserveCryptoUtil.decrypt(timesheetId);
		LundinPendingItemsForm pendingTimesheetForm = coherentEmployeeOvertimeLogic
				.getPendingItemForReview(timesheetId, employeeId, companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(pendingTimesheetForm,
				jsonConfig);
		return jsonObject.toString();

	}
	@Override
	@RequestMapping(value = "/admin/coherentEmployeeOvertimeAdmin/updateCoherentOvertimeDetailByRev.html", method = RequestMethod.POST)
	@ResponseBody
	public String updateCoherentOvertimeDetailByRev(
			@RequestParam(value = "timesheetDetailId") long employeeTimesheetDetailId,
			@RequestParam(value = "inTime") String inTime,
			@RequestParam(value = "outTime") String outTime,
			@RequestParam(value = "breakTime") String breakTime,
			@RequestParam(value = "totalHours") String totalHours,
			@RequestParam(value = "ot15hours") String ot15hours,
			@RequestParam(value = "ot10day") String ot10day,
			@RequestParam(value = "ot20day") String ot20day,
			@RequestParam(value = "grandot10day") String grandot10day,
			@RequestParam(value = "grandot15hours") String grandot15hours,
			@RequestParam(value = "grandot20day") String grandot20day,
			@RequestParam(value = "grandTotalHours") String grandTotalHours,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		CoherentTimesheetDTO coherentTimesheetDTO = new CoherentTimesheetDTO();
		coherentTimesheetDTO.setInTime(inTime);
		coherentTimesheetDTO.setOutTime(outTime);
		coherentTimesheetDTO
				.setEmployeeTimesheetDetailId(employeeTimesheetDetailId);
		coherentTimesheetDTO.setBreakTime(breakTime);
		coherentTimesheetDTO.setTotalHours(totalHours);
		coherentTimesheetDTO.setOt15hours(ot15hours);
		coherentTimesheetDTO.setOt10day(ot10day);
		coherentTimesheetDTO.setOt20day(ot20day);
		coherentTimesheetDTO.setGrandot10day(grandot10day);
		coherentTimesheetDTO.setGrandot15hours(grandot15hours);
		coherentTimesheetDTO.setGrandot20day(grandot20day);
		coherentTimesheetDTO.setGrandtotalhours(grandTotalHours);
		Map<String, String> statusMap = coherentEmployeeOvertimeLogic
				.updateCoherentOvertimeDetailByRev(coherentTimesheetDTO);

		JSONObject jsonObject = new JSONObject();
		if (statusMap.get(PayAsiaConstants.COHERENT_EXCEEDED_TOTAL_HOURS)
				.equalsIgnoreCase(
						PayAsiaConstants.COHERENT_EXCEEDED_TOTAL_HOURS)) {
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
	@RequestMapping(value = "/employee/coherentEmployeeOvertime/updateCoherentOvertimeDetailByRev.html", method = RequestMethod.POST)
	@ResponseBody
	public String updateCoherentOvertimeEmpDetailByRev(
			@RequestParam(value = "timesheetDetailId") long employeeTimesheetDetailId,
			@RequestParam(value = "inTime") String inTime,
			@RequestParam(value = "outTime") String outTime,
			@RequestParam(value = "breakTime") String breakTime,
			@RequestParam(value = "totalHours") String totalHours,
			@RequestParam(value = "ot15hours") String ot15hours,
			@RequestParam(value = "ot10day") String ot10day,
			@RequestParam(value = "ot20day") String ot20day,
			@RequestParam(value = "grandot10day") String grandot10day,
			@RequestParam(value = "grandot15hours") String grandot15hours,
			@RequestParam(value = "grandot20day") String grandot20day,
			@RequestParam(value = "grandTotalHours") String grandTotalHours,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		CoherentTimesheetDTO coherentTimesheetDTO = new CoherentTimesheetDTO();
		coherentTimesheetDTO.setInTime(inTime);
		coherentTimesheetDTO.setOutTime(outTime);
		coherentTimesheetDTO
				.setEmployeeTimesheetDetailId(employeeTimesheetDetailId);
		coherentTimesheetDTO.setBreakTime(breakTime);
		coherentTimesheetDTO.setTotalHours(totalHours);
		coherentTimesheetDTO.setOt15hours(ot15hours);
		coherentTimesheetDTO.setOt10day(ot10day);
		coherentTimesheetDTO.setOt20day(ot20day);
		coherentTimesheetDTO.setGrandot10day(grandot10day);
		coherentTimesheetDTO.setGrandot15hours(grandot15hours);
		coherentTimesheetDTO.setGrandot20day(grandot20day);
		coherentTimesheetDTO.setGrandtotalhours(grandTotalHours);
		Map<String, String> statusMap = coherentEmployeeOvertimeLogic
				.updateCoherentOvertimeDetailByRev(coherentTimesheetDTO);

		JSONObject jsonObject = new JSONObject();
		if (statusMap.get(PayAsiaConstants.COHERENT_EXCEEDED_TOTAL_HOURS)
				.equalsIgnoreCase(
						PayAsiaConstants.COHERENT_EXCEEDED_TOTAL_HOURS)) {
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
	@RequestMapping(value = "/admin/coherentEmployeeOvertimeAdmin/forwardTimesheet", method = RequestMethod.POST)
	@ResponseBody
	public String forwardTimesheet(HttpServletRequest request,
			HttpServletResponse response, HttpSession session, Locale locale) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		long timesheetId = Long.parseLong(request.getParameter("timesheetId"));
		/*ID DECRYPT*/
		timesheetId = FormatPreserveCryptoUtil.decrypt(timesheetId);
		String remarks = request.getParameter("remarks");
		PendingOTTimesheetForm otTimesheetForm = new PendingOTTimesheetForm();
		otTimesheetForm.setOtTimesheetId(timesheetId);
		otTimesheetForm.setRemarks(remarks);
		otTimesheetForm.setOtTimesheetReviewerId(employeeId);
		otTimesheetForm = coherentEmployeeOvertimeLogic.forwardTimesheet(
				otTimesheetForm, employeeId, companyId);
		String[] errorValArr = null;
		String[] errorVal = null;
		StringBuilder errorKeyFinalStr = new StringBuilder();
		String[] errorKeyArr;
		if (otTimesheetForm.getValidationClaimItemDTO() != null
				&& otTimesheetForm.getValidationClaimItemDTO().getErrorCode() == 1) {
			if (StringUtils.isNotBlank(otTimesheetForm
					.getValidationClaimItemDTO().getErrorKey())) {
				errorKeyArr = otTimesheetForm.getValidationClaimItemDTO()
						.getErrorKey().split(";");
				if (StringUtils.isNotBlank(otTimesheetForm
						.getValidationClaimItemDTO().getErrorValue())) {
					errorValArr = otTimesheetForm.getValidationClaimItemDTO()
							.getErrorValue().split(";");
				}

				for (int count = 0; count < errorKeyArr.length; count++) {
					if (StringUtils.isNotBlank(errorKeyArr[count])) {
						if (errorValArr.length > 0) {
							if (StringUtils.isNotBlank(errorValArr[count])) {
								errorVal = errorValArr[count].split(",");
							}
						}
						errorKeyFinalStr.append(messageSource.getMessage(
								errorKeyArr[count], errorVal, locale)
								+ " </br> ");
					}

				}

			}
			otTimesheetForm.getValidationClaimItemDTO().setErrorKey(
					errorKeyFinalStr.toString());
		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(otTimesheetForm,
				jsonConfig);
		return jsonObject.toString();
	}
	
	@Override
	@RequestMapping(value = "/employee/coherentEmployeeOvertime/forwardTimesheet", method = RequestMethod.POST)
	@ResponseBody
	public String forwardEmpTimesheet(HttpServletRequest request,
			HttpServletResponse response, HttpSession session, Locale locale) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		long timesheetId = Long.parseLong(request.getParameter("timesheetId"));
		/*ID DECRYPT*/
		timesheetId = FormatPreserveCryptoUtil.decrypt(timesheetId);
		String remarks = request.getParameter("remarks");
		PendingOTTimesheetForm otTimesheetForm = new PendingOTTimesheetForm();
		otTimesheetForm.setOtTimesheetId(timesheetId);
		otTimesheetForm.setRemarks(remarks);
		otTimesheetForm.setOtTimesheetReviewerId(employeeId);
		otTimesheetForm = coherentEmployeeOvertimeLogic.forwardTimesheet(
				otTimesheetForm, employeeId, companyId);
		String[] errorValArr = null;
		String[] errorVal = null;
		StringBuilder errorKeyFinalStr = new StringBuilder();
		String[] errorKeyArr;
		if (otTimesheetForm.getValidationClaimItemDTO() != null
				&& otTimesheetForm.getValidationClaimItemDTO().getErrorCode() == 1) {
			if (StringUtils.isNotBlank(otTimesheetForm
					.getValidationClaimItemDTO().getErrorKey())) {
				errorKeyArr = otTimesheetForm.getValidationClaimItemDTO()
						.getErrorKey().split(";");
				if (StringUtils.isNotBlank(otTimesheetForm
						.getValidationClaimItemDTO().getErrorValue())) {
					errorValArr = otTimesheetForm.getValidationClaimItemDTO()
							.getErrorValue().split(";");
				}

				for (int count = 0; count < errorKeyArr.length; count++) {
					if (StringUtils.isNotBlank(errorKeyArr[count])) {
						if (errorValArr.length > 0) {
							if (StringUtils.isNotBlank(errorValArr[count])) {
								errorVal = errorValArr[count].split(",");
							}
						}
						errorKeyFinalStr.append(messageSource.getMessage(
								errorKeyArr[count], errorVal, locale)
								+ " </br> ");
					}

				}

			}
			otTimesheetForm.getValidationClaimItemDTO().setErrorKey(
					errorKeyFinalStr.toString());
		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(otTimesheetForm,
				jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/admin/coherentEmployeeOvertimeAdmin/acceptTimesheet", method = RequestMethod.POST)
	@ResponseBody
	public String acceptTimesheet(HttpServletRequest request,
			HttpServletResponse response, HttpSession session, Locale locale) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		long timesheetId = Long.parseLong(request.getParameter("timesheetId"));
		/*ID DECRYPT*/
		timesheetId = FormatPreserveCryptoUtil.decrypt(timesheetId);
		String remarks = request.getParameter("remarks");
		PendingOTTimesheetForm otTimesheetForm = new PendingOTTimesheetForm();
		otTimesheetForm.setOtTimesheetId(timesheetId);
		otTimesheetForm.setRemarks(remarks);
		otTimesheetForm.setOtTimesheetReviewerId(employeeId);
		otTimesheetForm = coherentEmployeeOvertimeLogic.acceptTimesheet(
				otTimesheetForm, employeeId, companyId);

		String[] errorValArr = null;
		String[] errorVal = null;
		StringBuilder errorKeyFinalStr = new StringBuilder();
		String[] errorKeyArr;
		if (otTimesheetForm.getValidationClaimItemDTO() != null
				&& otTimesheetForm.getValidationClaimItemDTO().getErrorCode() == 1) {
			if (StringUtils.isNotBlank(otTimesheetForm
					.getValidationClaimItemDTO().getErrorKey())) {
				errorKeyArr = otTimesheetForm.getValidationClaimItemDTO()
						.getErrorKey().split(";");
				if (StringUtils.isNotBlank(otTimesheetForm
						.getValidationClaimItemDTO().getErrorValue())) {
					errorValArr = otTimesheetForm.getValidationClaimItemDTO()
							.getErrorValue().split(";");
				}

				for (int count = 0; count < errorKeyArr.length; count++) {
					if (StringUtils.isNotBlank(errorKeyArr[count])) {
						if (errorValArr.length > 0) {
							if (StringUtils.isNotBlank(errorValArr[count])) {
								errorVal = errorValArr[count].split(",");
							}
						}
						errorKeyFinalStr.append(messageSource.getMessage(
								errorKeyArr[count], errorVal, locale)
								+ " </br> ");
					}

				}

			}
			otTimesheetForm.getValidationClaimItemDTO().setErrorKey(
					errorKeyFinalStr.toString());
		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(otTimesheetForm,
				jsonConfig);
		return jsonObject.toString();
	}

	
	@Override
	@RequestMapping(value = "/employee/coherentEmployeeOvertime/acceptTimesheet", method = RequestMethod.POST)
	@ResponseBody
	public String acceptEmpTimesheet(HttpServletRequest request,
			HttpServletResponse response, HttpSession session, Locale locale) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		long timesheetId = Long.parseLong(request.getParameter("timesheetId"));
		/*ID DECRYPT*/
		timesheetId = FormatPreserveCryptoUtil.decrypt(timesheetId);
		String remarks = request.getParameter("remarks");
		PendingOTTimesheetForm otTimesheetForm = new PendingOTTimesheetForm();
		otTimesheetForm.setOtTimesheetId(timesheetId);
		otTimesheetForm.setRemarks(remarks);
		otTimesheetForm.setOtTimesheetReviewerId(employeeId);
		otTimesheetForm = coherentEmployeeOvertimeLogic.acceptTimesheet(
				otTimesheetForm, employeeId, companyId);

		String[] errorValArr = null;
		String[] errorVal = null;
		StringBuilder errorKeyFinalStr = new StringBuilder();
		String[] errorKeyArr;
		if (otTimesheetForm.getValidationClaimItemDTO() != null
				&& otTimesheetForm.getValidationClaimItemDTO().getErrorCode() == 1) {
			if (StringUtils.isNotBlank(otTimesheetForm
					.getValidationClaimItemDTO().getErrorKey())) {
				errorKeyArr = otTimesheetForm.getValidationClaimItemDTO()
						.getErrorKey().split(";");
				if (StringUtils.isNotBlank(otTimesheetForm
						.getValidationClaimItemDTO().getErrorValue())) {
					errorValArr = otTimesheetForm.getValidationClaimItemDTO()
							.getErrorValue().split(";");
				}

				for (int count = 0; count < errorKeyArr.length; count++) {
					if (StringUtils.isNotBlank(errorKeyArr[count])) {
						if (errorValArr.length > 0) {
							if (StringUtils.isNotBlank(errorValArr[count])) {
								errorVal = errorValArr[count].split(",");
							}
						}
						errorKeyFinalStr.append(messageSource.getMessage(
								errorKeyArr[count], errorVal, locale)
								+ " </br> ");
					}

				}

			}
			otTimesheetForm.getValidationClaimItemDTO().setErrorKey(
					errorKeyFinalStr.toString());
		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(otTimesheetForm,
				jsonConfig);
		return jsonObject.toString();
	}
	@Override
	@RequestMapping(value = "/admin/coherentEmployeeOvertimeAdmin/rejectTimesheet", method = RequestMethod.POST)
	@ResponseBody
	public String rejectTimesheet(HttpServletRequest request,
			HttpServletResponse response, HttpSession session) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		long timesheetId = Long.parseLong(request.getParameter("timesheetId"));
		/*ID DECRYPT*/
		timesheetId = FormatPreserveCryptoUtil.decrypt(timesheetId);
		String remarks = request.getParameter("remarks");
		PendingOTTimesheetForm otTimesheetForm = new PendingOTTimesheetForm();
		otTimesheetForm.setOtTimesheetId(timesheetId);
		otTimesheetForm.setRemarks(remarks);
		otTimesheetForm.setOtTimesheetReviewerId(employeeId);
		otTimesheetForm = coherentEmployeeOvertimeLogic.rejectTimesheet(
				otTimesheetForm, employeeId, companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(otTimesheetForm,
				jsonConfig);
		return jsonObject.toString();

	}
	
	@Override
	@RequestMapping(value = "/employee/coherentEmployeeOvertime/rejectTimesheet", method = RequestMethod.POST)
	@ResponseBody
	public String rejectEmpTimesheet(HttpServletRequest request,
			HttpServletResponse response, HttpSession session) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		long timesheetId = Long.parseLong(request.getParameter("timesheetId"));
		/*ID DECRYPT*/
		timesheetId = FormatPreserveCryptoUtil.decrypt(timesheetId);
		String remarks = request.getParameter("remarks");
		PendingOTTimesheetForm otTimesheetForm = new PendingOTTimesheetForm();
		otTimesheetForm.setOtTimesheetId(timesheetId);
		otTimesheetForm.setRemarks(remarks);
		otTimesheetForm.setOtTimesheetReviewerId(employeeId);
		otTimesheetForm = coherentEmployeeOvertimeLogic.rejectTimesheet(
				otTimesheetForm, employeeId, companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(otTimesheetForm,
				jsonConfig);
		return jsonObject.toString();

	}

	@Override
	@RequestMapping(value = "/admin/coherentEmployeeOvertimeAdmin/getTimesheetWorkflowHistory.html", method = RequestMethod.POST)
	@ResponseBody
	public String getTimesheetWorkflowHistory(
			@RequestParam(value = "otTimesheetId") Long otTimesheetId,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		/*ID DECRYPT*/
		otTimesheetId = FormatPreserveCryptoUtil.decrypt(otTimesheetId);
		OTPendingTimesheetForm otPendingTimesheetForm = coherentEmployeeOvertimeLogic
				.getTimesheetWorkflowHistory(otTimesheetId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(otPendingTimesheetForm,
				jsonConfig);

		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}
	
	@Override
	@RequestMapping(value = "/employee/coherentEmployeeOvertime/getTimesheetWorkflowHistory.html", method = RequestMethod.POST)
	@ResponseBody
	public String getTimesheetEmpWorkflowHistory(
			@RequestParam(value = "otTimesheetId") Long otTimesheetId,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		/*ID DECRYPT*/
		otTimesheetId = FormatPreserveCryptoUtil.decrypt(otTimesheetId);
		OTPendingTimesheetForm otPendingTimesheetForm = coherentEmployeeOvertimeLogic
				.getTimesheetWorkflowHistory(otTimesheetId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(otPendingTimesheetForm,
				jsonConfig);

		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/admin/coherentEmployeeOvertimeAdmin/printTimesheetDetail.html", method = RequestMethod.GET)
	public @ResponseBody byte[] printTimesheetDetail(
			@RequestParam(value = "timesheetId", required = true) Long timesheetId,
			HttpServletResponse response, HttpServletRequest request) {
		/*ID DECRYPT*/
		timesheetId = FormatPreserveCryptoUtil.decrypt(timesheetId);
		UUID uuid = UUID.randomUUID();
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		boolean hasCoherentTimesheetModule = (boolean) request
				.getSession()
				.getAttribute(
						PayAsiaSessionAttributes.COMPANY_HAS_LION_TIMESHEET_MODULE);
		TimesheetFormPdfDTO timesheetFormPdfDTO = coherentEmployeeOvertimeLogic
				.generateTimesheetPrintPDF(companyId, employeeId, timesheetId,
						hasCoherentTimesheetModule);

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
	@RequestMapping(value = "/employee/coherentEmployeeOvertime/printTimesheetDetail.html", method = RequestMethod.GET)
	public @ResponseBody byte[] printEmpTimesheetDetail(
			@RequestParam(value = "timesheetId", required = true) Long timesheetId,
			HttpServletResponse response, HttpServletRequest request) {
		/*ID DECRYPT*/
		timesheetId = FormatPreserveCryptoUtil.decrypt(timesheetId);
		UUID uuid = UUID.randomUUID();
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		boolean hasCoherentTimesheetModule = (boolean) request
				.getSession()
				.getAttribute(
						PayAsiaSessionAttributes.COMPANY_HAS_LION_TIMESHEET_MODULE);
		TimesheetFormPdfDTO timesheetFormPdfDTO = coherentEmployeeOvertimeLogic
				.generateTimesheetPrintPDF(companyId, employeeId, timesheetId,
						hasCoherentTimesheetModule);

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
	@RequestMapping(value = "/admin/coherentEmployeeOvertimeAdmin/exportOvertimeDetail.html", method = RequestMethod.GET)
	public void exportOvertimeDetail(
			@RequestParam(value = "timesheetId", required = true) Long timesheetId,
			HttpServletResponse response, HttpServletRequest request)
			throws IOException {
		UUID uuid = UUID.randomUUID();
		/*ID DECRYPT*/
		timesheetId = FormatPreserveCryptoUtil.decrypt(timesheetId);
		DataExportForm exportForm = coherentEmployeeOvertimeLogic
				.exportOvertimeDetail(timesheetId);

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
	
	@Override
	@RequestMapping(value = "/employee/coherentEmployeeOvertime/exportOvertimeDetail.html", method = RequestMethod.GET)
	public void exportOvertimeEmpDetail(
			@RequestParam(value = "timesheetId", required = true) Long timesheetId,
			HttpServletResponse response, HttpServletRequest request)
			throws IOException {
		UUID uuid = UUID.randomUUID();
		/*ID DECRYPT*/
		timesheetId = FormatPreserveCryptoUtil.decrypt(timesheetId);
		DataExportForm exportForm = coherentEmployeeOvertimeLogic
				.exportOvertimeDetail(timesheetId);

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
