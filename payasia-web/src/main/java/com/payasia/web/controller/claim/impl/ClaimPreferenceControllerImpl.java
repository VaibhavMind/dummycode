package com.payasia.web.controller.claim.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.payasia.common.bean.util.UserContext;
import com.payasia.common.form.ClaimPreferenceForm;
import com.payasia.common.form.EmployeeFilterListForm;
import com.payasia.common.util.PayAsiaURLConstant;
import com.payasia.common.util.ResponseDataConverter;
import com.payasia.logic.ClaimPreferenceLogic;
import com.payasia.web.controller.claim.ClaimPreferenceController;

/**
 * The Class ClaimPreferenceControllerImpl.
 */
@Controller
@RequestMapping(value = PayAsiaURLConstant.ADMIN_CLAIM_PREFERENCE)
public class ClaimPreferenceControllerImpl implements ClaimPreferenceController {

	@Resource
	ClaimPreferenceLogic claimPreferenceLogic;

	@Override
	// @PreAuthorize(PayAsiaPrivilegeConstant.CLAIM_PREFERENCE)
	@RequestMapping(value = PayAsiaURLConstant.SAVE_CLAIM_PREFERENCE, method = RequestMethod.POST)
	@ResponseBody
	public void saveClaimPreferences(
			@ModelAttribute(value = "claimPreferenceForm") ClaimPreferenceForm claimPreferenceForm) {

		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		claimPreferenceLogic.saveClaimPreference(claimPreferenceForm, companyId);

	}

	@Override
	// @PreAuthorize(PayAsiaPrivilegeConstant.CLAIM_PREFERENCE)
	@RequestMapping(value = PayAsiaURLConstant.CLAIM_PREFERENCE_FOR_EDIT, method = RequestMethod.POST)
	public @ResponseBody String getClaimPreference() {

		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		ClaimPreferenceForm claimPreferenceForm = claimPreferenceLogic.getClaimPreference(companyId);

		return ResponseDataConverter.getObjectToJson(claimPreferenceForm);
		
	}

	@Override
	// @PreAuthorize(PayAsiaPrivilegeConstant.CLAIM_PREFERENCE)
	@RequestMapping(value = PayAsiaURLConstant.GET_EMPLOYEE_FILTER_LIST, method = RequestMethod.POST)
	@ResponseBody
	public String getEmployeeFilterList() {
		
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		List<EmployeeFilterListForm> filterList = claimPreferenceLogic.getEmployeeFilterList(companyId);
		
		return ResponseDataConverter.getListToJson(filterList);
	}
}
