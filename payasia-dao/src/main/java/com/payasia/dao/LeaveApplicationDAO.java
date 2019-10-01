package com.payasia.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Tuple;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import com.payasia.common.dto.AddLeaveConditionDTO;
import com.payasia.common.dto.LeaveBalanceSummaryConditionDTO;
import com.payasia.common.dto.LeaveConditionDTO;
import com.payasia.common.dto.LeaveDTO;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.LeaveApplication;

public interface LeaveApplicationDAO {

	void save(LeaveApplication leaveApplication);

	void update(LeaveApplication leaveApplication);

	void delete(LeaveApplication leaveApplication);

	LeaveApplication saveReturn(LeaveApplication leaveApplication);

	List<LeaveApplication> findByCondition(AddLeaveConditionDTO conditionDTO, PageRequest pageDTO,
			SortCondition sortDTO);

	Long getCountForCondition(AddLeaveConditionDTO conditionDTO);

	LeaveApplication findById(Long leaveApplicationId);

	Path<String> getSortPathForleaveApplication(SortCondition sortDTO, Root<LeaveApplication> leaveAppRoot);

	List<LeaveApplication> findByLeaveAppIds(Long companyId, ArrayList<Long> leaveAppIdList, PageRequest pageDTO,
			SortCondition sortDTO);

	Long getCountLeaveAppIds(Long companyId, ArrayList<Long> leaveAppIdList);

	List<LeaveApplication> findEmployeeLeaveTransactions(AddLeaveConditionDTO conditionDTO, PageRequest pageDTO,
			SortCondition sortDTO, Long leaveApplicationId);

	List<Tuple> findLeaveCancellationIds(Long companyId);

	List<LeaveApplication> findByConditionLeaveCancel(AddLeaveConditionDTO conditionDTO, PageRequest pageDTO,
			SortCondition sortDTO);

	Long getCountForConditionLeaveCancel(AddLeaveConditionDTO conditionDTO);

	List<LeaveApplication> findByConditionCompletedForCancellation(AddLeaveConditionDTO conditionDTO,
			PageRequest pageDTO, SortCondition sortDTO);

	LeaveDTO validateLeaveApplication(LeaveConditionDTO leaveConditionDTO);

	List<LeaveApplication> findByLeaveStatus(List<String> leaveStatusNames, Long companyId);

	Long getCountForConditionSubmittedLeaveCancel(AddLeaveConditionDTO conditionDTO);

	List<LeaveApplication> findByConditionSubmittedLeaveCancel(AddLeaveConditionDTO conditionDTO, PageRequest pageDTO,
			SortCondition sortDTO);

	List<LeaveApplication> findByConditionSubmitted(AddLeaveConditionDTO conditionDTO, PageRequest pageDTO,
			SortCondition sortDTO);

	List<LeaveApplication> getPendingLeaveApplEmps(List<String> leaveStatusList, Long leaveTypeId, Long leaveSchemeId,
			Long companyId);

	Integer getCountPendingLeaveAppl(List<String> leaveStatusList, Long leaveTypeId, Long leaveSchemeId,
			Long companyId);

	List<LeaveApplication> getLeaveApplicationLeaveSchemeTypeId(Long companyId, Long leaveSchemeTypeId);

	List<LeaveApplication> findByConditionForRejected(AddLeaveConditionDTO conditionDTO, PageRequest pageDTO,
			SortCondition sortDTO);

	Long getCountForConditionRejected(AddLeaveConditionDTO conditionDTO);

	List<LeaveApplication> findByConditionWithDrawnCancel(AddLeaveConditionDTO conditionDTO, PageRequest pageDTO,
			SortCondition sortDTO);

	Long getCountForConditionWithdrawnCancel(AddLeaveConditionDTO conditionDTO);

	List<LeaveApplication> findCancelledLeaveApplications(Long leaveAppId, Long companyId);

	List<LeaveApplication> findEmployeesOnLeave(AddLeaveConditionDTO conditionDTO);

	List<LeaveApplication> findByLeaveStatus(LeaveBalanceSummaryConditionDTO conditionDTO, Long companyId,
			List<String> leaveStatusList, PageRequest pageDTO, SortCondition sortDTO);

	Long getCountForCondLeaveStatus(LeaveBalanceSummaryConditionDTO conditionDTO, Long companyId,
			List<String> leaveStatusList);

	List<LeaveApplication> findByYearAndMonth(LeaveBalanceSummaryConditionDTO conditionDTO, Long companyId,
			Long employeeId, Long reviewerId, List<String> leaveStatusList, String year, String month);

	List<LeaveApplication> findByLeaveStatusAndReviewer(LeaveBalanceSummaryConditionDTO conditionDTO, Long companyId,
			List<String> leaveStatusList, Long reviewerId, Long employeeId, PageRequest pageDTO, SortCondition sortDTO);

	Integer getCountForCondLeaveStatusAndReviewer(LeaveBalanceSummaryConditionDTO conditionDTO, Long companyId,
			Long employeeId, Long reviewerId, List<String> leaveStatusList);

	List<LeaveApplication> findByYearAndMonthForManager(LeaveBalanceSummaryConditionDTO conditionDTO, Long companyId,
			Long reviewerId, List<String> leaveStatusList, String year, String month);

	Integer getCountEmployeeLeaveTransactions(AddLeaveConditionDTO conditionDTO, Long leaveApplicationId);

	List<LeaveApplication> getApprovedNCancelLeaveForKeyPayInt(Long maxLeaveApplicationId, Long companyId,
			String leaveStatusName);

	List<Object[]> getLeaveAppForAutoApproval(Long companyId);

	LeaveApplication findByLeaveApplicationIdAndEmpId(Long leaveApplicationId, Long empId, Long companyId);

	List<LeaveApplication> findByLeaveAppIdsEmp(Long companyId, Long employeeId, ArrayList<Long> leaveAppIdList,
			PageRequest pageDTO, SortCondition sortDTO);

	LeaveApplication findLeaveApplicationByCompanyId(Long leaveApplicationId, Long companyId);

	LeaveApplication findByLeaveApplicationIdAndEmployeeId(Long leaveApplicationId, Long empId);
	
	List<LeaveApplication> findByConditionForAllLeaveRequest(AddLeaveConditionDTO conditionDTO, PageRequest pageDTO,
			SortCondition sortDTO);
	
	Long getCountForConditionAllLeave(AddLeaveConditionDTO conditionDTO);

	Long getCountForConditionCompletedForCancellation(AddLeaveConditionDTO conditionDTO);

	boolean findIsOnLeave(AddLeaveConditionDTO conditionDTO);

}
