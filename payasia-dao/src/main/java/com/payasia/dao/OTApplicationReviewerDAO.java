package com.payasia.dao;

import com.payasia.dao.bean.OTApplicationReviewer;

public interface OTApplicationReviewerDAO {

	OTApplicationReviewer findByID(long otApplicationReviewerId);

	void delete(OTApplicationReviewer otApplicationReviewer);

	void save(OTApplicationReviewer otApplicationReviewer);

	void update(OTApplicationReviewer otApplicationReviewer);

}
