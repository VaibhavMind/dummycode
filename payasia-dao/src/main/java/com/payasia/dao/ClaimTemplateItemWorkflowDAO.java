package com.payasia.dao;

import java.util.List;

import com.payasia.dao.bean.ClaimTemplateItemWorkflow;

public interface ClaimTemplateItemWorkflowDAO {

	void update(ClaimTemplateItemWorkflow claimTemplateItemWorkflow);

	void save(ClaimTemplateItemWorkflow claimTemplateItemWorkflow);

	void delete(ClaimTemplateItemWorkflow claimTemplateItemWorkflow);

	ClaimTemplateItemWorkflow findByID(long claimTemplateItemWorkflow);

	ClaimTemplateItemWorkflow saveReturn(
			ClaimTemplateItemWorkflow claimTemplateItemWorkflow);

	void deleteByCondition(long claimTemplateItemId);

	List<ClaimTemplateItemWorkflow> findByCondition(Long claimTemplateItemId);

}
