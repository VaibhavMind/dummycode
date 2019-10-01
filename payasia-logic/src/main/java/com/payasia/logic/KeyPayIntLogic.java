package com.payasia.logic;

import java.util.Map;

public interface KeyPayIntLogic {

	void getPayRunDetails(Long companyId, String baseURL, String apiKey);

	Map<String, Long> getLeaveCategoryDetails(String baseURL, String apiKey);

	void sendCancelledLeaveApp(Long companyId,
			Map<String, Long> leaveCategoryMap, String baseURL, String apiKey);

	void sendApprovedLeaveApp(Long companyId,
			Map<String, Long> leaveCategoryMap, String baseURL, String apiKey);

	void updateEmployeeExternalId(Long companyId, String baseURL, String apiKey);

	boolean checkLeaveStatusForCancellation(Long cancelLeaveApplicationId,
			Long companyId);

}
