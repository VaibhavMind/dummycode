package com.payasia.dao.impl;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.payasia.dao.BaseDAO;
import com.payasia.dao.ClaimAmountReviewerTemplateDAO;
import com.payasia.dao.bean.ClaimAmountReviewerTemplate;

@Repository
public class ClaimAmountReviewerTemplateDAOImpl extends BaseDAO implements
		ClaimAmountReviewerTemplateDAO {

	@Override
	protected Object getBaseEntity() {
		ClaimAmountReviewerTemplate claimAmountReviewerTemplate = new ClaimAmountReviewerTemplate();
		return claimAmountReviewerTemplate;
	}

	@Override
	public ClaimAmountReviewerTemplate findByID(
			long claimAmountReviewerTemplateId) {
		return super.findById(ClaimAmountReviewerTemplate.class,
				claimAmountReviewerTemplateId);
	}

	@Override
	public void update(ClaimAmountReviewerTemplate claimAmountReviewerTemplate) {
		super.update(claimAmountReviewerTemplate);

	}

	@Override
	public void delete(ClaimAmountReviewerTemplate claimAmountReviewerTemplate) {
		super.delete(claimAmountReviewerTemplate);

	}

	@Override
	public void save(ClaimAmountReviewerTemplate claimAmountReviewerTemplate) {
		super.save(claimAmountReviewerTemplate);

	}

	@Override
	public void deleteByCondition(long claimTemplateId) {
		String queryString = "DELETE FROM ClaimAmountReviewerTemplate cart WHERE cart.claimTemplate.claimTemplateId = :claimTemplateId";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("claimTemplateId", claimTemplateId);
		q.executeUpdate();
	}

}
