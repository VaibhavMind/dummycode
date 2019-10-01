package com.payasia.logic;

import java.util.List;

import com.payasia.common.dto.AddClaimDTO;
import com.payasia.common.dto.AssignClaimTemplateFormRes;
import com.payasia.common.form.AssignClaimSchemeResponse;
import com.payasia.common.form.AssignClaimTemplateForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;

public interface AssignClaimTemplateLogic {

	List<AssignClaimTemplateForm> getClaimTemplateList(Long companyId);

	String assignClaimTemplate(Long companyId, AssignClaimTemplateForm assignClaimTemplateForm);

	String updateAssignClaimTemplate(Long companyId, AssignClaimTemplateForm assignClaimTemplateForm);

	AssignClaimTemplateForm importAssignClaimTemplate(AssignClaimTemplateForm assignClaimTemplateForm, Long companyId);

	AssignClaimTemplateFormRes searchAssignClaimTemplate(AddClaimDTO claimDTO, PageRequest pageDTO,
			SortCondition sortDTO);

	AssignClaimTemplateForm getEmployeeClaimTemplateData(AddClaimDTO addClaimDTO);

	String deleteEmployeeClaimTemplateId(AddClaimDTO addClaimDTO);

	AssignClaimSchemeResponse searchEmployee(PageRequest pageDTO, SortCondition sortDTO, String empName,
			String empNumber, Long companyId, Long employeeId);
}
