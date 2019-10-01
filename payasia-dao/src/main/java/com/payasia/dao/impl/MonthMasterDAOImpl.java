package com.payasia.dao.impl;

import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.payasia.dao.BaseDAO;
import com.payasia.dao.MonthMasterDAO;
import com.payasia.dao.bean.MonthMaster;
import com.payasia.dao.bean.MonthMaster_;

/**
 * The Class MonthMasterDAOImpl.
 */
/**
 * @author vivekjain
 * 
 */
@Repository
public class MonthMasterDAOImpl extends BaseDAO implements MonthMasterDAO {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.MonthMasterDAO#findById(java.lang.Long)
	 */
	@Override
	public MonthMaster findById(Long monthId) {
		return super.findById(MonthMaster.class, monthId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.MonthMasterDAO#findAll()
	 */
	@Override
	public List<MonthMaster> findAll() {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<MonthMaster> criteriaQuery = cb
				.createQuery(MonthMaster.class);
		Root<MonthMaster> monthMasterRoot = criteriaQuery
				.from(MonthMaster.class);

		criteriaQuery.select(monthMasterRoot);

		criteriaQuery
				.orderBy(cb.asc(monthMasterRoot.get(MonthMaster_.monthId)));

		TypedQuery<MonthMaster> monthMasterTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<MonthMaster> allMonthMasterList = monthMasterTypedQuery
				.getResultList();
		return allMonthMasterList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.MonthMasterDAO#findByName(java.lang.String)
	 */
	@Override
	public List<MonthMaster> findByName(String monthName) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<MonthMaster> criteriaQuery = cb
				.createQuery(MonthMaster.class);
		Root<MonthMaster> monthMasterRoot = criteriaQuery
				.from(MonthMaster.class);

		criteriaQuery.select(monthMasterRoot);

		Predicate restriction = cb.conjunction();

		if (StringUtils.isNotBlank(monthName)) {

			restriction = cb.and(restriction, cb.like(
					cb.upper(monthMasterRoot.get(MonthMaster_.monthName)),
					monthName.toUpperCase()));
		}
		criteriaQuery.where(restriction);

		TypedQuery<MonthMaster> monthMasterTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<MonthMaster> monthMaster = monthMasterTypedQuery.getResultList();
		return monthMaster;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.MonthMasterDAO#findByMonthName(java.lang.String)
	 */
	@Override
	public MonthMaster findByMonthName(String monthName) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<MonthMaster> criteriaQuery = cb
				.createQuery(MonthMaster.class);
		Root<MonthMaster> monthMasterRoot = criteriaQuery
				.from(MonthMaster.class);

		criteriaQuery.select(monthMasterRoot);

		Predicate restriction = cb.conjunction();

		if (StringUtils.isNotBlank(monthName)) {

			restriction = cb.and(restriction, cb.equal(
					cb.upper(monthMasterRoot.get(MonthMaster_.monthName)),
					monthName.toUpperCase()));
		}
		criteriaQuery.where(restriction);

		TypedQuery<MonthMaster> monthMasterTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<MonthMaster> monthMasterList = monthMasterTypedQuery
				.getResultList();
		if (monthMasterList != null &&  !monthMasterList.isEmpty()) {
			return monthMasterList.get(0);
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
		MonthMaster monthMaster = new MonthMaster();
		return monthMaster;
	}

}
