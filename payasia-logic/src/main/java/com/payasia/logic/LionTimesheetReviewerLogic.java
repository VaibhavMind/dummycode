package com.payasia.logic;

import java.util.List;

import com.payasia.common.dto.LundinEmployeeTimesheetReviewerDTO;
import com.payasia.common.form.EmployeeListForm;
import com.payasia.common.form.LionTimesheetReviewerForm;
import com.payasia.common.form.LundinReviewerResponseForm;
import com.payasia.common.form.LundinTimesheetReviewerForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;

public interface LionTimesheetReviewerLogic {

	List<LundinEmployeeTimesheetReviewerDTO> getWorkFlowRuleList(
			long employeeId, long companyId);

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

	LundinTimesheetReviewerForm getLionWorkFlow(Long companyId);

	LundinReviewerResponseForm getLionReviewerData(Long employeeId,
			Long companyId);

	LionTimesheetReviewerForm checkLionReviewer(Long employeeId,
			Long companyId);

	void deleteLionReviewer(Long employeeId);

	LundinTimesheetReviewerForm getLionWorkFlowLevel(Long companyId);

	void saveLionReviewer(
			LionTimesheetReviewerForm lionTimesheetReviewerForm, Long companyId);

	void updateLionReviewer(
			LundinTimesheetReviewerForm otTimesheetReviewerForm, Long companyId);

	String configureLionRevWorkflow(Long companyId,
			LundinTimesheetReviewerForm otTimesheetReviewerForm);

	LundinTimesheetReviewerForm importLionReviewer(
			LundinTimesheetReviewerForm otReviewerform, Long companyId);

	LundinReviewerResponseForm getLionReviewers(String searchCondition,
			String searchText, PageRequest pageDTO, SortCondition sortDTO,
			Long companyId);
}
