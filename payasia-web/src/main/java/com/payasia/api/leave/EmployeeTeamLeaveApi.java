package com.payasia.api.leave;

import org.springframework.http.ResponseEntity;

import com.payasia.api.leave.model.SearchParam;

public interface EmployeeTeamLeaveApi {
	
	ResponseEntity<?> doShowManagerNumberForEmployee(String jsonStr);
	
	ResponseEntity<?> doShowPostLeaveSchemeDataForEmployee(String jsonStr);
	
	ResponseEntity<?> doShowTeamEmployeeLeaveSchemeType(SearchParam searchParam);
	
	ResponseEntity<?> doShowTeamEmployeeLeaveSchemeTypeHistoryList(String jsonStr);
	
	ResponseEntity<?> doShowLeaveScheme(String jsonStr);

	ResponseEntity<?> doShowMyLeaveSchemeTypeList(String jsonStr);
	
	ResponseEntity<?> doGetLeaveTransactionHistoryInfo(String jsonStr);

	ResponseEntity<?> doShowManagerNameForEmployee(String jsonStr);

	ResponseEntity<?> doShowLeavePreference();

	ResponseEntity<?> teamMembersDetails();

}
