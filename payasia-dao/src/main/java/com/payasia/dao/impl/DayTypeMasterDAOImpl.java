package com.payasia.dao.impl;

import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.payasia.dao.BaseDAO;
import com.payasia.dao.DayTypeMasterDAO;
import com.payasia.dao.bean.DayTypeMaster;

/**
 * The Class DayTypeMasterDAOImpl.
 */
/**
 * @author vivekjain
 * 
 */
@Repository
public class DayTypeMasterDAOImpl extends BaseDAO implements DayTypeMasterDAO {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.BaseDAO#getBaseEntity()
	 */
	@Override
	protected Object getBaseEntity() {
		DayTypeMaster dayTypeMaster = new DayTypeMaster();
		return dayTypeMaster;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.DayTypeMasterDAO#findAll()
	 */
	@Override
	public List<DayTypeMaster> findAll() {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<DayTypeMaster> criteriaQuery = cb
				.createQuery(DayTypeMaster.class);
		Root<DayTypeMaster> dayTypeMasterStatusRoot = criteriaQuery
				.from(DayTypeMaster.class);

		criteriaQuery.select(dayTypeMasterStatusRoot);

		TypedQuery<DayTypeMaster> dayTypeStatusTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		List<DayTypeMaster> dayTypeMasterList = dayTypeStatusTypedQuery
				.getResultList();
		return dayTypeMasterList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.DayTypeMasterDAO#findById(java.lang.Long)
	 */
	@Override
	public DayTypeMaster findById(Long dayTypeId) {
		return super.findById(DayTypeMaster.class, dayTypeId);
	}
}
