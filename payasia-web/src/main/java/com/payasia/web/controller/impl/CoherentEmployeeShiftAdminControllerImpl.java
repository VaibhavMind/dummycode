package com.payasia.web.controller.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
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

import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.DataImportLogDTO;
import com.payasia.common.dto.LeaveSessionDTO;
import com.payasia.common.exception.PayAsiaRollBackDataException;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.CoherentShiftDetailForm;
import com.payasia.common.form.CoherentShiftDetailFormResponse;
import com.payasia.common.form.ImportEmployeeOvertimeShiftForm;
import com.payasia.common.form.LundinPendingItemsForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.PendingOTTimesheetForm;
import com.payasia.common.form.PendingOTTimesheetResponseForm;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.logic.CoherentEmployeeOvertimeAdminLogic;
import com.payasia.logic.CoherentEmployeeShiftAdminLogic;
import com.payasia.web.controller.CoherentEmployeeShiftAdminController;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

@Controller
//@RequestMapping(value = "/admin/coherentEmployeeShift")
public class CoherentEmployeeShiftAdminControllerImpl implements
		CoherentEmployeeShiftAdminController {
	private static final Logger LOGGER = Logger
			.getLogger(CoherentEmployeeShiftAdminController.class);

	@Resource
	CoherentEmployeeOvertimeAdminLogic coherentEmployeeOvertimeAdminLogic;

	@Resource
	CoherentEmployeeShiftAdminLogic coherentEmployeeShiftAdminLogic;

	@Autowired
	MessageSource messageSource;

	@Override
	@RequestMapping(value = "/admin/coherentEmployeeShift/search/getPendingShift.html", method = RequestMethod.POST)
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

		PendingOTTimesheetResponseForm pendingOTTimesheetResponseForm = coherentEmployeeShiftAdminLogic
				.getPendingTimesheet(empId, pageDTO, sortDTO, searchCondition,
						searchText, companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(
				pendingOTTimesheetResponseForm, jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/admin/coherentEmployeeShift/search/getApprovedShift.html", method = RequestMethod.POST)
	@ResponseBody
	public String getApprovedShift(
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
		PendingOTTimesheetResponseForm addClaimFormResponse = coherentEmployeeShiftAdminLogic
				.getTimesheet(empId, pageDTO, sortDTO, searchCondition,
						searchText, StatusNameList, companyId);

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
	@RequestMapping(value = "/admin/coherentEmployeeShift/search/getRejectedShift.html", method = RequestMethod.POST)
	@ResponseBody
	public String getRejectedShift(
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
		PendingOTTimesheetResponseForm addClaimFormResponse = coherentEmployeeShiftAdminLogic
				.getTimesheet(empId, pageDTO, sortDTO, searchCondition,
						searchText, StatusNameList, companyId);

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
	@RequestMapping(value = "/admin/coherentEmployeeShift/search/getAllShift.html", method = RequestMethod.POST)
	@ResponseBody
	public String getAllShift(
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
		StatusNameList.add(PayAsiaConstants.CLAIM_STATUS_SUBMITTED);
		StatusNameList.add(PayAsiaConstants.CLAIM_STATUS_APPROVED);
		StatusNameList.add(PayAsiaConstants.CLAIM_STATUS_COMPLETED);
		PendingOTTimesheetResponseForm addClaimFormResponse = coherentEmployeeShiftAdminLogic
				.getAllTimesheet(empId, pageDTO, sortDTO, searchCondition,
						searchText, StatusNameList, companyId);

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
	@RequestMapping(value = "/admin/coherentEmployeeShift/forwardTimesheet", method = RequestMethod.POST)
	@ResponseBody
	public String forwardTimesheet(HttpServletRequest request,
			HttpServletResponse response, HttpSession session, Locale locale) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		long timesheetId = Long.parseLong(request.getParameter("timesheetId"));
		/*ID DECRYPT*/
		timesheetId = FormatPreserveCryptoUtil.decrypt(timesheetId);
		String remarks = request.getParameter("remarks");
		CoherentShiftDetailForm otTimesheetForm = new CoherentShiftDetailForm();
		otTimesheetForm.setRemarks(remarks);
		otTimesheetForm.setOtTimesheetId(timesheetId);
		PendingOTTimesheetForm otTimesheetFormResponse = coherentEmployeeShiftAdminLogic
				.forwardTimesheet(otTimesheetForm, employeeId, companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(otTimesheetFormResponse,
				jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/admin/coherentEmployeeShift/acceptTimesheet", method = RequestMethod.POST)
	@ResponseBody
	public String acceptTimesheet(HttpServletRequest request,
			HttpServletResponse response, HttpSession session, Locale locale) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		long timesheetId = Long.parseLong(request.getParameter("timesheetId"));
		/*ID DECRYPT*/
		timesheetId = FormatPreserveCryptoUtil.decrypt(timesheetId);
		String remarks = request.getParameter("remarks");
		CoherentShiftDetailForm otTimesheetForm = new CoherentShiftDetailForm();
		otTimesheetForm.setRemarks(remarks);
		otTimesheetForm.setOtTimesheetId(timesheetId);
	

		PendingOTTimesheetForm otTimesheetFormResponse = coherentEmployeeShiftAdminLogic
				.acceptTimesheet(otTimesheetForm, employeeId, companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(otTimesheetFormResponse,
				jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/admin/coherentEmployeeShift/rejectTimesheet", method = RequestMethod.POST)
	@ResponseBody
	public String rejectTimesheet(HttpServletRequest request,
			HttpServletResponse response, HttpSession session) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		long timesheetId = Long.parseLong(request.getParameter("timesheetId"));
		/*ID DECRYPT*/
		timesheetId = FormatPreserveCryptoUtil.decrypt(timesheetId);
		String remarks = request.getParameter("remarks");
		CoherentShiftDetailForm otTimesheetForm = new CoherentShiftDetailForm();
		otTimesheetForm.setRemarks(remarks);
		otTimesheetForm.setOtTimesheetId(timesheetId);
		PendingOTTimesheetForm otTimesheetFormResponse = coherentEmployeeShiftAdminLogic
				.rejectTimesheet(otTimesheetForm, employeeId, companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(otTimesheetFormResponse,
				jsonConfig);
		return jsonObject.toString();

	}

	@Override
	@RequestMapping(value = "/admin/coherentEmployeeShift/getDataForTimesheetReview.html", method = RequestMethod.POST)
	@ResponseBody
	public String getDataForTimesheetReview(HttpServletRequest request,
			HttpServletResponse response, HttpSession session) {
		long timesheetId = Long.parseLong(request
				.getParameter("shiftApplicationId"));
		/*ID DECRYPT*/
		timesheetId = FormatPreserveCryptoUtil.decrypt(timesheetId);
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		LundinPendingItemsForm pendingTimesheetForm = coherentEmployeeShiftAdminLogic
				.getPendingItemForReview(timesheetId, employeeId, companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(pendingTimesheetForm,
				jsonConfig);
		return jsonObject.toString();

	}

	@Override
	@RequestMapping(value = "/admin/coherentEmployeeShift/viewMultipleTimesheetApps.html", method = RequestMethod.POST)
	@ResponseBody
	public String viewMultipleTimesheetApps(
			@RequestParam(value = "timesheetIds", required = true) String[] timesheetIds,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		UserContext.setLocale(locale);
		CoherentShiftDetailFormResponse pendingItemsFormResponse = coherentEmployeeShiftAdminLogic
				.viewMultipleTimesheetApps(companyId, employeeId, timesheetIds);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(pendingItemsFormResponse,
				jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/admin/coherentEmployeeShift/reviewMultipleAppByAdmin.html", method = RequestMethod.POST)
	@ResponseBody
	public String reviewMultipleAppByAdmin(
			@ModelAttribute(value = "coherentShiftDetailForm") CoherentShiftDetailForm coherentShiftDetailForm,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session, Locale locale) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		LeaveSessionDTO sessionDTO = new LeaveSessionDTO();
		sessionDTO.setFromSessionName(messageSource.getMessage(
				PayAsiaConstants.LEAVE_SESSION_MESSAGE_FROMLABEL_KEY,
				new Object[] {}, locale));

		sessionDTO.setToSessionName(messageSource.getMessage(
				PayAsiaConstants.LEAVE_SESSION_MESSAGE_TOLABEL_KEY,
				new Object[] {}, locale));

		List<PendingOTTimesheetForm> addLeaveFormRes = coherentEmployeeShiftAdminLogic
				.reviewMultipleAppByAdmin(coherentShiftDetailForm, employeeId,
						companyId, sessionDTO);

		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray
				.fromObject(addLeaveFormRes, jsonConfig);
		return jsonObject.toString();

	}

	@Override
	@RequestMapping(value = "/admin/coherentEmployeeShift/importEmployeeShift.html", method = RequestMethod.POST)
	@ResponseBody
	public String importEmployeeShift(
			@ModelAttribute(value = "importEmployeeOvertimeShift") ImportEmployeeOvertimeShiftForm importEmployeeOvertimeShift,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) throws Exception {
		ImportEmployeeOvertimeShiftForm importResponse = new ImportEmployeeOvertimeShiftForm();

		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		try {
			importResponse = coherentEmployeeShiftAdminLogic
					.importEmployeeShift(importEmployeeOvertimeShift,
							companyId, employeeId);
			importResponse.setDataValid(true);
		} catch (PayAsiaRollBackDataException ex) {
			LOGGER.error(ex.getMessage(), ex);
			importResponse.setDataValid(false);
			importResponse.setDataImportLogDTOs(ex.getErrors());
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			List<DataImportLogDTO> errors = new ArrayList<DataImportLogDTO>();
			DataImportLogDTO error = new DataImportLogDTO();

			error.setFailureType("payasia.record.error");
			error.setRemarks("payasia.record.error");
			error.setFromMessageSource(false);

			errors.add(error);
			importResponse.setDataValid(false);
			importResponse.setDataImportLogDTOs(errors);
		}

		if (importResponse.getDataImportLogDTOs() != null
				&& importResponse.getDataImportLogDTOs().size() > 0) {

			for (DataImportLogDTO dataImportLogDTO : importResponse
					.getDataImportLogDTOs()) {
				try {
					String[] errorValArr = null;
					String[] errorVal = null;
					StringBuilder errorKeyFinalStr = new StringBuilder();
					String[] errorKeyArr;
					if (StringUtils.isNotBlank(dataImportLogDTO.getErrorKey())) {
						errorKeyArr = dataImportLogDTO.getErrorKey().split(";");
						if (StringUtils.isNotBlank(dataImportLogDTO
								.getErrorValue())) {
							errorValArr = dataImportLogDTO.getErrorValue()
									.split(";");
						}

						for (int count = 0; count < errorKeyArr.length; count++) {
							if (StringUtils.isNotBlank(errorKeyArr[count])) {
								// if (errorValArr.length > 0) {
								// if (StringUtils
								// .isNotBlank(errorValArr[count])) {
								// errorVal = errorValArr[count]
								// .split(",");
								// }
								// }
								errorKeyFinalStr.append(count + 1 + ". ");
								errorKeyFinalStr.append(messageSource
										.getMessage(errorKeyArr[count],
												errorVal, locale));
								// errorKeyFinalStr.append("<br>");
							}

						}

						dataImportLogDTO
								.setRemarks(errorKeyFinalStr.toString());

					} else {
						if (StringUtils.isNotBlank(dataImportLogDTO
								.getErrorValue())) {
							errorVal = dataImportLogDTO.getErrorValue().split(
									",");
							dataImportLogDTO.setRemarks(URLEncoder.encode(
									messageSource.getMessage(
											dataImportLogDTO.getRemarks(),
											errorVal, locale), "UTF-8"));
						} else {
							dataImportLogDTO.setRemarks(URLEncoder.encode(
									messageSource.getMessage(
											dataImportLogDTO.getRemarks(),
											new Object[] {}, locale), "UTF-8"));
						}
					}

				} catch (UnsupportedEncodingException | NoSuchMessageException exception) {
					LOGGER.error(exception.getMessage(), exception);
					throw new PayAsiaSystemException(exception.getMessage(),
							exception);
				}
			}
			importResponse.setDataValid(false);
		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(importResponse,
				jsonConfig);
		return jsonObject.toString();
	}

}
