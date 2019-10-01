package com.payasia.dao.impl;

import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.payasia.dao.BaseDAO;
import com.payasia.dao.WorkdayAppCodeMasterDAO;
import com.payasia.dao.bean.CountryMaster;
import com.payasia.dao.bean.CountryMaster_;
import com.payasia.dao.bean.WorkdayAppCodeMaster;
import com.payasia.dao.bean.WorkdayAppCodeMaster_;

// TODO: Auto-generated Javadoc
/**
 * The Class WorkdayAppCodeMasterDAOImpl.
 */
/**
 * @author peeyushpratap
 * 
 */
@Repository
public class WorkdayAppCodeMasterDAOImpl extends BaseDAO implements WorkdayAppCodeMasterDAO {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.WorkdayAppCodeMasterDAO#findByCategory(java.lang.String)
	 */
	@Override
	public List<WorkdayAppCodeMaster> findByCategory(String category) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<WorkdayAppCodeMaster> criteriaQuery = cb
				.createQuery(WorkdayAppCodeMaster.class);
		Root<WorkdayAppCodeMaster> appCodeRoot = criteriaQuery
				.from(WorkdayAppCodeMaster.class);

		criteriaQuery.select(appCodeRoot);
		criteriaQuery.where(cb.equal(
				cb.upper(appCodeRoot.get(WorkdayAppCodeMaster_.category)),
				category.toUpperCase()));
		criteriaQuery
				.orderBy(cb.asc(appCodeRoot.get(WorkdayAppCodeMaster_.workdayAppCodeId)));

		TypedQuery<WorkdayAppCodeMaster> appCodeTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return appCodeTypedQuery.getResultList();
	}


	
	/* (non-Javadoc)
	 * @see com.payasia.dao.WorkdayAppCodeMasterDAO#findByCondition(java.lang.String, java.lang.String)
	 */
	@Override
	public WorkdayAppCodeMaster findByCondition(String category, String value) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<WorkdayAppCodeMaster> criteriaQuery = cb
				.createQuery(WorkdayAppCodeMaster.class);
		Root<WorkdayAppCodeMaster> appCodeRoot = criteriaQuery
				.from(WorkdayAppCodeMaster.class);

		criteriaQuery.select(appCodeRoot);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(
				cb.upper(appCodeRoot.get(WorkdayAppCodeMaster_.category)),
				category.toUpperCase()));

		restriction = cb.and(restriction, cb.equal(
				cb.upper(appCodeRoot.get(WorkdayAppCodeMaster_.codeValue)),
				value.toUpperCase()));

		criteriaQuery.where(restriction);

		TypedQuery<WorkdayAppCodeMaster> appCodeTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<WorkdayAppCodeMaster> appCodeList = appCodeTypedQuery.getResultList();
		if (appCodeList != null && !appCodeList.isEmpty()) {
			return appCodeList.get(0);
		}
		return null;
	}
	
	@Override
	public WorkdayAppCodeMaster findByConditionAndCountry(String category, String value, long countryId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<WorkdayAppCodeMaster> criteriaQuery = cb
				.createQuery(WorkdayAppCodeMaster.class);
		Root<WorkdayAppCodeMaster> appCodeRoot = criteriaQuery.from(WorkdayAppCodeMaster.class);
		criteriaQuery.select(appCodeRoot);
		Join<WorkdayAppCodeMaster, CountryMaster> countryJoin =  appCodeRoot.join(WorkdayAppCodeMaster_.country);
		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(countryJoin.get(CountryMaster_.countryId), countryId));
		restriction = cb.and(restriction, cb.equal(
				cb.upper(appCodeRoot.get(WorkdayAppCodeMaster_.category)),
				category.toUpperCase()));

		restriction = cb.and(restriction, cb.equal(
				cb.upper(appCodeRoot.get(WorkdayAppCodeMaster_.codeValue)),
				value.toUpperCase()));

		criteriaQuery.where(restriction);

		TypedQuery<WorkdayAppCodeMaster> appCodeTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<WorkdayAppCodeMaster> appCodeList = appCodeTypedQuery.getResultList();
		if (appCodeList != null && !appCodeList.isEmpty()) {
			return appCodeList.get(0);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see com.payasia.dao.WorkdayAppCodeMasterDAO#findByCategoryAndDesc(java.lang.String, java.lang.String)
	 */
	@Override
	public WorkdayAppCodeMaster findByCategoryAndDesc(String category, String desc) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<WorkdayAppCodeMaster> criteriaQuery = cb
				.createQuery(WorkdayAppCodeMaster.class);
		Root<WorkdayAppCodeMaster> appCodeRoot = criteriaQuery
				.from(WorkdayAppCodeMaster.class);

		criteriaQuery.select(appCodeRoot);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(
				cb.upper(appCodeRoot.get(WorkdayAppCodeMaster_.category)),
				category.toUpperCase()));

		restriction = cb.and(restriction, cb.equal(
				cb.upper(appCodeRoot.get(WorkdayAppCodeMaster_.codeDesc)),
				desc.toUpperCase()));

		criteriaQuery.where(restriction);

		TypedQuery<WorkdayAppCodeMaster> appCodeTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<WorkdayAppCodeMaster> appCodeList = appCodeTypedQuery.getResultList();
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
		WorkdayAppCodeMaster WorkdayAppCodeMaster = new WorkdayAppCodeMaster();
		return WorkdayAppCodeMaster;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.WorkdayAppCodeMasterDAO#findById(long)
	 */
	@Override
	public WorkdayAppCodeMaster findById(long appCodeID) {

		WorkdayAppCodeMaster WorkdayAppCodeMaster = super.findById(WorkdayAppCodeMaster.class,
				appCodeID);
		return WorkdayAppCodeMaster;
	}
	
	@Override
	public List<WorkdayAppCodeMaster> findByCountryId(Long countryId) {
		
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<WorkdayAppCodeMaster> criteriaQuery = cb.createQuery(WorkdayAppCodeMaster.class);
		Root<WorkdayAppCodeMaster> workdayAppCodeMasterRoot = criteriaQuery.from(WorkdayAppCodeMaster.class);

		criteriaQuery.select(workdayAppCodeMasterRoot);
		Join<WorkdayAppCodeMaster, CountryMaster> countryJoin = workdayAppCodeMasterRoot.join(WorkdayAppCodeMaster_.country);
		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(countryJoin.get(CountryMaster_.countryId), countryId));
		criteriaQuery.where(restriction);
		TypedQuery<WorkdayAppCodeMaster> typedQuery = entityManagerFactory.createQuery(criteriaQuery);
		return typedQuery.getResultList();
	}

	@Override
	public WorkdayAppCodeMaster findPaygroupTypeByBatchType(Long batchTypeId) {
		
		WorkdayAppCodeMaster batchType = findById(batchTypeId);
		
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<WorkdayAppCodeMaster> criteriaQuery = cb
				.createQuery(WorkdayAppCodeMaster.class);
		Root<WorkdayAppCodeMaster> appCodeRoot = criteriaQuery.from(WorkdayAppCodeMaster.class);
		criteriaQuery.select(appCodeRoot);
		Join<WorkdayAppCodeMaster, CountryMaster> countryJoin =  appCodeRoot.join(WorkdayAppCodeMaster_.country);
		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(countryJoin.get(CountryMaster_.countryId), batchType.getCountry().getCountryId()));
		restriction = cb.and(restriction, cb.equal(
				cb.upper(appCodeRoot.get(WorkdayAppCodeMaster_.category)),
				"Paygroup".toUpperCase()));

		restriction = cb.and(restriction, cb.equal(
				cb.upper(appCodeRoot.get(WorkdayAppCodeMaster_.type)),
				batchType.getType().toUpperCase()));

		criteriaQuery.where(restriction);

		TypedQuery<WorkdayAppCodeMaster> appCodeTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<WorkdayAppCodeMaster> appCodeList = appCodeTypedQuery.getResultList();
		if (appCodeList != null && !appCodeList.isEmpty()) {
			return appCodeList.get(0);
		}
		return null;
	}
	
	@Override
	public WorkdayAppCodeMaster findBatchTypeByPaygroupType(Long paygroupTypeId) {
		
		WorkdayAppCodeMaster paygroupType = findById(paygroupTypeId);
		
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<WorkdayAppCodeMaster> criteriaQuery = cb
				.createQuery(WorkdayAppCodeMaster.class);
		Root<WorkdayAppCodeMaster> appCodeRoot = criteriaQuery.from(WorkdayAppCodeMaster.class);
		criteriaQuery.select(appCodeRoot);
		Join<WorkdayAppCodeMaster, CountryMaster> countryJoin =  appCodeRoot.join(WorkdayAppCodeMaster_.country);
		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(countryJoin.get(CountryMaster_.countryId), paygroupType.getCountry().getCountryId()));
		restriction = cb.and(restriction, cb.equal(
				cb.upper(appCodeRoot.get(WorkdayAppCodeMaster_.category)),
				"Batch_Type".toUpperCase()));

		restriction = cb.and(restriction, cb.equal(
				cb.upper(appCodeRoot.get(WorkdayAppCodeMaster_.type)),
				paygroupType.getType().toUpperCase()));

		criteriaQuery.where(restriction);

		TypedQuery<WorkdayAppCodeMaster> appCodeTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<WorkdayAppCodeMaster> appCodeList = appCodeTypedQuery.getResultList();
		if (appCodeList != null && !appCodeList.isEmpty()) {
			return appCodeList.get(0);
		}
		return null;
	}
	
}
