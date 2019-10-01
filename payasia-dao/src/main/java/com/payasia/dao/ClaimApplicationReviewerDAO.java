package com.payasia.dao;

import java.util.List;

import com.payasia.common.dto.PendingClaimConditionDTO;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.ClaimApplicationReviewer;

public interface ClaimApplicationReviewerDAO {

	void save(ClaimApplicationReviewer claimApplicationReviewer);

	void update(ClaimApplicationReviewer claimApplicationReviewer);

	List<ClaimApplicationReviewer> findByCondition(Long empId,
			PageRequest pageDTO, SortCondition sortDTO,
			PendingClaimConditionDTO claimConditionDTO);

	ClaimApplicationReviewer findByID(long claimApplicationReviewerId);

	List<ClaimApplicationReviewer> checkClaimEmployeeReviewer(Long employeeId,
			List<String> claimStatusList);

	void deleteByCondition(Long claimApplicationId);

	Integer getClaimReviewerCount(long claimApplicationId);

	Integer findByConditionCountRecords(Long empId,
			PendingClaimConditionDTO claimConditionDTO);
	ClaimApplicationReviewer getClaimApplicationReviewerDetail(Long claimApplicationReviewerId, Long employeeId);

}
