package com.payasia.logic;

import java.util.List;

import com.payasia.common.form.ClaimPreferenceForm;
import com.payasia.common.form.EmployeeFilterListForm;

public interface ClaimPreferenceLogic {

	ClaimPreferenceForm getClaimPreference(Long companyId);

	void saveClaimPreference(ClaimPreferenceForm claimPreferenceForm, Long companyId);

	boolean getAdditionalValueOfClaimPreference(Long companyId);

	List<EmployeeFilterListForm> getEmployeeFilterList(Long companyId);
}
