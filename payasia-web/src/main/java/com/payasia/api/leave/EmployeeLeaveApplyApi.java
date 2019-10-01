package com.payasia.api.leave;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface EmployeeLeaveApplyApi {

	ResponseEntity<?> doShowLeaveSchemeInfo();
	
	ResponseEntity<?> doShowLeaveTypes(String jsonStr);
	
	ResponseEntity<?>  doShowLeaveReviewersEmailAndCustomFields(String jsonStr);
	
	ResponseEntity<?>  doShowLeaveBalance(String jsonStr);
	
	ResponseEntity<?>  doShowLeaveDays(String jsonStr);
	
	//ResponseEntity<?> doApplyLeave(String jsonStr);

	ResponseEntity<?> doApplyLeave(String jsonStr, MultipartFile[] files);

	ResponseEntity<?> doSaveDefaultEmailCC(String jsonStr);

	ResponseEntity<?> doShowDefaultEmailCC(String jsonStr);

	//ResponseEntity<?> doApplyLeave(String jsonStr, MultipartFile file);

	//ResponseEntity<?> doApplyLeave(AddLeaveForm addLeaveForm1);
	
	 /*ResponseEntity<?> doApplyLeave(String jsonStr,MultipartFile files[]);*/
	
   ResponseEntity<?> deleteAttachment(String attachmentId);
	
}
