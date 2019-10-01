package com.payasia.api.emp.hris;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.payasia.common.dto.DynamicFormDataForm;
import com.payasia.common.dto.DynamicFormTableDataForm;
import com.payasia.common.form.CalculatoryForm;
import com.payasia.common.form.DataDictionaryForm;
import com.payasia.common.form.EmployeeDocumentForm;

public interface EmpMyProfileDetails {

	ResponseEntity<?> getDynamicFormData(DynamicFormDataForm dynamicFormDataForm);

	ResponseEntity<?> getUserDetails();

	ResponseEntity<?> getReference(DataDictionaryForm dataDictionaryForm);

	ResponseEntity<?> getCalculatory(CalculatoryForm calculatoryFieldForm);

	ResponseEntity<?> saveUserDetails(String userDetails);

	ResponseEntity<?> getChangesReqId(DataDictionaryForm dataDictionaryForm);

	ResponseEntity<?> getDynamicFormTableData(DynamicFormTableDataForm dynamicFormTableDataForm);
	
	ResponseEntity<?> saveTableDetails(String userDetails);
	
	ResponseEntity<?> deleteTableDetails(String userDetails);

	ResponseEntity<?> saveDocumentDetails(String userDetails, MultipartFile[] files);

	ResponseEntity<?> showHistory(EmployeeDocumentForm employeeDocumentForm);

	ResponseEntity<?> getDocumentFile(String fileName);

	

}
