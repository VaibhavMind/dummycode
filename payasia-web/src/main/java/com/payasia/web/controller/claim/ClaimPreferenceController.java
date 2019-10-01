package com.payasia.web.controller.claim;

import com.payasia.common.form.ClaimPreferenceForm;

public interface ClaimPreferenceController {

	void saveClaimPreferences(ClaimPreferenceForm claimPreferenceForm);

	String getClaimPreference();

	String getEmployeeFilterList();

}
