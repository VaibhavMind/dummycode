package com.payasia.dao.impl;

import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.payasia.dao.BaseDAO;
import com.payasia.dao.CurrencyMasterDAO;
import com.payasia.dao.bean.CurrencyMaster;
import com.payasia.dao.bean.CurrencyMaster_;

/**
 * The Class CurrencyMasterDAOImpl.
 */
/**
 * @author vivekjain
 * 
 */
@Repository
public class CurrencyMasterDAOImpl extends BaseDAO implements CurrencyMasterDAO {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.CurrencyMasterDAO#findAll()
	 */
	@Override
	public List<CurrencyMaster> findAll() {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CurrencyMaster> criteriaQuery = cb
				.createQuery(CurrencyMaster.class);
		Root<CurrencyMaster> currencyMasterRoot = criteriaQuery
				.from(CurrencyMaster.class);

		criteriaQuery.select(currencyMasterRoot);

		criteriaQuery.orderBy(cb.asc(currencyMasterRoot
				.get(CurrencyMaster_.currencyName)));

		TypedQuery<CurrencyMaster> currencyMasterTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<CurrencyMaster> allCurrencyMasterList = currencyMasterTypedQuery
				.getResultList();
		return allCurrencyMasterList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.BaseDAO#getBaseEntity()
	 */
	@Override
	protected Object getBaseEntity() {
		CurrencyMaster currencyMaster = new CurrencyMaster();
		return currencyMaster;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.CurrencyMasterDAO#save(com.payasia.dao.bean.CurrencyMaster
	 * )
	 */
	@Override
	public void save(CurrencyMaster currencyMaster) {
		super.save(currencyMaster);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.CurrencyMasterDAO#findById(java.lang.Long)
	 */
	@Override
	public CurrencyMaster findById(Long currencyId) {
		return super.findById(CurrencyMaster.class, currencyId);
	}

	@Override
	public CurrencyMaster findByCurrencyCode(String code) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CurrencyMaster> criteriaQuery = cb
				.createQuery(CurrencyMaster.class);
		Root<CurrencyMaster> currencyMasterRoot = criteriaQuery
				.from(CurrencyMaster.class);

		criteriaQuery.select(currencyMasterRoot);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(
				currencyMasterRoot.get(CurrencyMaster_.currencyCode), code));
		criteriaQuery.where(restriction);
		criteriaQuery.orderBy(cb.asc(currencyMasterRoot
				.get(CurrencyMaster_.currencyName)));

		TypedQuery<CurrencyMaster> currencyMasterTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<CurrencyMaster> allCurrencyMasterList = currencyMasterTypedQuery
				.getResultList();
		if (!allCurrencyMasterList.isEmpty()) {

			CurrencyMaster currencyMaster = allCurrencyMasterList.get(0);
			return currencyMaster;

		}
		return null;
	}

}
