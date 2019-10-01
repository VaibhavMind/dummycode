package com.payasia.api.leave;

import java.io.IOException;

import org.springframework.http.ResponseEntity;

import com.payasia.api.leave.model.SearchParam;

public interface EmployeeLeaveCancelApi {
	
	ResponseEntity<?> doShowCancelLeaves(SearchParam searchParam);
	
	ResponseEntity<?> doShowLeaveReviewers(String jsonStr);
	
	ResponseEntity<?> doShowLeaveCalendarMonthList(String jsonStr);
	
	ResponseEntity<?> doShowHolidayCalender(String jsonStr);

	ResponseEntity<?> employeeViewProfileImage(String jsonStr) throws IOException;

	ResponseEntity<?> doCancelLeave(String jsonStr);

	ResponseEntity<?> doshowEmployeeOnLeaveByDate(SearchParam searchParamObj, String leaveID[]);
}
