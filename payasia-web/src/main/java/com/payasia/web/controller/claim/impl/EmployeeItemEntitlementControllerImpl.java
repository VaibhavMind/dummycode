package com.payasia.web.controller.claim.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.EmployeeItemEntitlementDetailsDTO;
import com.payasia.common.form.AssignClaimTemplateForm;
import com.payasia.common.form.EmployeeItemEntitlementForm;
import com.payasia.common.util.FileUtils;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.common.util.PayAsiaURLConstant;
import com.payasia.common.util.ResponseDataConverter;
import com.payasia.logic.EmployeeUtemEntitlementsLogic;
import com.payasia.web.controller.claim.EmployeeItemEntitlementController;

@Controller
@RequestMapping(value = PayAsiaURLConstant.ADMIN_EMPLOYEE_ITEM_ENTITLEMENT)
public class EmployeeItemEntitlementControllerImpl implements EmployeeItemEntitlementController {
	
	@Resource
	EmployeeUtemEntitlementsLogic employeeUtemEntitlementsLogic;

	@Override
	@RequestMapping(value = PayAsiaURLConstant.GET_CLAIM_ITEM_LIST, method = RequestMethod.GET)
	@ResponseBody
	public String getClaimItemList(@RequestParam(value = "claimTemplateName", required = true) String claimTemplateName) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		
		EmployeeItemEntitlementDetailsDTO employeeItemEntitlementDetailsDTO = employeeUtemEntitlementsLogic
				.getEmpItemEntitlementDetails(claimTemplateName, companyId);
		return ResponseDataConverter.getObjectToJson(employeeItemEntitlementDetailsDTO);
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.GET_ITEM_DETAILS, method = RequestMethod.GET)
	@ResponseBody
	public String getItemDetails(@RequestParam(value = "itemId", required = true) String itemId,
			@RequestParam(value = "employeeNumber", required = true) String employeeNumber,
			@RequestParam(value = "claimTemplate", required = true) String claimTemplateName) {
		
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		itemId=""+FormatPreserveCryptoUtil.decrypt(Long.parseLong(itemId));
		EmployeeItemEntitlementForm employeeItemEntitlementForm = employeeUtemEntitlementsLogic.getItemDetails(itemId,
				employeeNumber, claimTemplateName, companyId);
		return ResponseDataConverter.getObjectToJson(employeeItemEntitlementForm);
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.SAVE_ITEM_ENTITLEMENT_DETAILS, method = RequestMethod.POST)
	@ResponseBody
	public String saveItemEntitlementDetails(
			@ModelAttribute(value = "itemEntitlementForm") EmployeeItemEntitlementForm itemEntitlementForm) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		
		employeeUtemEntitlementsLogic.saveItemEntitlementDetails(companyId, itemEntitlementForm);
		
		return "";
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.IMPORT_ITEM_ENTITLEMENT_TEMPLATE, method = RequestMethod.POST)
	@ResponseBody
	public String importItemEntitlementTemplate(
			@ModelAttribute(value = "importAssignClaimTemplateForm") AssignClaimTemplateForm importAssignClaimTemplateForm) throws Exception {
		String message = PayAsiaConstants.PAYASIA_SUCCESS;
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		try {
			boolean isFileValid = false;
			if (importAssignClaimTemplateForm.getFileUpload() != null) {
				isFileValid = FileUtils.isValidFile(importAssignClaimTemplateForm.getFileUpload(),
						importAssignClaimTemplateForm.getFileUpload().getOriginalFilename(),
						PayAsiaConstants.ALLOWED_ONLY_UPLOAD_EXCLE_FILE_EXT, PayAsiaConstants.ALLOWED_ONLY_UPLOAD_EXCLE_MINE_TYPE, PayAsiaConstants.FILE_SIZE);
			}
			if (isFileValid) {
				employeeUtemEntitlementsLogic.importItemEntitlementTemplate(companyId, importAssignClaimTemplateForm);
			}

		} catch (Exception e) {
			message = PayAsiaConstants.PAYASIA_ERROR;
		}

		return message;
	}

	@Override
	@RequestMapping(value = PayAsiaURLConstant.DELETE_ITEM_ENTITLEMENT_DETAILS, method = RequestMethod.POST)
	@ResponseBody
	public String deleteItemEntitlementDetails(
			@ModelAttribute(value = "itemEntitlementForm") EmployeeItemEntitlementForm itemEntitlementForm) {
		
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		
		employeeUtemEntitlementsLogic.deleteItemEntitlementDetails(companyId, itemEntitlementForm);

		return "";
	}
}
