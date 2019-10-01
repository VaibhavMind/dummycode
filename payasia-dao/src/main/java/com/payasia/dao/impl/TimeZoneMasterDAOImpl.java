package com.payasia.dao.impl;

import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.payasia.dao.BaseDAO;
import com.payasia.dao.TimeZoneMasterDAO;
import com.payasia.dao.bean.TimeZoneMaster;
import com.payasia.dao.bean.TimeZoneMaster_;

/**
 * The Class TimeZoneMasterDAOImpl.
 */
@Repository
public class TimeZoneMasterDAOImpl extends BaseDAO implements TimeZoneMasterDAO {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.BaseDAO#getBaseEntity()
	 */
	@Override
	protected Object getBaseEntity() {
		TimeZoneMaster timeZoneMaster = new TimeZoneMaster();
		return timeZoneMaster;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.TimeZoneMasterDAO#findByName(java.lang.String)
	 */
	@Override
	public TimeZoneMaster findByName(String timeZoneName) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<TimeZoneMaster> criteriaQuery = cb
				.createQuery(TimeZoneMaster.class);
		Root<TimeZoneMaster> timeRoot = criteriaQuery
				.from(TimeZoneMaster.class);

		criteriaQuery.select(timeRoot);
		criteriaQuery.where(cb.equal(
				cb.upper(timeRoot.get(TimeZoneMaster_.timeZoneName)),
				timeZoneName.toUpperCase()));
		TypedQuery<TimeZoneMaster> timeMasterTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<TimeZoneMaster> timeMasterList = timeMasterTypedQuery
				.getResultList();
		if (timeMasterList != null &&  !timeMasterList.isEmpty()) {
			return timeMasterList.get(0);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.TimeZoneMasterDAO#findById(long)
	 */
	@Override
	public TimeZoneMaster findById(long timeZoneID) {
		TimeZoneMaster timeZoneMaster = super.findById(TimeZoneMaster.class,
				timeZoneID);
		return timeZoneMaster;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.TimeZoneMasterDAO#findAll()
	 */
	@Override
	public List<TimeZoneMaster> findAll() {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<TimeZoneMaster> criteriaQuery = cb
				.createQuery(TimeZoneMaster.class);
		Root<TimeZoneMaster> timeRoot = criteriaQuery
				.from(TimeZoneMaster.class);

		criteriaQuery.select(timeRoot);
		criteriaQuery
				.orderBy(cb.asc(timeRoot.get(TimeZoneMaster_.timeZoneName)));
		TypedQuery<TimeZoneMaster> timeMasterTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		return timeMasterTypedQuery.getResultList();
	}

}
