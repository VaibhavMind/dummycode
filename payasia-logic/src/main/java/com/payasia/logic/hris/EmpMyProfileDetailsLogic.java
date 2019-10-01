package com.payasia.logic.hris;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.payasia.common.dto.DynamicFormDataForm;
import com.payasia.common.dto.DynamicFormRecordDTO;
import com.payasia.common.dto.DynamicFormTableDataForm;
import com.payasia.common.dto.DynamicFormTableRecordDTO;
import com.payasia.common.form.CalculatoryForm;
import com.payasia.common.form.DataDictionaryForm;
import com.payasia.common.form.DynamicFormTableDocumentDTO;
import com.payasia.common.form.EmployeeDetailForm;
import com.payasia.common.form.EmployeeDocumentForm;
import com.payasia.common.form.EmployeeListFormPage;
import com.payasia.dao.bean.HRISChangeRequest;

import net.sf.json.JSONObject;

public interface EmpMyProfileDetailsLogic {
	
	EmployeeDetailForm getUserDetailsData(Long companyId,Long employeeId);

	DynamicFormRecordDTO getDynamicFormRecordData(DynamicFormDataForm dynamicFormDataForm);	
	
	List<DynamicFormTableRecordDTO> getDynamicFormTableRecordData(DynamicFormTableDataForm dynamicFormTableDataForm);

	String getReferenceValue(DataDictionaryForm dataDictionaryForm);

	String getCalculatoryValue(CalculatoryForm calculatoryFieldForm);
	
	HRISChangeRequest getChangesReqId(DataDictionaryForm dataDictionaryForm);

	Map<String, Object> saveUserDetails(DynamicFormDataForm dynamicFormDataForm, JSONObject formValue);

	Map<String, Object> saveTableDetails(DynamicFormDataForm dynamicFormDataForm, JSONObject employeeData);

	Map<String, Object> saveDocumentDetails(DynamicFormDataForm dynamicFormDataForm, JSONObject formValue,
			MultipartFile[] files);

	Map<String, Object> deleteTableDetails(DynamicFormDataForm dynamicFormDataForm);

	DynamicFormTableDocumentDTO getDocumentFile(Long companyId, Long employeeId, String fileName);

	EmployeeListFormPage getEmployeeDocHistory(EmployeeDocumentForm employeeDocumentForm);
}

	
