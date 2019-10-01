package com.payasia.logic;

import java.util.List;

import com.payasia.common.dto.LundinEmployeeTimesheetReviewerDTO;
import com.payasia.common.form.EmployeeListForm;
import com.payasia.common.form.LundinReviewerResponseForm;
import com.payasia.common.form.LundinTimesheetReviewerForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;

public interface LundinTimesheetReviewerLogic {

	List<LundinEmployeeTimesheetReviewerDTO> getWorkFlowRuleList(long employeeId, long companyId);

	LundinReviewerResponseForm searchEmployee(PageRequest pageDTO,
			SortCondition sortDTO, String empName, String empNumber,
			Long companyId, Long employeeId);

	LundinReviewerResponseForm searchEmployeeBySessionCompany(
			PageRequest pageDTO, SortCondition sortDTO, String empName,
			String empNumber, Long companyId, Long employeeId);

	List<EmployeeListForm> getEmployeeId(Long companyId, String searchString,
			Long employeeId);

	List<EmployeeListForm> getCompanyGroupEmployeeId(Long companyId,
			String searchString, Long employeeId);

	List<LundinTimesheetReviewerForm> getWorkFlowRuleList();

	LundinTimesheetReviewerForm getLundinWorkFlow(Long companyId);

	LundinReviewerResponseForm getLundinReviewerData(Long employeeId,
			Long companyId);

	LundinTimesheetReviewerForm checkLundinReviewer(Long employeeId,
			Long companyId);

	void deleteLundinReviewer(Long employeeId);

	LundinTimesheetReviewerForm getLundinWorkFlowLevel(Long companyId);

	void saveLundinReviewer(
			LundinTimesheetReviewerForm otTimesheetReviewerForm, Long companyId);

	void updateLundinReviewer(
			LundinTimesheetReviewerForm otTimesheetReviewerForm, Long companyId);

	String configureLundinRevWorkflow(Long companyId,
			LundinTimesheetReviewerForm otTimesheetReviewerForm);

	LundinTimesheetReviewerForm importLundinReviewer(
			LundinTimesheetReviewerForm otReviewerform, Long companyId);

	LundinReviewerResponseForm getLundinReviewers(String searchCondition,
			String searchText, PageRequest pageDTO, SortCondition sortDTO,
			Long companyId);
	
	
}
