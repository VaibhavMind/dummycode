package com.payasia.dao;

import com.payasia.dao.bean.ClaimAmountReviewerTemplate;

public interface ClaimAmountReviewerTemplateDAO {

	ClaimAmountReviewerTemplate findByID(long claimAmountReviewerTemplateId);

	void delete(ClaimAmountReviewerTemplate claimAmountReviewerTemplate);

	void update(ClaimAmountReviewerTemplate claimAmountReviewerTemplate);

	void save(ClaimAmountReviewerTemplate claimAmountReviewerTemplate);

	void deleteByCondition(long claimTemplateId);

}
