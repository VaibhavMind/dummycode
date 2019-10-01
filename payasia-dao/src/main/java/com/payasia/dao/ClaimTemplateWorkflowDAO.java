package com.payasia.dao;

import java.util.List;

import com.payasia.dao.bean.ClaimTemplateWorkflow;

public interface ClaimTemplateWorkflowDAO {

	void deleteByCondition(Long claimTemplateId);

	List<ClaimTemplateWorkflow> findByCondition(Long claimTemplateId);

	void save(ClaimTemplateWorkflow claimTemplateWorkflow);

	ClaimTemplateWorkflow findByTemplateIdRuleName(Long claimTemplateId, Long companyId, String leaveReviewerRule);

}
