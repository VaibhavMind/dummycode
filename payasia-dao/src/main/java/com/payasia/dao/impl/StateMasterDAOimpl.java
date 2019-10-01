package com.payasia.dao.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.util.NullAwareBeanUtilsBean;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.StateMasterDAO;
import com.payasia.dao.bean.CountryMaster;
import com.payasia.dao.bean.CountryMaster_;
import com.payasia.dao.bean.StateMaster;
import com.payasia.dao.bean.StateMaster_;

/**
 * The Class StateMasterDAOimpl.
 */
/**
 * @author vivekjain
 * 
 */
@Repository
public class StateMasterDAOimpl extends BaseDAO implements StateMasterDAO {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.StateMasterDAO#findAll()
	 */
	@Override
	public List<StateMaster> findAll() {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<StateMaster> criteriaQuery = cb
				.createQuery(StateMaster.class);
		Root<StateMaster> stateMasterRoot = criteriaQuery
				.from(StateMaster.class);

		criteriaQuery.select(stateMasterRoot);

		criteriaQuery
				.orderBy(cb.asc(stateMasterRoot.get(StateMaster_.stateId)));

		TypedQuery<StateMaster> stateMasterTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<StateMaster> allStateList = stateMasterTypedQuery.getResultList();
		return allStateList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.StateMasterDAO#save(com.payasia.dao.bean.StateMaster)
	 */
	@Override
	public void save(StateMaster stateMaster) {
		super.save(stateMaster);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.StateMasterDAO#findByCountry(java.lang.Long)
	 */
	@Override
	public List<StateMaster> findByCountry(Long countryId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<StateMaster> criteriaQuery = cb
				.createQuery(StateMaster.class);
		Root<StateMaster> stateMasterRoot = criteriaQuery
				.from(StateMaster.class);

		criteriaQuery.select(stateMasterRoot);

		Join<StateMaster, CountryMaster> stateMasterRootJoin = stateMasterRoot
				.join(StateMaster_.countryMaster);
		Path<Long> countryID = stateMasterRootJoin
				.get(CountryMaster_.countryId);
		criteriaQuery.where(cb.equal(countryID, countryId));

		criteriaQuery.orderBy(cb.asc(stateMasterRoot
				.get(StateMaster_.stateName)));

		TypedQuery<StateMaster> stateMasterTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<StateMaster> stateMasterList = stateMasterTypedQuery
				.getResultList();
		return stateMasterList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.StateMasterDAO#findById(long)
	 */
	@Override
	public StateMaster findById(long stateId) {
		return super.findById(StateMaster.class, stateId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.BaseDAO#getBaseEntity()
	 */
	@Override
	protected Object getBaseEntity() {
		StateMaster stateMaster = new StateMaster();
		return stateMaster;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.StateMasterDAO#saveAndPersist(com.payasia.dao.bean.
	 * StateMaster)
	 */
	@Override
	public StateMaster saveAndPersist(StateMaster stateMaster) {

		StateMaster persistObj = stateMaster;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (StateMaster) getBaseEntity();
			beanUtil.copyProperties(persistObj, stateMaster);
		} catch (IllegalAccessException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		} catch (InvocationTargetException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		}
		this.entityManagerFactory.persist(persistObj);
		return persistObj;

	}

}
