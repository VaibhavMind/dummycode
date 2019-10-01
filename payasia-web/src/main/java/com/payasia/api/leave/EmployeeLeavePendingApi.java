package com.payasia.api.leave;

import java.io.IOException;
import java.util.Locale;

import javax.xml.bind.JAXBException;

import org.springframework.http.ResponseEntity;
import org.xml.sax.SAXException;

import com.itextpdf.text.DocumentException;
import com.payasia.api.leave.model.SearchParam;
import com.payasia.common.form.PendingItemsForm;

public interface EmployeeLeavePendingApi {
	/*ResponseEntity<?> getPendingLeaves(SearchParam searchParamObj,Locale locale);*/
	
	ResponseEntity<?> getDataForLeaveReviewEmp(String jsonStr);
	
 ResponseEntity<?> viewMultipleLeaveApplicationsEmp(String jsonStr,Locale locale);
 
 
 ResponseEntity<?> reviewMultipleLeaveApp(PendingItemsForm pendingItemsFrm,Locale locale);



//ResponseEntity<?>  getEmployeesOnLeavesEmp(String strJson,Locale locale);

 //ResponseEntity<?> viewLeaveTransactionsEmp(String strJson);
 
 ResponseEntity<?> acceptLeave(PendingItemsForm pendingItemsForm ,Locale locale);
 ResponseEntity<?> rejectLeave(PendingItemsForm pendingItemsForm,Locale locale);
 
 
 ResponseEntity<?> forwardLeaveEmp(PendingItemsForm pendingItemsForm,Locale locale);
 
 //public ResponseEntity<?> printLeaveApplicationFormEmp(Long leaveApplicationReviewerId,javax.servlet.http.HttpServletResponse response);
 
 public ResponseEntity<?> searchEmployee(String strJson);
 
 public ResponseEntity<?> showEmpLeaveWorkflowStatusEmp(String strJson,Locale locale);
 
 public ResponseEntity<?> showEmpLeaveWorkflowStatusEmpForMobile(String strJson,Locale locale);
 
 public ResponseEntity<?>  viewAttachmentRev(String strJson)throws DocumentException, IOException, JAXBException, SAXException;

 ResponseEntity<?> getPendingLeaves(SearchParam searchParamObj,String requestType);

ResponseEntity<?> viewLeaveTransactionsEmp(SearchParam searchParamObj);

ResponseEntity<?> printLeaveApplicationFormEmp(String strJson)
		throws DocumentException, IOException, JAXBException, SAXException;

ResponseEntity<?> getEmployeesOnLeavesEmp(SearchParam searchParamObj);

ResponseEntity<?> reviewMultipleLeaveApproveandForward(PendingItemsForm pendingItemsFrm, Locale locale);

}
