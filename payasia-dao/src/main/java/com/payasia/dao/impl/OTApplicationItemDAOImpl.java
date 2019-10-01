package com.payasia.dao.impl;

import java.lang.reflect.InvocationTargetException;

import org.springframework.stereotype.Repository;

import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.util.NullAwareBeanUtilsBean;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.OTApplicationItemDAO;
import com.payasia.dao.bean.OTApplicationItem;

@Repository
public class OTApplicationItemDAOImpl extends BaseDAO implements
		OTApplicationItemDAO {

	@Override
	public OTApplicationItem saveOTApplicationItem(
			OTApplicationItem otApplicationItem) {

		OTApplicationItem persistObj = otApplicationItem;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (OTApplicationItem) getBaseEntity();
			beanUtil.copyProperties(persistObj, otApplicationItem);
		} catch (IllegalAccessException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		} catch (InvocationTargetException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		}
		this.entityManagerFactory.persist(persistObj);
		return persistObj;

	}

	@Override
	protected Object getBaseEntity() {
		OTApplicationItem otApplicationItem = new OTApplicationItem();
		return otApplicationItem;
	}

	@Override
	public void update(OTApplicationItem otApplicationItem) {
		super.update(otApplicationItem);

	}

	@Override
	public void save(OTApplicationItem otApplicationItem) {
		super.save(otApplicationItem);
	}

	@Override
	public void delete(OTApplicationItem otApplicationItem) {
		super.delete(otApplicationItem);

	}

	@Override
	public OTApplicationItem findByID(long otApplicationItemId) {
		return super.findById(OTApplicationItem.class, otApplicationItemId);
	}

}
