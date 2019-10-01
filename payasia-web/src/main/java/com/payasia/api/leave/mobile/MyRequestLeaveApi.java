package com.payasia.api.leave.mobile;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface MyRequestLeaveApi {

	ResponseEntity<?> doShowLeaveRequest(String jsonDataStr);
	
	ResponseEntity<?> applyLeave();
	
	ResponseEntity<?> submitedLeave(String jsonDataStr);
	
	ResponseEntity<?> deleteLeave(String jsonDataStr);
	
	ResponseEntity<?> getPendingLeaves();

	ResponseEntity<?> saveLeave(String jsonDataStr,MultipartFile files[]); 
	
	ResponseEntity<?> getDays(String jsonDataStr);
	
    ResponseEntity<?> employeeLeaveSchemeTypeInfo(String jsonDataStr);
    
    ResponseEntity<?> viewLeave(String jsonDataStr);
    
    ResponseEntity<?> withdrawLeave(String jsonDataStr);
    
	ResponseEntity<?> downloadAttachment(Long attachmentId);
	
	ResponseEntity<?> removeAttachement(String jsonDataStr);
	
	ResponseEntity<?>  getCompletedLeaves(String jsonDataStr);
	
	ResponseEntity<?> viewCancelLeave(String jsonDataStr);
	
	ResponseEntity<?> editLeaveApplication(String jsonDataStr);
	
	ResponseEntity<?> configLeave();
	
}
