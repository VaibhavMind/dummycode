package com.payasia.dao;

import java.util.List;

import com.payasia.dao.bean.LeaveSchemeTypeWorkflow;

public interface LeaveSchemeTypeWorkflowDAO {

	List<LeaveSchemeTypeWorkflow> findByCondition(Long leaveTypeId);

	LeaveSchemeTypeWorkflow findByID(long leaveSchemeTypeId);

	LeaveSchemeTypeWorkflow findByLeaveSchemeIdRuleName(Long leaveSchemeTypeId,
			String leaveReviewerRule);

	void update(LeaveSchemeTypeWorkflow leaveSchemeTypeWorkflow);

	void delete(LeaveSchemeTypeWorkflow leaveSchemeTypeWorkflow);

	void save(LeaveSchemeTypeWorkflow leaveSchemeTypeWorkflow);

	void deleteByCondition(long leaveSchemeTypeId);

	LeaveSchemeTypeWorkflow findMaxWorkFlowRuleValByLeaveScheme(
			Long leaveSchemeId, String leaveReviewerRule);

	List<LeaveSchemeTypeWorkflow> findByCondition(Long leaveTypeId, Long companyId);

}
