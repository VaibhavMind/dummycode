package com.payasia.web.controller.claim.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.AddClaimDTO;
import com.payasia.common.dto.ClaimFormPdfDTO;
import com.payasia.common.dto.DataImportLogDTO;
import com.payasia.common.exception.PayAsiaRollBackDataException;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.AddClaimForm;
import com.payasia.common.form.AddClaimFormResponse;
import com.payasia.common.form.EmployeeListForm;
import com.payasia.common.form.ImportEmployeeClaimForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.FileUtils;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.PayAsiaURLConstant;
import com.payasia.common.util.ResponseDataConverter;
import com.payasia.logic.EmployeeClaimsLogic;
import com.payasia.logic.MyClaimLogic;
import com.payasia.web.controller.claim.EmployeeClaimsController;
import com.payasia.web.util.PayAsiaSessionAttributes;

@Controller
@RequestMapping(value = PayAsiaURLConstant.ADMIN_EMPLOYEE_CLAIMS)
public class EmployeeClaimsControllerImpl implements EmployeeClaimsController {

	/** The Constant logger. */
	private static final Logger LOGGER = Logger.getLogger(EmployeeClaimsControllerImpl.class);
	@Resource
	EmployeeClaimsLogic employeeClaimsLogic;

	@Resource
	MyClaimLogic myClaimLogic;

	@Resource
	MessageSource messageSource;

	@Override
	@RequestMapping(value = PayAsiaURLConstant.GET_EMPLOYEE_ID, method = RequestMethod.POST)
	@ResponseBody
	public String getEmployeeId(@RequestParam(value = "searchString", required = true) String searchString,
			HttpServletRequest request, HttpServletResponse response) {
		
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		List<EmployeeListForm> empListFormList = employeeClaimsLogic.getEmployeeId(companyId, searchString, employeeId);

		return ResponseDataConverter.getListToJson(empListFormList);

	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.GET_PENDING_CLAIMS, method = RequestMethod.POST)
	@ResponseBody
	public String getPendingClaims(@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "transactionType", required = false) String transactionType,
			@RequestParam(value = "employeeNumber", required = false) String employeeNumber,
			@RequestParam(value = "fromDate", required = true) String fromDate,
			@RequestParam(value = "toDate", required = true) String toDate,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows, HttpServletRequest request,
			HttpServletResponse response, HttpSession session) {

		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		AddClaimFormResponse addClaimFormResponse = employeeClaimsLogic.getPendingClaims(fromDate, toDate, employeeId,
				pageDTO, sortDTO, transactionType, employeeNumber, companyId);
		addClaimFormResponse.setPage(1);

		return ResponseDataConverter.getJsonURLEncodedData(addClaimFormResponse);
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.GET_SUBMITTED_CLAIMS, method = RequestMethod.POST)
	@ResponseBody
	public String getSubmittedClaims(@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "transactionType", required = false) String transactionType,
			@RequestParam(value = "employeeNumber", required = false) String employeeNumber,
			@RequestParam(value = "fromDate", required = true) String fromDate,
			@RequestParam(value = "toDate", required = true) String toDate,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows, HttpServletRequest request,
			HttpServletResponse response, HttpSession session) {

		Long empId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);
		String pageContextPath = request.getContextPath();
		AddClaimFormResponse addClaimFormResponse = null;
		addClaimFormResponse = employeeClaimsLogic.getSubmittedClaims(fromDate, toDate, empId, pageDTO, sortDTO,
				pageContextPath, transactionType, employeeNumber, companyId);
		addClaimFormResponse.setPage(1);

		return ResponseDataConverter.getJsonURLEncodedData(addClaimFormResponse);
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.GET_APPROVED_CLAIMS, method = RequestMethod.POST)
	@ResponseBody
	public String getApprovedClaims(@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "transactionType", required = false) String transactionType,
			@RequestParam(value = "employeeNumber", required = false) String employeeNumber,
			@RequestParam(value = "fromDate", required = true) String fromDate,
			@RequestParam(value = "toDate", required = true) String toDate,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows, HttpServletRequest request,
			HttpServletResponse response, HttpSession session) {
		Long empId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);
		String pageContextPath = request.getContextPath();
		AddClaimFormResponse addClaimFormResponse = null;
		addClaimFormResponse = employeeClaimsLogic.getApprovedClaims(fromDate, toDate, empId, pageDTO, sortDTO,
				pageContextPath, transactionType, employeeNumber, companyId);
		addClaimFormResponse.setPage(1);

		return ResponseDataConverter.getJsonURLEncodedData(addClaimFormResponse);
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.GET_REJECTED_CLAIMS, method = RequestMethod.POST)
	@ResponseBody
	public String getRejectedClaims(@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "transactionType", required = false) String transactionType,
			@RequestParam(value = "employeeNumber", required = false) String employeeNumber,
			@RequestParam(value = "fromDate", required = true) String fromDate,
			@RequestParam(value = "toDate", required = true) String toDate,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows, HttpServletRequest request,
			HttpServletResponse response, HttpSession session) {
		Long empId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);
		String pageContextPath = request.getContextPath();
		AddClaimFormResponse addClaimFormResponse = null;
		addClaimFormResponse = employeeClaimsLogic.getRejectedClaims(fromDate, toDate, empId, pageDTO, sortDTO,
				pageContextPath, transactionType, employeeNumber, companyId);
		addClaimFormResponse.setPage(1);

		return ResponseDataConverter.getJsonURLEncodedData(addClaimFormResponse);
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.GET_WITHDRAWN_CLAIMS, method = RequestMethod.POST)
	@ResponseBody
	public String getWithdrawnClaims(@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "transactionType", required = false) String transactionType,
			@RequestParam(value = "employeeNumber", required = false) String employeeNumber,
			@RequestParam(value = "fromDate", required = true) String fromDate,
			@RequestParam(value = "toDate", required = true) String toDate,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows, HttpServletRequest request,
			HttpServletResponse response, HttpSession session) {
		Long empId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);
		String pageContextPath = request.getContextPath();
		AddClaimFormResponse addClaimFormResponse = null;
		addClaimFormResponse = employeeClaimsLogic.getWithdrawnClaims(fromDate, toDate, empId, pageDTO, sortDTO,
				pageContextPath, transactionType, employeeNumber, companyId);
		addClaimFormResponse.setPage(1);

		return ResponseDataConverter.getJsonURLEncodedData(addClaimFormResponse);
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.GET_ALL_CLAIMS, method = RequestMethod.POST)
	@ResponseBody
	public String getAllClaims(@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "transactionType", required = false) String transactionType,
			@RequestParam(value = "employeeNumber", required = false) String employeeNumber,
			@RequestParam(value = "fromDate", required = true) String fromDate,
			@RequestParam(value = "toDate", required = true) String toDate,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows, HttpServletRequest request,
			HttpServletResponse response, HttpSession session, Locale locale) {
		Long empId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);
		String pageContextPath = request.getContextPath();
		AddClaimFormResponse addClaimFormResponse = null;
		addClaimFormResponse = employeeClaimsLogic.getAllClaims(fromDate, toDate, empId, pageDTO, sortDTO,
				pageContextPath, transactionType, employeeNumber, companyId, locale);
		addClaimFormResponse.setPage(1);

		return ResponseDataConverter.getJsonURLEncodedData(addClaimFormResponse);
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.ACCEPT_CLAIM, method = RequestMethod.POST)
	@ResponseBody
	public String acceptClaim(@ModelAttribute(value = "addClaimForm") AddClaimForm addClaimForm,
			HttpServletRequest request, HttpServletResponse response, HttpSession session, Locale locale) {
		Long employeeId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		AddClaimForm claimsForm = employeeClaimsLogic.acceptClaim(addClaimForm, employeeId);

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
	public String rejectClaim(@ModelAttribute(value = "addClaimForm") AddClaimForm addClaimForm,
			HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		Long employeeId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		AddClaimForm claimsForm = employeeClaimsLogic.rejectClaim(addClaimForm, employeeId);
		return ResponseDataConverter.getObjectToJson(claimsForm);
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.IS_PAYASIA_ADMIN_CAN_APPROVE, method = RequestMethod.POST)
	@ResponseBody
	public String isPayAsiaAdminCanApprove(HttpServletRequest request) {
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		String status = employeeClaimsLogic.isPayAsiaAdminCanApprove(companyId);
		return status;
	}

	@Override
	@RequestMapping(value =  PayAsiaURLConstant.IS_ADMIN_CAN_EDIT_CLAIM_BEFORE_APPROVAL , method = RequestMethod.POST)
	@ResponseBody
	public String isAdminCanEditClaimBeforeApproval(
			@RequestParam(value = "claimApplicationId", required = false) Long claimApplicationId,
			HttpServletRequest request) {
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		String status = employeeClaimsLogic.isAdminCanEditClaimBeforeApproval(claimApplicationId, companyId);
		return status;
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.EMPLOYEE_NAME, method = RequestMethod.POST)
	@ResponseBody
	public String getEmployeeName(@RequestParam(value = "employeeNumber", required = false) String employeeNumber,
			HttpServletResponse response, HttpServletRequest request) {
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		String empName = employeeClaimsLogic.getEmployeeName(employeeNumber, companyId);
		return ResponseDataConverter.getURLEncoded(empName);
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.IMPORT_EMPLOYEE_CLAIM, method = RequestMethod.POST)
	@ResponseBody
	public String importEmployeeClaim(
			@ModelAttribute(value = "importEmployeeClaimForm") ImportEmployeeClaimForm importEmployeeClaimForm,
			HttpServletRequest request, HttpServletResponse response, Locale locale) throws Exception {
		ImportEmployeeClaimForm importResponse = new ImportEmployeeClaimForm();

		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long employeeId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		boolean hasLundinTimesheetModule = (boolean) request.getSession()
				.getAttribute(PayAsiaSessionAttributes.COMPANY_HAS_LUNDIN_TIMESHEET_MODULE);
		try {
			
			boolean isFileValid = false;
			if (importEmployeeClaimForm.getFileUpload()!=null) {
				isFileValid = FileUtils.isValidFile(importEmployeeClaimForm.getFileUpload(), importEmployeeClaimForm.getFileUpload().getOriginalFilename(), PayAsiaConstants.ALLOWED_ONLY_UPLOAD_EXCLE_FILE_EXT, PayAsiaConstants.ALLOWED_ONLY_UPLOAD_EXCLE_MINE_TYPE, PayAsiaConstants.FILE_SIZE);
			}
			if(isFileValid){
				importResponse = employeeClaimsLogic.importEmployeeClaim(importEmployeeClaimForm, companyId, employeeId,
						hasLundinTimesheetModule);
				importResponse.setDataValid(true);
			}
			
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

		if (importResponse.getDataImportLogDTOs() != null && importResponse.getDataImportLogDTOs().size() > 0) {

			for (DataImportLogDTO dataImportLogDTO : importResponse.getDataImportLogDTOs()) {
				try {

					String[] errorVal = null;
					StringBuilder errorKeyFinalStr = new StringBuilder();
					String[] errorKeyArr;
					if (StringUtils.isNotBlank(dataImportLogDTO.getErrorKey())) {
						errorKeyArr = dataImportLogDTO.getErrorKey().split(";");

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
								errorKeyFinalStr.append(messageSource.getMessage(errorKeyArr[count], errorVal, locale));
								// errorKeyFinalStr.append("<br>");
							}

						}

						dataImportLogDTO.setRemarks(errorKeyFinalStr.toString());

					} else {
						if (StringUtils.isNotBlank(dataImportLogDTO.getErrorValue())) {
							errorVal = dataImportLogDTO.getErrorValue().split(",");
							dataImportLogDTO.setRemarks(URLEncoder.encode(
									messageSource.getMessage(dataImportLogDTO.getRemarks(), errorVal, locale),
									"UTF-8"));
						} else {
							dataImportLogDTO.setRemarks(URLEncoder.encode(
									messageSource.getMessage(dataImportLogDTO.getRemarks(), new Object[] {}, locale),
									"UTF-8"));
						}
					}

				} catch (UnsupportedEncodingException | NoSuchMessageException exception) {
					LOGGER.error(exception.getMessage(), exception);
					throw new PayAsiaSystemException(exception.getMessage(), exception);
				}
			}
			importResponse.setDataValid(false);
		}

		return ResponseDataConverter.getObjectToJson(importResponse);
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.PRINT_CLAIM_APPLICATION_FORM, method = RequestMethod.GET)
	public @ResponseBody byte[] printClaimApplicationForm(
			@RequestParam(value = "claimApplicationId", required = true) Long claimApplicationId,
			HttpServletResponse response, HttpServletRequest request) {
		UUID uuid = UUID.randomUUID();
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long employeeId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		boolean hasLundinTimesheetModule = (boolean) request.getSession()
				.getAttribute(PayAsiaSessionAttributes.COMPANY_HAS_LUNDIN_TIMESHEET_MODULE);
		ClaimFormPdfDTO claimFormPdfDTO = employeeClaimsLogic.generateClaimFormPrintPDF(companyId, employeeId,
				FormatPreserveCryptoUtil.decrypt(claimApplicationId), hasLundinTimesheetModule);

		response.reset();
		String mimeType = URLConnection.guessContentTypeFromName(
				claimFormPdfDTO.getEmployeeNumber() + "_" + claimFormPdfDTO.getClaimTemplateName() + uuid + ".pdf");
		response.setContentType("application/" + mimeType);
		response.setContentLength(claimFormPdfDTO.getClaimFormPdfByteFile().length);
		String filename = claimFormPdfDTO.getEmployeeNumber() + "_" + claimFormPdfDTO.getClaimTemplateName() + uuid
				+ ".pdf";

		response.setHeader("Content-Disposition", "attachment;filename=" + filename);

		return claimFormPdfDTO.getClaimFormPdfByteFile();
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.GET_ADMIN_CLAIM_APPLICATION_DATA, method = RequestMethod.POST)
	@ResponseBody
	public String getAdminClaimApplicationData(@ModelAttribute("claimApplicationId") Long claimApplicationId,
			@RequestParam(value = "employeeNumber", required = true) String employeeNumber, BindingResult result,
			ModelMap model, HttpServletRequest request, Locale locale) {

		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long employeeId = employeeClaimsLogic.getEmployeeId(companyId, employeeNumber);

		AddClaimDTO claimDTO = new AddClaimDTO();
		claimDTO.setCompanyId(companyId);
		claimDTO.setEmployeeId(employeeId);
		/*ID DECRYPT*/
		claimDTO.setClaimApplicationId(FormatPreserveCryptoUtil.decrypt(claimApplicationId));
		claimDTO.setLocale(locale);
		claimDTO.setAdmin(true);

		AddClaimForm addClaimForm = myClaimLogic.getClaimApplicationData(claimDTO);

		return ResponseDataConverter.getJsonURLEncodedData(addClaimForm);

	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.SEARCH_CLAIM_TEMPLATE_ITEMS, method = RequestMethod.POST)
	@ResponseBody
	public String searchClaimTemplateItems(@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "claimApplicationId", required = false) Long claimApplicationId,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows, HttpServletRequest request,
			HttpServletResponse response, HttpSession session, Locale locale) {

		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		AddClaimDTO claimDTO = new AddClaimDTO();
		claimDTO.setCompanyId(companyId);
		claimDTO.setEmployeeId(FormatPreserveCryptoUtil.decrypt(employeeId));
		claimDTO.setClaimApplicationId(FormatPreserveCryptoUtil.decrypt(claimApplicationId));
		claimDTO.setLocale(locale);
		claimDTO.setAdmin(true);

		AddClaimFormResponse addClaimFormResponse = myClaimLogic.searchClaimTemplateItems(claimDTO, pageDTO, sortDTO,null);

		return ResponseDataConverter.getJsonURLEncodedData(addClaimFormResponse);

	}

}
