package com.payasia.dao.impl;

import java.lang.reflect.InvocationTargetException;

import org.springframework.stereotype.Repository;

import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.util.NullAwareBeanUtilsBean;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.LeaveSchemeTypeProrationDAO;
import com.payasia.dao.bean.LeaveSchemeTypeProration;

@Repository
public class LeaveSchemeTypeProrationDAOImpl extends BaseDAO implements
		LeaveSchemeTypeProrationDAO {

	@Override
	protected Object getBaseEntity() {
		LeaveSchemeTypeProration leaveSchemeTypeProration = new LeaveSchemeTypeProration();
		return leaveSchemeTypeProration;
	}

	@Override
	public LeaveSchemeTypeProration findById(Long leaveSchemeTypeProrationId) {
		LeaveSchemeTypeProration leaveSchemeTypeProration = super.findById(
				LeaveSchemeTypeProration.class, leaveSchemeTypeProrationId);
		return leaveSchemeTypeProration;
	}

	@Override
	public void update(LeaveSchemeTypeProration leaveSchemeTypeProration) {
		super.update(leaveSchemeTypeProration);
	}

	@Override
	public void save(LeaveSchemeTypeProration leaveSchemeTypeProration) {
		super.save(leaveSchemeTypeProration);

	}

	@Override
	public void delete(LeaveSchemeTypeProration leaveSchemeTypeProration) {
		super.delete(leaveSchemeTypeProration);
	}

	@Override
	public LeaveSchemeTypeProration saveObj(
			LeaveSchemeTypeProration schemeTypeProration) {
		LeaveSchemeTypeProration persistObj = schemeTypeProration;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (LeaveSchemeTypeProration) getBaseEntity();
			beanUtil.copyProperties(persistObj, schemeTypeProration);
		} catch (IllegalAccessException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		} catch (InvocationTargetException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		}
		this.entityManagerFactory.persist(persistObj);
		return persistObj;
	}

}
