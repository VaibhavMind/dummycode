package com.payasia.dao;

import com.payasia.dao.bean.LeaveApplicationWorkflow;

public interface LeaveApplicationWorkflowDAO {

	LeaveApplicationWorkflow saveReturn(
			LeaveApplicationWorkflow leaveApplicationWorkflow);

	void save(LeaveApplicationWorkflow leaveApplicationWorkflow);

	LeaveApplicationWorkflow findByCondition(Long leaveApplicationId,
			Long createdById);

	LeaveApplicationWorkflow findByConditionLeaveStatus(
			Long leaveApplicationId, Long createdById, Long leaveStatusId);

	void deleteByCondition(Long leaveApplicationId);

}
