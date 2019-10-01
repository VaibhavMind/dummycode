package com.payasia.dao.impl;

import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.payasia.dao.BaseDAO;
import com.payasia.dao.SchedulerMasterDAO;
import com.payasia.dao.bean.SchedulerMaster;
import com.payasia.dao.bean.SchedulerMaster_;

@Repository
public class SchedulerMasterDAOImpl extends BaseDAO implements
		SchedulerMasterDAO {

	@Override
	protected Object getBaseEntity() {
		SchedulerMaster schedulerMaster = new SchedulerMaster();
		return schedulerMaster;
	}

	@Override
	public SchedulerMaster findByCondition(String schedulerName) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<SchedulerMaster> criteriaQuery = cb
				.createQuery(SchedulerMaster.class);
		Root<SchedulerMaster> schRoot = criteriaQuery
				.from(SchedulerMaster.class);

		criteriaQuery.select(schRoot);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.like(
				cb.upper(schRoot.get(SchedulerMaster_.schedulerName)),
				schedulerName.toUpperCase()));

		criteriaQuery.where(restriction);

		TypedQuery<SchedulerMaster> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<SchedulerMaster> schedulerMasterList = typedQuery.getResultList();
		if (schedulerMasterList != null &&  !schedulerMasterList.isEmpty()) {
			return schedulerMasterList.get(0);
		}
		return null;
	}
}
