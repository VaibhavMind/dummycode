package com.payasia.dao.impl;

import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.payasia.dao.AppConfigMasterDAO;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.bean.AppConfigMaster;
import com.payasia.dao.bean.AppConfigMaster_;

/**
 * The Class AppConfigMasterDAOImpl.
 */
/**
 * @author vivekjain
 * 
 */
@Repository
public class AppConfigMasterDAOImpl extends BaseDAO implements
		AppConfigMasterDAO {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.BaseDAO#getBaseEntity()
	 */
	@Override
	protected Object getBaseEntity() {
		AppConfigMaster appConfigMaster = new AppConfigMaster();
		return appConfigMaster;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.AppConfigMasterDAO#findByName(java.lang.String)
	 */
	@Override
	public AppConfigMaster findByName(String paramName) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<AppConfigMaster> criteriaQuery = cb
				.createQuery(AppConfigMaster.class);
		Root<AppConfigMaster> appConfigMasterRoot = criteriaQuery
				.from(AppConfigMaster.class);
		criteriaQuery.select(appConfigMasterRoot);

		criteriaQuery.where(cb.equal(
				cb.upper(appConfigMasterRoot.get(AppConfigMaster_.paramName)),
				paramName));
		TypedQuery<AppConfigMaster> appConfigMaster = entityManagerFactory
				.createQuery(criteriaQuery);

		List<AppConfigMaster> appConfigMasterList = appConfigMaster
				.getResultList();
		if (appConfigMasterList != null && !appConfigMasterList.isEmpty()) {
			return appConfigMasterList.get(0);
		}
		return null;

	}
}
