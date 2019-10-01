package com.payasia.dao;

import com.payasia.dao.bean.OTApplication;

public interface OTApplicationDAO {

	void update(OTApplication otApplication);

	void save(OTApplication otApplication);

	void delete(OTApplication otApplication);

	OTApplication findByID(long otApplicationId);

	OTApplication saveOTTemplate(OTApplication otApplication);

}
