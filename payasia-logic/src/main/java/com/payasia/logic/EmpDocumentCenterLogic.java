package com.payasia.logic;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.payasia.common.dto.SelectOptionDTO;
import com.payasia.common.form.CompanyDocumentCenterResponseForm;
import com.payasia.common.form.EmpDocumentCenterForm;
import com.payasia.common.form.EmpDocumentCentreResponse;
import com.payasia.common.form.EmployeeDocFormResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;

@Transactional
public interface EmpDocumentCenterLogic {

	EmpDocumentCentreResponse searchDocument(String searchCondition, String searchText, PageRequest pageDTO,
			SortCondition sortDTO, Long employeeId);

	void uploadDocument(EmpDocumentCenterForm employeeDocumentCenterForm, Long companyId, Long employeeId);

	String viewDocument(long docId);

	void updateDocument(EmpDocumentCenterForm empDocumentCenterForm, Long employeeId);

	void deleteCompanyDocument(Long docId);

	CompanyDocumentCenterResponseForm searchDocumentEmployeeDocumentCenter(String searchCondition, String searchText,                                  //changed return type
			PageRequest pageDTO, SortCondition sortDTO, Long companyId, Long categoryId, Long employeeId);

	String viewDocument(long docId, Long companyId, Long loggedInEmployeeId);

	EmployeeDocFormResponse searchEmployeeDocument(String searchCondition, String searchText, PageRequest pageDTO,
			SortCondition sortDTO, Long companyId, Long categoryId, Long employeeId);

	List<SelectOptionDTO> fetchList();

}
