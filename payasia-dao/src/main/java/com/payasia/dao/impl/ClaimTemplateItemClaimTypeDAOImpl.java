package com.payasia.dao.impl;

import java.lang.reflect.InvocationTargetException;

import org.springframework.stereotype.Repository;

import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.util.NullAwareBeanUtilsBean;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.ClaimTemplateItemClaimTypeDAO;
import com.payasia.dao.bean.ClaimTemplateItemClaimType;

@Repository
public class ClaimTemplateItemClaimTypeDAOImpl extends BaseDAO implements
		ClaimTemplateItemClaimTypeDAO {

	@Override
	protected Object getBaseEntity() {
		ClaimTemplateItemClaimType claimTemplateItemClaimType = new ClaimTemplateItemClaimType();
		return claimTemplateItemClaimType;
	}

	@Override
	public void update(ClaimTemplateItemClaimType claimTemplateItemClaimType) {
		super.update(claimTemplateItemClaimType);

	}

	@Override
	public void save(ClaimTemplateItemClaimType claimTemplateItemClaimType) {
		super.save(claimTemplateItemClaimType);
	}

	@Override
	public void delete(ClaimTemplateItemClaimType claimTemplateItemClaimType) {
		super.delete(claimTemplateItemClaimType);

	}

	@Override
	public ClaimTemplateItemClaimType findByID(long claimTemplateItemClaimTypeId) {
		return super.findById(ClaimTemplateItemClaimType.class,
				claimTemplateItemClaimTypeId);
	}

	@Override
	public ClaimTemplateItemClaimType saveReturn(
			ClaimTemplateItemClaimType claimTemplateItemClaimType) {

		ClaimTemplateItemClaimType persistObj = claimTemplateItemClaimType;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (ClaimTemplateItemClaimType) getBaseEntity();
			beanUtil.copyProperties(persistObj, claimTemplateItemClaimType);
		} catch (IllegalAccessException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		} catch (InvocationTargetException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		}
		this.entityManagerFactory.persist(persistObj);
		return persistObj;
	}

}
