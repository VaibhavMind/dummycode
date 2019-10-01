package com.payasia.web.controller.claim.impl;

import java.net.URLConnection;
import java.util.Locale;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.payasia.common.dto.AddClaimDTO;
import com.payasia.common.dto.ClaimFormPdfDTO;
import com.payasia.common.form.ClaimApplicationItemAttach;
import com.payasia.common.form.ClaimApplicationItemForm;
import com.payasia.common.form.ClaimApplicationItemWorkflowForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.PendingClaimsForm;
import com.payasia.common.form.PendingClaimsResponseForm;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaURLConstant;
import com.payasia.common.util.ResponseDataConverter;
import com.payasia.logic.PendingClaimsLogic;
import com.payasia.web.controller.claim.PendingClaimsController;
import com.payasia.web.util.PayAsiaSessionAttributes;

@Controller
@RequestMapping(value = PayAsiaURLConstant.EMPLOYEE_PENDING_CLAIM)
public class PendingClaimsControllerImpl implements PendingClaimsController {
	
	@Resource
	PendingClaimsLogic pendingClaimsLogic;

	@Autowired
	MessageSource messageSource;

	@Override
	@RequestMapping(value = PayAsiaURLConstant.GET_PENDING_CLAIMS, method = RequestMethod.POST)
	@ResponseBody
	public String getPendingClaims(@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "searchCondition", required = false) String searchCondition,
			@RequestParam(value = "searchText", required = false) String searchText,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows, HttpServletRequest request) {
		
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long employeeId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);
		
		AddClaimDTO addClaimDTO = new AddClaimDTO();
		addClaimDTO.setCompanyId(companyId);
		addClaimDTO.setEmployeeId(employeeId);
		addClaimDTO.setSearchText(searchText);
		addClaimDTO.setSearchCondition(searchCondition);
		
		PendingClaimsResponseForm pendingClaimsResponseForm = pendingClaimsLogic.getPendingClaims( addClaimDTO,pageDTO,
				sortDTO);

		return ResponseDataConverter.getJsonURLEncodedData(pendingClaimsResponseForm);
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.GET_DATA_FOR_CLAIM_REVIEW, method = RequestMethod.POST)
	@ResponseBody
	public String getDataForClaimReview(
			@RequestParam(value = "claimApplicationreviewerId") Long claimApplicationreviewerId,
			HttpServletRequest request, Locale locale) {
		Long empId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		boolean hasLundinTimesheetModule = (boolean) request.getSession()
				.getAttribute(PayAsiaSessionAttributes.COMPANY_HAS_LUNDIN_TIMESHEET_MODULE);
		PendingClaimsForm claimsForm = pendingClaimsLogic.getDataForClaimReview(FormatPreserveCryptoUtil.decrypt(claimApplicationreviewerId), empId,
				locale, messageSource);
		if (claimsForm == null) {
			return null;
		}
		claimsForm.setLundinTimesheetModule(hasLundinTimesheetModule);
		return ResponseDataConverter.getJsonURLEncodedData(claimsForm);
		
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.SAVE_OVER_RIDE_ITEM_INFO, method = RequestMethod.POST)
	@ResponseBody
	public String saveOverrideItemInfo(
			@ModelAttribute("claimApplicationItemWorkflowForm") ClaimApplicationItemWorkflowForm claimApplicationItemWorkflowForm,
			HttpServletRequest request,Locale locale) {
		
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long employeeId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		AddClaimDTO addClaimDTO = new AddClaimDTO();
		addClaimDTO.setClaimApplicationItemId(claimApplicationItemWorkflowForm.getClaimApplicationItemID());
		addClaimDTO.setCompanyId(companyId);
		addClaimDTO.setEmployeeId(employeeId);
		addClaimDTO.setAdmin(false);
	
		ClaimApplicationItemForm claimApplicationItemForm = pendingClaimsLogic.saveOverrideItemInfo(addClaimDTO,claimApplicationItemWorkflowForm);
		String[] errorValArr = null;
		String[] errorVal = null;
		StringBuilder errorKeyFinalStr = new StringBuilder();
		String[] errorKeyArr;
		if (claimApplicationItemForm.getValidationClaimItemDTO() != null
				&& claimApplicationItemForm.getValidationClaimItemDTO().getErrorCode() == 1) {
			if (StringUtils.isNotBlank(claimApplicationItemForm.getValidationClaimItemDTO().getErrorKey())) {
				errorKeyArr = claimApplicationItemForm.getValidationClaimItemDTO().getErrorKey().split(";");
				if (StringUtils.isNotBlank(claimApplicationItemForm.getValidationClaimItemDTO().getErrorValue())) {
					errorValArr = claimApplicationItemForm.getValidationClaimItemDTO().getErrorValue().split(";");
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
			claimApplicationItemForm.getValidationClaimItemDTO().setErrorKey(errorKeyFinalStr.toString());
		}
		
		return ResponseDataConverter.getObjectToJson(claimApplicationItemForm);
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.SAVE_REJECT_ITEM_INFO, method = RequestMethod.POST)
	@ResponseBody
	public String saveRejectItemInfo(
			@ModelAttribute("claimApplicationItemWorkflowForm") ClaimApplicationItemWorkflowForm claimApplicationItemWorkflowForm,
			HttpServletRequest request) {
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long employeeId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		AddClaimDTO addClaimDTO = new AddClaimDTO();
		addClaimDTO.setClaimApplicationItemId(claimApplicationItemWorkflowForm.getClaimApplicationItemID());
		addClaimDTO.setCompanyId(companyId);
		addClaimDTO.setEmployeeId(employeeId);
		addClaimDTO.setAdmin(false);
	
		ClaimApplicationItemForm claimApplicationItemForm = pendingClaimsLogic.saveRejectItemInfo(addClaimDTO,
				claimApplicationItemWorkflowForm);
		return ResponseDataConverter.getObjectToJson(claimApplicationItemForm);
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.FORWORD_CLAIM, method = RequestMethod.POST)
	@ResponseBody
	public String forwardLeave(@ModelAttribute(value = "pendingClaimsForm") PendingClaimsForm pendingClaimsForm,
			HttpServletRequest request, Locale locale) {
		
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long employeeId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		AddClaimDTO addClaimDTO = new AddClaimDTO();
		addClaimDTO.setCompanyId(companyId);
		addClaimDTO.setEmployeeId(employeeId);
		addClaimDTO.setAdmin(false);

		PendingClaimsForm claimsForm = pendingClaimsLogic.forwardClaim(pendingClaimsForm, addClaimDTO);
		
		String[] errorValArr = null;
		String[] errorVal = null;
		StringBuilder errorKeyFinalStr = new StringBuilder();
		String[] errorKeyArr;
		if (claimsForm.getValidationClaimItemDTO() != null
				&& claimsForm.getValidationClaimItemDTO().getErrorCode() == 1) {
			if (StringUtils.isNotBlank(claimsForm.getValidationClaimItemDTO().getErrorKey())) {
				errorKeyArr = claimsForm.getValidationClaimItemDTO().getErrorKey().split(";");
				if (StringUtils.isNotBlank(claimsForm.getValidationClaimItemDTO().getErrorValue())) {
					errorValArr = claimsForm.getValidationClaimItemDTO().getErrorValue().split(";");
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
			claimsForm.getValidationClaimItemDTO().setErrorKey(errorKeyFinalStr.toString());
		}

		return ResponseDataConverter.getObjectToJson(claimsForm);
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.ACCEPT_CLAIM, method = RequestMethod.POST)
	@ResponseBody
	public String acceptClaim(@ModelAttribute(value = "pendingClaimsForm") PendingClaimsForm pendingClaimsForm,
			HttpServletRequest request, Locale locale) {
		
		Long employeeId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		
		PendingClaimsForm claimsForm = pendingClaimsLogic.acceptClaim(pendingClaimsForm, employeeId, companyId);
		String[] errorValArr = null;
		String[] errorVal = null;
		StringBuilder errorKeyFinalStr = new StringBuilder();
		String[] errorKeyArr;
		if (claimsForm.getValidationClaimItemDTO() != null
				&& claimsForm.getValidationClaimItemDTO().getErrorCode() == 1) {
			if (StringUtils.isNotBlank(claimsForm.getValidationClaimItemDTO().getErrorKey())) {
				errorKeyArr = claimsForm.getValidationClaimItemDTO().getErrorKey().split(";");
				if (StringUtils.isNotBlank(claimsForm.getValidationClaimItemDTO().getErrorValue())) {
					errorValArr = claimsForm.getValidationClaimItemDTO().getErrorValue().split(";");
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
			claimsForm.getValidationClaimItemDTO().setErrorKey(errorKeyFinalStr.toString());
		}

		return ResponseDataConverter.getObjectToJson(claimsForm);
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.REJECT_CLAIM, method = RequestMethod.POST)
	@ResponseBody
	public String rejectClaim(@ModelAttribute(value = "pendingClaimsForm") PendingClaimsForm pendingClaimsForm,
			HttpServletRequest request) {
		
		Long employeeId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		
		PendingClaimsForm claimsForm = pendingClaimsLogic.rejectClaim(pendingClaimsForm, employeeId, companyId);

		return ResponseDataConverter.getObjectToJson(claimsForm);
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.PRINT_CLAIM_APPLICATION_FORM, method = RequestMethod.GET)
	public @ResponseBody byte[] printClaimApplicationForm(
			@RequestParam(value = "claimApplicationReviewerId", required = true) Long claimApplicationReviewerId,
			HttpServletResponse response, HttpServletRequest request) {
		
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long employeeId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		boolean hasLundinTimesheetModule = (boolean) request.getSession()
				.getAttribute(PayAsiaSessionAttributes.COMPANY_HAS_LUNDIN_TIMESHEET_MODULE);
		ClaimFormPdfDTO claimFormPdfDTO = pendingClaimsLogic.generateClaimFormPrintPDF(companyId, employeeId,
				FormatPreserveCryptoUtil.decrypt(claimApplicationReviewerId), hasLundinTimesheetModule);
		response.reset();
		String mimeType = URLConnection.guessContentTypeFromName(
				claimFormPdfDTO.getEmployeeNumber() + "_" + claimFormPdfDTO.getClaimTemplateName() + ".pdf");
		response.setContentType("application/" + mimeType);
		response.setContentLength(claimFormPdfDTO.getClaimFormPdfByteFile().length);
		String filename = claimFormPdfDTO.getEmployeeNumber() + "_" + claimFormPdfDTO.getClaimTemplateName() + ".pdf";

		response.setHeader("Content-Disposition", "attachment;filename=" + filename);

		return claimFormPdfDTO.getClaimFormPdfByteFile();
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.VIEW_ATTACHMENT_REVIEWER, method = RequestMethod.GET)
	public @ResponseBody byte[] viewAttachment(
			@RequestParam(value = "attachmentId", required = true) long claimApplicationItemAttachmentId,
			HttpServletResponse response, HttpServletRequest request) {

		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long employeeId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		AddClaimDTO addClaimDTO = new AddClaimDTO();
		addClaimDTO.setClaimApplicationItemAttachmentId(claimApplicationItemAttachmentId);
		addClaimDTO.setCompanyId(companyId);
		addClaimDTO.setEmployeeId(employeeId);
		addClaimDTO.setAdmin(true);

		ClaimApplicationItemAttach claimApplicationItemAttach = pendingClaimsLogic.viewAttachment(addClaimDTO);

		byte[] byteFile = claimApplicationItemAttach.getAttachmentBytes();

		response.reset();
		String mimeType = URLConnection.guessContentTypeFromName(claimApplicationItemAttach.getFileName());
		response.setContentType("application/" + mimeType);
		response.setContentLength(byteFile.length);
		String filename = claimApplicationItemAttach.getFileName();

		response.setHeader("Content-Disposition", "attachment;filename=" + filename);

		return byteFile;
	}

}
