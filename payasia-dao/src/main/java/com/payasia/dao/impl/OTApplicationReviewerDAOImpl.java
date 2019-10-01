package com.payasia.dao.impl;

import org.springframework.stereotype.Repository;

import com.payasia.dao.BaseDAO;
import com.payasia.dao.OTApplicationReviewerDAO;
import com.payasia.dao.bean.OTApplicationReviewer;

@Repository
public class OTApplicationReviewerDAOImpl extends BaseDAO implements
		OTApplicationReviewerDAO {

	@Override
	protected Object getBaseEntity() {
		OTApplicationReviewer otApplicationReviewer = new OTApplicationReviewer();
		return otApplicationReviewer;
	}

	@Override
	public void update(OTApplicationReviewer otApplicationReviewer) {
		super.update(otApplicationReviewer);

	}

	@Override
	public void save(OTApplicationReviewer otApplicationReviewer) {
		super.save(otApplicationReviewer);
	}

	@Override
	public void delete(OTApplicationReviewer otApplicationReviewer) {
		super.delete(otApplicationReviewer);

	}

	@Override
	public OTApplicationReviewer findByID(long otApplicationReviewerId) {
		return super.findById(OTApplicationReviewer.class,
				otApplicationReviewerId);
	}

}
