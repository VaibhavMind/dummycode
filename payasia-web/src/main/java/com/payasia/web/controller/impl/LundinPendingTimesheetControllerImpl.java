package com.payasia.web.controller.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Locale;
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
import com.payasia.common.dto.LundinTimesheetDTO;
import com.payasia.common.dto.TimesheetFormPdfDTO;
import com.payasia.common.form.LundinPendingItemsForm;
import com.payasia.common.form.OTPendingTimesheetForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.PendingOTTimesheetForm;
import com.payasia.common.form.PendingOTTimesheetResponseForm;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.logic.LundinPendingTimesheetLogic;
import com.payasia.web.controller.LundinPendingTimesheetController;
import com.payasia.web.util.PayAsiaSessionAttributes;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

@Controller
public class LundinPendingTimesheetControllerImpl implements
		LundinPendingTimesheetController {
	private static final Logger LOGGER = Logger
			.getLogger(LundinPendingTimesheetControllerImpl.class);

	@Resource
	LundinPendingTimesheetLogic lundinPendingTimesheetLogic;

	@Autowired
	MessageSource messageSource;

	@Override
	@RequestMapping(value = "/employee/lundinPendingTimesheet/getPendingTimesheet", method = RequestMethod.POST)
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
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		PendingOTTimesheetResponseForm pendingOTTimesheetResponseForm = lundinPendingTimesheetLogic
				.getPendingTimesheet(empId, pageDTO, sortDTO, searchCondition,
						searchText);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(
				pendingOTTimesheetResponseForm, jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/employee/lundinPendingTimesheet/getDataForTimesheetReview.html", method = RequestMethod.POST)
	@ResponseBody
	public String getDataForTimesheetReview(HttpServletRequest request,
			HttpServletResponse response, HttpSession session) {
	
		long timesheetId = Long.parseLong(request.getParameter("timesheetId"));
		timesheetId  = FormatPreserveCryptoUtil.decrypt(timesheetId);
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		LundinPendingItemsForm pendingTimesheetForm = lundinPendingTimesheetLogic
				.getPendingItemForReview(timesheetId, employeeId, companyId);
		
		if(pendingTimesheetForm!=null) {
			
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(pendingTimesheetForm,
				jsonConfig);
		return jsonObject.toString();
		}
		return "";

	}

	@Override
	@RequestMapping(value = "/employee/lundinPendingTimesheet/forwardTimesheet", method = RequestMethod.POST)
	@ResponseBody
	public String forwardTimesheet(HttpServletRequest request,
			HttpServletResponse response, HttpSession session, Locale locale) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		long timesheetId = Long.parseLong(request.getParameter("timesheetId"));
		/*ID DECRYPT*/
		timesheetId = FormatPreserveCryptoUtil.decrypt(timesheetId);
		String remarks = request.getParameter("remarks");
		String emailCC = request.getParameter("emailCC");
		PendingOTTimesheetForm otTimesheetForm = new PendingOTTimesheetForm();
		otTimesheetForm.setEmailCC(emailCC);
		otTimesheetForm.setOtTimesheetId(timesheetId);
		otTimesheetForm.setRemarks(remarks);
		otTimesheetForm.setOtTimesheetReviewerId(employeeId);
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		otTimesheetForm = lundinPendingTimesheetLogic.forwardTimesheet(
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
	@RequestMapping(value = "/employee/lundinPendingTimesheet/acceptTimesheet", method = RequestMethod.POST)
	@ResponseBody
	public String acceptTimesheet(HttpServletRequest request,
			HttpServletResponse response, HttpSession session, Locale locale) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		long timesheetId = Long.parseLong(request.getParameter("timesheetId"));
		/*ID DECRYPT*/
		 timesheetId = FormatPreserveCryptoUtil.decrypt(timesheetId);
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
	@RequestMapping(value = "/employee/lundinPendingTimesheet/rejectTimesheet", method = RequestMethod.POST)
	@ResponseBody
	public String rejectTimesheet(HttpServletRequest request,
			HttpServletResponse response, HttpSession session) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		long timesheetId = Long.parseLong(request.getParameter("timesheetId"));
		/*ID DECRYPT*/
	    timesheetId = FormatPreserveCryptoUtil.decrypt(timesheetId);
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
	@RequestMapping(value = "/employee/lundinPendingTimesheet/getotPendingTimesheetReviewWorkflow.html", method = RequestMethod.POST)
	@ResponseBody
	public String getotPendingTimesheetReviewWorkflow(
			@RequestParam(value = "otTimesheetId") Long otTimesheetId,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		
		/*ID DECRYPT*/
		otTimesheetId = FormatPreserveCryptoUtil.decrypt(otTimesheetId);
		
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
	@RequestMapping(value = "/employee/lundinPendingTimesheet/printTimesheetDetail.html", method = RequestMethod.GET)
	public @ResponseBody byte[] printTimesheetDetail(
			@RequestParam(value = "timesheetId", required = true) Long timesheetId,
			HttpServletResponse response, HttpServletRequest request) {
		UUID uuid = UUID.randomUUID();
		/*ID DECRYPT*/
        timesheetId = FormatPreserveCryptoUtil.decrypt(timesheetId);
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		LundinTimesheetDTO lundinTimesheet = new LundinTimesheetDTO();
		boolean hasLundinTimesheetModule = (boolean) request
				.getSession()
				.getAttribute(
						PayAsiaSessionAttributes.COMPANY_HAS_LUNDIN_TIMESHEET_MODULE);
		TimesheetFormPdfDTO timesheetFormPdfDTO = lundinPendingTimesheetLogic
				.generateTimesheetPrintPDF(companyId, employeeId, timesheetId,
						hasLundinTimesheetModule, lundinTimesheet);
		if(timesheetFormPdfDTO!=null){
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
  return null;
	}

}
