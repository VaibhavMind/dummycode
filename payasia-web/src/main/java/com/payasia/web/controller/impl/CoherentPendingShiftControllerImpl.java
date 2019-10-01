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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.TimesheetFormPdfDTO;
import com.payasia.common.form.LundinPendingItemsForm;
import com.payasia.common.form.OTPendingTimesheetForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.PendingOTTimesheetForm;
import com.payasia.common.form.PendingOTTimesheetResponseForm;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.logic.CoherentEmployeeOvertimeLogic;
import com.payasia.logic.CoherentMyShiftLogic;
import com.payasia.logic.LundinPendingTimesheetLogic;
import com.payasia.web.controller.CoherentPendingShiftController;
import com.payasia.web.util.PayAsiaSessionAttributes;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

@Controller
public class CoherentPendingShiftControllerImpl implements
		CoherentPendingShiftController {
	private static final Logger LOGGER = Logger
			.getLogger(CoherentPendingShiftControllerImpl.class);
	@Resource
	LundinPendingTimesheetLogic lundinPendingTimesheetLogic;
	@Resource
	CoherentEmployeeOvertimeLogic coherentEmployeeOvertimeLogic;

	@Resource
	CoherentMyShiftLogic coherentMyShiftLogic;

	@Autowired
	MessageSource messageSource;

	@Override
	@RequestMapping(value = "/employee/coherentShift/search/getPendingShift", method = RequestMethod.POST)
	@ResponseBody
	public String getPendingShift(
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

		PendingOTTimesheetResponseForm pendingOTTimesheetResponseForm = coherentMyShiftLogic
				.getPendingEmployeeShift(empId, pageDTO, sortDTO,
						searchCondition, searchText, companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(
				pendingOTTimesheetResponseForm, jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/employee/coherentShift/search/getApprovedShift.html", method = RequestMethod.POST)
	@ResponseBody
	public String getApprovedOTShift(
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
		PendingOTTimesheetResponseForm addClaimFormResponse = coherentMyShiftLogic
				.getShift(empId, pageDTO, sortDTO, searchCondition, searchText,
						StatusNameList, companyId);

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
	@RequestMapping(value = "/employee/coherentShift/search/getRejectedShift.html", method = RequestMethod.POST)
	@ResponseBody
	public String getRejectedOTShift(
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
		PendingOTTimesheetResponseForm addClaimFormResponse = coherentMyShiftLogic
				.getShift(empId, pageDTO, sortDTO, searchCondition, searchText,
						StatusNameList, companyId);
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
	@RequestMapping(value = "/employee/coherentShift/search/getAllShift.html", method = RequestMethod.POST)
	@ResponseBody
	public String getAllOTShift(
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
		// StatusNameList.add(PayAsiaConstants.CLAIM_STATUS_SUBMITTED);
		// StatusNameList.add(PayAsiaConstants.CLAIM_STATUS_APPROVED);
		StatusNameList.add(PayAsiaConstants.CLAIM_STATUS_COMPLETED);
		PendingOTTimesheetResponseForm addClaimFormResponse = coherentMyShiftLogic
				.getAllShift(empId, pageDTO, sortDTO, searchCondition,
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
	@RequestMapping(value = "/employee/coherentShift/openSubmittedShift.html", method = RequestMethod.POST)
	@ResponseBody
	public String openSubmittedShift(
			@RequestParam("shiftApplicationId") long shiftApplicationId,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		     /*ID DECRYPT*/
		shiftApplicationId =  FormatPreserveCryptoUtil.decrypt(shiftApplicationId);
		
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		Long employeeId = coherentMyShiftLogic
				.getEmployeeIdFromShiftId(shiftApplicationId);

		return coherentMyShiftLogic.shiftApplications(shiftApplicationId,
				employeeId, companyId);

	}

	@Override
	@RequestMapping(value = "/employee/coherentShift/getDataForTimesheetReview.html", method = RequestMethod.POST)
	@ResponseBody
	public String getDataForTimesheetReview(HttpServletRequest request,
			HttpServletResponse response, HttpSession session) {
		long timesheetId = Long.parseLong(request
				.getParameter("shiftApplicationId"));
		/*ID DECRYPT*/
		timesheetId = FormatPreserveCryptoUtil.decrypt(timesheetId);
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		LundinPendingItemsForm pendingTimesheetForm = coherentMyShiftLogic
				.getPendingItemForReview(timesheetId, employeeId, companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(pendingTimesheetForm,
				jsonConfig);
		return jsonObject.toString();

	}

	@Override
	@RequestMapping(value = "/employee/coherentShift/acceptShift", method = RequestMethod.POST)
	@ResponseBody
	public String acceptShift(HttpServletRequest request,
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
		otTimesheetForm = coherentMyShiftLogic.acceptShift(otTimesheetForm,
				employeeId, companyId);

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
	@RequestMapping(value = "/employee/coherentShift/rejectShift", method = RequestMethod.POST)
	@ResponseBody
	public String rejectShift(HttpServletRequest request,
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
		otTimesheetForm = coherentMyShiftLogic.rejectShift(otTimesheetForm,
				employeeId, companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(otTimesheetForm,
				jsonConfig);
		return jsonObject.toString();

	}

	@Override
	@RequestMapping(value = "/employee/coherentShift/forwardShift", method = RequestMethod.POST)
	@ResponseBody
	public String forwardShift(HttpServletRequest request,
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
		otTimesheetForm = coherentMyShiftLogic.forwardShift(otTimesheetForm,
				employeeId, companyId);
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
	@RequestMapping(value = "/employee/coherentShift/updateShiftForBatch.html", method = RequestMethod.POST)
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
				.updateCoherentShiftApplicationDetailRevewer(
						String.valueOf(shiftId), totalShift, remarks, isShift,
						coherentShiftType, shiftTypePerDate);

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("success", true);
		jsonObject.put("totShifts", statusMap.get("totShifts"));
		jsonObject.put("isShft", statusMap.get("isShft"));
		jsonObject.put("shiftType", statusMap.get("shiftType"));
		jsonObject.put("isShiftChanged", statusMap.get("isShiftChanged"));
		jsonObject.put("isShiftTypeChanged",
				statusMap.get("isShiftTypeChanged"));

		jsonObject.put("coherentShiftType", statusMap.get("coherentShiftType"));
		jsonObject.put("remarks", statusMap.get("remarks"));
		return jsonObject.toString();

	}

	@Override
	@RequestMapping(value = "/employee/coherentShift/getTimesheetWorkflowHistory.html", method = RequestMethod.POST)
	@ResponseBody
	public String getTimesheetWorkflowHistory(
			@RequestParam(value = "otTimesheetId") Long otTimesheetId,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		/*ID DECRYPT*/
		otTimesheetId = FormatPreserveCryptoUtil.decrypt(otTimesheetId);
		OTPendingTimesheetForm otPendingTimesheetForm = coherentMyShiftLogic
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
	@RequestMapping(value = "/employee/coherentShift/printShiftDetail.html", method = RequestMethod.GET)
	public @ResponseBody byte[] printShiftDetail(
			@RequestParam(value = "ShiftApplicationId", required = true) Long timesheetId,
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
		TimesheetFormPdfDTO timesheetFormPdfDTO = coherentMyShiftLogic
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

}
