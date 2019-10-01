package com.payasia.web.controller.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.AppCodeDTO;
import com.payasia.common.dto.DataImportLogDTO;
import com.payasia.common.dto.EmployeeLeaveSchemeTypeDTO;
import com.payasia.common.dto.EmployeeLeaveSchemeTypeHistoryDTO;
import com.payasia.common.dto.LeaveApplicationAttachmentDTO;
import com.payasia.common.dto.LeaveApplicationWorkflowDTO;
import com.payasia.common.dto.LeaveDTO;
import com.payasia.common.dto.LeaveSessionDTO;
import com.payasia.common.exception.PayAsiaRollBackDataException;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.AddLeaveForm;
import com.payasia.common.form.AddLeaveFormResponse;
import com.payasia.common.form.EmployeeFilterListForm;
import com.payasia.common.form.LeaveBalanceSummaryForm;
import com.payasia.common.form.LeaveBalanceSummaryResponse;
import com.payasia.common.form.LeavePreferenceForm;
import com.payasia.common.form.LeaveReviewerResponseForm;
import com.payasia.common.form.LeaveSchemeForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.ExcelUtils;
import com.payasia.common.util.FileUtils;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.dao.EmployeeLeaveSchemeTypeDAO;
import com.payasia.dao.LeaveApplicationAttachmentDAO;
import com.payasia.dao.LeaveApplicationDAO;
import com.payasia.dao.LeaveSchemeTypeDAO;
import com.payasia.dao.bean.EmployeeLeaveSchemeType;
import com.payasia.dao.bean.LeaveApplication;
import com.payasia.dao.bean.LeaveApplicationAttachment;
import com.payasia.logic.AddLeaveLogic;
import com.payasia.logic.DataImportUtils;
import com.payasia.logic.LeaveBalanceSummaryLogic;
import com.payasia.web.controller.LeaveBalanceSummaryController;
import com.payasia.web.util.PayAsiaSessionAttributes;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

@Controller
public class LeaveBalanceSummaryControllerImpl implements
		LeaveBalanceSummaryController {

	/** The Constant logger. */
	private static final Logger LOGGER = Logger
			.getLogger(LeaveBalanceSummaryControllerImpl.class);

	@Resource
	LeaveBalanceSummaryLogic leaveBalanceSummaryLogic;

	@Resource
	AddLeaveLogic addLeaveLogic;

	@Resource
	MessageSource messageSource;

	@Resource
	DataImportUtils dataImportUtils;
	
	@Resource
	EmployeeLeaveSchemeTypeDAO employeeLeaveSchemeTypeDAO;
	
	@Resource
	LeaveApplicationAttachmentDAO leaveApplicationAttachmentDAO;
	
	@Resource
	LeaveSchemeTypeDAO leaveSchemeTypeDAO;

	@Resource
	LeaveApplicationDAO leaveApplicationDAO;
	
	@Override
	@RequestMapping(value = "/admin/leaveBalanceSummary/employeeLeaveSchemeTypeList.html", method = RequestMethod.POST)
	@ResponseBody
	public String getEmployeeLeaveSchemeType(
			@RequestParam(value = "year", required = true) int year,
			@RequestParam(value = "employeeNumber", required = false) String employeeNumber,
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows) {

		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		Long employeeId = Long.parseLong(UserContext.getUserId());
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		LeaveBalanceSummaryResponse leaveSchemeResponse = leaveBalanceSummaryLogic
				.getEmployeeLeaveSchemeType(year, employeeNumber, pageDTO,
						sortDTO, companyId, employeeId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(leaveSchemeResponse,
				jsonConfig);
		return jsonObject.toString();
	}

	@Override
	//@PreAuthorize("hasRole('PRIV_EMPLOYEE_LEAVE_BALANCE_SUMMARY')")
	@RequestMapping(value = "/employee/leaveBalanceSummary/getTeamEmployeeLeaveSchemeType.html", method = RequestMethod.POST)
	@ResponseBody
	public String getTeamEmployeeLeaveSchemeType(
			@RequestParam(value = "year", required = true) int year,
			@RequestParam(value = "employeeNumber", required = false) String employeeNumber,
			@RequestParam(value = "searchStringEmpId", required = false) String searchStringEmpId,
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows) {

		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		LeaveBalanceSummaryResponse leaveSchemeResponse = leaveBalanceSummaryLogic
				.getTeamEmployeeLeaveSchemeType(year, employeeNumber, pageDTO,
						sortDTO, companyId, employeeId, searchStringEmpId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(leaveSchemeResponse,
				jsonConfig);
		return jsonObject.toString();
	}

	@Override
	//@PreAuthorize("hasRole('PRIV_EMPLOYEE_LEAVE_BALANCE_SUMMARY')")
	@RequestMapping(value = "/employee/leaveBalanceSummary/myLeaveSchemeTypeList.html", method = RequestMethod.POST)
	@ResponseBody
	public String myLeaveSchemeTypeList(
			@RequestParam(value = "year", required = false) Integer year,
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows) {

		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		LeaveBalanceSummaryResponse leaveSchemeResponse = leaveBalanceSummaryLogic
				.getMyLeaveSchemeType(year, pageDTO, sortDTO, companyId,
						employeeId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(leaveSchemeResponse,
				jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/admin/leaveBalanceSummary/empLeaveSchemeTypeHistoryList.html", method = RequestMethod.POST)
	@ResponseBody
	public String empLeaveSchemeTypeHistoryList(
			@RequestParam(value = "leaveSchemeTypeId", required = true) Long leaveSchemeTypeId,
			@RequestParam(value = "postLeaveTypeFilterId", required = true) String postLeaveTypeFilterId,
			@RequestParam(value = "year", required = true) int year,
			@RequestParam(value = "employeeNumber", required = false) String employeeNumber,
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			Locale locale) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		Long employeeId = Long.parseLong(UserContext.getUserId());
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);
		
		LeaveBalanceSummaryResponse leaveSchemeResponse = leaveBalanceSummaryLogic
				.getEmpLeaveSchemeTypeHistoryList(leaveSchemeTypeId,
						postLeaveTypeFilterId, year, employeeNumber, companyId,
						employeeId, pageDTO, sortDTO, true);

		List<EmployeeLeaveSchemeTypeHistoryDTO> employeeLeaveSchemeTypeHistoryDTOs = leaveSchemeResponse
				.getEmpLeaveSchemeTypeHistoryList() != null ? leaveSchemeResponse
				.getEmpLeaveSchemeTypeHistoryList()
				: new ArrayList<EmployeeLeaveSchemeTypeHistoryDTO>();

		for (EmployeeLeaveSchemeTypeHistoryDTO historyDTO : employeeLeaveSchemeTypeHistoryDTOs) {
			if (StringUtils.isNotBlank(historyDTO.getType())) {
				historyDTO.setType(messageSource.getMessage(
						historyDTO.getType(), new Object[] {}, locale));
			}
			if (StringUtils.isNotBlank(historyDTO.getFromSessionLabelKey())) {
				historyDTO.setFromSessionLabelKey(messageSource.getMessage(
						historyDTO.getFromSessionLabelKey(), new Object[] {},
						locale));
			}
			if (StringUtils.isNotBlank(historyDTO.getToSessionLabelKey())) {
				historyDTO.setToSessionLabelKey(messageSource.getMessage(
						historyDTO.getToSessionLabelKey(), new Object[] {},
						locale));
			}
			
			/* ID ENCRYPT*/
			
			historyDTO.setEmployeeLeaveSchemeTypeHistoryId(FormatPreserveCryptoUtil.encrypt(historyDTO.getEmployeeLeaveSchemeTypeHistoryId()));

		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(leaveSchemeResponse,
				jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return null;
	}

	@Override
	//@PreAuthorize("hasRole('PRIV_EMPLOYEE_LEAVE_BALANCE_SUMMARY')")
	@RequestMapping(value = "/employee/leaveBalanceSummary/getTeamEmpLeaveSchemeTypeHistoryList.html", method = RequestMethod.POST)
	@ResponseBody
	public String getTeamEmpLeaveSchemeTypeHistoryList(
			@RequestParam(value = "leaveSchemeTypeId", required = true) Long leaveSchemeTypeId,
			@RequestParam(value = "postLeaveTypeFilterId", required = true) String postLeaveTypeFilterId,
			@RequestParam(value = "year", required = true) int year,
			@RequestParam(value = "employeeNumber", required = false) String employeeNumber,
			@RequestParam(value = "searchStringEmpId", required = false) String searchStringEmpId,
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			Locale locale) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		LeaveBalanceSummaryResponse leaveSchemeResponse = leaveBalanceSummaryLogic
				.getTeamEmpLeaveSchemeTypeHistoryList(leaveSchemeTypeId,
						postLeaveTypeFilterId, year, employeeNumber, companyId,
						employeeId, searchStringEmpId, pageDTO, sortDTO);

		List<EmployeeLeaveSchemeTypeHistoryDTO> employeeLeaveSchemeTypeHistoryDTOs = leaveSchemeResponse
				.getEmpLeaveSchemeTypeHistoryList();
		for (EmployeeLeaveSchemeTypeHistoryDTO historyDTO : employeeLeaveSchemeTypeHistoryDTOs) {
			if (StringUtils.isNotBlank(historyDTO.getType())) {
				historyDTO.setType(messageSource.getMessage(
						historyDTO.getType(), new Object[] {}, locale));
			}
			if (StringUtils.isNotBlank(historyDTO.getFromSessionLabelKey())) {
				historyDTO.setFromSessionLabelKey(messageSource.getMessage(
						historyDTO.getFromSessionLabelKey(), new Object[] {},
						locale));
			}
			if (StringUtils.isNotBlank(historyDTO.getToSessionLabelKey())) {
				historyDTO.setToSessionLabelKey(messageSource.getMessage(
						historyDTO.getToSessionLabelKey(), new Object[] {},
						locale));
			}
		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(leaveSchemeResponse,
				jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return null;
	}
	
	// to do url not mappped
	@Override
	@RequestMapping(value = "/employeeSchemeDetail.html", method = RequestMethod.POST)
	@ResponseBody
	public String employeeSchemeDetail(
			@RequestParam(value = "empNumber", required = true) String empNumber,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		LeaveBalanceSummaryForm leaveBalanceSummaryForm = leaveBalanceSummaryLogic
				.getEmployeeSchemeDetail(empNumber, companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(leaveBalanceSummaryForm,
				jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return null;
	}

	// to do url not mappped
	@Override
	@RequestMapping(value = "/empLeaveSchemeTypeData.html", method = RequestMethod.POST)
	@ResponseBody
	public String empLeaveSchemeTypeData(
			@RequestParam(value = "employeeLeaveSchemeTypeId", required = true) Long empLeaveSchemeTypeId) {
		EmployeeLeaveSchemeTypeDTO leaveSchemeTypeData = leaveBalanceSummaryLogic
				.getDataForSchemeType(empLeaveSchemeTypeId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(leaveSchemeTypeData,
				jsonConfig);
		return jsonObject.toString();

	}

	@Override
	@RequestMapping(value = "/admin/leaveBalanceSummary/savePostTransactionLeaveType.html", method = RequestMethod.POST)
	@ResponseBody
	public String savePostTransactionLeaveType(
			@ModelAttribute(value = "leaveBalanceSummaryForm") LeaveBalanceSummaryForm leaveBalanceSummaryForm,
			Locale locale) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		LeaveBalanceSummaryResponse leaveSchemeResponse = leaveBalanceSummaryLogic
				.savePostTransactionLeaveType(leaveBalanceSummaryForm
						.getEmployeeLeaveSchemeTypeHistory(), companyId,
						employeeId, true);
		String[] errorValArr = null;
		String[] errorVal = null;
		StringBuilder errorKeyFinalStr = new StringBuilder();
		String[] errorKeyArr;
		if (leaveSchemeResponse.getLeaveDTO() != null
				&& leaveSchemeResponse.getLeaveDTO().getErrorCode() == 1) {
			if (StringUtils.isNotBlank(leaveSchemeResponse.getLeaveDTO()
					.getErrorKey())) {
				errorKeyArr = leaveSchemeResponse.getLeaveDTO().getErrorKey()
						.split(";");
				if (StringUtils.isNotBlank(leaveSchemeResponse.getLeaveDTO()
						.getErrorValue())) {
					errorValArr = leaveSchemeResponse.getLeaveDTO()
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
			leaveSchemeResponse.getLeaveDTO().setErrorKey(
					errorKeyFinalStr.toString());
		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(leaveSchemeResponse,
				jsonConfig);
		return jsonObject.toString();

	}

	@Override
	@RequestMapping(value = "/admin/leaveBalanceSummary/updatePostTransactionLeaveType.html", method = RequestMethod.POST)
	@ResponseBody
	public String updatePostTransactionLeaveType(
			@ModelAttribute(value = "leaveBalanceSummaryForm") LeaveBalanceSummaryForm leaveBalanceSummaryForm,
			Locale locale) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		Long employeeId = Long.parseLong(UserContext.getUserId());

		LeaveBalanceSummaryResponse leaveSchemeResponse = leaveBalanceSummaryLogic
				.updatePostTransactionLeaveType(leaveBalanceSummaryForm
						.getEmployeeLeaveSchemeTypeHistory(), companyId,
						employeeId, leaveBalanceSummaryForm
								.getEmployeeLeaveSchemeTypeHistoryId());

		String[] errorValArr = null;
		String[] errorVal = null;
		StringBuilder errorKeyFinalStr = new StringBuilder();
		String[] errorKeyArr;
		if (leaveSchemeResponse.getLeaveDTO() != null
				&& leaveSchemeResponse.getLeaveDTO().getErrorCode() == 1) {
			if (StringUtils.isNotBlank(leaveSchemeResponse.getLeaveDTO()
					.getErrorKey())) {
				errorKeyArr = leaveSchemeResponse.getLeaveDTO().getErrorKey()
						.split(";");
				if (StringUtils.isNotBlank(leaveSchemeResponse.getLeaveDTO()
						.getErrorValue())) {
					errorValArr = leaveSchemeResponse.getLeaveDTO()
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
			leaveSchemeResponse.getLeaveDTO().setErrorKey(
					errorKeyFinalStr.toString());
		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(leaveSchemeResponse,
				jsonConfig);
		return jsonObject.toString();

	}

	@Override
	@RequestMapping(value = "/admin/leaveBalanceSummary/getEmployeeId.html", method = RequestMethod.POST)
	@ResponseBody
	public String getEmployeeId(
			@RequestParam(value = "searchString", required = true) String searchString) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		Long employeeId = Long.parseLong(UserContext.getUserId());
		List<LeaveBalanceSummaryForm> leaveBalanceSummaryFormList = leaveBalanceSummaryLogic
				.getEmployeeId(companyId, searchString, employeeId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(
				leaveBalanceSummaryFormList, jsonConfig);
		return jsonObject.toString();

	}

	@Override
	//@PreAuthorize("hasRole('PRIV_EMPLOYEE_LEAVE_BALANCE_SUMMARY')")
	@RequestMapping(value = "/employee/leaveBalanceSummary/getEmployeeIdForManager.html", method = RequestMethod.POST)
	@ResponseBody
	public String getEmployeeIdForManager(
			@RequestParam(value = "searchString", required = true) String searchString) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		Long employeeId = Long.parseLong(UserContext.getUserId());
		List<LeaveBalanceSummaryForm> leaveBalanceSummaryFormList = null;
		//if (StringUtils.isNotBlank(searchString)) {
		leaveBalanceSummaryFormList = leaveBalanceSummaryLogic.getEmployeeIdForManager(companyId, searchString, employeeId);
	//}
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(
				leaveBalanceSummaryFormList, jsonConfig);
		return jsonObject.toString();

	}

	@Override
	@RequestMapping(value = "/admin/leaveBalanceSummary/getHolidaycalendar.html", method = RequestMethod.POST)
	@ResponseBody
	public String getHolidaycalendar(
			@RequestParam(value = "year", required = true) int year) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		Long employeeId = Long.parseLong(UserContext.getUserId());
		List<LeaveBalanceSummaryForm> holidayCalendarList = leaveBalanceSummaryLogic
				.getHolidaycalendar(companyId, employeeId, year);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(holidayCalendarList,
				jsonConfig);
		return jsonObject.toString();

	}

	@Override
	@RequestMapping(value = "/admin/leaveBalanceSummary/getDashBoardEmpOnLeaveList.html", method = RequestMethod.POST)
	@ResponseBody
	public String getDashBoardEmpOnLeaveList(
			@RequestParam(value = "fromDate", required = true) String fromDate,
			@RequestParam(value = "toDate", required = true) String toDate,
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			Locale locale) {
		UserContext.setLocale(locale);
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		Long employeeId = Long.parseLong(UserContext.getUserId());
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		LeaveBalanceSummaryResponse leaveSchemeResponse = leaveBalanceSummaryLogic
				.getDashBoardEmpOnLeaveList(fromDate, toDate, pageDTO, sortDTO,
						companyId, employeeId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(leaveSchemeResponse,
				jsonConfig);
		return jsonObject.toString();
	}

	@Override
	//@PreAuthorize("hasRole('PRIV_EMPLOYEE_LEAVE_BALANCE_SUMMARY')")
	@RequestMapping(value = "/employee/leaveBalanceSummary/getDashBoardByManagerEmpOnLeaveList.html", method = RequestMethod.POST)
	@ResponseBody
	public String getDashBoardByManagerEmpOnLeaveList(
			@RequestParam(value = "fromDate", required = true) String fromDate,
			@RequestParam(value = "toDate", required = true) String toDate,
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			Locale locale) {
		UserContext.setLocale(locale);
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		Long employeeId = Long.parseLong(UserContext.getUserId());
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);
		/*
		LeaveBalanceSummaryResponse leaveSchemeResponse = leaveBalanceSummaryLogic
				.getDashBoardByManagerEmpOnLeaveList(fromDate, toDate, pageDTO,
						sortDTO, companyId, employeeId);
 		*/
		LeaveBalanceSummaryResponse leaveSchemeResponse = null;//leaveBalanceSummaryLogic.getDashBoardByManagerEmpOnLeaveList(fromDate, toDate, pageDTO,sortDTO, companyId, employeeId, null, messageSource,null);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(leaveSchemeResponse,
				jsonConfig);
		return jsonObject.toString();
	}
	// to do url not mapped
	@Override
	@RequestMapping(value = "/myLeaveSchemeDetail.html", method = RequestMethod.POST)
	@ResponseBody
	public String myLeaveSchemeDetail(HttpServletRequest request,
			HttpServletResponse response, Locale locale) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		LeaveBalanceSummaryForm leaveBalanceSummaryForm = leaveBalanceSummaryLogic
				.myLeaveSchemeDetail(employeeId, companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(leaveBalanceSummaryForm,
				jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/admin/leaveBalanceSummary/getLeaveCalMonthList.html", method = RequestMethod.POST)
	@ResponseBody
	public String getLeaveCalMonthList(
			@RequestParam(value = "year", required = true) String year,
			@RequestParam(value = "month", required = true) String month) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		Long employeeId = Long.parseLong(UserContext.getUserId());
		LeaveBalanceSummaryForm leaveBalanceSummaryForm = leaveBalanceSummaryLogic
				.getLeaveCalMonthList(year, month, companyId, employeeId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(leaveBalanceSummaryForm,
				jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e);
		}
		return null;
	}

	@Override
	//@PreAuthorize("hasRole('PRIV_EMPLOYEE_LEAVE_BALANCE_SUMMARY')")
	@RequestMapping(value = "/employee/leaveBalanceSummary/getLeaveCalMonthListByManager.html", method = RequestMethod.POST)
	@ResponseBody
	public String getLeaveCalMonthListByManager(
			@RequestParam(value = "year", required = true) String year,
			@RequestParam(value = "month", required = true) String month) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		LeaveBalanceSummaryForm leaveBalanceSummaryForm = leaveBalanceSummaryLogic
				.getLeaveCalMonthListByManager(year, month, companyId,
						employeeId,null);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(leaveBalanceSummaryForm,
				jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/admin/leaveBalanceSummary/getEmpOnLeaveByDate.html", method = RequestMethod.POST)
	@ResponseBody
	public String getEmpOnLeaveByDate(
			@RequestParam(value = "leaveAppIds", required = true) String[] leaveAppIds,
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			Locale locale) {
		UserContext.setLocale(locale);
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		Long employeeId = Long.parseLong(UserContext.getUserId());
		LeaveBalanceSummaryResponse leaveSchemeResponse = leaveBalanceSummaryLogic
				.getEmpOnLeaveByDate(leaveAppIds, companyId, employeeId,
						pageDTO, sortDTO, true);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(leaveSchemeResponse,
				jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/admin/leaveBalanceSummary/getPostLeaveSchemeData.html", method = RequestMethod.POST)
	@ResponseBody
	public String getPostLeaveSchemeData(
			@RequestParam(value = "empNumber", required = true) String empNumber,
			@RequestParam(value = "year", required = true) String year) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		LeaveBalanceSummaryResponse leaveBalSummaryResponse = leaveBalanceSummaryLogic
				.getPostLeaveSchemeData(empNumber, year, companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(leaveBalSummaryResponse,
				jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/admin/leaveBalanceSummary/getLeaveScheme.html", method = RequestMethod.POST)
	@ResponseBody
	public String getLeaveScheme(
			@RequestParam(value = "empNumber", required = true) String empNumber,
			@RequestParam(value = "year", required = true) String year) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		List<LeaveBalanceSummaryForm> leaveBalSummaryFrmList = leaveBalanceSummaryLogic
				.getLeaveScheme(empNumber, year, companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(leaveBalSummaryFrmList,
				jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/admin/leaveBalanceSummary/getMyLeaveScheme.html", method = RequestMethod.POST)
	@ResponseBody
	public String getMyLeaveScheme(
			@RequestParam(value = "year", required = true) String year) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		Long employeeId = Long.parseLong(UserContext.getUserId());
		List<LeaveBalanceSummaryForm> leaveBalSummaryFrmList = leaveBalanceSummaryLogic
				.getMyLeaveScheme(employeeId, year, companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(leaveBalSummaryFrmList,
				jsonConfig);
		return jsonObject.toString();
	}

	@Override
	//@PreAuthorize("hasRole('PRIV_EMPLOYEE_LEAVE_BALANCE_SUMMARY')")
	@RequestMapping(value = "/employee/leaveBalanceSummary/getCompletedLeaves.html", method = RequestMethod.POST)
	@ResponseBody
	public String getCompletedLeaves(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			HttpServletRequest request) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		Long empId = Long.parseLong(UserContext.getUserId());

		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);
		String pageContextPath = request.getContextPath();

		AddLeaveFormResponse addLeaveResponse = null;
		addLeaveResponse = leaveBalanceSummaryLogic.getCompletedLeaves(empId,
				pageDTO, sortDTO, pageContextPath, companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(addLeaveResponse,
				jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	//@PreAuthorize("hasRole('PRIV_EMPLOYEE_LEAVE_BALANCE_SUMMARY')")
	@RequestMapping(value = "/employee/leaveBalanceSummary/getLeaveReviewers.html", method = RequestMethod.POST)
	@ResponseBody
	public String getLeaveReviewers(
			@RequestParam(value = "leaveApplicationId", required = false) Long leaveApplicationId) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		Long empId = Long.parseLong(UserContext.getUserId());
		AddLeaveFormResponse addLeaveResponse = null;
		LeaveApplication leaveApplication = leaveApplicationDAO.findByLeaveApplicationIdAndEmpId(leaveApplicationId, empId, companyId);
		if(leaveApplication == null)
		{
			throw new PayAsiaSystemException("Authentication Exception");
		}
		addLeaveResponse = leaveBalanceSummaryLogic
				.getLeaveReviewers(leaveApplicationId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(addLeaveResponse,
				jsonConfig);
		return jsonObject.toString();
	}

	@Override
	//@PreAuthorize("hasRole('PRIV_EMPLOYEE_LEAVE_BALANCE_SUMMARY')")
	@RequestMapping(value = "/employee/leaveBalanceSummary/cancelLeave.html", method = RequestMethod.POST)
	@ResponseBody
	public String cancelLeave(
			@ModelAttribute(value = "leaveBalanceSummaryForm") LeaveBalanceSummaryForm leaveBalanceSummaryForm,
			 Locale locale) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		Long employeeId = Long.parseLong(UserContext.getUserId());
		LeaveSessionDTO sessionDTO = new LeaveSessionDTO();
		sessionDTO.setFromSessionName(messageSource.getMessage(
				PayAsiaConstants.LEAVE_SESSION_MESSAGE_FROMLABEL_KEY,
				new Object[] {}, locale));

		sessionDTO.setToSessionName(messageSource.getMessage(
				PayAsiaConstants.LEAVE_SESSION_MESSAGE_TOLABEL_KEY,
				new Object[] {}, locale));

		leaveBalanceSummaryLogic.canCelLeave(leaveBalanceSummaryForm,
				employeeId, companyId, sessionDTO);
		return null;
	}

	@Override
	@RequestMapping(value = "/admin/leaveBalanceSummary/getLeaveBalance.html", method = RequestMethod.POST)
	@ResponseBody
	public String getLeaveBalance(
			@RequestParam(value = "employeeLeaveSchemeTypeId") Long employeeLeaveSchemeTypeId) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		//EmployeeLeaveSchemeType employeeLeaveSchemeType = employeeLeaveSchemeTypeDAO.findSchemeTypeByCompanyId(employeeLeaveSchemeTypeId, companyId);
		/*if(employeeLeaveSchemeType == null)
		{
			throw new PayAsiaSystemException("Authentication Exception");
		}*/
		AddLeaveForm addLeaveForm = leaveBalanceSummaryLogic
				.getLeaveBalance(employeeLeaveSchemeTypeId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(addLeaveForm, jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/admin/leaveBalanceSummary/getDays.html", method = RequestMethod.POST)
	@ResponseBody
	public String getDays(
			@RequestParam(value = "fromDate") String fromDate,
			@RequestParam(value = "toDate") String toDate,
			@RequestParam(value = "session1") Long session1,
			@RequestParam(value = "session2") Long session2,
			@RequestParam(value = "employeeLeaveSchemeTypeId") Long employeeLeaveSchemeTypeId,

			HttpServletRequest request, Locale locale) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		LeaveDTO leaveDTO = new LeaveDTO();
		leaveDTO.setFromDate(fromDate);
		leaveDTO.setToDate(toDate);
		leaveDTO.setSession1(session1);
		leaveDTO.setSession2(session2);
		EmployeeLeaveSchemeType employeeLeaveSchemeType = employeeLeaveSchemeTypeDAO.findSchemeTypeByCompanyId(employeeLeaveSchemeTypeId, companyId);
		if(employeeLeaveSchemeType == null)
		{
			throw new PayAsiaSystemException("Authentication Exception");
		}
		leaveDTO.setEmployeeLeaveSchemeTypeId(employeeLeaveSchemeTypeId);
		AddLeaveForm addLeaveForm = leaveBalanceSummaryLogic.getNoOfDays(
				companyId, employeeId, leaveDTO);
		if (addLeaveForm.getLeaveDTO() != null) {
			if (StringUtils
					.isNotBlank(addLeaveForm.getLeaveDTO().getErrorKey())) {
				addLeaveForm.getLeaveDTO().setErrorKey(
						messageSource.getMessage(addLeaveForm.getLeaveDTO()
								.getErrorKey(), new Object[] {}, locale));
			}
		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(addLeaveForm, jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/admin/leaveBalanceSummary/employeeName.html", method = RequestMethod.POST)
	@ResponseBody
	public String getEmployeeName(
			@RequestParam(value = "employeeNumber", required = false) String employeeNumber) {
		Long loggedInEmployeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		String empName = leaveBalanceSummaryLogic.getEmployeeName(
				loggedInEmployeeId, employeeNumber, companyId);
		try {
			return URLEncoder.encode(empName, "UTF-8");
		} catch (UnsupportedEncodingException unsupportedEncodingException) {
			LOGGER.error(unsupportedEncodingException.getMessage(),
					unsupportedEncodingException);
		}
		return null;
	}

	@Override
	//@PreAuthorize("hasRole('PRIV_EMPLOYEE_LEAVE_BALANCE_SUMMARY')")
	@RequestMapping(value = "/employee/leaveBalanceSummary/getEmployeeNameForManager.html", method = RequestMethod.POST)
	@ResponseBody
	public String getEmployeeNameForManager(
			@RequestParam(value = "employeeNumber", required = false) String employeeNumber) {
		Long loggedInEmployeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		if (StringUtils.isNotBlank(employeeNumber)) {
			String empName = leaveBalanceSummaryLogic.getEmployeeNameForManager(loggedInEmployeeId, employeeNumber,
		    	companyId);
		try {
			return URLEncoder.encode(empName, "UTF-8");
		} catch (UnsupportedEncodingException unsupportedEncodingException) {
			LOGGER.error(unsupportedEncodingException.getMessage(),
					unsupportedEncodingException);
		}
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/admin/leaveBalanceSummary/deleteLeaveTransaction.html", method = RequestMethod.POST)
	@ResponseBody
	public String deleteLeaveTransaction(
			@RequestParam(value = "leaveTranId", required = true) Long leaveTranId) {
		
		/* ID DECRYPT */
		leaveTranId= FormatPreserveCryptoUtil.decrypt(leaveTranId);
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		String status;
		try {
			leaveBalanceSummaryLogic.deleteLeaveTransaction(companyId,
					leaveTranId);
			status = "false";
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			status = "true";
		}
		return status;

	}

	@Override
	@RequestMapping(value = "/admin/leaveBalanceSummary/cancelLeaveTransaction.html", method = RequestMethod.POST)
	@ResponseBody
	public String cancelLeaveTransaction(
			@RequestParam(value = "leaveTranId", required = true) Long leaveTranId) {
		
		/* ID DECRYPT */
		leaveTranId= FormatPreserveCryptoUtil.decrypt(leaveTranId);
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		Long employeeId = Long.parseLong(UserContext.getUserId());
		String status;
		try {

			leaveBalanceSummaryLogic.cancelLeaveTransaction(companyId,
					leaveTranId, employeeId);
			status = "false";
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			status = "true";
		}
		return status;

	}

	@Override
	@RequestMapping(value = "/admin/leaveBalanceSummary/getLeavetransactionData.html", method = RequestMethod.POST)
	@ResponseBody
	public String getLeavetransactionData(
			@RequestParam(value = "leaveTranId", required = true) Long leaveTranId) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		
		/* ID DECRYPT */
		leaveTranId= FormatPreserveCryptoUtil.decrypt(leaveTranId);
		
		LeaveBalanceSummaryResponse leaveSchemeResponse = leaveBalanceSummaryLogic
				.getLeavetransactionData(leaveTranId, companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(leaveSchemeResponse,
				jsonConfig);
		return jsonObject.toString();

	}

	@Override
	@RequestMapping(value = "/admin/leaveBalanceSummary/importPostLeaveTran.html", method = RequestMethod.POST)
	@ResponseBody
	public String importPostLeaveTran(
			@ModelAttribute(value = "leaveBalanceSummaryForm") final LeaveBalanceSummaryForm leaveBalanceSummaryForm,
			final Locale locale) throws Exception {
		final String modifiedfileName = new String(
				Base64.encodeBase64((leaveBalanceSummaryForm.getFileUpload()
						.getOriginalFilename() + DateUtils
						.convertCurrentDateTimeWithMilliSecToString(PayAsiaConstants.TEMP_FILE_TIMESTAMP_WITH_MILLISEC_FORMAT))
						.getBytes()));
		final Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		final Long employeeId = Long.parseLong(UserContext.getUserId());
		
		boolean isFileValid = false;
		if (leaveBalanceSummaryForm.getFileUpload()!=null) {
			isFileValid = FileUtils.isValidFile(leaveBalanceSummaryForm.getFileUpload(), leaveBalanceSummaryForm.getFileUpload().getOriginalFilename(), PayAsiaConstants.ALLOWED_ONLY_UPLOAD_EXCLE_FILE_EXT, PayAsiaConstants.ALLOWED_ONLY_UPLOAD_EXCLE_MINE_TYPE, PayAsiaConstants.FILE_SIZE);
		}
		if(isFileValid){
			
		String excelFileName = leaveBalanceSummaryForm.getFileUpload()
				.getOriginalFilename();
		final String fileExt = excelFileName.substring(
				excelFileName.lastIndexOf(".") + 1, excelFileName.length());
		LeaveBalanceSummaryForm leaveBalSummExcelFieldForm = new LeaveBalanceSummaryForm();
		if (fileExt.toLowerCase().equals(PayAsiaConstants.FILE_TYPE_XLS)) {
			leaveBalSummExcelFieldForm = ExcelUtils
					.getPostLeaveTranFromXLS(leaveBalanceSummaryForm
							.getFileUpload());
		} else if (fileExt.toLowerCase()
				.equals(PayAsiaConstants.FILE_TYPE_XLSX)) {
			leaveBalSummExcelFieldForm = ExcelUtils
					.getPostLeaveTranFromXLSX(leaveBalanceSummaryForm
							.getFileUpload());
		}
		
		final LeaveBalanceSummaryForm leaveBalSummExcelFieldFormCopy = leaveBalSummExcelFieldForm;

		Runnable runnable = new Runnable() {
			@Override
			public void run() {

				dataImportUtils.leaveImportStatusMap.put(modifiedfileName,
						"processing");

				LeaveBalanceSummaryForm leaveBalanceSummaryFrm = new LeaveBalanceSummaryForm();
				try {
					
						leaveBalanceSummaryFrm = leaveBalanceSummaryLogic
								.importPostLeaveTran(
										leaveBalSummExcelFieldFormCopy, companyId,
										employeeId);
						dataImportUtils.leaveImportStatusMap.put(modifiedfileName,
								"success");
				
				} catch (PayAsiaRollBackDataException ex) {
					LOGGER.error(ex.getMessage(), ex);
					leaveBalanceSummaryFrm.setDataValid(false);
					leaveBalanceSummaryFrm.setDataImportLogDTOs(ex.getErrors());
					dataImportUtils.leaveImportStatusMap.put(modifiedfileName,
							"error");
				} catch (Exception e) {
					LOGGER.error(e.getMessage(), e);
					List<DataImportLogDTO> errors = new ArrayList<DataImportLogDTO>();
					DataImportLogDTO error = new DataImportLogDTO();

					error.setFailureType("payasia.record.error");
					error.setRemarks("payasia.record.error");
					error.setFromMessageSource(false);

					errors.add(error);
					leaveBalanceSummaryFrm.setDataValid(false);
					leaveBalanceSummaryFrm.setDataImportLogDTOs(errors);
					dataImportUtils.leaveImportStatusMap.put(modifiedfileName,
							"error");
				}

				if (leaveBalanceSummaryFrm.getDataImportLogDTOs() != null) {
					for (DataImportLogDTO dataImportLogDTO : leaveBalanceSummaryFrm
							.getDataImportLogDTOs()) {
						try {
							String[] errorValArr = null;
							String[] errorVal = null;
							StringBuilder errorKeyFinalStr = new StringBuilder();
							String[] errorKeyArr;
							if (StringUtils.isNotBlank(dataImportLogDTO
									.getErrorKey())) {
								errorKeyArr = dataImportLogDTO.getErrorKey()
										.split(";");
								if (StringUtils.isNotBlank(dataImportLogDTO
										.getErrorValue())) {
									errorValArr = dataImportLogDTO
											.getErrorValue().split(";");
								}

								for (int count = 0; count < errorKeyArr.length; count++) {
									if (StringUtils
											.isNotBlank(errorKeyArr[count])) {
										if (errorValArr.length > 0) {
											if (StringUtils
													.isNotBlank(errorValArr[count])) {
												errorVal = errorValArr[count]
														.split(",");
											}
										}
										errorKeyFinalStr.append(count + 1
												+ ". ");
										errorKeyFinalStr.append(messageSource
												.getMessage(errorKeyArr[count],
														errorVal, locale));

									}

								}

								dataImportLogDTO.setRemarks(errorKeyFinalStr
										.toString());

							} else {
								if (StringUtils.isNotBlank(dataImportLogDTO
										.getErrorValue())) {
									errorVal = dataImportLogDTO.getErrorValue()
											.split(",");
									dataImportLogDTO
											.setRemarks(URLEncoder.encode(
													messageSource.getMessage(
															dataImportLogDTO
																	.getRemarks(),
															errorVal, locale),
													"UTF-8"));
								} else {
									dataImportLogDTO.setRemarks(URLEncoder
											.encode(messageSource.getMessage(
													dataImportLogDTO
															.getRemarks(),
													new Object[] {}, locale),
													"UTF-8"));
								}
							}

						} catch (UnsupportedEncodingException
								| NoSuchMessageException exception) {
							LOGGER.error(exception.getMessage(), exception);
							throw new PayAsiaSystemException(
									exception.getMessage(), exception);
						}
					}
					dataImportUtils.leaveImportStatusMap.put(modifiedfileName,
							"error");
				}

				dataImportUtils.leaveImportStatusMap.put(modifiedfileName,
						"success");

				dataImportUtils.leaveImportLogMap.put(modifiedfileName,
						leaveBalanceSummaryFrm);

			}
		};
		Thread thread = new Thread(runnable, modifiedfileName);
		thread.start();
		}else{
			
		}
		return modifiedfileName;

	}

	@Override
	@RequestMapping(value = "/admin/leaveBalanceSummary/leaveDataImportStatus.html", method = RequestMethod.POST)
	@ResponseBody
	public String dataImportStatus(
			@RequestParam(value = "fileName", required = true) String fileName) {

		LeaveBalanceSummaryForm leaveBalanceSummaryFrm = null;

		String key = fileName;
		String status = dataImportUtils.leaveImportStatusMap.get(key);

		if ((status != null) && (status != "")
				&& ("SUCCESS".equals(status.toUpperCase()))) {

			dataImportUtils.leaveImportStatusMap.put(key, "");
			leaveBalanceSummaryFrm = dataImportUtils.leaveImportLogMap.get(key);
		}
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(leaveBalanceSummaryFrm,
				jsonConfig);

		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/admin/leaveBalanceSummary/isEncashedVisible.html", method = RequestMethod.POST)
	@ResponseBody
	public String isEncashedVisible() {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		LeavePreferenceForm leavePreferenceForm = leaveBalanceSummaryLogic
				.isEncashedVisible(companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(leavePreferenceForm,
				jsonConfig);
		return jsonObject.toString();

	}

	@Override
	@RequestMapping(value = "/admin/leaveBalanceSummary/getAppCodeDTOList.html", method = RequestMethod.POST)
	@ResponseBody
	public String getAppCodeDTOList(Locale locale) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		List<AppCodeDTO> appCodeDTOList = leaveBalanceSummaryLogic
				.getLeaveTransactionType(companyId);

		for (AppCodeDTO appCodeDTO : appCodeDTOList) {

			try {
				appCodeDTO.setCodeDesc(URLEncoder.encode(messageSource
						.getMessage(appCodeDTO.getCodeDesc(), new Object[] {},
								locale), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				LOGGER.error(e.getMessage(), e);
			} catch (NoSuchMessageException e) {
				LOGGER.error(e.getMessage(), e);
			}
		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(appCodeDTOList, jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/admin/leaveBalanceSummary/getLeaveTransactionHistory.html", method = RequestMethod.POST)
	@ResponseBody
	public String getLeaveTransactionHistory(
			@RequestParam(value = "leaveApplicationId", required = true) Long leaveApplicationId,
			@RequestParam(value = "transactionType", required = true) String transactionType,
			Locale locale) {
		/* ID DECRYPT */
		leaveApplicationId= FormatPreserveCryptoUtil.decrypt(leaveApplicationId);
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		LeaveBalanceSummaryResponse transactionHistoryResponse = leaveBalanceSummaryLogic
				.getLeaveTransactionHistory(leaveApplicationId,
						transactionType, companyId);
		List<LeaveApplicationWorkflowDTO> applicationWorkflowDTOs = transactionHistoryResponse
				.getEmployeeLeaveSchemeTypeHistoryDTO().getWorkflowList();
		for (LeaveApplicationWorkflowDTO applicationWorkflowDTO : applicationWorkflowDTOs) {
			if (StringUtils.isNotBlank(applicationWorkflowDTO.getStatus())) {
				applicationWorkflowDTO.setStatus(messageSource.getMessage(
						applicationWorkflowDTO.getStatus(), new Object[] {},
						locale));
			}

		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(
				transactionHistoryResponse, jsonConfig);
		return jsonObject.toString();

	}

	@Override
	@RequestMapping(value = "/admin/leaveBalanceSummary/viewAttachment.html", method = RequestMethod.GET)
	public @ResponseBody byte[] viewAttachment(
			@RequestParam(value = "attachmentId", required = true) long attachmentId,
			HttpServletResponse response) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		LeaveApplicationAttachment leaveApplicationAttachment = leaveApplicationAttachmentDAO.findAttachmentByEmployeeCompanyId(attachmentId, companyId);
		if(leaveApplicationAttachment == null)
		{
			throw new PayAsiaSystemException("Authentication Exception");
		}
		LeaveApplicationAttachmentDTO attachment = leaveBalanceSummaryLogic
				.viewAttachment(attachmentId);

		byte[] byteFile = attachment.getAttachmentBytes();

		response.reset();
		String mimeType = URLConnection.guessContentTypeFromName(attachment
				.getFileName());
		response.setContentType("application/" + mimeType);
		response.setContentLength(byteFile.length);
		String filename = attachment.getFileName();

		response.setHeader("Content-Disposition", "attachment;filename="
				+ filename);

		return byteFile;

	}

	@Override
	@RequestMapping(value = "/admin/leaveBalanceSummary/getYearList.html", method = RequestMethod.POST)
	@ResponseBody
	public String getYearList() {
		long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		List<EmployeeLeaveSchemeTypeDTO> yearList = leaveBalanceSummaryLogic
				.getDistinctYears(companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(yearList, jsonConfig);
		return jsonObject.toString();
	}
	
	@Override
	//@PreAuthorize("hasRole('PRIV_EMPLOYEE_LEAVE_BALANCE_SUMMARY')")
	@RequestMapping(value = "/employee/leaveBalanceSummary/getEmpLeaveSchemeInfo.html", method = RequestMethod.POST)
	@ResponseBody
	public String getEmpLeaveSchemeInfo() {

		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		LeaveSchemeForm leaveSchemeForm = addLeaveLogic.getLeaveSchemes(companyId, employeeId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(leaveSchemeForm, jsonConfig);
		return jsonObject.toString();
	}

	@Override
	//@PreAuthorize("hasRole('PRIV_EMPLOYEE_LEAVE_BALANCE_SUMMARY')")
	@RequestMapping(value = "/employee/leaveBalanceSummary/getDefaultEmailCCByEmp.html", method = RequestMethod.POST)
	@ResponseBody
	public String getDefaultEmailCCByEmp(@RequestParam(value = "moduleName", required = false) String moduleName,
			HttpServletRequest request, HttpServletResponse response) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		boolean moduleEnabled = false;
		if (moduleName.equalsIgnoreCase(PayAsiaConstants.COMPANY_MODULE_CLAIM)) {
			moduleEnabled = (boolean) request.getSession()
					.getAttribute(PayAsiaSessionAttributes.COMPANY_HAS_CLAIM_MODULE);
		}
		if (moduleName.equalsIgnoreCase(PayAsiaConstants.COMPANY_MODULE_LEAVE)) {
			moduleEnabled = (boolean) request.getSession()
					.getAttribute(PayAsiaSessionAttributes.COMPANY_HAS_LEAVE_MODULE);
		}

		String emailIds = leaveBalanceSummaryLogic.getDefaultEmailCCByEmp(companyId, employeeId, moduleName,
				moduleEnabled);
		return emailIds;
	}

	@Override
	//@PreAuthorize("hasRole('PRIV_EMPLOYEE_LEAVE_BALANCE_SUMMARY')")
	@RequestMapping(value = "/employee/leaveBalanceSummary/getLeaveTypes.html", method = RequestMethod.POST)
	@ResponseBody
	public String getLeaveTypes(@RequestParam(value = "leaveSchemeId") Long leaveSchemeId) {

		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		AddLeaveForm addLeaveForm = null;
		if(leaveSchemeId != null && leaveSchemeId != 0L)
		{
			addLeaveForm = addLeaveLogic.getLeaveTypes(leaveSchemeId, companyId, employeeId);
		}
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(addLeaveForm, jsonConfig);
		return jsonObject.toString();
	}

	@Override
	//@PreAuthorize("hasRole('PRIV_EMPLOYEE_LEAVE_BALANCE_SUMMARY')")
	@RequestMapping(value = "/employee/leaveBalanceSummary/getLeaveCustomFields.html", method = RequestMethod.POST)
	@ResponseBody
	public String getLeaveCustomFields(@RequestParam(value = "leaveSchemeId") Long leaveSchemeId,
			@RequestParam(value = "leaveTypeId") Long leaveTypeId,
			@RequestParam(value = "employeeLeaveSchemeId") Long employeeLeaveSchemeTypeId) {

		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		AddLeaveForm addLeaveForm = addLeaveLogic.getLeaveCustomFields(leaveSchemeId, leaveTypeId, companyId,
				employeeId, employeeLeaveSchemeTypeId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(addLeaveForm, jsonConfig);

		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	//@PreAuthorize("hasRole('PRIV_EMPLOYEE_LEAVE_BALANCE_SUMMARY')")
	@RequestMapping(value = "/employee/leaveBalanceSummary/searchEmployee.html", method = RequestMethod.POST)
	@ResponseBody
	public String searchEmployee(@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			@RequestParam(value = "empName", required = true) String empName,
			@RequestParam(value = "empNumber", required = true) String empNumber) {

		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);
		LeaveReviewerResponseForm leaveReviewerResponse = addLeaveLogic.searchEmployee(pageDTO, sortDTO, employeeId,
				empName, empNumber, companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(leaveReviewerResponse, jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return null;
	}

	@Override
	//@PreAuthorize("hasRole('PRIV_EMPLOYEE_LEAVE_BALANCE_SUMMARY')")
	@RequestMapping(value = "/employee/leaveBalanceSummary/getDefaultEmailCCListByEmp.html", method = RequestMethod.POST)
	@ResponseBody
	public String getDefaultEmailCCListByEmployee(
			@RequestParam(value = "moduleName", required = false) String moduleName, HttpServletRequest request) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		boolean moduleEnabled = false;
		if (moduleName.equalsIgnoreCase(PayAsiaConstants.COMPANY_MODULE_CLAIM)) {
			moduleEnabled = (boolean) request.getSession()
					.getAttribute(PayAsiaSessionAttributes.COMPANY_HAS_CLAIM_MODULE);
		}
		if (moduleName.equalsIgnoreCase(PayAsiaConstants.COMPANY_MODULE_LEAVE)) {
			moduleEnabled = (boolean) request.getSession()
					.getAttribute(PayAsiaSessionAttributes.COMPANY_HAS_LEAVE_MODULE);
		}

		List<EmployeeFilterListForm> filterList = leaveBalanceSummaryLogic.getDefaultEmailCCListByEmployee(companyId,
				employeeId, moduleName, moduleEnabled);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(filterList, jsonConfig);
		return jsonObject.toString();
	}

	@Override
	//@PreAuthorize("hasRole('PRIV_EMPLOYEE_LEAVE_BALANCE_SUMMARY')")
	@RequestMapping(value = "/employee/leaveBalanceSummary/saveDefaultEmailCCByEmp.html", method = RequestMethod.POST)
	@ResponseBody
	public void saveDefaultEmailCCByEmployee(@RequestParam(value = "ccEmailIds", required = false) String ccEmailIds,
			@RequestParam(value = "moduleName", required = false) String moduleName, HttpServletRequest request) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		boolean moduleEnabled = false;
		if (moduleName.equalsIgnoreCase(PayAsiaConstants.COMPANY_MODULE_CLAIM)) {
			moduleEnabled = (boolean) request.getSession()
					.getAttribute(PayAsiaSessionAttributes.COMPANY_HAS_CLAIM_MODULE);
		}
		if (moduleName.equalsIgnoreCase(PayAsiaConstants.COMPANY_MODULE_LEAVE)) {
			moduleEnabled = (boolean) request.getSession()
					.getAttribute(PayAsiaSessionAttributes.COMPANY_HAS_LEAVE_MODULE);
		}

		leaveBalanceSummaryLogic.saveDefaultEmailCCByEmployee(companyId, employeeId, ccEmailIds, moduleName,
				moduleEnabled);
	}
	
	@Override
	//@PreAuthorize("hasRole('PRIV_EMPLOYEE_LEAVE_BALANCE_SUMMARY')")
	@RequestMapping(value = "/employee/leaveBalanceSummary/employeeLeaveSchemeTypeList.html", method = RequestMethod.POST)
	@ResponseBody
	public String getEmployeeLeaveSchemeTypeEmp(@RequestParam(value = "year", required = true) int year,
			@RequestParam(value = "employeeNumber", required = false) String employeeNumber,
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows) {

		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);
		LeaveBalanceSummaryResponse	leaveSchemeResponse = leaveBalanceSummaryLogic.getEmployeeLeaveSchemeType(year,
				employeeNumber, pageDTO, sortDTO, companyId, employeeId);
		
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(leaveSchemeResponse, jsonConfig);
		return jsonObject.toString();
	}
	
	@Override
	//@PreAuthorize("hasRole('PRIV_EMPLOYEE_LEAVE_BALANCE_SUMMARY')")
	@RequestMapping(value = "/employee/leaveBalanceSummary/empLeaveSchemeTypeHistoryList.html", method = RequestMethod.POST)
	@ResponseBody
	public String empLeaveSchemeTypeHistoryListEmp(
			@RequestParam(value = "leaveSchemeTypeId", required = true) Long leaveSchemeTypeId,
			@RequestParam(value = "postLeaveTypeFilterId", required = true) String postLeaveTypeFilterId,
			@RequestParam(value = "year", required = true) int year,
			@RequestParam(value = "employeeNumber", required = false) String employeeNumber,
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows, Locale locale) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);
		LeaveBalanceSummaryResponse leaveSchemeResponse = leaveBalanceSummaryLogic.getEmpLeaveSchemeTypeHistoryList(
				leaveSchemeTypeId, postLeaveTypeFilterId, year, employeeNumber, companyId, employeeId, pageDTO,
				sortDTO, false);
		List<EmployeeLeaveSchemeTypeHistoryDTO> employeeLeaveSchemeTypeHistoryDTOs = leaveSchemeResponse
				.getEmpLeaveSchemeTypeHistoryList() != null ? leaveSchemeResponse.getEmpLeaveSchemeTypeHistoryList()
						: new ArrayList<EmployeeLeaveSchemeTypeHistoryDTO>();

		for (EmployeeLeaveSchemeTypeHistoryDTO historyDTO : employeeLeaveSchemeTypeHistoryDTOs) {
			if (StringUtils.isNotBlank(historyDTO.getType())) {
				historyDTO.setType(messageSource.getMessage(historyDTO.getType(), new Object[] {}, locale));
			}
			if (StringUtils.isNotBlank(historyDTO.getFromSessionLabelKey())) {
				historyDTO.setFromSessionLabelKey(
						messageSource.getMessage(historyDTO.getFromSessionLabelKey(), new Object[] {}, locale));
			}
			if (StringUtils.isNotBlank(historyDTO.getToSessionLabelKey())) {
				historyDTO.setToSessionLabelKey(
						messageSource.getMessage(historyDTO.getToSessionLabelKey(), new Object[] {}, locale));
			}

		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(leaveSchemeResponse, jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return null;
	}
	
	@Override
	//@PreAuthorize("hasRole('PRIV_EMPLOYEE_LEAVE_BALANCE_SUMMARY')")
	@RequestMapping(value = "/employee/leaveBalanceSummary/savePostTransactionLeaveType.html", method = RequestMethod.POST)
	@ResponseBody
	public String savePostTransactionLeaveTypeEmp(
			@ModelAttribute(value = "leaveBalanceSummaryForm") LeaveBalanceSummaryForm leaveBalanceSummaryForm, Locale locale) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		LeaveBalanceSummaryResponse leaveSchemeResponse = leaveBalanceSummaryLogic.savePostTransactionLeaveType(
				leaveBalanceSummaryForm.getEmployeeLeaveSchemeTypeHistory(), companyId, employeeId, false);
		String[] errorValArr = null;
		String[] errorVal = null;
		StringBuilder errorKeyFinalStr = new StringBuilder();
		String[] errorKeyArr;
		if (leaveSchemeResponse.getLeaveDTO() != null && leaveSchemeResponse.getLeaveDTO().getErrorCode() == 1) {
			if (StringUtils.isNotBlank(leaveSchemeResponse.getLeaveDTO().getErrorKey())) {
				errorKeyArr = leaveSchemeResponse.getLeaveDTO().getErrorKey().split(";");
				if (StringUtils.isNotBlank(leaveSchemeResponse.getLeaveDTO().getErrorValue())) {
					errorValArr = leaveSchemeResponse.getLeaveDTO().getErrorValue().split(";");
				}

				for (int count = 0; count < errorKeyArr.length; count++) {
					if (StringUtils.isNotBlank(errorKeyArr[count])) {
						if (errorValArr.length > 0) {
							if (StringUtils.isNotBlank(errorValArr[count])) {
								errorVal = errorValArr[count].split(",");
							}
						}
						errorKeyFinalStr
								.append(messageSource.getMessage(errorKeyArr[count], errorVal, locale) + " </br> ");
					}

				}

			}
			leaveSchemeResponse.getLeaveDTO().setErrorKey(errorKeyFinalStr.toString());
		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(leaveSchemeResponse, jsonConfig);
		return jsonObject.toString();

	}
	
	
	@Override
	//@PreAuthorize("hasRole('PRIV_EMPLOYEE_LEAVE_BALANCE_SUMMARY')")
	@RequestMapping(value = "/employee/leaveBalanceSummary/getHolidaycalendar.html", method = RequestMethod.POST)
	@ResponseBody
	public String getHolidaycalendarEmp(@RequestParam(value = "year", required = true) int year) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		List<LeaveBalanceSummaryForm> holidayCalendarList = leaveBalanceSummaryLogic.getHolidaycalendar(companyId,
				employeeId, year);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(holidayCalendarList, jsonConfig);
		return jsonObject.toString();

	}
	
	@Override
	//@PreAuthorize("hasRole('PRIV_EMPLOYEE_LEAVE_BALANCE_SUMMARY')")
	@RequestMapping(value = "/employee/leaveBalanceSummary/getEmpOnLeaveByDate.html", method = RequestMethod.POST)
	@ResponseBody
	public String getEmpOnLeaveByDateEmp(@RequestParam(value = "leaveAppIds", required = true) String[] leaveAppIds,
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,  Locale locale) {
		UserContext.setLocale(locale);
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		LeaveBalanceSummaryResponse leaveSchemeResponse = leaveBalanceSummaryLogic.getEmpOnLeaveByDate(leaveAppIds,
				companyId, employeeId, pageDTO, sortDTO, false);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(leaveSchemeResponse, jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e);
		}
		return null;
	}
	
	@Override
	@RequestMapping(value = "/employee/leaveBalanceSummary/getPostLeaveSchemeData.html", method = RequestMethod.POST)
	@ResponseBody
	public String getPostLeaveSchemeDataEmp(@RequestParam(value = "empNumber", required = true) String empNumber,
			@RequestParam(value = "year", required = true) String year) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		LeaveBalanceSummaryResponse leaveBalSummaryResponse = leaveBalanceSummaryLogic.getPostLeaveSchemeData(empNumber,
				year, companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(leaveBalSummaryResponse, jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return null;
	}
	
	
	@Override
	@RequestMapping(value = "/employee/leaveBalanceSummary/getLeaveScheme.html", method = RequestMethod.POST)
	@ResponseBody
	public String getLeaveSchemeEmp(@RequestParam(value = "empNumber", required = true) String empNumber,
			@RequestParam(value = "year", required = true) String year) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		List<LeaveBalanceSummaryForm> leaveBalSummaryFrmList = leaveBalanceSummaryLogic.getLeaveScheme(empNumber, year,
				companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(leaveBalSummaryFrmList, jsonConfig);
		return jsonObject.toString();
	}

	@Override
	//@PreAuthorize("hasRole('PRIV_EMPLOYEE_LEAVE_BALANCE_SUMMARY')")
	@RequestMapping(value = "/employee/leaveBalanceSummary/getLeaveBalance.html", method = RequestMethod.POST)
	@ResponseBody
	public String getLeaveBalanceEmp(@RequestParam(value = "employeeLeaveSchemeTypeId") Long employeeLeaveSchemeTypeId) {

		/* ID DECRYPT */
		//employeeLeaveSchemeTypeId= FormatPreserveCryptoUtil.decrypt(employeeLeaveSchemeTypeId);
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		AddLeaveForm addLeaveForm = leaveBalanceSummaryLogic.getLeaveBalance(employeeLeaveSchemeTypeId, employeeId,
				companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(addLeaveForm, jsonConfig);
		return jsonObject.toString();
	}
	
	@Override
	//@PreAuthorize("hasRole('PRIV_EMPLOYEE_LEAVE_BALANCE_SUMMARY')")
	@RequestMapping(value = "/employee/leaveBalanceSummary/getDays.html", method = RequestMethod.POST)
	@ResponseBody
	public String getDaysEmp(@RequestParam(value = "fromDate") String fromDate,
			@RequestParam(value = "toDate") String toDate, @RequestParam(value = "session1") Long session1,
			@RequestParam(value = "session2") Long session2,
			@RequestParam(value = "employeeLeaveSchemeTypeId") Long employeeLeaveSchemeTypeId, Locale locale) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		LeaveDTO leaveDTO = new LeaveDTO();
		leaveDTO.setFromDate(fromDate);
		leaveDTO.setToDate(toDate);
		leaveDTO.setSession1(session1);
		leaveDTO.setSession2(session2);
		EmployeeLeaveSchemeType employeeLeaveSchemeType = employeeLeaveSchemeTypeDAO
				.findByleaveSchemeTypeIdAndCompanyIdAndEmpId(employeeLeaveSchemeTypeId, companyId, employeeId);
		if (employeeLeaveSchemeType == null) {
			throw new PayAsiaSystemException("Authentication Exception");
		}
		else
		{
			leaveDTO.setEmployeeLeaveSchemeTypeId(employeeLeaveSchemeType.getEmployeeLeaveSchemeTypeId());
		}
		AddLeaveForm addLeaveForm = leaveBalanceSummaryLogic.getNoOfDays(companyId, employeeId, leaveDTO);
		if (addLeaveForm.getLeaveDTO() != null) {
			if (StringUtils.isNotBlank(addLeaveForm.getLeaveDTO().getErrorKey())) {
				addLeaveForm.getLeaveDTO().setErrorKey(
						messageSource.getMessage(addLeaveForm.getLeaveDTO().getErrorKey(), new Object[] {}, locale));
			}
		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(addLeaveForm, jsonConfig);
		return jsonObject.toString();
	}
	
	@Override
	//@PreAuthorize("hasRole('PRIV_EMPLOYEE_LEAVE_BALANCE_SUMMARY')")
	@RequestMapping(value = "/employee/leaveBalanceSummary/isEncashedVisible.html", method = RequestMethod.POST)
	@ResponseBody
	public String isEncashedVisibleEmp() {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		LeavePreferenceForm leavePreferenceForm = leaveBalanceSummaryLogic.isEncashedVisible(companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(leavePreferenceForm, jsonConfig);
		return jsonObject.toString();

	}
	
	@Override
	//@PreAuthorize("hasRole('PRIV_EMPLOYEE_LEAVE_BALANCE_SUMMARY')")
	@RequestMapping(value = "/employee/leaveBalanceSummary/getLeaveTransactionHistory.html", method = RequestMethod.POST)
	@ResponseBody
	public String getLeaveTransactionHistoryEmp(
			@RequestParam(value = "leaveApplicationId", required = true) Long leaveApplicationId,
			@RequestParam(value = "transactionType", required = true) String transactionType, Locale locale) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		
		/* ID DECRYPT */
		leaveApplicationId= FormatPreserveCryptoUtil.decrypt(leaveApplicationId);
		
		LeaveBalanceSummaryResponse transactionHistoryResponse = leaveBalanceSummaryLogic
				.getLeaveTransactionHistory(leaveApplicationId, transactionType, companyId);
		List<LeaveApplicationWorkflowDTO> applicationWorkflowDTOs = transactionHistoryResponse
				.getEmployeeLeaveSchemeTypeHistoryDTO().getWorkflowList();
		for (LeaveApplicationWorkflowDTO applicationWorkflowDTO : applicationWorkflowDTOs) {
			if (StringUtils.isNotBlank(applicationWorkflowDTO.getStatus())) {
				applicationWorkflowDTO.setStatus(
						messageSource.getMessage(applicationWorkflowDTO.getStatus(), new Object[] {}, locale));
			}

		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(transactionHistoryResponse, jsonConfig);
		return jsonObject.toString();

	}
	
	@Override
	@RequestMapping(value = "/employee/leaveBalanceSummary/viewAttachment.html", method = RequestMethod.GET)
	public @ResponseBody byte[] viewAttachmentEmp(@RequestParam(value = "attachmentId", required = true) long attachmentId,
			HttpServletResponse response) {
		
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		Long employeeId = Long.parseLong(UserContext.getUserId());
		
		LeaveApplicationAttachment leaveApplicationAttachment = leaveApplicationAttachmentDAO.findAttachmentByEmployeeCompanyId(attachmentId, employeeId, companyId);
		if(leaveApplicationAttachment == null)
		{
			throw new PayAsiaSystemException("Authentication Exception");
		}
		LeaveApplicationAttachmentDTO attachment = leaveBalanceSummaryLogic.viewAttachment(attachmentId);
		byte[] byteFile = attachment.getAttachmentBytes();
		response.reset();
		String mimeType = URLConnection.guessContentTypeFromName(attachment.getFileName());
		response.setContentType("application/" + mimeType);
		response.setContentLength(byteFile.length);
		String filename = attachment.getFileName();

		response.setHeader("Content-Disposition", "attachment;filename=" + filename);

		return byteFile;

	}

	@Override
	//@PreAuthorize("hasRole('PRIV_EMPLOYEE_LEAVE_BALANCE_SUMMARY')")
	@RequestMapping(value = "/employee/leaveBalanceSummary/getYearList.html", method = RequestMethod.POST)
	@ResponseBody
	public String getYearListEmp() {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		List<EmployeeLeaveSchemeTypeDTO> yearList = leaveBalanceSummaryLogic.getDistinctYears(companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(yearList, jsonConfig);
		return jsonObject.toString();
	}
}
