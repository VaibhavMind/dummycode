package com.payasia.dao;

import java.util.List;

import com.payasia.dao.bean.ClaimApplicationWorkflow;

public interface ClaimApplicationWorkflowDAO {

	void save(ClaimApplicationWorkflow claimApplicationWorkflow);

	ClaimApplicationWorkflow saveReturn(
			ClaimApplicationWorkflow claimApplication);

	ClaimApplicationWorkflow findByCondition(Long claimApplicationId,
			Long createdById);

	void deleteByCondition(Long claimApplicationId);

	ClaimApplicationWorkflow findByAppIdAndStatus(Long claimApplicationId,
			String StatusName);
	
	ClaimApplicationWorkflow findByReviewerCondition(Long claimApplicationId, Long createdById);

	List<ClaimApplicationWorkflow> findWorkFlowByClaimAppId(Long claimApplicationId);

}
