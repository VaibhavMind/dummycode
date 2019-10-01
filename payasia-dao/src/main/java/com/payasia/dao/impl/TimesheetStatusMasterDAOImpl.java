package com.payasia.dao.impl;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.payasia.dao.BaseDAO;
import com.payasia.dao.TimesheetStatusMasterDAO;
import com.payasia.dao.bean.TimesheetStatusMaster;
import com.payasia.dao.bean.TimesheetStatusMaster_;

@Repository
public class TimesheetStatusMasterDAOImpl extends BaseDAO implements
		TimesheetStatusMasterDAO {

	@Override
	public TimesheetStatusMaster findById(long id) {
		return super.findById(TimesheetStatusMaster.class, id);
	}

	@Override
	public TimesheetStatusMaster findByName(String statusName) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<TimesheetStatusMaster> criteriaQuery = cb
				.createQuery(TimesheetStatusMaster.class);
		Root<TimesheetStatusMaster> statusRoot = criteriaQuery
				.from(TimesheetStatusMaster.class);

		criteriaQuery.select(statusRoot);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(
				statusRoot.get(TimesheetStatusMaster_.timesheetStatusName),
				statusName));
		criteriaQuery.where(restriction);

		TypedQuery<TimesheetStatusMaster> statusQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return statusQuery.getSingleResult();
	}

	@Override
	protected Object getBaseEntity() {
		TimesheetStatusMaster lundinTimesheetStatusMaster = new TimesheetStatusMaster();
		return lundinTimesheetStatusMaster;
	}

}
