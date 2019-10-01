package com.payasia.api.common.hris;

import org.springframework.http.ResponseEntity;

import com.payasia.common.dto.DynamicFormDataForm;
import com.payasia.common.form.DataDictionaryForm;


public interface CommonMyProfileDetails {
	
	ResponseEntity<?> getHRISCompanyConfig();

	ResponseEntity<?> getSectionDetails();
	
	ResponseEntity<?> getDynamicForm(DynamicFormDataForm dynamicFormDataForm);

	ResponseEntity<?> getCodeDesc(DataDictionaryForm dataDictionaryForm);

	ResponseEntity<?> getEmployeeList();	
}
