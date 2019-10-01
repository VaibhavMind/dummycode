package com.payasia.logic;

import java.util.List;

import com.payasia.common.dto.AppCodeDTO;
import com.payasia.common.dto.LeaveSessionDTO;
import com.payasia.common.form.LeaveGranterForm;
import com.payasia.common.form.LeaveYearEndEmployeeDetailForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.PendingItemsForm;
import com.payasia.common.form.PendingItemsFormResponse;
import com.payasia.common.form.SortCondition;
import com.payasia.common.form.YearEndProcessFilterForm;
import com.payasia.common.form.YearEndProcessForm;
import com.payasia.common.form.YearEndProcessingFormResponse;

public interface YearEndProcessingLogic {

	PendingItemsFormResponse getPendingLeaves(PageRequest pageDTO,
			SortCondition sortDTO, Long leaveTypeId, Long leaveSchemeId,
			Long companyId);

	PendingItemsForm getDataForLeaveReview(Long companyId,
			Long leaveApplicationId);

	List<AppCodeDTO> getWorkflowTypeList();

	void acceptLeave(PendingItemsForm pendingItemsForm, Long employeeId,
			LeaveSessionDTO sessionDTO);

	void rejectLeave(PendingItemsForm pendingItemsForm, Long employeeId,
			LeaveSessionDTO sessionDTO);

	YearEndProcessingFormResponse getLeaveYearEndEmpDetailList(
			PageRequest pageDTO, SortCondition sortDTO, Long companyId,
			Long leaveYearEndBatchId, String employeeNumber);

	YearEndProcessingFormResponse getLeaveYearEndBatchList(PageRequest pageDTO,
			SortCondition sortDTO, Long companyId, int year, Long leaveTypeId,
			Long groupId, Long leaveSchemeId);

	List<Integer> getDistinctYears(Long companyId);

	List<LeaveGranterForm> getLeaveType(Long companyId);

	String deleteLeaveYearEndBatch(Long leaveYearBatchId);

	String deleteLeaveYearEndEmpDetail(Long leaveGrantBatchEmpDetailId);

	YearEndProcessingFormResponse getYEPLeaveTypeList(SortCondition sortDTO,
			Long companyId, Long leaveSchemeId);

	YearEndProcessForm getGroupCompanies();

	void performYearEndProcessSave(YearEndProcessForm yearEndProcessForm);

	YearEndProcessFilterForm getYearEndProcessFilterData(Long groupId);

	YearEndProcessFilterForm getYearEndProcessFilterDataOnCmpChange(
			Long companyId);

	LeaveYearEndEmployeeDetailForm editLeaveYearEndEmpDetail(
			Long leaveYearEndEmployeeDetailId);

	void updateEmployeeYearEndSummaryDetail(
			LeaveYearEndEmployeeDetailForm leaveYearEndEmployeeDetailForm);

}
