package com.payasia.logic;

import java.util.List;

import com.payasia.common.form.LeaveGranterForm;
import com.payasia.common.form.LeaveGranterFormResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.form.YearEndProcessingForm;

public interface LeaveGranterLogic {

	LeaveGranterFormResponse getLeaveGrantBatchEmpDetailList(
			PageRequest pageDTO, SortCondition sortDTO, Long companyId,
			Long leaveGrantBatchDetailId, String employeeNumber);

	List<YearEndProcessingForm> getLeaveScheme(Long companyId);

	LeaveGranterFormResponse getLeaveGranterEmpList(PageRequest pageDTO,
			SortCondition sortDTO, Long companyId, Long leaveSchemeId,
			Long leaveTypeId);

	String deleteLeaveGrantBatchDetail(Long leaveGrantBatchDetailId);

	String deleteLeaveGrantBatchEmpDetail(Long leaveGrantBatchEmpDetailId);

	LeaveGranterFormResponse getAnnualRollbackEmpList(PageRequest pageDTO,
			SortCondition sortDTO, Long companyId, String fromDate,
			String toDate);

	LeaveGranterFormResponse getLeaveGranterLeaveTypeList(Long companyId,
			Long leaveSchemeId);

	Boolean callLeaveGrantProc(Long companyId, String leaveSchemeTypeIds,
			Boolean isNewHires, String fromDate, String toDate,
			String employeeIdsList);

	Boolean rollbackResignedEmployeeLeaveProc(Long companyId,
			String employeeIds, Long loggedInEmployeeId);

	LeaveGranterFormResponse getLeaveGrantBatchDetailList(PageRequest pageDTO,
			SortCondition sortDTO, Long companyId, String fromDate,
			String toDate, Long leaveType);

	List<LeaveGranterForm> getLeaveType(Long companyId);

}
