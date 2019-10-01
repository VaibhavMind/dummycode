package com.payasia.api.leave;

import org.springframework.http.ResponseEntity;

import com.payasia.api.leave.model.SearchParam;

public interface EmployeeLeaveDashboardApi {

	ResponseEntity<?> doShowLeaveDashboard(SearchParam searchParam, int fromDate,int toDate,int year);
	
	ResponseEntity<?> isEncashedVisible();
	
	ResponseEntity<?> doShowYearList();
	
	ResponseEntity<?> doShowEmployeeLeaveSchemeTypeHistoryList(String jsonStr);

}
