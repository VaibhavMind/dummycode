package com.payasia.dao.impl;

import org.springframework.stereotype.Repository;

import com.payasia.dao.BaseDAO;
import com.payasia.dao.OTApplicationItemDetailDAO;
import com.payasia.dao.bean.OTApplicationItemDetail;

@Repository
public class OTApplicationItemDetailDAOImpl extends BaseDAO implements
		OTApplicationItemDetailDAO {

	@Override
	protected Object getBaseEntity() {
		OTApplicationItemDetail otApplicationItemDetail = new OTApplicationItemDetail();
		return otApplicationItemDetail;
	}

	@Override
	public void update(OTApplicationItemDetail otApplicationItemDetail) {
		super.update(otApplicationItemDetail);

	}

	@Override
	public void save(OTApplicationItemDetail otApplicationItemDetail) {
		super.save(otApplicationItemDetail);
	}

	@Override
	public void delete(OTApplicationItemDetail otApplicationItemDetail) {
		super.delete(otApplicationItemDetail);

	}

	@Override
	public OTApplicationItemDetail findByID(long otApplicationItemDetailId) {
		return super.findById(OTApplicationItemDetail.class,
				otApplicationItemDetailId);
	}
}
