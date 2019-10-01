package com.payasia.logic.hris;

import java.util.List;
import java.util.Map;

import com.payasia.common.dto.CodeDescDTO;
import com.payasia.common.dto.DynamicFormDataForm;
import com.payasia.common.dto.DynamicFormDetailsDTO;
import com.payasia.common.dto.DynamicFormTableDataForm;
import com.payasia.common.dto.DynamicFormTableRecordDTO;
import com.payasia.common.dto.EmployeeListDTO;
import com.payasia.common.dto.SectionDetailsDTO;
import com.payasia.common.form.DataDictionaryForm;
import com.payasia.dao.bean.DynamicForm;
import com.payasia.dao.bean.DynamicFormTableRecord;


public interface CommonMyProfileDetailsLogic {

	List<SectionDetailsDTO> getSectionDetails(Long companyId,Long employeeId);
	
	DynamicFormDetailsDTO getDynamicForm(Long companyId,DynamicFormDataForm dynamicFormDataForm,Long languageId);

	List<CodeDescDTO> getCodeDesc(Long companyId, DataDictionaryForm dataDictionaryForm);

	List<DynamicFormTableRecordDTO> getDynamicFormTableRecordValue(DynamicFormTableDataForm dynamicFormTableDataForm,
			List<DynamicFormTableRecord> dynamicFormTableRecordList);

	Map<String, Boolean> getHRISCompanyConfig(Long companyId);

	DynamicForm getDynamicForm(DynamicFormDataForm dynamicFormDataForm);

	List<EmployeeListDTO> getEmployeeList(Long companyId);
}
