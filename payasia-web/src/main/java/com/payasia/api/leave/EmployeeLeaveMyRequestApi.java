package com.payasia.api.leave;

import java.io.IOException;
import java.util.Locale;

import javax.xml.bind.JAXBException;

import org.springframework.http.ResponseEntity;
import org.xml.sax.SAXException;

import com.itextpdf.text.DocumentException;
import com.payasia.api.leave.model.SearchParam;
import com.payasia.common.form.AddLeaveForm;

public interface EmployeeLeaveMyRequestApi {

    ResponseEntity<?> getDataForPendingLeave(String strJson);

	ResponseEntity<?> deleteLeave(String strJson);

	ResponseEntity<?> viewLeave(String strJson);

	ResponseEntity<?> withdrawLeave(String strJson);

	ResponseEntity<?> extensionLeave(String strJson, Locale locale);

	ResponseEntity<?> viewLeave(SearchParam searchParamObj, String requestType);
	
	ResponseEntity<?> doPrintLeave(String strJson) throws DocumentException, IOException, JAXBException, SAXException;

	ResponseEntity<?> viewAttachment(String strJson) throws DocumentException, IOException, JAXBException, SAXException;

	ResponseEntity<?> getLeaveCustomFields(String strJson);

	ResponseEntity<?> extendLeaveEmployee(Long leaveApplicationId);

	ResponseEntity<?> getLeaveBalance(Long employeeLeaveSchemeTypeId);
	
	ResponseEntity<?> getEmployeeLeaveDays(SearchParam searchParamObj, Long employeeLeaveSchemeTypeId);

	ResponseEntity<?> extensionLeave(AddLeaveForm addLeaveForm);

}
