package com.payasia.dao.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.util.NullAwareBeanUtilsBean;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.CountryMasterDAO;
import com.payasia.dao.bean.CountryMaster;
import com.payasia.dao.bean.CountryMaster_;

/**
 * The Class CountryMasterDAOImpl.
 */
/**
 * @author vivekjain
 * 
 */
@Repository
public class CountryMasterDAOImpl extends BaseDAO implements CountryMasterDAO {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.CountryMasterDAO#findAll()
	 */
	@Override
	public List<CountryMaster> findAll() {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CountryMaster> criteriaQuery = cb
				.createQuery(CountryMaster.class);
		Root<CountryMaster> countryMasterRoot = criteriaQuery
				.from(CountryMaster.class);

		criteriaQuery.select(countryMasterRoot);

		criteriaQuery.orderBy(cb.asc(countryMasterRoot
				.get(CountryMaster_.countryName)));

		TypedQuery<CountryMaster> countryMasterTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<CountryMaster> allCountryList = countryMasterTypedQuery
				.getResultList();
		return allCountryList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.CountryMasterDAO#save(com.payasia.dao.bean.CountryMaster)
	 */
	@Override
	public void save(CountryMaster countryMaster) {
		super.save(countryMaster);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.CountryMasterDAO#saveAndPersist(com.payasia.dao.bean.
	 * CountryMaster)
	 */
	@Override
	public CountryMaster saveAndPersist(CountryMaster countryMaster) {

		CountryMaster persistObj = countryMaster;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (CountryMaster) getBaseEntity();
			beanUtil.copyProperties(persistObj, countryMaster);
		} catch (IllegalAccessException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		} catch (InvocationTargetException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		}
		this.entityManagerFactory.persist(persistObj);
		return persistObj;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.CountryMasterDAO#findById(long)
	 */
	@Override
	public CountryMaster findById(long countryId) {
		return super.findById(CountryMaster.class, countryId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.CountryMasterDAO#findByName(java.lang.String)
	 */
	@Override
	public CountryMaster findByName(String countryName) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CountryMaster> criteriaQuery = cb
				.createQuery(CountryMaster.class);
		Root<CountryMaster> countryMasterRoot = criteriaQuery
				.from(CountryMaster.class);

		criteriaQuery.select(countryMasterRoot);
		criteriaQuery.where(cb.equal(
				cb.upper(countryMasterRoot.get(CountryMaster_.countryName)),
				countryName.toUpperCase()));
		TypedQuery<CountryMaster> countryMasterTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<CountryMaster> countryMasterList = countryMasterTypedQuery
				.getResultList();
		if (countryMasterList != null && !countryMasterList.isEmpty()) {
			return countryMasterList.get(0);
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
		CountryMaster countryMaster = new CountryMaster();
		return countryMaster;
	}

}
