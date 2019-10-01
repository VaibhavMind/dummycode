package com.payasia.dao;

import java.util.List;

import com.payasia.dao.bean.LeaveSchemeWorkflow;

public interface LeaveSchemeWorkflowDAO {

	LeaveSchemeWorkflow findByID(long leaveSchemeWorkflowId);

	void delete(LeaveSchemeWorkflow leaveSchemeWorkflow);

	void save(LeaveSchemeWorkflow leaveSchemeWorkflow);

	void update(LeaveSchemeWorkflow leaveSchemeWorkflow);

	List<LeaveSchemeWorkflow> findByCondition(Long leaveSchemeId);

	void deleteByCondition(long leaveSchemeId);

	LeaveSchemeWorkflow findByLeaveSchemeIdRuleName(Long leaveSchemeId,
			String leaveReviewerRule);

}
