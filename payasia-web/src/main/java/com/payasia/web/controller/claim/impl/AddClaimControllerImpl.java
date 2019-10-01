package com.payasia.web.controller.claim.impl;

import java.net.URLConnection;
import java.util.List;
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

import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.AddClaimDTO;
import com.payasia.common.form.AddClaimForm;
import com.payasia.common.form.ClaimApplicationItemAttach;
import com.payasia.common.form.ClaimApplicationItemForm;
import com.payasia.common.form.LundinTimesheetReportsForm;
import com.payasia.common.util.DateUtils;
import com.payasia.common.util.FileUtils;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.PayAsiaURLConstant;
import com.payasia.common.util.ResponseDataConverter;
import com.payasia.logic.AddClaimLogic;
import com.payasia.logic.GeneralLogic;
import com.payasia.logic.MyClaimLogic;
import com.payasia.web.controller.claim.AddClaimController;
import com.payasia.web.util.PayAsiaSessionAttributes;

@Controller
@RequestMapping(value = { PayAsiaURLConstant.EMPLOYEE_ADD_CLAIM, PayAsiaURLConstant.ADMIN_ADD_CLAIM })
public class AddClaimControllerImpl implements AddClaimController {

	@Resource
	AddClaimLogic addClaimLogic;
	
	@Resource
	MyClaimLogic myClaimLogic;

	@Autowired
	MessageSource messageSource;

	@Resource
	GeneralLogic generalLogic;

	@Override
	@RequestMapping(value = PayAsiaURLConstant.GET_CLAIM_TEMPLATE_ITEMS, method = RequestMethod.POST)
	@ResponseBody
	public String getClaimTemplateItems(
			@RequestParam(value = "claimTemplateId", required = true) Long employeeClaimTemplateId,
			HttpServletRequest request) {

		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		
		AddClaimDTO addClaimDTO = new AddClaimDTO();
		addClaimDTO.setEmployeeClaimTemplateId(FormatPreserveCryptoUtil.decrypt(employeeClaimTemplateId));
		addClaimDTO.setCompanyId(companyId);
		addClaimDTO.setEmployeeId(employeeId);
		addClaimDTO.setAdmin(isURLfromAdmin(request));

		AddClaimForm addClaimForm = addClaimLogic.getClaimTemplateItemList(addClaimDTO);

		return ResponseDataConverter.getJsonURLEncodedData(addClaimForm);
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.GET_CLAIM_TEMPLATE_ITEMS_DATA, method = RequestMethod.POST)
	@ResponseBody
	public String getClaimTemplateItemData(
			@ModelAttribute("employeeClaimTemplateItemId") Long employeeClaimTemplateItemId,
			@ModelAttribute("claimApplicationId") Long claimApplicationId,
			@ModelAttribute("claimApplicationItemId") Long claimApplicationItemId, HttpServletRequest request,
			Locale locale) {

		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		
		boolean hasLundinTimesheetModule = (boolean) request.getSession()
				.getAttribute(PayAsiaSessionAttributes.COMPANY_HAS_LUNDIN_TIMESHEET_MODULE);

		AddClaimDTO addClaimDTO = new AddClaimDTO();
		if(claimApplicationItemId == 0){
			addClaimDTO.setClaimApplicationItemId(claimApplicationItemId);
		}else{
			addClaimDTO.setClaimApplicationItemId(FormatPreserveCryptoUtil.decrypt(claimApplicationItemId));
		}
		
		addClaimDTO.setCompanyId(companyId);
		addClaimDTO.setEmployeeClaimTemplateItemId(FormatPreserveCryptoUtil.decrypt(employeeClaimTemplateItemId));
		addClaimDTO.setClaimApplicationId(FormatPreserveCryptoUtil.decrypt(claimApplicationId));
		addClaimDTO.setEmployeeId(employeeId);
		addClaimDTO.setHasLundinTimesheetModule(hasLundinTimesheetModule);
		addClaimDTO.setAdmin(isURLfromAdmin(request));

		AddClaimForm addClaimForm = addClaimLogic.getClaimTemplateItemConfigData(addClaimDTO);
		addClaimForm.setLundinTimesheetModule(hasLundinTimesheetModule);

		String[] errorValArr = null;
		String[] errorVal = null;
		StringBuilder errorKeyFinalStr = new StringBuilder();
		String[] errorKeyArr;
		if (addClaimForm.getClaimItemBalanceDTO() != null
				&& addClaimForm.getClaimItemBalanceDTO().getErrorCode() == 1) {
			if (StringUtils.isNotBlank(addClaimForm.getClaimItemBalanceDTO().getErrorKey())) {
				errorKeyArr = addClaimForm.getClaimItemBalanceDTO().getErrorKey().split(";");
				if (StringUtils.isNotBlank(addClaimForm.getClaimItemBalanceDTO().getErrorValue())) {
					errorValArr = addClaimForm.getClaimItemBalanceDTO().getErrorValue().split(";");
				}

				for (int count = 0; count < errorKeyArr.length; count++) {
					if (StringUtils.isNotBlank(errorKeyArr[count])) {
						if (errorValArr != null && errorValArr.length > 0) {
							if (StringUtils.isNotBlank(errorValArr[count])) {
								errorVal = errorValArr[count].split(",");
							}
						}
						errorKeyFinalStr
								.append(messageSource.getMessage(errorKeyArr[count], errorVal, locale) + " </br> ");
					}
				}
			}
			addClaimForm.getClaimItemBalanceDTO().setErrorKey(errorKeyFinalStr.toString());
		} else if (addClaimForm.getClaimItemBalanceDTO() != null
				&& addClaimForm.getClaimItemBalanceDTO().getEntitlementType() != null) {
			addClaimForm.getClaimItemBalanceDTO().setEntitlementType(messageSource
					.getMessage(addClaimForm.getClaimItemBalanceDTO().getEntitlementType(), new Object[] {}, locale));
		}

		return ResponseDataConverter.getJsonURLEncodedData(addClaimForm);

	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.PERSIST_CLAIM_APPLICATION, method = RequestMethod.POST)
	@ResponseBody
	public String persistClaimApplication(@ModelAttribute("addClaimForm") AddClaimForm addClaimForm,
			HttpServletRequest request, Locale locale) {

		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		
		AddClaimForm addClaimFormRes = null;

		AddClaimDTO addClaimDTO = new AddClaimDTO();
		addClaimDTO.setCompanyId(companyId);
		addClaimDTO.setEmployeeId(employeeId);
		addClaimDTO.setAdmin(isURLfromAdmin(request));
		addClaimDTO.setClaimApplicationId(FormatPreserveCryptoUtil.decrypt(addClaimForm.getClaimApplicationId()));
		addClaimForm.setClaimApplicationId(FormatPreserveCryptoUtil.decrypt(addClaimForm.getClaimApplicationId()));
		addClaimFormRes = addClaimLogic.persistClaimApplication(addClaimDTO, addClaimForm);

		setClaimApllicationError(addClaimFormRes, locale);

		return ResponseDataConverter.getJsonURLEncodedData(addClaimFormRes);

	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.SAVE_AS_DRAFT_FROM_WITHDRAW_CLAIM, method = RequestMethod.POST)
	@ResponseBody
	public String saveAsDraftFromWithdrawClaim(@ModelAttribute("addClaimForm") AddClaimForm addClaimForm,
			HttpServletRequest request, Locale locale) {

		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		
		AddClaimDTO addClaimDTO = new AddClaimDTO();
		addClaimDTO.setCompanyId(companyId);
		addClaimDTO.setEmployeeId(employeeId);
		addClaimDTO.setAdmin(isURLfromAdmin(request));
		addClaimDTO.setClaimApplicationId(FormatPreserveCryptoUtil.decrypt(addClaimForm.getClaimApplicationId()));

		AddClaimForm addClaimFormRes = addClaimLogic.saveAsDraftFromWithdrawClaim(addClaimDTO, addClaimForm);

		setError(addClaimFormRes, locale);

		return ResponseDataConverter.getJsonURLEncodedData(addClaimForm);
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.COPY_CLAIM_APPLICATION, method = RequestMethod.POST)
	@ResponseBody
	public String copyClaimApplication(@ModelAttribute("addClaimForm") AddClaimForm addClaimForm,
			HttpServletRequest request, Locale locale) {

		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		AddClaimDTO addClaimDTO = new AddClaimDTO();
		addClaimDTO.setCompanyId(companyId);
		addClaimDTO.setEmployeeId(employeeId);
		addClaimDTO.setAdmin(isURLfromAdmin(request));
		addClaimDTO.setClaimApplicationId(FormatPreserveCryptoUtil.decrypt(addClaimForm.getClaimApplicationId()));
		
		AddClaimForm addClaimFormRes = addClaimLogic.copyClaimApplication(addClaimDTO, addClaimForm);

		setError(addClaimFormRes, locale);

		return ResponseDataConverter.getJsonURLEncodedData(addClaimForm);
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.PERSIST_ADMIN_CLAIM_APPLICATION, method = RequestMethod.POST)
	@ResponseBody
	public String persistAdminClaimApplication(@ModelAttribute("addClaimForm") AddClaimForm addClaimForm,
			HttpServletRequest request, Locale locale) {

		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		AddClaimDTO addClaimDTO = new AddClaimDTO();
		addClaimDTO.setCompanyId(companyId);
		addClaimDTO.setEmployeeId(employeeId);
		addClaimDTO.setAdmin(isURLfromAdmin(request));

		AddClaimForm addClaimFormRes = addClaimLogic.persistAdminClaimApplication(addClaimDTO, addClaimForm);

		setClaimApllicationError(addClaimFormRes, locale);

		return ResponseDataConverter.getJsonURLEncodedData(addClaimFormRes);

	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.SAVE_CLAIM_APPLICATION, method = RequestMethod.POST)
	@ResponseBody
	public String saveClaimApplication(
			@RequestParam(value = "claimTemplateId", required = true) Long employeeClaimTemplateId,
			HttpServletRequest request, Locale locale) {

		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());

		AddClaimDTO addClaimDTO = new AddClaimDTO();
		addClaimDTO.setEmployeeClaimTemplateId(FormatPreserveCryptoUtil.decrypt(employeeClaimTemplateId));
		addClaimDTO.setCompanyId(companyId);
		addClaimDTO.setEmployeeId(employeeId);
		addClaimDTO.setAdmin(false);

		AddClaimForm addClaimForm = addClaimLogic.saveClaimApplication(addClaimDTO);
		if (addClaimForm.getClaimReviewerNotDefined()) {
			addClaimForm.setClaimApplicationStatus(false);
		} else {
			addClaimForm.setClaimApplicationStatus(true);
		}

		setError(addClaimForm, locale);

		return ResponseDataConverter.getJsonURLEncodedData(addClaimForm);
	}
	
	@Override
	@RequestMapping(value = PayAsiaURLConstant.GET_ADMIN_CLAIM_TEMPLATE_ITEMS, method = RequestMethod.POST)
	@ResponseBody
	public String getAdminClaimTemplateItems(
			@RequestParam(value = "claimTemplateId", required = true) Long employeeClaimTemplateId,
			@RequestParam(value = "employeeNumber", required = true) String employeeNumber) {

		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		Long employeeId = addClaimLogic.getEmployeeId(companyId, employeeNumber);
		
		AddClaimDTO addClaimDTO = new AddClaimDTO();
		addClaimDTO.setEmployeeClaimTemplateId(FormatPreserveCryptoUtil.decrypt(employeeClaimTemplateId));
		addClaimDTO.setCompanyId(companyId);
		addClaimDTO.setEmployeeId(employeeId);
		addClaimDTO.setAdmin(true);
		
		AddClaimForm addClaimForm = addClaimLogic.getClaimTemplateItemList(addClaimDTO);

		return ResponseDataConverter.getJsonURLEncodedData(addClaimForm);
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.SAVE_ADMIN_CLAIM_APPLICATION, method = RequestMethod.POST)
	@ResponseBody
	public String saveAdminClaimApplication(
			@RequestParam(value = "employeeClaimTemplateId", required = true) Long employeeClaimTemplate,
			@RequestParam(value = "employeeNumber", required = true) String employeeNumber, HttpServletRequest request,
			Locale locale) {

		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		
		AddClaimForm addClaimForm=null;
		if(isURLfromAdmin(request)){
			Long employeeId = addClaimLogic.getEmployeeId(companyId, employeeNumber);
	
			AddClaimDTO addClaimDTO = new AddClaimDTO();
			addClaimDTO.setEmployeeClaimTemplateId(FormatPreserveCryptoUtil.decrypt(employeeClaimTemplate));
			addClaimDTO.setCompanyId(companyId);
			addClaimDTO.setEmployeeId(employeeId);
			addClaimDTO.setAdmin(true);
	
			addClaimForm = addClaimLogic.saveClaimApplication(addClaimDTO);
			if (addClaimForm.getClaimReviewerNotDefined()) {
				addClaimForm.setClaimApplicationStatus(false);
			} else {
				addClaimForm.setClaimApplicationStatus(true);
			}
		}
		setError(addClaimForm, locale);

		return ResponseDataConverter.getJsonURLEncodedData(addClaimForm);
	}

	@Override
	@RequestMapping(value = { PayAsiaURLConstant.SAVE_CLAIM_TEMPLATE_ITEM }, method = RequestMethod.POST)
	@ResponseBody
	public String saveClaimTemplateItem(
			@ModelAttribute("claimApplicationItemForm") ClaimApplicationItemForm claimApplicationItemForm,
			HttpServletRequest request, Locale locale) {

		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		
		AddClaimDTO addClaimDTO = new AddClaimDTO();
		addClaimDTO.setCompanyId(companyId);
		addClaimDTO.setEmployeeId(employeeId);
		addClaimDTO.setAdmin(false);

		boolean hasLundinTimesheetModule = (boolean) request.getSession()
				.getAttribute(PayAsiaSessionAttributes.COMPANY_HAS_LUNDIN_TIMESHEET_MODULE);
		claimApplicationItemForm.setLundinTimesheetModule(hasLundinTimesheetModule);
		AddClaimForm addClaimForm = addClaimLogic.saveClaimTemplateItem(claimApplicationItemForm, companyId);

		setError(addClaimForm, locale);

		return ResponseDataConverter.getJsonURLEncodedData(addClaimForm);
	}

	@Override
	@RequestMapping(value = { PayAsiaURLConstant.UPDATE_CLAIM_TEMPLATE_ITEM }, method = RequestMethod.POST)
	@ResponseBody
	public String updateClaimTemplateItem(
			@ModelAttribute("claimApplicationItemForm") ClaimApplicationItemForm claimApplicationItemForm,
			HttpServletRequest request, Locale locale) {

		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		
		boolean hasLundinTimesheetModule = (boolean) request.getSession()
				.getAttribute(PayAsiaSessionAttributes.COMPANY_HAS_LUNDIN_TIMESHEET_MODULE);
		claimApplicationItemForm.setLundinTimesheetModule(hasLundinTimesheetModule);

		AddClaimForm addClaimForm = addClaimLogic.updateClaimTemplateItem(claimApplicationItemForm, companyId);

		setError(addClaimForm, locale);

		return ResponseDataConverter.getJsonURLEncodedData(addClaimForm);
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.DELETE_CLAIM_TEMPLATE_ITEM, method = RequestMethod.POST)
	@ResponseBody
	public String deleteClaimTemplateItem(@ModelAttribute("claimTemplateItemId") Long claimApplicationItemId,
			HttpServletRequest request) {
		
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		
		AddClaimDTO addClaimDTO = new AddClaimDTO();
		addClaimDTO.setClaimApplicationItemId(FormatPreserveCryptoUtil.decrypt(claimApplicationItemId));
		addClaimDTO.setCompanyId(companyId);
		addClaimDTO.setEmployeeId(employeeId);
		addClaimDTO.setAdmin(isURLfromAdmin(request));

		AddClaimForm addClaimForm = addClaimLogic.deleteClaimTemplateItem(addClaimDTO);

		return ResponseDataConverter.getJsonURLEncodedData(addClaimForm);
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.DELETE_CLAIM_APPLICATION_ATTACHMENT, method = RequestMethod.POST)
	@ResponseBody
	public String deleteClaimApplicationAttachement(@ModelAttribute("attachementId") Long attachementId,
			HttpServletRequest request) {

		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		
		AddClaimDTO addClaimDTO = new AddClaimDTO();
		addClaimDTO.setClaimApplicationAttachementId(FormatPreserveCryptoUtil.decrypt(attachementId));
		addClaimDTO.setCompanyId(companyId);
		addClaimDTO.setEmployeeId(employeeId);
		addClaimDTO.setAdmin(isURLfromAdmin(request));

		AddClaimForm addClaimForm = addClaimLogic.deleteClaimApplicationAttachement(addClaimDTO);

		return ResponseDataConverter.getJsonURLEncodedData(addClaimForm);
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.DELETE_CLAIM_APPLICATION, method = RequestMethod.POST)
	@ResponseBody
	public String deleteClaimApplication(@ModelAttribute("claimApplicationId") Long claimApplicationId,
			HttpServletRequest request) {
		
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		
		AddClaimDTO addClaimDTO = new AddClaimDTO();
		addClaimDTO.setClaimApplicationId(FormatPreserveCryptoUtil.decrypt(claimApplicationId));
		addClaimDTO.setCompanyId(companyId);
		addClaimDTO.setEmployeeId(employeeId);
		addClaimDTO.setAdmin(isURLfromAdmin(request));

		AddClaimForm addClaimForm = addClaimLogic.deleteClaimApplication(addClaimDTO);

		return ResponseDataConverter.getJsonURLEncodedData(addClaimForm);
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.WITHDRAW_CLAIM, method = RequestMethod.POST)
	@ResponseBody
	public String withdrawClaim(@RequestParam(value = "claimApplicationId", required = true) Long claimApplicationId,
			HttpServletRequest request) {
		
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		
		AddClaimDTO addClaimDTO = new AddClaimDTO();
		addClaimDTO.setClaimApplicationId(FormatPreserveCryptoUtil.decrypt(claimApplicationId));
		addClaimDTO.setCompanyId(companyId);
		addClaimDTO.setEmployeeId(employeeId);
		addClaimDTO.setAdmin(isURLfromAdmin(request));

		return addClaimLogic.withdrawClaim(addClaimDTO);

	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.GET_EMPLOYEE_ITEM_DATA, method = RequestMethod.POST)
	@ResponseBody
	public String getEmployeeItemData(@ModelAttribute("claimTemplateItemId") Long claimApplicationItemId,
			HttpServletRequest request) {

		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		
		AddClaimDTO addClaimDTO = new AddClaimDTO();
		addClaimDTO.setClaimApplicationItemId(FormatPreserveCryptoUtil.decrypt(claimApplicationItemId));
		addClaimDTO.setCompanyId(companyId);
		addClaimDTO.setEmployeeId(employeeId);
		addClaimDTO.setAdmin(isURLfromAdmin(request));

		AddClaimForm addClaimForm = addClaimLogic.getEmployeeItemData(addClaimDTO);

		return ResponseDataConverter.getJsonURLEncodedData(addClaimForm);
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.UPLOAD_CLAIM_ITEM_ATTACHMENT, method = RequestMethod.POST, produces = "text/html; charset=utf-8")
	@ResponseBody
	public String uploadClaimItemAttachement(
			@ModelAttribute("claimApplicationItemAttach") ClaimApplicationItemAttach claimApplicationItemAttach,
			@ModelAttribute("claimAppItemId") Long claimApplicationItemId, HttpServletRequest request) {

		AddClaimForm addClaimForm = new AddClaimForm();
		
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		
		String fileName = claimApplicationItemAttach.getAttachment().getOriginalFilename();
		
		boolean isFileTypeValid  = FileUtils.isValidFile(claimApplicationItemAttach.getAttachment(), fileName, PayAsiaConstants.ALLOWED_GENERAL_UPLOAD_FILE_EXT, PayAsiaConstants.ALLOWED_GENERAL_UPLOAD_FILE_MINE_TYPE, PayAsiaConstants.FILE_SIZE);

		addClaimForm.setDataValid(false);
		if (claimApplicationItemAttach.getAttachment() != null
				&& claimApplicationItemAttach.getAttachment().getSize() == 0) {
			isFileTypeValid = false;
		}
		if (isFileTypeValid) {
			AddClaimDTO addClaimDTO = new AddClaimDTO();
			addClaimDTO.setClaimApplicationItemId(FormatPreserveCryptoUtil.decrypt(claimApplicationItemId));			
			addClaimDTO.setCompanyId(companyId);
			addClaimDTO.setEmployeeId(employeeId);
			addClaimDTO.setAdmin(isURLfromAdmin(request));

			addClaimForm = addClaimLogic.uploadClaimItemAttachement(claimApplicationItemAttach, addClaimDTO);
			addClaimForm.setDataValid(true);
		}

		return ResponseDataConverter.getJsonURLEncodedData(addClaimForm);
	}

	

	@Override
	@RequestMapping(value = PayAsiaURLConstant.GET_FOREX_RATE, method = RequestMethod.POST)
	@ResponseBody
	public String getForexRate(@RequestParam(value = "currencyDate", required = true) String currencyDate,
			@RequestParam(value = "currencyId", required = true) Long currencyId, HttpServletRequest request) {

		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		
		AddClaimForm addClaimForm = addClaimLogic.getForexRate(DateUtils.stringToDate(currencyDate), currencyId,
				companyId);

		return ResponseDataConverter.getJsonURLEncodedData(addClaimForm);
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.GET_EMPLOYEE_CLAIM_TEMPLATES, method = RequestMethod.POST)
	@ResponseBody
	public String getEmployeeClaimTemplates(@ModelAttribute("employeeNumber") String employeeNumber,
			HttpServletRequest request) {

		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		
		AddClaimForm addClaimForm = addClaimLogic.getEmployeeClaimTemplates(employeeNumber, companyId, employeeId);

		return ResponseDataConverter.getJsonURLEncodedData(addClaimForm);
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.GET_LOGGED_IN_EMP_TEMPLATES, method = RequestMethod.POST)
	@ResponseBody
	public String getLoggedinEmpTemplates(HttpServletRequest request) {

		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		
		AddClaimForm addClaimForm = addClaimLogic.getClaimTemplates(companyId, employeeId, false);

		return ResponseDataConverter.getJsonURLEncodedData(addClaimForm);
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.DELETE_APPROVED_CLAIM_APPLICATION, method = RequestMethod.POST)
	@ResponseBody
	public String deleteApprovedClaimApplication(@ModelAttribute("attachementId") Long claimApplicationAttachementId,
			HttpServletRequest request) {
		
		AddClaimForm addClaimForm=null;
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		if(isURLfromAdmin(request)){
			AddClaimDTO addClaimDTO = new AddClaimDTO();
			addClaimDTO.setClaimApplicationAttachementId(FormatPreserveCryptoUtil.decrypt(claimApplicationAttachementId));			
			addClaimDTO.setCompanyId(companyId);
			addClaimDTO.setAdmin(true);
			addClaimForm = addClaimLogic.deleteApprovedClaimApplication(addClaimDTO);
		}
		return ResponseDataConverter.getJsonURLEncodedData(addClaimForm);
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.LUNDIN_BLOCK_LIST, method = RequestMethod.POST)
	@ResponseBody
	public String lundinBlockList(
			@RequestParam(value = "claimItemAccountCode", required = true) String claimItemAccountCode,
			HttpServletRequest request) {

		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		
		List<LundinTimesheetReportsForm> otBatchList = addClaimLogic.lundinBlockList(companyId, claimItemAccountCode);

		return ResponseDataConverter.getListToJson(otBatchList);
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.LUNDIN_AFE_LIST, method = RequestMethod.POST)
	@ResponseBody
	public String lundinAFEList(HttpServletRequest request,
			@RequestParam(value = "blockId", required = false) Long blockId,
			@RequestParam(value = "claimItemAccountCode", required = false) String claimItemAccountCode) {

		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		
		List<LundinTimesheetReportsForm> otBatchList = addClaimLogic.lundinAFEList(companyId, blockId,
				claimItemAccountCode);

		return ResponseDataConverter.getListToJson(otBatchList);
	}
	
	@Override
	@RequestMapping(value = PayAsiaURLConstant.CLAIM_APPLICATION_ITEM_LIST, method = RequestMethod.POST)
	@ResponseBody
	public String getClaimApplicationItemList(
			@ModelAttribute("claimApplicationId") Long claimApplicationId, HttpServletRequest request) {
		
		Long employeeId = Long.parseLong(UserContext.getUserId());
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		
		AddClaimDTO addClaimDTO = new AddClaimDTO();
		addClaimDTO.setCompanyId(companyId);
		addClaimDTO.setEmployeeId(employeeId);
		addClaimDTO.setClaimApplicationId(FormatPreserveCryptoUtil.decrypt(claimApplicationId));
		addClaimDTO.setAdmin(isURLfromAdmin(request));

		boolean hasLundinTimesheetModule = (boolean) request
				.getSession()
				.getAttribute(
						PayAsiaSessionAttributes.COMPANY_HAS_LUNDIN_TIMESHEET_MODULE);

		AddClaimForm addClaimForm = addClaimLogic.getClaimApplicationItemList(addClaimDTO);
		addClaimForm.setLundinTimesheetModule(hasLundinTimesheetModule);
		
		return ResponseDataConverter.getObjectToJson(addClaimForm);
	}
	
	
	private void setError(AddClaimForm addClaimForm, Locale locale) {

		String[] errorValArr = null;
		String[] errorVal = null;
		StringBuilder errorKeyFinalStr = new StringBuilder();
		String[] errorKeyArr;
		if (addClaimForm.getValidationClaimItemDTO() != null
				&& addClaimForm.getValidationClaimItemDTO().getErrorCode() == 1) {
			if (StringUtils.isNotBlank(addClaimForm.getValidationClaimItemDTO().getErrorKey())) {
				errorKeyArr = addClaimForm.getValidationClaimItemDTO().getErrorKey().split(";");
				if (StringUtils.isNotBlank(addClaimForm.getValidationClaimItemDTO().getErrorValue())) {
					errorValArr = addClaimForm.getValidationClaimItemDTO().getErrorValue().split(";");
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
			addClaimForm.getValidationClaimItemDTO().setErrorKey(errorKeyFinalStr.toString());
		}
	}
	
	
	private void setClaimApllicationError(AddClaimForm addClaimForm, Locale locale) {

		String[] errorValArr = null;
		String[] errorVal = null;
		StringBuilder errorKeyFinalStr = new StringBuilder();
		String[] errorKeyArr;
		if (addClaimForm.getValidateClaimApplicationDTO() != null
				&& addClaimForm.getValidateClaimApplicationDTO().getErrorCode() == 1) {
			if (StringUtils.isNotBlank(addClaimForm.getValidateClaimApplicationDTO().getErrorKey())) {
				errorKeyArr = addClaimForm.getValidateClaimApplicationDTO().getErrorKey().split(";");
				if (StringUtils.isNotBlank(addClaimForm.getValidateClaimApplicationDTO().getErrorValue())) {
					errorValArr = addClaimForm.getValidateClaimApplicationDTO().getErrorValue().split(";");
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
			addClaimForm.getValidateClaimApplicationDTO().setErrorKey(errorKeyFinalStr.toString());
		}
	}
	
	
	@Override
	@RequestMapping(value = PayAsiaURLConstant.VIEW_ATTACHMENT, method = RequestMethod.GET)
	public @ResponseBody byte[] viewAttachment(@RequestParam(value = "attachmentId", required = true) long claimApplicationItemAttachmentId,
			HttpServletResponse response) {
		
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		
		AddClaimDTO addClaimDTO = new AddClaimDTO();
		addClaimDTO.setClaimApplicationItemAttachmentId(FormatPreserveCryptoUtil.decrypt(claimApplicationItemAttachmentId));
		addClaimDTO.setCompanyId(companyId);
		addClaimDTO.setAdmin(true);

		ClaimApplicationItemAttach claimApplicationItemAttach = myClaimLogic.viewAttachment(addClaimDTO);

		byte[] byteFile = claimApplicationItemAttach.getAttachmentBytes();

		response.reset();
		String mimeType = URLConnection.guessContentTypeFromName(claimApplicationItemAttach.getFileName());
		response.setContentType("application/" + mimeType);
		response.setContentLength(byteFile.length);
		String filename = claimApplicationItemAttach.getFileName();

		response.setHeader("Content-Disposition", "attachment;filename=" + filename);

		return byteFile;
	}

	private boolean isURLfromAdmin(HttpServletRequest request) {

		String url = request.getRequestURL().toString();
		if (url != null && StringUtils.isNotBlank(url)) {
			return url.contains("/admin/");
		}
		return false;
	}
	

}
