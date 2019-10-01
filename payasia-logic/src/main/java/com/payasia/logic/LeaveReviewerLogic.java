package com.payasia.logic;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.payasia.common.form.EmployeeListForm;
import com.payasia.common.form.LeaveReviewerForm;
import com.payasia.common.form.LeaveReviewerResponseForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;

@Transactional
public interface LeaveReviewerLogic {

	List<LeaveReviewerForm> getLeaveSchemeList(Long companyId);

	List<LeaveReviewerForm> getWorkFlowRuleList();

	LeaveReviewerResponseForm getLeaveReviewers(String searchCondition,
			String searchText, PageRequest pageDTO, SortCondition sortDTO,
			Long companyId);

	LeaveReviewerResponseForm searchEmployee(PageRequest pageDTO,
			SortCondition sortDTO, String empName, String empNumber,
			Long companyId, Long employeeId);

	LeaveReviewerResponseForm getLeaveType(Long employeeId, Long companyId);

	LeaveReviewerForm checkLeaveReviewer(Long employeeId,
			Long employeeLeaveSchemeId, Long employeeLeaveSchemeTypeId,
			Long companyId);

	void deleteLeaveReviewer(Long[] filterIds);

	LeaveReviewerResponseForm getLeaveReviewerData(Long[] filterIds,
			Long companyId);

	LeaveReviewerForm getLeaveTypeWorkFlow(Long employeeLeaveSchemeTypeId,
			Long employeeLeaveSchemeId);

	String isAllowManagerSelfApproveLeave(Long companyId);

	List<EmployeeListForm> getCompanyGroupEmployeeId(Long companyId,
			String searchString, Long employeeId);

	List<EmployeeListForm> getEmployeeId(Long companyId, String searchString,
			Long employeeId);

	LeaveReviewerResponseForm searchEmployeeBySessionCompany(
			PageRequest pageDTO, SortCondition sortDTO, String empName,
			String empNumber, Long companyId, Long employeeId);

	LeaveReviewerResponseForm getActiveWithFutureLeaveScheme(Long employeeId,
			Long companyId);

	LeaveReviewerResponseForm getLeaveTypeListForleaveScheme(
			Long employeeLeaveSchemeId, Long companyId);

	LeaveReviewerForm importLeaveReviewer(LeaveReviewerForm leaveReviewerForm,
			Long companyId);

	void saveLeaveReviewer(LeaveReviewerForm leaveReviewerForm, Long companyId,
			Long loggedInEmployeeId);

	void updateLeaveReviewer(LeaveReviewerForm leaveReviewerForm,
			Long companyId, Long loggedInEmployeeId);

}
