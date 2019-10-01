package com.payasia.dao.impl;

import java.lang.reflect.InvocationTargetException;

import org.springframework.stereotype.Repository;

import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.util.NullAwareBeanUtilsBean;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.LeaveSchemeTypeYearEndDAO;
import com.payasia.dao.bean.LeaveSchemeTypeYearEnd;

@Repository
public class LeaveSchemeTypeYearEndDAOImpl extends BaseDAO implements
		LeaveSchemeTypeYearEndDAO {

	@Override
	protected Object getBaseEntity() {
		LeaveSchemeTypeYearEnd leaveSchemeTypeYearEnd = new LeaveSchemeTypeYearEnd();
		return leaveSchemeTypeYearEnd;
	}

	@Override
	public LeaveSchemeTypeYearEnd findById(Long leaveSchemeTypeYearEndId) {
		LeaveSchemeTypeYearEnd leaveSchemeTypeYearEnd = super.findById(
				LeaveSchemeTypeYearEnd.class, leaveSchemeTypeYearEndId);
		return leaveSchemeTypeYearEnd;
	}

	@Override
	public void update(LeaveSchemeTypeYearEnd leaveSchemeTypeYearEnd) {
		super.update(leaveSchemeTypeYearEnd);
	}

	@Override
	public void save(LeaveSchemeTypeYearEnd leaveSchemeTypeYearEnd) {
		super.save(leaveSchemeTypeYearEnd);

	}

	@Override
	public void delete(LeaveSchemeTypeYearEnd leaveSchemeTypeYearEnd) {
		super.delete(leaveSchemeTypeYearEnd);
	}

	@Override
	public LeaveSchemeTypeYearEnd saveObj(
			LeaveSchemeTypeYearEnd schemeTypeYearEnd) {
		LeaveSchemeTypeYearEnd persistObj = schemeTypeYearEnd;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (LeaveSchemeTypeYearEnd) getBaseEntity();
			beanUtil.copyProperties(persistObj, schemeTypeYearEnd);
		} catch (IllegalAccessException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		} catch (InvocationTargetException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		}
		this.entityManagerFactory.persist(persistObj);
		return persistObj;
	}
}
