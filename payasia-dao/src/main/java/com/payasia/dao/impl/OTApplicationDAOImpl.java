package com.payasia.dao.impl;

import java.lang.reflect.InvocationTargetException;

import org.springframework.stereotype.Repository;

import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.util.NullAwareBeanUtilsBean;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.OTApplicationDAO;
import com.payasia.dao.bean.OTApplication;

@Repository
public class OTApplicationDAOImpl extends BaseDAO implements OTApplicationDAO {

	@Override
	public OTApplication saveOTTemplate(OTApplication otApplication) {

		OTApplication persistObj = otApplication;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (OTApplication) getBaseEntity();
			beanUtil.copyProperties(persistObj, otApplication);
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
		OTApplication otApplication = new OTApplication();
		return otApplication;
	}

	@Override
	public void update(OTApplication otApplication) {
		super.update(otApplication);

	}

	@Override
	public void save(OTApplication otApplication) {
		super.save(otApplication);
	}

	@Override
	public void delete(OTApplication otApplication) {
		super.delete(otApplication);

	}

	@Override
	public OTApplication findByID(long otApplicationId) {
		return super.findById(OTApplication.class, otApplicationId);
	}
}
