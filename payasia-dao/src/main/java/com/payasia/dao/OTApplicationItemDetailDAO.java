package com.payasia.dao;

import com.payasia.dao.bean.OTApplicationItemDetail;

public interface OTApplicationItemDetailDAO {

	OTApplicationItemDetail findByID(long otApplicationItemDetailId);

	void delete(OTApplicationItemDetail otApplicationItemDetail);

	void save(OTApplicationItemDetail otApplicationItemDetail);

	void update(OTApplicationItemDetail otApplicationItemDetail);

}
