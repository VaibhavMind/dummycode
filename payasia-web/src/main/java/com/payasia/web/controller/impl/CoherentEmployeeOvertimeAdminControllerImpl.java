package com.payasia.web.controller.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

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
import com.payasia.common.dto.TimesheetFormPdfDTO;
import com.payasia.common.exception.PayAsiaRollBackDataException;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.CoherentOvertimeDetailForm;
import com.payasia.common.form.CoherentOvertimeDetailFormResponse;
import com.payasia.common.form.CoherentTimesheetDTO;
import com.payasia.common.form.ImportEmployeeOvertimeShiftForm;
import com.payasia.common.form.LundinPendingItemsForm;
import com.payasia.common.form.OTPendingTimesheetForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.PendingOTTimesheetForm;
import com.payasia.common.form.PendingOTTimesheetResponseForm;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.logic.CoherentEmployeeOvertimeAdminLogic;
import com.payasia.web.controller.CoherentEmployeeOvertimeAdminController;
import com.payasia.web.util.PayAsiaSessionAttributes;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

@Controller
//@RequestMapping(value = "/admin/coherentEmployeeOvertime")
public class CoherentEmployeeOvertimeAdminControllerImpl implements
		CoherentEmployeeOvertimeAdminController {
	private static final Logger LOGGER = Logger
			.getLogger(CoherentEmployeeOvertimeAdminControllerImpl.class);

	@Resource
	CoherentEmployeeOvertimeAdminLogic coherentEmployeeOvertimeAdminLogic;

	@Autowired
	MessageSource messageSource;

	@Override
	@RequestMapping(value = "/admin/coherentEmployeeOvertime/search/getPendingTimesheet.html", method = RequestMethod.POST)
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

		PendingOTTimesheetResponseForm pendingOTTimesheetResponseForm = coherentEmployeeOvertimeAdminLogic
				.getPendingTimesheet(empId, companyId, pageDTO, sortDTO,
						searchCondition, searchText);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(
				pendingOTTimesheetResponseForm, jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/admin/coherentEmployeeOvertime/search/getApprovedTimesheet.html", method = RequestMethod.POST)
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
		PendingOTTimesheetResponseForm addClaimFormResponse = coherentEmployeeOvertimeAdminLogic
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
	@RequestMapping(value = "/admin/coherentEmployeeOvertime/search/getRejectedTimesheet.html", method = RequestMethod.POST)
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
		PendingOTTimesheetResponseForm addClaimFormResponse = coherentEmployeeOvertimeAdminLogic
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
	@RequestMapping(value = "/admin/coherentEmployeeOvertime/search/getAllTimesheet.html", method = RequestMethod.POST)
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
		StatusNameList.add(PayAsiaConstants.CLAIM_STATUS_SUBMITTED);
		StatusNameList.add(PayAsiaConstants.CLAIM_STATUS_APPROVED);
		StatusNameList.add(PayAsiaConstants.CLAIM_STATUS_COMPLETED);
		PendingOTTimesheetResponseForm addClaimFormResponse = coherentEmployeeOvertimeAdminLogic
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
	@RequestMapping(value = "/admin/coherentEmployeeOvertime/openTimesheet.html", method = RequestMethod.POST)
	@ResponseBody
	public String openTimesheetForEditing(
			@RequestParam("timesheetId") long timesheetId,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		/*ID DECRYPT*/
		timesheetId = FormatPreserveCryptoUtil.decrypt(timesheetId);
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		return coherentEmployeeOvertimeAdminLogic.getTimesheetApplications(
				timesheetId, companyId);

	}

	@Override
	@RequestMapping(value = "/admin/coherentEmployeeOvertime/updateCoherentOvertimeDetailByRev.html", method = RequestMethod.POST)
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
		employeeTimesheetDetailId=FormatPreserveCryptoUtil.decrypt(employeeTimesheetDetailId);
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
		Map<String, String> statusMap = coherentEmployeeOvertimeAdminLogic
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
	@RequestMapping(value = "/admin/coherentEmployeeOvertime/forwardTimesheet", method = RequestMethod.POST)
	@ResponseBody
	public String forwardTimesheet(HttpServletRequest request,
			HttpServletResponse response, HttpSession session, Locale locale) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		long timesheetId = Long.parseLong(request.getParameter("timesheetId"));
		/*ID DECRYPT*/
		timesheetId = FormatPreserveCryptoUtil.decrypt(timesheetId);

		String remarks = request.getParameter("remarks");
		CoherentOvertimeDetailForm otTimesheetForm = new CoherentOvertimeDetailForm();
		otTimesheetForm.setRemarks(remarks);
		otTimesheetForm.setOtTimesheetId(timesheetId);
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		PendingOTTimesheetForm otTimesheetFormResponse = coherentEmployeeOvertimeAdminLogic
				.forwardTimesheet(otTimesheetForm, employeeId, companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(otTimesheetFormResponse,
				jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/admin/coherentEmployeeOvertime/acceptTimesheet", method = RequestMethod.POST)
	@ResponseBody
	public String acceptTimesheet(HttpServletRequest request,
			HttpServletResponse response, HttpSession session, Locale locale) {
		Long employeeId = Long.parseLong(UserContext.getUserId());

		long timesheetId = Long.parseLong(request.getParameter("timesheetId"));
		/*ID DECRYPT*/
		timesheetId = FormatPreserveCryptoUtil.decrypt(	timesheetId);
		String remarks = request.getParameter("remarks");
		CoherentOvertimeDetailForm otTimesheetForm = new CoherentOvertimeDetailForm();
		otTimesheetForm.setRemarks(remarks);
		otTimesheetForm.setOtTimesheetId(timesheetId);
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		PendingOTTimesheetForm otTimesheetFormResponse = coherentEmployeeOvertimeAdminLogic
				.acceptTimesheet(otTimesheetForm, employeeId, companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(otTimesheetFormResponse,
				jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/admin/coherentEmployeeOvertime/rejectTimesheet", method = RequestMethod.POST)
	@ResponseBody
	public String rejectTimesheet(HttpServletRequest request,
			HttpServletResponse response, HttpSession session) {
		Long employeeId = Long.parseLong(UserContext.getUserId());

		long timesheetId = Long.parseLong(request.getParameter("timesheetId"));
		/*ID DECRYPT*/
		timesheetId = FormatPreserveCryptoUtil.decrypt(	timesheetId);
		String remarks = request.getParameter("remarks");
		CoherentOvertimeDetailForm otTimesheetForm = new CoherentOvertimeDetailForm();
		otTimesheetForm.setRemarks(remarks);
		otTimesheetForm.setOtTimesheetId(timesheetId);
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		PendingOTTimesheetForm otTimesheetFormResponse = coherentEmployeeOvertimeAdminLogic
				.rejectTimesheet(otTimesheetForm, employeeId, companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(otTimesheetFormResponse,
				jsonConfig);
		return jsonObject.toString();

	}

	@Override
	@RequestMapping(value = "/admin/coherentEmployeeOvertime/getTimesheetWorkflowHistory.html", method = RequestMethod.POST)
	@ResponseBody
	public String getTimesheetWorkflowHistory(
			@RequestParam(value = "otTimesheetId") Long otTimesheetId,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		/*ID DECRYPT*/
		otTimesheetId = FormatPreserveCryptoUtil.decrypt(otTimesheetId);
		OTPendingTimesheetForm otPendingTimesheetForm = coherentEmployeeOvertimeAdminLogic
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
	@RequestMapping(value = "/admin/coherentEmployeeOvertime/viewMultipleTimesheetApps.html", method = RequestMethod.POST)
	@ResponseBody
	public String viewMultipleTimesheetApps(
			@RequestParam(value = "timesheetIds", required = true) String[] timesheetIds,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		UserContext.setLocale(locale);
		CoherentOvertimeDetailFormResponse pendingItemsFormResponse = coherentEmployeeOvertimeAdminLogic
				.viewMultipleTimesheetApps(companyId, employeeId, timesheetIds);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(pendingItemsFormResponse,
				jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/admin/coherentEmployeeOvertime/showEmpWorkflowHistory.html", method = RequestMethod.POST)
	@ResponseBody
	public String showEmpWorkflowHistory(
			@RequestParam(value = "timesheetId", required = true) Long timesheetId,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) {
		/*ID DECRYPT*/
		timesheetId = FormatPreserveCryptoUtil.decrypt(	timesheetId);
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		CoherentOvertimeDetailForm pendingItemResponse = coherentEmployeeOvertimeAdminLogic
				.showEmpWorkflowHistory(companyId, timesheetId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(pendingItemResponse,
				jsonConfig);
		return jsonObject.toString();

	}

	@Override
	@RequestMapping(value = "/admin/coherentEmployeeOvertime/reviewMultipleAppByAdmin.html", method = RequestMethod.POST)
	@ResponseBody
	public String reviewMultipleAppByAdmin(
			@ModelAttribute(value = "coherentOvertimeDetailForm") CoherentOvertimeDetailForm coherentOvertimeDetailForm,
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

		List<PendingOTTimesheetForm> addLeaveFormRes = coherentEmployeeOvertimeAdminLogic
				.reviewMultipleAppByAdmin(coherentOvertimeDetailForm,
						employeeId, companyId, sessionDTO);

		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray
				.fromObject(addLeaveFormRes, jsonConfig);
		return jsonObject.toString();

	}

	@Override
	@RequestMapping(value = "/admin/coherentEmployeeOvertime/getDataForTimesheetReview.html", method = RequestMethod.POST)
	@ResponseBody
	public String getDataForTimesheetReview(HttpServletRequest request,
			HttpServletResponse response, HttpSession session) {
		long timesheetId = Long.parseLong(request.getParameter("timesheetId"));
		/*ID DECRYPT*/
		timesheetId = FormatPreserveCryptoUtil.decrypt(timesheetId);
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		LundinPendingItemsForm pendingTimesheetForm = coherentEmployeeOvertimeAdminLogic
				.getPendingItemForReview(timesheetId, employeeId, companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(pendingTimesheetForm,
				jsonConfig);
		return jsonObject.toString();

	}

	@Override
	@RequestMapping(value = "/admin/coherentEmployeeOvertime/printTimesheetDetail.html", method = RequestMethod.GET)
	public @ResponseBody byte[] printTimesheetDetail(
			@RequestParam(value = "timesheetId", required = true) Long timesheetId,
			HttpServletResponse response, HttpServletRequest request) {
		UUID uuid = UUID.randomUUID();
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		/*ID DECRYPT*/
		timesheetId = FormatPreserveCryptoUtil.decrypt(	timesheetId);
		boolean hasLundinTimesheetModule = (boolean) request
				.getSession()
				.getAttribute(
						PayAsiaSessionAttributes.COMPANY_HAS_LUNDIN_TIMESHEET_MODULE);
		TimesheetFormPdfDTO timesheetFormPdfDTO = coherentEmployeeOvertimeAdminLogic
				.generateTimesheetPrintPDF(companyId, employeeId, timesheetId,
						hasLundinTimesheetModule);

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
	@RequestMapping(value = "/admin/coherentEmployeeOvertime/importEmployeeOvertime.html", method = RequestMethod.POST)
	@ResponseBody
	public String importEmployeeOvertime(
			@ModelAttribute(value = "importEmployeeOvertimeShift") ImportEmployeeOvertimeShiftForm importEmployeeOvertimeShift,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) throws Exception {
		ImportEmployeeOvertimeShiftForm importResponse = new ImportEmployeeOvertimeShiftForm();

		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		try {
			importResponse = coherentEmployeeOvertimeAdminLogic
					.importEmployeeOvertime(importEmployeeOvertimeShift,
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
