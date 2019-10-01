package com.payasia.web.controller.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.payasia.common.dto.LeaveSessionDTO;
import com.payasia.common.form.EmployeeListFormPage;
import com.payasia.common.form.LeaveEventReminderForm;
import com.payasia.common.form.LeaveGranterForm;
import com.payasia.common.form.LeaveYearEndEmployeeDetailForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.PendingItemsForm;
import com.payasia.common.form.PendingItemsFormResponse;
import com.payasia.common.form.SortCondition;
import com.payasia.common.form.YearEndProcessFilterForm;
import com.payasia.common.form.YearEndProcessForm;
import com.payasia.common.form.YearEndProcessingFormResponse;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.logic.LeaveEventReminderLogic;
import com.payasia.logic.YearEndProcessingLogic;
import com.payasia.web.controller.YearEndProcessingController;
import com.payasia.web.util.PayAsiaSessionAttributes;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * The Class YearEndProcessingControllerImpl.
 */
@Controller
@RequestMapping(value = "/admin/yearEndProcessing")
public class YearEndProcessingControllerImpl implements
		YearEndProcessingController {

	/** The Constant logger. */
	private static final Logger LOGGER = Logger
			.getLogger(LeaveGranterControllerImpl.class);

	/** The message source. */
	@Autowired
	private MessageSource messageSource;
	@Resource
	YearEndProcessingLogic yearEndProcessingLogic;

	@Resource
	LeaveEventReminderLogic leaveEventReminderLogic;

	@Override
	@RequestMapping(value = "/getYEPLeaveTypeList.html", method = RequestMethod.POST)
	@ResponseBody public String getYEPLeaveTypeList(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			@RequestParam(value = "leaveSchemeId", required = false) Long leaveSchemeId,
			HttpServletRequest request, HttpServletResponse response) {

		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		YearEndProcessingFormResponse yearEndFormResponse = yearEndProcessingLogic
				.getYEPLeaveTypeList(sortDTO, companyId, leaveSchemeId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(yearEndFormResponse,
				jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/getEmpsPendingLeaves.html", method = RequestMethod.POST)
	@ResponseBody public String getEmpsPendingLeaves(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "leaveTypeId", required = false) Long leaveTypeId,
			@RequestParam(value = "leaveSchemeId", required = false) Long leaveSchemeId,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		PendingItemsFormResponse pendingItemsResponse = null;
		pendingItemsResponse = yearEndProcessingLogic.getPendingLeaves(pageDTO,
				sortDTO, leaveTypeId, leaveSchemeId, companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(pendingItemsResponse,
				jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/getDataForLeaveReview.html", method = RequestMethod.POST)
	@ResponseBody public String getDataForLeaveReview(
			@RequestParam(value = "leaveApplicationId") Long leaveApplicationId,
			HttpServletRequest request) {

		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		PendingItemsForm pendingItemsForm = yearEndProcessingLogic
				.getDataForLeaveReview(companyId, leaveApplicationId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(pendingItemsForm,
				jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/acceptLeave.html", method = RequestMethod.POST)
	@ResponseBody public String acceptLeave(
			@ModelAttribute(value = "pendingItemsForm") PendingItemsForm pendingItemsForm,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session, Locale locale) {
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		String status = "true";
		LeaveSessionDTO sessionDTO = new LeaveSessionDTO();
		sessionDTO.setFromSessionName(messageSource.getMessage(
				PayAsiaConstants.LEAVE_SESSION_MESSAGE_FROMLABEL_KEY,
				new Object[] {}, locale));

		sessionDTO.setToSessionName(messageSource.getMessage(
				PayAsiaConstants.LEAVE_SESSION_MESSAGE_TOLABEL_KEY,
				new Object[] {}, locale));
		try {
			yearEndProcessingLogic.acceptLeave(pendingItemsForm, employeeId,
					sessionDTO);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			status = "false";
		}

		return status;

	}

	@Override
	@RequestMapping(value = "/rejectLeave.html", method = RequestMethod.POST)
	@ResponseBody public String rejectLeave(
			@ModelAttribute(value = "pendingItemsForm") PendingItemsForm pendingItemsForm,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session, Locale locale) {
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		String status = "true";
		LeaveSessionDTO sessionDTO = new LeaveSessionDTO();
		sessionDTO.setFromSessionName(messageSource.getMessage(
				PayAsiaConstants.LEAVE_SESSION_MESSAGE_FROMLABEL_KEY,
				new Object[] {}, locale));

		sessionDTO.setToSessionName(messageSource.getMessage(
				PayAsiaConstants.LEAVE_SESSION_MESSAGE_TOLABEL_KEY,
				new Object[] {}, locale));
		try {
			yearEndProcessingLogic.rejectLeave(pendingItemsForm, employeeId,
					sessionDTO);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			status = "false";
		}

		return status;

	}

	@Override
	@RequestMapping(value = "/getLeaveYearEndBatchList.html", method = RequestMethod.POST)
	@ResponseBody public String getLeaveYearEndBatchList(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			@RequestParam(value = "year", required = false) int year,
			@RequestParam(value = "leaveTypeId", required = false) Long leaveTypeId,
			@RequestParam(value = "companyId", required = false) Long companyId,
			@RequestParam(value = "groupId", required = false) Long groupId,
			@RequestParam(value = "leaveSchemeId", required = false) Long leaveSchemeId,
			HttpServletRequest request, HttpServletResponse response) {

		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		YearEndProcessingFormResponse yepFormResponse = yearEndProcessingLogic
				.getLeaveYearEndBatchList(pageDTO, sortDTO, companyId, year,
						leaveTypeId, groupId, leaveSchemeId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(yepFormResponse,
				jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/getLeaveYearEndEmpDetailList.html", method = RequestMethod.POST)
	@ResponseBody public String getLeaveYearEndEmpDetailList(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			@RequestParam(value = "leaveYearEndBatchId", required = true) Long leaveYearEndBatchId,
			@RequestParam(value = "employeeNumber", required = false) String employeeNumber,
			HttpServletRequest request, HttpServletResponse response) {

		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		YearEndProcessingFormResponse yepFormResponse = yearEndProcessingLogic
				.getLeaveYearEndEmpDetailList(pageDTO, sortDTO, companyId,
						leaveYearEndBatchId, employeeNumber);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(yepFormResponse,
				jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/getLeaveType.html", method = RequestMethod.POST)
	@ResponseBody public String getLeaveType(HttpServletRequest request,
			HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		List<LeaveGranterForm> leaveTypeList = yearEndProcessingLogic
				.getLeaveType(companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(leaveTypeList, jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/deleteLeaveYearEndBatch.html", method = RequestMethod.POST)
	@ResponseBody public String deleteLeaveYearEndBatch(
			@RequestParam(value = "leaveYearBatchId", required = true) Long leaveYearBatchId,
			HttpServletRequest request, HttpServletResponse response) {
		String status = yearEndProcessingLogic
				.deleteLeaveYearEndBatch(leaveYearBatchId);
		return status;
	}

	@Override
	@RequestMapping(value = "/editLeaveYearEndEmpDetail.html", method = RequestMethod.POST)
	@ResponseBody public String editLeaveYearEndEmpDetail(
			@RequestParam(value = "leaveYearEndEmployeeDetailId", required = true) Long leaveYearEndEmployeeDetailId,
			HttpServletRequest request, HttpServletResponse response) {
		LeaveYearEndEmployeeDetailForm leaveYearEndEmployeeDetailForm = yearEndProcessingLogic
				.editLeaveYearEndEmpDetail(leaveYearEndEmployeeDetailId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(
				leaveYearEndEmployeeDetailForm, jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/deleteLeaveYearEndEmpDetail.html", method = RequestMethod.POST)
	@ResponseBody public String deleteLeaveYearEndEmpDetail(
			@RequestParam(value = "leaveGrantBatchEmpDetailId", required = true) Long leaveGrantBatchEmpDetailId,
			HttpServletRequest request, HttpServletResponse response) {
		String status = yearEndProcessingLogic
				.deleteLeaveYearEndEmpDetail(leaveGrantBatchEmpDetailId);
		return status;
	}

	@Override
	@RequestMapping(value = "/getGroupCompanies.html", method = RequestMethod.POST)
	@ResponseBody public String getGroupCompanies(HttpServletRequest request,
			HttpServletResponse response) {

		YearEndProcessForm yearEndProcessForm = yearEndProcessingLogic
				.getGroupCompanies();

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(yearEndProcessForm,
				jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/company/save.html", method = RequestMethod.POST)
	@ResponseBody public String performYearEndProcessSave(
			@ModelAttribute("yearEndProcessForm") YearEndProcessForm yearEndProcessForm,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session, Locale locale) {
		EmployeeListFormPage employeeListFormPage = new EmployeeListFormPage();
		try {

			yearEndProcessingLogic
					.performYearEndProcessSave(yearEndProcessForm);

		} catch (NoSuchMessageException noSuchMessageException) {
			LOGGER.error(noSuchMessageException.getMessage(),
					noSuchMessageException);
		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(employeeListFormPage,
				jsonConfig);
		return jsonObject.toString();

	}

	@Override
	@RequestMapping(value = "/groupChange.html", method = RequestMethod.POST)
	@ResponseBody public String groupChange(
			@RequestParam(value = "groupId", required = true) Long groupId,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session, Locale locale) {
		YearEndProcessFilterForm yearEndProcessFilterForm = new YearEndProcessFilterForm();
		try {

			yearEndProcessFilterForm = yearEndProcessingLogic
					.getYearEndProcessFilterData(groupId);

		} catch (NoSuchMessageException noSuchMessageException) {
			LOGGER.error(noSuchMessageException.getMessage(),
					noSuchMessageException);
		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(yearEndProcessFilterForm,
				jsonConfig);
		return jsonObject.toString();

	}

	@Override
	@RequestMapping(value = "/companyChange.html", method = RequestMethod.POST)
	@ResponseBody public String companyChange(
			@RequestParam(value = "companyId", required = true) Long companyId,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session, Locale locale) {
		YearEndProcessFilterForm yearEndProcessFilterForm = new YearEndProcessFilterForm();
		try {

			yearEndProcessFilterForm = yearEndProcessingLogic
					.getYearEndProcessFilterDataOnCmpChange(companyId);

		} catch (NoSuchMessageException noSuchMessageException) {
			LOGGER.error(noSuchMessageException.getMessage(),
					noSuchMessageException);
		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(yearEndProcessFilterForm,
				jsonConfig);
		return jsonObject.toString();

	}

	@Override
	@RequestMapping(value = "/onLeaveTypeChange.html", method = RequestMethod.POST)
	@ResponseBody public String onLeaveTypeChange(
			@RequestParam(value = "leaveSchemeId", required = true) Long leaveSchemeId,
			@RequestParam(value = "companyId", required = true) Long companyId,
			HttpServletRequest request, HttpServletResponse response) {

		LeaveEventReminderForm eventReminderForm = new LeaveEventReminderForm();
		eventReminderForm.setLeaveTypeDTOs(leaveEventReminderLogic
				.getLeaveTypes(leaveSchemeId, companyId));

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(eventReminderForm,
				jsonConfig);
		return jsonObject.toString();

	}



	@Override
	@RequestMapping(value = "/updateEmployeeYearEndSummaryDetail.html", method = RequestMethod.POST)
	@ResponseBody public String updateEmployeeYearEndSummaryDetail(
			@ModelAttribute(value = "leaveYearEndEmployeeDetailForm") LeaveYearEndEmployeeDetailForm leaveYearEndEmployeeDetailForm,
			HttpServletRequest request, HttpServletResponse response) {

		yearEndProcessingLogic
				.updateEmployeeYearEndSummaryDetail(leaveYearEndEmployeeDetailForm);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(
				leaveYearEndEmployeeDetailForm, jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return null;
	}

}
