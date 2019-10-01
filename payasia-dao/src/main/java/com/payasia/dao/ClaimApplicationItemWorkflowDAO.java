package com.payasia.dao;

import com.payasia.dao.bean.ClaimApplicationItem;
import com.payasia.dao.bean.ClaimApplicationItemWorkflow;

public interface ClaimApplicationItemWorkflowDAO {

	void save(ClaimApplicationItemWorkflow claimApplicationItemWorkflow);

	ClaimApplicationItemWorkflow findClaimItemStatus(
			ClaimApplicationItem claimApplicationItem, Long employeeId);

	ClaimApplicationItemWorkflow findByClaimItem(Long claimApplicationItemId,
			Long appCodeIdForOverrideAction);

	void deleteByCondition(Long claimApplicationItemId);

}
