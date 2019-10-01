package com.payasia.dao;

import java.sql.Timestamp;
import java.util.List;

import com.payasia.common.dto.DayWiseLeaveTranReportDTO;
import com.payasia.common.dto.EmployeeLeaveSchemeTypeHistoryDTO;
import com.payasia.common.dto.LeaveTranReportDTO;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.EmployeeLeaveSchemeTypeHistory;
import com.payasia.dao.bean.LeaveApplication;

public interface EmployeeLeaveSchemeTypeHistoryDAO {

	void save(EmployeeLeaveSchemeTypeHistory employeeLeaveSchemeTypeHistory);

	Long getCountBySchemeType(Long leaveSchemeTypeId, Long postLeaveTypeFilterId, String monthStartDate, String monthEndDate, String dateFormat,
			Long employeeId, PageRequest pageDTO, SortCondition sortDTO);

	List<EmployeeLeaveSchemeTypeHistory> findBySchemeType(long leaveSchemeTypeId, Long postLeaveTypeFilterId, String monthStartDate, String monthEndDate,
			String dateFormat, Long employeeId, PageRequest pageDTO, SortCondition sortDTO);

	void update(EmployeeLeaveSchemeTypeHistory employeeLeaveSchemeTypeHistory);

	void delete(EmployeeLeaveSchemeTypeHistory employeeLeaveSchemeTypeHistory);

	EmployeeLeaveSchemeTypeHistory findById(long employeeLeaveSchemeTypeHistoryId);

	List<Integer> getYearList(Long companyId);

	List<EmployeeLeaveSchemeTypeHistory> getLeaveTransactionreport(Long companyId, String startDate, String endDate, Long leaveTransactionId,
			Long leaveTypeId, String dateFormat, List<Long> empIdsList, Long leaveStatusId, Boolean isManager);

	List<EmployeeLeaveSchemeTypeHistoryDTO> getEmployeeLeaveTransDetailProc(Long employeeId, Long leaveSchemeTypeId, Integer year, String dateFormat,
			Long loggedInUser, Timestamp leaveCalendarStartDate, Timestamp leaveCalendarEndDate);

	void deleteByCondition(Long employeeLeaveSchemeTypeId, Long transactionTypeId, Long leaveApplicationId);

	EmployeeLeaveSchemeTypeHistory findByCondition(Long employeeLeaveSchemeTypeId, Long transactionTypeId, Long leaveApplicationId);

	List<LeaveTranReportDTO> getLeaveTransactionReportProc(Long companyId, String employeeIdList, String fromdate, String todate, String leaveTypeIdList,
			String leaveTransactionList, boolean multipleRecord, boolean includeApprovalCancel, boolean isIncludeResignedEmployees, String dateFormat,
			Boolean isManager, Long leaveReviewerId, boolean leavePreferencePreApproval, boolean leaveExtensionPreference);

	List<DayWiseLeaveTranReportDTO> getDayWiseLeaveTransactionReportProc(Long companyId, String employeeIdList, String fromdate, String todate,
			String leaveTypeList, String leaveTransactionList, boolean multipleRecord, boolean includeApprovalCancel, boolean isIncludeResignedEmployees,
			String dateFormat, Boolean isManager, Long leaveReviewerId, boolean leavePreferencePreApproval, boolean leaveExtensionPreference);

	EmployeeLeaveSchemeTypeHistory saveReturn(EmployeeLeaveSchemeTypeHistory employeeLeaveSchemeTypeHistory);

	EmployeeLeaveSchemeTypeHistory findByCondition(Long employeeLeaveSchemeTypeId, Long transactionTypeId, Timestamp startDate, Timestamp endDate);

	LeaveApplication findByLeaveApplicationId(Long leaveApplicationId);

	EmployeeLeaveSchemeTypeHistory findLeaveTransByCompanyId(Long leaveTranId, Long companyId);

	EmployeeLeaveSchemeTypeHistory findHistoryByCompanyId(Long historyId, Long companyId);
	
	EmployeeLeaveSchemeTypeHistory findEmployeeLeaveSchemeCCLCreditObj(Long employeeLeaveSchemeTypeId, Long appCodeID);

	void deleteByCondition(Long employeeLeaveSchemeTypeId, Long transactionTypeId);

}
