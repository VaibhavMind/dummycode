package com.payasia.logic;

import java.util.List;

import com.payasia.common.form.EmployeeListForm;
import com.payasia.common.form.HRISReviewerForm;
import com.payasia.common.form.HRISReviewerResponseForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;

public interface HRISReviewerLogic {

	HRISReviewerResponseForm getHRISReviewers(String searchCondition,
			String searchText, PageRequest pageDTO, SortCondition sortDTO,
			Long companyId);

	HRISReviewerForm checkHRISReviewer(Long employeeId, Long companyId);

	void saveHRISReviewer(HRISReviewerForm hrisReviewerForm, Long companyId);

	HRISReviewerResponseForm searchEmployee(PageRequest pageDTO,
			SortCondition sortDTO, String empName, String empNumber,
			Long companyId, Long employeeId);

	void updateHRISReviewer(HRISReviewerForm hrisReviewerForm, Long companyId);

	HRISReviewerResponseForm searchEmployeeBySessionCompany(
			PageRequest pageDTO, SortCondition sortDTO, String empName,
			String empNumber, Long companyId, Long employeeId);

	List<EmployeeListForm> getEmployeeId(Long companyId, String searchString,
			Long employeeId);

	List<EmployeeListForm> getCompanyGroupEmployeeId(Long companyId,
			String searchString, Long employeeId);

	HRISReviewerForm getHRISWorkFlow(Long companyId);

	List<HRISReviewerForm> getWorkFlowRuleList();

	void deleteHRISReviewer(Long employeeId,Long companyId);

	String configureHRISRevWorkflow(Long companyId,
			HRISReviewerForm hrisReviewerForm);

	HRISReviewerForm getHRISWorkFlowLevel(Long companyId);

	HRISReviewerResponseForm getHRISReviewerData(Long employeeId, Long companyId);

	HRISReviewerForm importHRISReviewer(HRISReviewerForm hrisReviewerform,
			Long companyId);
	


}
