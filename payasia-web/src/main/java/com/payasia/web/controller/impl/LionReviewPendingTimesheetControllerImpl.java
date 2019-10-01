package com.payasia.web.controller.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLConnection;
import java.net.URLEncoder;
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

import com.payasia.common.dto.TimesheetFormPdfDTO;
import com.payasia.common.form.LundinPendingItemsForm;
import com.payasia.common.form.OTPendingTimesheetForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.PendingOTTimesheetForm;
import com.payasia.common.form.PendingOTTimesheetResponseForm;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.logic.LionPendingTimesheetLogic;
import com.payasia.logic.LionReviewPendingTimesheetLogic;
import com.payasia.logic.LionTimesheetApplicationReviewerLogic;
import com.payasia.logic.LundinPendingTimesheetLogic;
import com.payasia.web.controller.LionPendingTimesheetController;
import com.payasia.web.util.PayAsiaSessionAttributes;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

@Controller
@RequestMapping(value = { "/admin/lionPendingTimesheet" })
public class LionReviewPendingTimesheetControllerImpl implements
		LionPendingTimesheetController {
	private static final Logger LOGGER = Logger
			.getLogger(LundinPendingTimesheetControllerImpl.class);

	@Resource
	LundinPendingTimesheetLogic lundinPendingTimesheetLogic;
	@Resource
	LionPendingTimesheetLogic lionPendingTimesheetLogic;

	@Resource
	LionReviewPendingTimesheetLogic lionReviewPendingTimesheetLogic;

	@Resource
	LionTimesheetApplicationReviewerLogic lionEmployeeApplicationReviewerLogic;

	@Autowired
	MessageSource messageSource;

	@Override
	@RequestMapping(value = "/search/getPendingTimesheet", method = RequestMethod.POST)
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

		PendingOTTimesheetResponseForm pendingOTTimesheetResponseForm = lionReviewPendingTimesheetLogic
				.getPendingTimesheet(empId, pageDTO, sortDTO, searchCondition,
						searchText, pageContextPath, companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(
				pendingOTTimesheetResponseForm, jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/getDataForTimesheetReview.html", method = RequestMethod.POST)
	@ResponseBody
	public String getDataForTimesheetReview(HttpServletRequest request,
			HttpServletResponse response, HttpSession session) {
		long timesheetId = Long.parseLong(request.getParameter("timesheetId"));
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		LundinPendingItemsForm pendingTimesheetForm = lundinPendingTimesheetLogic
				.getPendingItemForReview(timesheetId, employeeId, companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(pendingTimesheetForm,
				jsonConfig);
		return jsonObject.toString();

	}

	@Override
	@RequestMapping(value = "/search/getSubmittedTimesheet", method = RequestMethod.POST)
	@ResponseBody
	public String getSubmittedTimesheet(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
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

		PendingOTTimesheetResponseForm pendingOTTimesheetResponseForm = lionReviewPendingTimesheetLogic
				.getSubmittedTimesheet(empId, pageDTO, sortDTO,
						searchCondition, searchText, pageContextPath, companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(
				pendingOTTimesheetResponseForm, jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/search/getApprovedTimesheet", method = RequestMethod.POST)
	@ResponseBody
	public String getApprovedTimesheet(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
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

		PendingOTTimesheetResponseForm pendingOTTimesheetResponseForm = lionReviewPendingTimesheetLogic
				.getApprovedTimesheet(empId, pageDTO, sortDTO, searchCondition,
						searchText, pageContextPath, companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(
				pendingOTTimesheetResponseForm, jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/openTimesheetReviewer.html", method = RequestMethod.POST)
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

	@Override
	@RequestMapping(value = "/openMultipleTimesheetReviewer.html", method = RequestMethod.POST)
	@ResponseBody
	public String openMultipleTimesheetForEditingReviewer(
			@RequestParam("timesheetIds") String timesheetIds,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		String returnJSON = lionEmployeeApplicationReviewerLogic
				.getMultipleTimesheetApplications(timesheetIds, employeeId,
						companyId);
		return returnJSON;
	}

	@Override
	@RequestMapping(value = "/acceptTimesheet", method = RequestMethod.POST)
	@ResponseBody
	public String acceptTimesheet(HttpServletRequest request,
			HttpServletResponse response, HttpSession session, Locale locale) {
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		long timesheetId = Long.parseLong(request.getParameter("timesheetId"));
		String remarks = request.getParameter("remarks");
		String emailCC = request.getParameter("emailCC");
		PendingOTTimesheetForm otTimesheetForm = new PendingOTTimesheetForm();
		otTimesheetForm.setEmailCC(emailCC);
		otTimesheetForm.setOtTimesheetId(timesheetId);
		otTimesheetForm.setRemarks(remarks);
		otTimesheetForm.setOtTimesheetReviewerId(employeeId);
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		otTimesheetForm = lundinPendingTimesheetLogic.acceptTimesheet(
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
	@RequestMapping(value = "/rejectTimesheet", method = RequestMethod.POST)
	@ResponseBody
	public String rejectTimesheet(HttpServletRequest request,
			HttpServletResponse response, HttpSession session) {
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		long timesheetId = Long.parseLong(request.getParameter("timesheetId"));
		String remarks = request.getParameter("remarks");
		String emailCC = request.getParameter("emailCC");
		PendingOTTimesheetForm otTimesheetForm = new PendingOTTimesheetForm();
		otTimesheetForm.setEmailCC(emailCC);
		otTimesheetForm.setOtTimesheetId(timesheetId);
		otTimesheetForm.setRemarks(remarks);
		otTimesheetForm.setOtTimesheetReviewerId(employeeId);
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		otTimesheetForm = lundinPendingTimesheetLogic.rejectTimesheet(
				otTimesheetForm, employeeId, companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(otTimesheetForm,
				jsonConfig);
		return jsonObject.toString();

	}

	@Override
	@RequestMapping(value = "/getotPendingTimesheetReviewWorkflow.html", method = RequestMethod.POST)
	@ResponseBody
	public String getotPendingTimesheetReviewWorkflow(
			@RequestParam(value = "otTimesheetId") Long otTimesheetId,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {

		OTPendingTimesheetForm otPendingTimesheetForm = lundinPendingTimesheetLogic
				.getDataForPendingOtReviewWorkflow(otTimesheetId);

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
	@RequestMapping(value = "/printTimesheetDetail.html", method = RequestMethod.GET)
	public @ResponseBody byte[] printTimesheetDetail(
			@RequestParam(value = "timesheetId", required = true) Long timesheetId,
			HttpServletResponse response, HttpServletRequest request) {
		/*ID DECRYPT*/
		timesheetId = FormatPreserveCryptoUtil.decrypt(timesheetId);
		UUID uuid = UUID.randomUUID();
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		TimesheetFormPdfDTO timesheetFormPdfDTO = lionPendingTimesheetLogic
				.generateTimesheetPrintPDF(companyId, employeeId, timesheetId);

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
	@RequestMapping(value = "/updateTimesheetRow.html", method = RequestMethod.POST)
	@ResponseBody
	public String updateTimesheetRow(
			@RequestParam(value = "timesheetDetailId") long employeeTimesheetDetailId,
			@RequestParam(value = "inTime") String inTime,
			@RequestParam(value = "outTime") String outTime,
			@RequestParam(value = "breakTime") String breakTime,
			@RequestParam(value = "totalHours") String totalHours,
			@RequestParam(value = "remarks") String remarks,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		/*ID DECRYPT*/
		employeeTimesheetDetailId = FormatPreserveCryptoUtil.decrypt(employeeTimesheetDetailId);
		Map<String, String> statusMap = lionReviewPendingTimesheetLogic
				.updateLionEmployeeTimesheetApplicationDetail(
						employeeTimesheetDetailId, inTime, outTime, breakTime,
						totalHours, remarks);

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("success", true);
		jsonObject.put("inTimeStatus", statusMap.get("inTimeStatus"));
		jsonObject.put("outTimeStatus", statusMap.get("outTimeStatus"));
		jsonObject.put("breakTimeStatus", statusMap.get("breakTimeStatus"));
		jsonObject.put("grandTotalHours", statusMap.get("grandTotalHours"));
		jsonObject.put("excessHoursWorked", statusMap.get("excessHoursWorked"));
		return jsonObject.toString();

	}

	@Override
	@RequestMapping(value = "/approveTimesheetRow.html", method = RequestMethod.POST)
	@ResponseBody
	public String approveTimesheetRow(
			@RequestParam(value = "timesheetDetailId") long employeeTimesheetDetailId,
			@RequestParam(value = "inTime") String inTime,
			@RequestParam(value = "outTime") String outTime,
			@RequestParam(value = "breakTime") String breakTime,
			@RequestParam(value = "totalHours") String totalHours,
			@RequestParam(value = "remarks") String remarks,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		/*ID DECRYPT*/
		employeeTimesheetDetailId = FormatPreserveCryptoUtil.decrypt(employeeTimesheetDetailId);
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		Map<String, String> statusMap = lionReviewPendingTimesheetLogic
				.approveLionEmployeeTimesheetApplicationDetail(employeeId,
						employeeTimesheetDetailId, inTime, outTime, breakTime,
						totalHours, remarks);

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("success", true);
		jsonObject.put("inTimeStatus", statusMap.get("inTimeStatus"));
		jsonObject.put("outTimeStatus", statusMap.get("outTimeStatus"));
		jsonObject.put("breakTimeStatus", statusMap.get("breakTimeStatus"));
		jsonObject.put("grandTotalHours", statusMap.get("grandTotalHours"));
		jsonObject.put("excessHoursWorked", statusMap.get("excessHoursWorked"));
		return jsonObject.toString();

	}

	@Override
	public void exportTimesheetDetail(Long timesheetId,
			HttpServletResponse response, HttpServletRequest request)
			throws IOException {

	}

}
