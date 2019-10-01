package com.payasia.dao.impl;

import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.payasia.dao.AppCodeMasterDAO;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.bean.AppCodeMaster;
import com.payasia.dao.bean.AppCodeMaster_;

/**
 * The Class AppCodeMasterDAOImpl.
 */
/**
 * @author vivekjain
 * 
 */
@Repository
public class AppCodeMasterDAOImpl extends BaseDAO implements AppCodeMasterDAO {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.AppCodeMasterDAO#findByCondition(java.lang.String)
	 */
	@Override
	public List<AppCodeMaster> findByCondition(String category) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<AppCodeMaster> criteriaQuery = cb
				.createQuery(AppCodeMaster.class);
		Root<AppCodeMaster> appCodeRoot = criteriaQuery
				.from(AppCodeMaster.class);

		criteriaQuery.select(appCodeRoot);
		criteriaQuery.where(cb.equal(
				cb.upper(appCodeRoot.get(AppCodeMaster_.category)),
				category.toUpperCase()));
		criteriaQuery
				.orderBy(cb.asc(appCodeRoot.get(AppCodeMaster_.appCodeID)));

		TypedQuery<AppCodeMaster> appCodeTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return appCodeTypedQuery.getResultList();
	}

	@Override
	public List<AppCodeMaster> findByConditionWorkFlowDelegate(String category) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<AppCodeMaster> criteriaQuery = cb
				.createQuery(AppCodeMaster.class);
		Root<AppCodeMaster> appCodeRoot = criteriaQuery
				.from(AppCodeMaster.class);

		criteriaQuery.select(appCodeRoot);
		Predicate restriction = cb.conjunction();

		restriction = cb.equal(
				cb.upper(appCodeRoot.get(AppCodeMaster_.category)),
				category.toUpperCase());
		restriction = cb.and(
				restriction,
				appCodeRoot.get(AppCodeMaster_.codeValue).in("01", "02", "03",
						"05", "06", "07", "10", "11"));
		criteriaQuery.where(restriction);
		criteriaQuery.orderBy(cb.asc(appCodeRoot.get(AppCodeMaster_.codeDesc)));

		TypedQuery<AppCodeMaster> appCodeTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return appCodeTypedQuery.getResultList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.AppCodeMasterDAO#findByCondition(java.lang.String)
	 */
	@Override
	public List<AppCodeMaster> findByConditionPendingItems(String category) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<AppCodeMaster> criteriaQuery = cb
				.createQuery(AppCodeMaster.class);
		Root<AppCodeMaster> appCodeRoot = criteriaQuery
				.from(AppCodeMaster.class);

		criteriaQuery.select(appCodeRoot);
		Predicate restriction = cb.conjunction();
		restriction = cb.equal(
				cb.upper(appCodeRoot.get(AppCodeMaster_.category)),
				category.toUpperCase());

		restriction = cb.and(restriction,
				appCodeRoot.get(AppCodeMaster_.codeValue).in("03", "04"));

		criteriaQuery.where(restriction);
		criteriaQuery.orderBy(cb.asc(appCodeRoot.get(AppCodeMaster_.codeDesc)));

		TypedQuery<AppCodeMaster> appCodeTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return appCodeTypedQuery.getResultList();
	}

	@Override
	public AppCodeMaster findByCondition(String category, String value) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<AppCodeMaster> criteriaQuery = cb
				.createQuery(AppCodeMaster.class);
		Root<AppCodeMaster> appCodeRoot = criteriaQuery
				.from(AppCodeMaster.class);

		criteriaQuery.select(appCodeRoot);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(
				cb.upper(appCodeRoot.get(AppCodeMaster_.category)),
				category.toUpperCase()));

		restriction = cb.and(restriction, cb.equal(
				cb.upper(appCodeRoot.get(AppCodeMaster_.codeValue)),
				value.toUpperCase()));

		criteriaQuery.where(restriction);

		TypedQuery<AppCodeMaster> appCodeTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<AppCodeMaster> appCodeList = appCodeTypedQuery.getResultList();
		if (appCodeList != null && !appCodeList.isEmpty()) {
			return appCodeList.get(0);
		}
		return null;
	}

	@Override
	public AppCodeMaster findByCategoryAndDesc(String category, String desc) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<AppCodeMaster> criteriaQuery = cb
				.createQuery(AppCodeMaster.class);
		Root<AppCodeMaster> appCodeRoot = criteriaQuery
				.from(AppCodeMaster.class);

		criteriaQuery.select(appCodeRoot);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(
				cb.upper(appCodeRoot.get(AppCodeMaster_.category)),
				category.toUpperCase()));

		restriction = cb.and(restriction, cb.equal(
				cb.upper(appCodeRoot.get(AppCodeMaster_.codeDesc)),
				desc.toUpperCase()));

		criteriaQuery.where(restriction);

		TypedQuery<AppCodeMaster> appCodeTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<AppCodeMaster> appCodeList = appCodeTypedQuery.getResultList();
		if (appCodeList != null && !appCodeList.isEmpty()) {
			return appCodeList.get(0);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.BaseDAO#getBaseEntity()
	 */
	@Override
	protected Object getBaseEntity() {
		AppCodeMaster appCodeMaster = new AppCodeMaster();
		return appCodeMaster;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.AppCodeMasterDAO#findById(long)
	 */
	@Override
	public AppCodeMaster findById(long appCodeID) {

		AppCodeMaster appCodeMaster = super.findById(AppCodeMaster.class,
				appCodeID);
		return appCodeMaster;
	}

}
