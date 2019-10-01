package com.payasia.dao;

import java.util.List;

import com.payasia.common.dto.PendingLeaveDTO;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.LeaveApplication;
import com.payasia.dao.bean.LeaveApplicationReviewer;

public interface LeaveApplicationReviewerDAO {

	void save(LeaveApplicationReviewer leaveApplicationReviewer);

	void update(LeaveApplicationReviewer leaveApplicationReviewer);

	LeaveApplicationReviewer saveReturn(
			LeaveApplicationReviewer leaveApplicationReviewer);

	List<LeaveApplicationReviewer> findByCondition(Long empId,
			PageRequest pageDTO, SortCondition sortDTO);

	LeaveApplicationReviewer findById(Long reviewId);

	List<LeaveApplicationReviewer> findByConditionLeaveType(Long empId,
			PageRequest pageDTO, SortCondition sortDTO,
			PendingLeaveDTO pendingLeaveDTO);

	Long getCountForCondition(Long employeeId);

	List<LeaveApplicationReviewer> checkEmployeeReviewer(Long employeeId,
			List<String> leaveStatusList);

	void deleteByCondition(Long leaveApplicationId);

	List<LeaveApplicationReviewer> findByConditionLeaveTypeForAdmin(Long empId,
			Long companyId, PageRequest pageDTO, SortCondition sortDTO,
			List<String> leaveStatusNames, PendingLeaveDTO pendingLeaveDTO);

	Long getCountForConditionForAdmin(Long employeeId, Long companyId,
			PendingLeaveDTO pendingLeaveDTO);

	Integer getLeaveReviewerCount(Long leaveApplicationId);

	List<LeaveApplicationReviewer> getPendingLeaveApplicationByIds(Long empId,
			List<Long> leaveApplicationRevIdsList);
	 LeaveApplicationReviewer getLeaveApplicationReviewerDetail(Long claimApplicationReviewerId,
			Long employeeId);

	boolean checkLeaveReviewers(LeaveApplication leaveApplication, Long leaveReviewerId);
}
