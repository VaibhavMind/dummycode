package com.payasia.web.controller.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.LeaveApplicationAttachmentDTO;
import com.payasia.common.dto.LeaveApplicationPdfDTO;
import com.payasia.common.dto.LeaveApplicationWorkflowDTO;
import com.payasia.common.dto.LeaveDTO;
import com.payasia.common.dto.LeaveSessionDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.AddLeaveForm;
import com.payasia.common.form.LeaveReviewerResponseForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.PendingItemsForm;
import com.payasia.common.form.PendingItemsFormResponse;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.LeaveApplicationDAO;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.LeaveApplication;
import com.payasia.logic.PendingItemsLogic;
import com.payasia.web.controller.PendingItemsController;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

@Controller
public class PendingItemsControllerImpl implements PendingItemsController {

	/** The Constant logger. */
	private static final Logger LOGGER = Logger
			.getLogger(AddLeaveControllerImpl.class);

	@Resource
	PendingItemsLogic pendingItemsLogic;

	@Autowired
	private MessageSource messageSource;
	
	@Resource
	EmployeeDAO employeeDAO;
	
	@Resource
	LeaveApplicationDAO leaveApplicationDAO;

	@Override
	//@PreAuthorize("hasRole('PRIV_PENDING_ITEMS')")
	@RequestMapping(value = "/employee/pendingItems/getPendingLeaves.html", method = RequestMethod.POST)
	@ResponseBody
	public String getPendingLeaves(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "workflowType", required = true) String workflowType,
			@RequestParam(value = "searchCondition", required = false) String searchCondition,
			@RequestParam(value = "searchText", required = false) String searchText,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows, Locale locale) {
		Long empId = Long.parseLong(UserContext.getUserId());

		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		PendingItemsFormResponse pendingItemsResponse = null;
		pendingItemsResponse = pendingItemsLogic.getPendingLeaves(empId,
				pageDTO, sortDTO, workflowType, searchCondition, searchText);

		List<PendingItemsForm> pendingItemsFormList = pendingItemsResponse
				.getRows();
		for (PendingItemsForm pendingItemsForm : pendingItemsFormList) {
			if (StringUtils.isNotBlank(pendingItemsForm.getStatus())) {
				pendingItemsForm.setStatus(messageSource.getMessage(
						pendingItemsForm.getStatus(), new Object[] {}, locale));
			}
		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(pendingItemsResponse,
				jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/admin/pendingItems/getPendingLeavesForAdmin.html", method = RequestMethod.POST)
	@ResponseBody
	public String getPendingLeavesForAdmin(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "workflowType", required = true) String workflowType,
			@RequestParam(value = "searchCondition", required = false) String searchCondition,
			@RequestParam(value = "searchText", required = false) String searchText,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows, Locale locale) {
		Long empId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		PendingItemsFormResponse pendingItemsResponse = null;
		pendingItemsResponse = pendingItemsLogic.getPendingLeavesForAdmin(
				empId, companyId, pageDTO, sortDTO, workflowType,
				searchCondition, searchText);
		pendingItemsResponse.setPage(1);
		List<PendingItemsForm> pendingItemsFormList = pendingItemsResponse
				.getRows();
		for (PendingItemsForm pendingItemsForm : pendingItemsFormList) {
			if (StringUtils.isNotBlank(pendingItemsForm.getStatus())) {
				pendingItemsForm.setStatus(messageSource.getMessage(
						pendingItemsForm.getStatus(), new Object[] {}, locale));
			}
		}
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(pendingItemsResponse,
				jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/admin/pendingItems/getEmployeesOnLeave.html", method = RequestMethod.POST)
	@ResponseBody
	public String getEmployeesOnLeaves(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "fromDate", required = true) String fromDate,
			@RequestParam(value = "toDate", required = true) String toDate,
			@RequestParam(value = "leaveApplicationId", required = true) Long leaveApplicationId,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows) {
		Long empId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);
		LeaveApplication leaveApplication = leaveApplicationDAO.findLeaveApplicationByCompanyId(leaveApplicationId, companyId);
		if(leaveApplication == null)
		{
			throw new PayAsiaSystemException("Authentication Exception");
		}
		PendingItemsFormResponse pendingItemsResponse = null;
		pendingItemsResponse = pendingItemsLogic.getEmployeesOnLeave(fromDate,
				toDate, leaveApplicationId, empId, companyId, pageDTO, sortDTO);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(pendingItemsResponse,
				jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/admin/pendingItems/viewLeaveTransactions.html", method = RequestMethod.POST)
	@ResponseBody
	public String viewLeaveTransactions(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "createdById", required = true) Long createdById,
			@RequestParam(value = "leaveApplicationId", required = true) Long leaveApplicationId,
			@RequestParam(value = "startDate", required = false) String startDate,
			@RequestParam(value = "endDate", required = false) String endDate,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows) {
		Long empId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		/* ID DECRYPT */
		leaveApplicationId = FormatPreserveCryptoUtil.decrypt(leaveApplicationId);
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);
		LeaveApplication leaveApplication = leaveApplicationDAO.findLeaveApplicationByCompanyId(leaveApplicationId, companyId);
		if(leaveApplication == null)
		{
			throw new PayAsiaSystemException("Authentication Exception");
		}
		PendingItemsFormResponse pendingItemsResponse = null;
		pendingItemsResponse = pendingItemsLogic.getLeaveTransactions(
				createdById, empId, pageDTO, sortDTO, companyId,
				leaveApplicationId, startDate, endDate);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(pendingItemsResponse,
				jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/admin/pendingItems/getDataForLeaveReview.html", method = RequestMethod.POST)
	@ResponseBody
	public String getDataForLeaveReview(
			@RequestParam(value = "reviewId") Long reviewId, Locale locale) {
		/* ID DECRYPT */
		reviewId = FormatPreserveCryptoUtil.decrypt(reviewId);
		PendingItemsForm pendingItemsForm = pendingItemsLogic
				.getDataForLeaveReview(reviewId);

		AddLeaveForm addLeaveForm = pendingItemsForm.getAddLeaveForm();

		if (StringUtils.isNotBlank(addLeaveForm.getLeaveAppStatus())) {
			addLeaveForm.setLeaveAppStatus(messageSource.getMessage(
					addLeaveForm.getLeaveAppStatus(), new Object[] {}, locale));
		}
		if (StringUtils.isNotBlank(addLeaveForm.getStatus())) {
			addLeaveForm.setStatus(messageSource.getMessage(
					addLeaveForm.getStatus(), new Object[] {}, locale));
		}
		if (StringUtils.isNotBlank(addLeaveForm.getFromSessionLabelKey())) {
			addLeaveForm.setFromSession(messageSource.getMessage(
					addLeaveForm.getFromSessionLabelKey(), new Object[] {},
					locale));
		}
		if (StringUtils.isNotBlank(addLeaveForm.getToSessionLabelKey())) {
			addLeaveForm.setToSession(messageSource.getMessage(
					addLeaveForm.getToSessionLabelKey(), new Object[] {},
					locale));
		}

		List<LeaveApplicationWorkflowDTO> leaveApplicationWorkflowDTOs = addLeaveForm
				.getWorkflowList();
		for (LeaveApplicationWorkflowDTO leaveApplicationWorkflowDTO : leaveApplicationWorkflowDTOs) {
			if (StringUtils.isNotBlank(leaveApplicationWorkflowDTO.getStatus())) {
				leaveApplicationWorkflowDTO.setStatus(messageSource.getMessage(
						leaveApplicationWorkflowDTO.getStatus(),
						new Object[] {}, locale));
			}
		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(pendingItemsForm,
				jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	//@PreAuthorize("hasRole('PRIV_PENDING_ITEMS')")
	@RequestMapping(value = "/employee/pendingItems/acceptLeave.html", method = RequestMethod.POST)
	@ResponseBody
	public String acceptLeave(
			@ModelAttribute(value = "pendingItemsForm") PendingItemsForm pendingItemsForm ,Locale locale) {
		/* ID DECRYPT */
		pendingItemsForm.setLeaveApplicationReviewerId(FormatPreserveCryptoUtil.decrypt(pendingItemsForm.getLeaveApplicationReviewerId()));
		pendingItemsForm.setLeaveApplicationId(FormatPreserveCryptoUtil.decrypt(pendingItemsForm.getLeaveApplicationId()));
		pendingItemsForm.setForwardToId(FormatPreserveCryptoUtil.decrypt(pendingItemsForm.getForwardToId()));
		
		Long employeeId = Long.parseLong(UserContext.getUserId());
		AddLeaveForm addLeaveFormRes = new AddLeaveForm();
		LeaveSessionDTO sessionDTO = new LeaveSessionDTO();
		sessionDTO.setFromSessionName(messageSource.getMessage(
				PayAsiaConstants.LEAVE_SESSION_MESSAGE_FROMLABEL_KEY,
				new Object[] {}, locale));

		sessionDTO.setToSessionName(messageSource.getMessage(
				PayAsiaConstants.LEAVE_SESSION_MESSAGE_TOLABEL_KEY,
				new Object[] {}, locale));

		try {
			addLeaveFormRes = pendingItemsLogic.acceptLeave(pendingItemsForm,
					employeeId, sessionDTO);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			LeaveDTO leaveDTO = new LeaveDTO();
			leaveDTO.setErrorCode(1);
			leaveDTO.setErrorKey("payasia.leave.accept.error");
			leaveDTO.setErrorValue(" ;");
			addLeaveFormRes.setLeaveDTO(leaveDTO);
		}
		String[] errorValArr = null;
		String[] errorVal = null;
		StringBuilder errorKeyFinalStr = new StringBuilder();
		String[] errorKeyArr;
		if (addLeaveFormRes.getLeaveDTO() != null
				&& addLeaveFormRes.getLeaveDTO().getErrorCode() == 1) {
			if (StringUtils.isNotBlank(addLeaveFormRes.getLeaveDTO()
					.getErrorKey())) {
				errorKeyArr = addLeaveFormRes.getLeaveDTO().getErrorKey()
						.split(";");
				if (StringUtils.isNotBlank(addLeaveFormRes.getLeaveDTO()
						.getErrorValue())) {
					errorValArr = addLeaveFormRes.getLeaveDTO().getErrorValue()
							.split(";");
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
			addLeaveFormRes.getLeaveDTO().setErrorKey(
					errorKeyFinalStr.toString());
		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(addLeaveFormRes,
				jsonConfig);
		return jsonObject.toString();

	}

	@Override
	@RequestMapping(value = "/admin/pendingItems/forwardLeave.html", method = RequestMethod.POST)
	@ResponseBody
	public String forwardLeave(
			@ModelAttribute(value = "pendingItemsForm") PendingItemsForm pendingItemsForm, Locale locale) {
		/* ID DECRYPT */
		pendingItemsForm.setLeaveApplicationReviewerId(FormatPreserveCryptoUtil.decrypt(pendingItemsForm.getLeaveApplicationReviewerId()));
		pendingItemsForm.setLeaveApplicationId(FormatPreserveCryptoUtil.decrypt(pendingItemsForm.getLeaveApplicationId()));
		pendingItemsForm.setForwardToId(FormatPreserveCryptoUtil.decrypt(pendingItemsForm.getForwardToId()));
		
		Long employeeId = Long.parseLong(UserContext.getUserId());
		LeaveSessionDTO sessionDTO = new LeaveSessionDTO();
		sessionDTO.setFromSessionName(messageSource.getMessage(
				PayAsiaConstants.LEAVE_SESSION_MESSAGE_FROMLABEL_KEY,
				new Object[] {}, locale));

		sessionDTO.setToSessionName(messageSource.getMessage(
				PayAsiaConstants.LEAVE_SESSION_MESSAGE_TOLABEL_KEY,
				new Object[] {}, locale));
		AddLeaveForm addLeaveFormRes = new AddLeaveForm();
		try {
			addLeaveFormRes = pendingItemsLogic.forwardLeave(pendingItemsForm,
					employeeId, sessionDTO);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			LeaveDTO leaveDTO = new LeaveDTO();
			leaveDTO.setErrorCode(1);
			leaveDTO.setErrorKey("payasia.leave.accept.error");
			leaveDTO.setErrorValue(" ;");
			addLeaveFormRes.setLeaveDTO(leaveDTO);
		}

		String[] errorValArr = null;
		String[] errorVal = null;
		StringBuilder errorKeyFinalStr = new StringBuilder();
		String[] errorKeyArr;
		if (addLeaveFormRes.getLeaveDTO() != null
				&& addLeaveFormRes.getLeaveDTO().getErrorCode() == 1) {
			if (StringUtils.isNotBlank(addLeaveFormRes.getLeaveDTO()
					.getErrorKey())) {
				errorKeyArr = addLeaveFormRes.getLeaveDTO().getErrorKey()
						.split(";");
				if (StringUtils.isNotBlank(addLeaveFormRes.getLeaveDTO()
						.getErrorValue())) {
					errorValArr = addLeaveFormRes.getLeaveDTO().getErrorValue()
							.split(";");
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
			addLeaveFormRes.getLeaveDTO().setErrorKey(
					errorKeyFinalStr.toString());
		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(addLeaveFormRes,
				jsonConfig);
		return jsonObject.toString();

	}

	@Override
	//@PreAuthorize("hasRole('PRIV_PENDING_ITEMS')")
	@RequestMapping(value = "/employee/pendingItems/rejectLeave.html", method = RequestMethod.POST)
	@ResponseBody
	public String rejectLeave(
			@ModelAttribute(value = "pendingItemsForm") PendingItemsForm pendingItemsForm,
			 Locale locale) {
		/* ID DECRYPT */
		pendingItemsForm.setLeaveApplicationReviewerId(FormatPreserveCryptoUtil.decrypt(pendingItemsForm.getLeaveApplicationReviewerId()));
		pendingItemsForm.setLeaveApplicationId(FormatPreserveCryptoUtil.decrypt(pendingItemsForm.getLeaveApplicationId()));
		pendingItemsForm.setForwardToId(FormatPreserveCryptoUtil.decrypt(pendingItemsForm.getForwardToId()));
		
		Long employeeId = Long.parseLong(UserContext.getUserId());
		String status = "true";
		LeaveSessionDTO sessionDTO = new LeaveSessionDTO();
		sessionDTO.setFromSessionName(messageSource.getMessage(
				PayAsiaConstants.LEAVE_SESSION_MESSAGE_FROMLABEL_KEY,
				new Object[] {}, locale));

		sessionDTO.setToSessionName(messageSource.getMessage(
				PayAsiaConstants.LEAVE_SESSION_MESSAGE_TOLABEL_KEY,
				new Object[] {}, locale));
		try {
			status = pendingItemsLogic.rejectLeave(pendingItemsForm,
					employeeId, sessionDTO);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			status = "false";
		}

		return status;

	}

	@Override
	@RequestMapping(value = "/admin/pendingItems/rejectLeaveForAdmin.html", method = RequestMethod.POST)
	@ResponseBody
	public String rejectLeaveForAdmin(
			@ModelAttribute(value = "pendingItemsForm") PendingItemsForm pendingItemsForm, Locale locale) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		/* ID DECRYPT */
		pendingItemsForm.setLeaveApplicationReviewerId(FormatPreserveCryptoUtil.decrypt(pendingItemsForm.getLeaveApplicationReviewerId()));
		pendingItemsForm.setLeaveApplicationId(FormatPreserveCryptoUtil.decrypt(pendingItemsForm.getLeaveApplicationId()));
		pendingItemsForm.setForwardToId(FormatPreserveCryptoUtil.decrypt(pendingItemsForm.getForwardToId()));
		
		AddLeaveForm addLeaveFormRes = new AddLeaveForm();
		LeaveSessionDTO sessionDTO = new LeaveSessionDTO();
		sessionDTO.setFromSessionName(messageSource.getMessage(
				PayAsiaConstants.LEAVE_SESSION_MESSAGE_FROMLABEL_KEY,
				new Object[] {}, locale));

		sessionDTO.setToSessionName(messageSource.getMessage(
				PayAsiaConstants.LEAVE_SESSION_MESSAGE_TOLABEL_KEY,
				new Object[] {}, locale));
		try {
			addLeaveFormRes = pendingItemsLogic.rejectLeaveForAdmin(
					pendingItemsForm, employeeId, sessionDTO);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			LeaveDTO leaveDTO = new LeaveDTO();
			leaveDTO.setErrorCode(1);
			leaveDTO.setErrorKey("payasia.leave.reject.error");
			leaveDTO.setErrorValue(" ;");
			addLeaveFormRes.setLeaveDTO(leaveDTO);
		}

		String[] errorValArr = null;
		String[] errorVal = null;
		StringBuilder errorKeyFinalStr = new StringBuilder();
		String[] errorKeyArr;
		if (addLeaveFormRes.getLeaveDTO() != null
				&& addLeaveFormRes.getLeaveDTO().getErrorCode() == 1) {
			if (StringUtils.isNotBlank(addLeaveFormRes.getLeaveDTO()
					.getErrorKey())) {
				errorKeyArr = addLeaveFormRes.getLeaveDTO().getErrorKey()
						.split(";");
				if (StringUtils.isNotBlank(addLeaveFormRes.getLeaveDTO()
						.getErrorValue())) {
					errorValArr = addLeaveFormRes.getLeaveDTO().getErrorValue()
							.split(";");
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
			addLeaveFormRes.getLeaveDTO().setErrorKey(
					errorKeyFinalStr.toString());
		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(addLeaveFormRes,
				jsonConfig);
		return jsonObject.toString();

	}

	@Override
	@RequestMapping(value = "/admin/pendingItems/acceptLeaveforAdmin.html", method = RequestMethod.POST)
	@ResponseBody
	public String acceptLeaveforAdmin(
			@ModelAttribute(value = "pendingItemsForm") PendingItemsForm pendingItemsForm,
			 Locale locale) {
		/* ID DECRYPT */
		pendingItemsForm.setLeaveApplicationReviewerId(FormatPreserveCryptoUtil.decrypt(pendingItemsForm.getLeaveApplicationReviewerId()));
		pendingItemsForm.setLeaveApplicationId(FormatPreserveCryptoUtil.decrypt(pendingItemsForm.getLeaveApplicationId()));
		pendingItemsForm.setForwardToId(FormatPreserveCryptoUtil.decrypt(pendingItemsForm.getForwardToId()));
		
		Long employeeId = Long.parseLong(UserContext.getUserId());
		AddLeaveForm addLeaveFormRes = new AddLeaveForm();
		LeaveSessionDTO sessionDTO = new LeaveSessionDTO();
		sessionDTO.setFromSessionName(messageSource.getMessage(
				PayAsiaConstants.LEAVE_SESSION_MESSAGE_FROMLABEL_KEY,
				new Object[] {}, locale));

		sessionDTO.setToSessionName(messageSource.getMessage(
				PayAsiaConstants.LEAVE_SESSION_MESSAGE_TOLABEL_KEY,
				new Object[] {}, locale));
		try {
			addLeaveFormRes = pendingItemsLogic.acceptLeaveforAdmin(
					pendingItemsForm, employeeId, sessionDTO);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			LeaveDTO leaveDTO = new LeaveDTO();
			leaveDTO.setErrorCode(1);
			leaveDTO.setErrorKey("payasia.leave.accept.error");
			leaveDTO.setErrorValue(" ;");
			addLeaveFormRes.setLeaveDTO(leaveDTO);
		}

		String[] errorValArr = null;
		String[] errorVal = null;
		StringBuilder errorKeyFinalStr = new StringBuilder();
		String[] errorKeyArr;
		if (addLeaveFormRes.getLeaveDTO() != null
				&& addLeaveFormRes.getLeaveDTO().getErrorCode() == 1) {
			if (StringUtils.isNotBlank(addLeaveFormRes.getLeaveDTO()
					.getErrorKey())) {
				errorKeyArr = addLeaveFormRes.getLeaveDTO().getErrorKey()
						.split(";");
				if (StringUtils.isNotBlank(addLeaveFormRes.getLeaveDTO()
						.getErrorValue())) {
					errorValArr = addLeaveFormRes.getLeaveDTO().getErrorValue()
							.split(";");
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
			addLeaveFormRes.getLeaveDTO().setErrorKey(
					errorKeyFinalStr.toString());
		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(addLeaveFormRes,
				jsonConfig);
		return jsonObject.toString();

	}

	@Override
	@RequestMapping(value = "/admin/pendingItems/printLeaveApplicationForm.html", method = RequestMethod.GET)
	public @ResponseBody byte[] printLeaveApplicationForm(
			@RequestParam(value = "leaveApplicationReviewerId", required = true) Long leaveApplicationReviewerId,
			HttpServletResponse response) {
		/* ID DECRYPT */
		leaveApplicationReviewerId=FormatPreserveCryptoUtil.decrypt(leaveApplicationReviewerId);
		
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		Long employeeId = Long.parseLong(UserContext.getUserId());
		LeaveApplicationPdfDTO leaveAppPdfDTO = pendingItemsLogic
				.generateLeaveApplicationPrintPDF(companyId, employeeId,
						leaveApplicationReviewerId);
		response.reset();
		String mimeType = URLConnection.guessContentTypeFromName(leaveAppPdfDTO
				.getEmployeeNumber()
				+ "_"
				+ leaveAppPdfDTO.getLeaveSchemeName() + ".pdf");
		response.setContentType("application/" + mimeType);
		response.setContentLength(leaveAppPdfDTO.getLeaveAppPdfByteFile().length);
		String filename = leaveAppPdfDTO.getEmployeeNumber() + "_"
				+ leaveAppPdfDTO.getLeaveSchemeName() + ".pdf";

		response.setHeader("Content-Disposition", "attachment;filename="
				+ filename);

		return leaveAppPdfDTO.getLeaveAppPdfByteFile();
	}

	@Override
	@RequestMapping(value = "/admin/pendingItems/viewMultipleLeaveApplications.html", method = RequestMethod.POST)
	@ResponseBody
	public String viewMultipleLeaveApplications(
			@RequestParam(value = "leaveApplicationRevIds", required = true) String[] leaveApplicationRevIds,
			Locale locale) {
				 
		for(int i =0 ;i<leaveApplicationRevIds.length; i++)
		{
			leaveApplicationRevIds[i]= String.valueOf(FormatPreserveCryptoUtil.decrypt(Long
					.parseLong(leaveApplicationRevIds[i])));
		}
				
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		Long employeeId = Long.parseLong(UserContext.getUserId());

		UserContext.setLocale(locale);
		PendingItemsFormResponse pendingItemsFormResponse = pendingItemsLogic
				.viewMultipleLeaveApplications(companyId, employeeId,
						leaveApplicationRevIds);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(pendingItemsFormResponse,
				jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/admin/pendingItems/showEmpLeaveWorkflowStatus.html", method = RequestMethod.POST)
	@ResponseBody
	public String showEmpLeaveWorkflowStatus(
			@RequestParam(value = "leaveApplicationId", required = true) Long leaveApplicationId,
			Locale locale) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		Long employeeId = Long.parseLong(UserContext.getUserId());
		
		LeaveApplication leaveApplication = leaveApplicationDAO.findLeaveApplicationByCompanyId(leaveApplicationId,companyId);
		if(leaveApplication == null)
		{
			throw new PayAsiaSystemException("Authentication Exception");
		}
		PendingItemsForm pendingItemResponse = pendingItemsLogic
				.showEmpLeaveWorkflowStatus(companyId, employeeId,leaveApplicationId);

		AddLeaveForm addLeaveForm = pendingItemResponse.getAddLeaveForm();
		if (StringUtils.isNotBlank(addLeaveForm.getLeaveAppStatus())) {
			addLeaveForm.setLeaveAppStatus(messageSource.getMessage(
					addLeaveForm.getLeaveAppStatus(), new Object[] {}, locale));
		}
		if (StringUtils.isNotBlank(addLeaveForm.getStatus())) {
			addLeaveForm.setStatus(messageSource.getMessage(
					addLeaveForm.getStatus(), new Object[] {}, locale));
		}
		List<LeaveApplicationWorkflowDTO> leaveApplicationWorkflowDTOs = addLeaveForm
				.getWorkflowList();
		for (LeaveApplicationWorkflowDTO leaveApplicationWorkflowDTO : leaveApplicationWorkflowDTOs) {
			if (StringUtils.isNotBlank(leaveApplicationWorkflowDTO.getStatus())) {
				leaveApplicationWorkflowDTO.setStatus(messageSource.getMessage(
						leaveApplicationWorkflowDTO.getStatus(),
						new Object[] {}, locale));
			}
		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(pendingItemResponse,
				jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;

	}

	@Override
	@RequestMapping(value = "/admin/pendingItems/reviewMultipleLeaveAppByAdmin.html", method = RequestMethod.POST)
	@ResponseBody
	public String reviewMultipleLeaveAppByAdmin(
			@ModelAttribute(value = "pendingItemsFrm") PendingItemsForm pendingItemsFrm,
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

		List<AddLeaveForm> addLeaveFormRes = pendingItemsLogic
				.reviewMultipleLeaveAppByAdmin(pendingItemsFrm, employeeId,
						companyId, sessionDTO);
		for (AddLeaveForm addLeaveForm : addLeaveFormRes) {
			String[] errorValArr = null;
			String[] errorVal = null;
			StringBuilder errorKeyFinalStr = new StringBuilder();
			String[] errorKeyArr;
			if (addLeaveForm.getLeaveDTO() != null
					&& addLeaveForm.getLeaveDTO().getErrorCode() == 1) {
				if (StringUtils.isNotBlank(addLeaveForm.getLeaveDTO()
						.getErrorKey())) {
					errorKeyArr = addLeaveForm.getLeaveDTO().getErrorKey()
							.split(";");
					if (StringUtils.isNotBlank(addLeaveForm.getLeaveDTO()
							.getErrorValue())) {
						errorValArr = addLeaveForm.getLeaveDTO()
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
				addLeaveForm.getLeaveDTO().setErrorKey(
						errorKeyFinalStr.toString());
			}
		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray
				.fromObject(addLeaveFormRes, jsonConfig);
		return jsonObject.toString();

	}

	@Override
	//@PreAuthorize("hasRole('PRIV_PENDING_ITEMS')")
	@RequestMapping(value = "/employee/pendingItems/reviewMultipleLeaveApp.html", method = RequestMethod.POST)
	@ResponseBody
	public String reviewMultipleLeaveApp(
			@ModelAttribute(value = "pendingItemsFrm") PendingItemsForm pendingItemsFrm,
			 Locale locale) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		LeaveSessionDTO sessionDTO = new LeaveSessionDTO();
		sessionDTO.setFromSessionName(messageSource.getMessage(
				PayAsiaConstants.LEAVE_SESSION_MESSAGE_FROMLABEL_KEY,
				new Object[] {}, locale));

		sessionDTO.setToSessionName(messageSource.getMessage(
				PayAsiaConstants.LEAVE_SESSION_MESSAGE_TOLABEL_KEY,
				new Object[] {}, locale));

		List<AddLeaveForm> addLeaveFormRes = pendingItemsLogic
				.reviewMultipleLeaveApp(pendingItemsFrm, employeeId, companyId,
						sessionDTO);
		for (AddLeaveForm addLeaveForm : addLeaveFormRes) {
			String[] errorValArr = null;
			String[] errorVal = null;
			StringBuilder errorKeyFinalStr = new StringBuilder();
			String[] errorKeyArr;
			if (addLeaveForm.getLeaveDTO() != null
					&& addLeaveForm.getLeaveDTO().getErrorCode() == 1) {
				if (StringUtils.isNotBlank(addLeaveForm.getLeaveDTO()
						.getErrorKey())) {
					errorKeyArr = addLeaveForm.getLeaveDTO().getErrorKey()
							.split(";");
					if (StringUtils.isNotBlank(addLeaveForm.getLeaveDTO()
							.getErrorValue())) {
						errorValArr = addLeaveForm.getLeaveDTO()
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
				addLeaveForm.getLeaveDTO().setErrorKey(
						errorKeyFinalStr.toString());
			}
		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray
				.fromObject(addLeaveFormRes, jsonConfig);
		return jsonObject.toString();

	}

	@Override
	//@PreAuthorize("hasRole('PRIV_PENDING_ITEMS')")
	@RequestMapping(value = "/employee/pendingItems/getDataForLeaveReview.html", method = RequestMethod.POST)
	@ResponseBody
	public String getDataForLeaveReviewEmp(
			@RequestParam(value = "reviewId") Long reviewId, Locale locale) {
		/* ID DECRYPT */
		reviewId= FormatPreserveCryptoUtil.decrypt(reviewId);
		
		Long employeeId = Long.parseLong(UserContext.getUserId());
		PendingItemsForm pendingItemsForm = pendingItemsLogic
				.getDataForLeaveReviewEmp(reviewId, employeeId);

		if (pendingItemsForm == null) {

			return null;
		}

		LeaveSessionDTO sessionDTO = new LeaveSessionDTO();
		AddLeaveForm addLeaveForm = pendingItemsForm.getAddLeaveForm();

		if (StringUtils.isNotBlank(addLeaveForm.getLeaveAppStatus())) {
			addLeaveForm.setLeaveAppStatus(messageSource.getMessage(
					addLeaveForm.getLeaveAppStatus(), new Object[] {}, locale));
		}
		if (StringUtils.isNotBlank(addLeaveForm.getStatus())) {
			addLeaveForm.setStatus(messageSource.getMessage(
					addLeaveForm.getStatus(), new Object[] {}, locale));
		}
		if (StringUtils.isNotBlank(addLeaveForm.getFromSessionLabelKey())) {
			addLeaveForm.setFromSession(messageSource.getMessage(
					addLeaveForm.getFromSessionLabelKey(), new Object[] {},
					locale));
		}
		if (StringUtils.isNotBlank(addLeaveForm.getToSessionLabelKey())) {
			addLeaveForm.setToSession(messageSource.getMessage(
					addLeaveForm.getToSessionLabelKey(), new Object[] {},
					locale));
		}

		List<LeaveApplicationWorkflowDTO> leaveApplicationWorkflowDTOs = addLeaveForm
				.getWorkflowList();
		for (LeaveApplicationWorkflowDTO leaveApplicationWorkflowDTO : leaveApplicationWorkflowDTOs) {
			if (StringUtils.isNotBlank(leaveApplicationWorkflowDTO.getStatus())) {
				leaveApplicationWorkflowDTO.setStatus(messageSource.getMessage(
						leaveApplicationWorkflowDTO.getStatus(),
						new Object[] {}, locale));
			}
		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(pendingItemsForm,
				jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}
	
	@Override
	//@PreAuthorize("hasRole('PRIV_PENDING_ITEMS')")
	@RequestMapping(value = "/employee/pendingItems/viewAttachmentRev.html", method = RequestMethod.GET)
	public @ResponseBody byte[] viewAttachmentRev(@RequestParam(value = "attachmentId", required = true) long attachmentId,
			HttpServletResponse response) {

		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		LeaveApplicationAttachmentDTO attachmentDTO = pendingItemsLogic.viewAttachmentByReviewer(attachmentId,employeeId,companyId);
		byte[] byteFile = attachmentDTO.getAttachmentBytes();

		response.reset();
		String mimeType = URLConnection.guessContentTypeFromName(attachmentDTO.getFileName());
		response.setContentType("application/" + mimeType);
		response.setContentLength(byteFile.length);
		String filename = attachmentDTO.getFileName();

		response.setHeader("Content-Disposition", "attachment;filename=" + filename);

		return byteFile;
	}
	
	@Override
	//@PreAuthorize("hasRole('PRIV_PENDING_ITEMS')")
	@RequestMapping(value = "/employee/pendingItems/searchEmployee.html", method = RequestMethod.POST)
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

		LeaveReviewerResponseForm leaveReviewerResponse = pendingItemsLogic.searchEmployee(pageDTO, sortDTO, employeeId,
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
	//@PreAuthorize("hasRole('PRIV_PENDING_ITEMS')")
	@RequestMapping(value = "/employee/pendingItems/getEmployeesOnLeave.html", method = RequestMethod.POST)
	@ResponseBody
	public String getEmployeesOnLeavesEmp(@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "fromDate", required = true) String fromDate,
			@RequestParam(value = "toDate", required = true) String toDate,
			@RequestParam(value = "leaveApplicationId", required = true) Long leaveApplicationId,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows) {
		
		Long empId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		/* ID DECRYPT */
		leaveApplicationId= FormatPreserveCryptoUtil.decrypt(leaveApplicationId);
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);
		
		Employee employeeReviewer= employeeDAO.findEmpByIdinReviewers(empId, leaveApplicationId);
		
		if(employeeReviewer ==  null)
		{
			throw new PayAsiaSystemException("Authentication Exception");
		}
		PendingItemsFormResponse pendingItemsResponse = null;
		pendingItemsResponse = pendingItemsLogic.getEmployeesOnLeave(fromDate, toDate, leaveApplicationId, empId,
				companyId, pageDTO, sortDTO);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(pendingItemsResponse, jsonConfig);
		return jsonObject.toString();
	}

	@Override
	//@PreAuthorize("hasRole('PRIV_PENDING_ITEMS')")
	@RequestMapping(value = "/employee/pendingItems/viewLeaveTransactions.html", method = RequestMethod.POST)
	@ResponseBody
	public String viewLeaveTransactionsEmp(@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "createdById", required = true) Long createdById,
			@RequestParam(value = "leaveApplicationId", required = true) Long leaveApplicationId,
			@RequestParam(value = "startDate", required = false) String startDate,
			@RequestParam(value = "endDate", required = false) String endDate,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows) {
		
		Long empId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		leaveApplicationId = FormatPreserveCryptoUtil.decrypt(leaveApplicationId);
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);
		Employee employeeReviewer= employeeDAO.findEmpByIdinReviewers(empId, leaveApplicationId);
		
		if(employeeReviewer ==  null)
		{
			throw new PayAsiaSystemException("Authentication Exception");
		}

		PendingItemsFormResponse pendingItemsResponse = null;
		pendingItemsResponse = pendingItemsLogic.getLeaveTransactions(createdById, empId, pageDTO, sortDTO, companyId,
				leaveApplicationId, startDate, endDate);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(pendingItemsResponse, jsonConfig);
		return jsonObject.toString();
	}
	
	@Override
	//@PreAuthorize("hasRole('PRIV_PENDING_ITEMS')")
	@RequestMapping(value = "/employee/pendingItems/forwardLeave.html", method = RequestMethod.POST)
	@ResponseBody
	public String forwardLeaveEmp(@ModelAttribute(value = "pendingItemsForm") PendingItemsForm pendingItemsForm,
			 Locale locale) {
		/* ID DECRYPT */
		pendingItemsForm.setLeaveApplicationReviewerId(FormatPreserveCryptoUtil.decrypt(pendingItemsForm.getLeaveApplicationReviewerId()));
		pendingItemsForm.setLeaveApplicationId(FormatPreserveCryptoUtil.decrypt(pendingItemsForm.getLeaveApplicationId()));
		Long employeeId = Long.parseLong(UserContext.getUserId());
		LeaveSessionDTO sessionDTO = new LeaveSessionDTO();
		sessionDTO.setFromSessionName(messageSource.getMessage(PayAsiaConstants.LEAVE_SESSION_MESSAGE_FROMLABEL_KEY,
				new Object[] {}, locale));

		sessionDTO.setToSessionName(
				messageSource.getMessage(PayAsiaConstants.LEAVE_SESSION_MESSAGE_TOLABEL_KEY, new Object[] {}, locale));
		AddLeaveForm addLeaveFormRes = new AddLeaveForm();
		try {
			addLeaveFormRes = pendingItemsLogic.forwardLeave(pendingItemsForm, employeeId, sessionDTO);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			LeaveDTO leaveDTO = new LeaveDTO();
			leaveDTO.setErrorCode(1);
			leaveDTO.setErrorKey("payasia.leave.accept.error");
			leaveDTO.setErrorValue(" ;");
			addLeaveFormRes.setLeaveDTO(leaveDTO);
		}

		String[] errorValArr = null;
		String[] errorVal = null;
		StringBuilder errorKeyFinalStr = new StringBuilder();
		String[] errorKeyArr;
		if (addLeaveFormRes.getLeaveDTO() != null && addLeaveFormRes.getLeaveDTO().getErrorCode() == 1) {
			if (StringUtils.isNotBlank(addLeaveFormRes.getLeaveDTO().getErrorKey())) {
				errorKeyArr = addLeaveFormRes.getLeaveDTO().getErrorKey().split(";");
				if (StringUtils.isNotBlank(addLeaveFormRes.getLeaveDTO().getErrorValue())) {
					errorValArr = addLeaveFormRes.getLeaveDTO().getErrorValue().split(";");
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
			addLeaveFormRes.getLeaveDTO().setErrorKey(errorKeyFinalStr.toString());
		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(addLeaveFormRes, jsonConfig);
		return jsonObject.toString();

	}
	
	@Override
	//@PreAuthorize("hasRole('PRIV_PENDING_ITEMS')")
	@RequestMapping(value = "/employee/pendingItems/printLeaveApplicationForm.html", method = RequestMethod.GET)
	public @ResponseBody byte[] printLeaveApplicationFormEmp(
			@RequestParam(value = "leaveApplicationReviewerId", required = true) Long leaveApplicationReviewerId,
			HttpServletResponse response) {
		/* ID DECRYPT */
		leaveApplicationReviewerId = FormatPreserveCryptoUtil.decrypt(leaveApplicationReviewerId);
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		LeaveApplicationPdfDTO leaveAppPdfDTO = pendingItemsLogic.generateLeaveApplicationPrintPDF(companyId,
				employeeId, leaveApplicationReviewerId);

		response.reset();
		String mimeType = URLConnection.guessContentTypeFromName(
				leaveAppPdfDTO.getEmployeeNumber() + "_" + leaveAppPdfDTO.getLeaveSchemeName() + ".pdf");
		response.setContentType("application/" + mimeType);
		response.setContentLength(leaveAppPdfDTO.getLeaveAppPdfByteFile().length);
		String filename = leaveAppPdfDTO.getEmployeeNumber() + "_" + leaveAppPdfDTO.getLeaveSchemeName() + ".pdf";

		response.setHeader("Content-Disposition", "attachment;filename=" + filename);

		return leaveAppPdfDTO.getLeaveAppPdfByteFile();
	}
	
	//To Do
	@Override
	@RequestMapping(value = "/employee/pendingItems/viewMultipleLeaveApplications.html", method = RequestMethod.POST)
	@ResponseBody
	public String viewMultipleLeaveApplicationsEmp(
			@RequestParam(value = "leaveApplicationRevIds", required = true) String[] leaveApplicationRevIds, Locale locale) {
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		for(int i =0 ;i<leaveApplicationRevIds.length; i++)
		{
			leaveApplicationRevIds[i]= String.valueOf(FormatPreserveCryptoUtil.decrypt(Long
					.parseLong(leaveApplicationRevIds[i])));
		}
		UserContext.setLocale(locale);
		PendingItemsFormResponse pendingItemsFormResponse = pendingItemsLogic.viewMultipleLeaveApplications(companyId,
				employeeId, leaveApplicationRevIds);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(pendingItemsFormResponse, jsonConfig);
		return jsonObject.toString();
	}
	
	@Override
	//@PreAuthorize("hasRole('PRIV_PENDING_ITEMS')")
	@RequestMapping(value = "/employee/pendingItems/showEmpLeaveWorkflowStatus.html", method = RequestMethod.POST)
	@ResponseBody
	public String showEmpLeaveWorkflowStatusEmp(
			@RequestParam(value = "leaveApplicationId", required = true) Long leaveApplicationId,
			 Locale locale) {
		
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
        Employee employeeReviewer= employeeDAO.findEmpByIdinReviewers(employeeId, leaveApplicationId);
		
		if(employeeReviewer ==  null)
		{
			throw new PayAsiaSystemException("Authentication Exception");
		}
		
		PendingItemsForm pendingItemResponse = pendingItemsLogic.showEmpLeaveWorkflowStatus(companyId,employeeId,
				leaveApplicationId);

		AddLeaveForm addLeaveForm = pendingItemResponse.getAddLeaveForm();
		if (StringUtils.isNotBlank(addLeaveForm.getLeaveAppStatus())) {
			addLeaveForm.setLeaveAppStatus(
					messageSource.getMessage(addLeaveForm.getLeaveAppStatus(), new Object[] {}, locale));
		}
		if (StringUtils.isNotBlank(addLeaveForm.getStatus())) {
			addLeaveForm.setStatus(messageSource.getMessage(addLeaveForm.getStatus(), new Object[] {}, locale));
		}
		List<LeaveApplicationWorkflowDTO> leaveApplicationWorkflowDTOs = addLeaveForm.getWorkflowList();
		for (LeaveApplicationWorkflowDTO leaveApplicationWorkflowDTO : leaveApplicationWorkflowDTOs) {
			if (StringUtils.isNotBlank(leaveApplicationWorkflowDTO.getStatus())) {
				leaveApplicationWorkflowDTO.setStatus(
						messageSource.getMessage(leaveApplicationWorkflowDTO.getStatus(), new Object[] {}, locale));
			}
		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(pendingItemResponse, jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;

	}

}
