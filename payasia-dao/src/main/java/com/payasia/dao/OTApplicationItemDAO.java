package com.payasia.dao;

import com.payasia.dao.bean.OTApplicationItem;

public interface OTApplicationItemDAO {

	void update(OTApplicationItem otApplicationItem);

	void save(OTApplicationItem otApplicationItem);

	OTApplicationItem findByID(long otApplicationItemId);

	void delete(OTApplicationItem otApplicationItem);

	OTApplicationItem saveOTApplicationItem(OTApplicationItem otApplicationItem);

}
