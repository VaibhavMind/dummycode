package com.payasia.dao.impl;

import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.payasia.dao.BaseDAO;
import com.payasia.dao.LionEmployeeTimesheetDAO;
import com.payasia.dao.bean.LionEmployeeTimesheet;
import com.payasia.dao.bean.LionEmployeeTimesheet_;

@Repository
public class LionEmployeeTimesheetDAOImpl extends BaseDAO implements
		LionEmployeeTimesheetDAO {

	@Override
	protected Object getBaseEntity() {
		LionEmployeeTimesheet lionEmployeeTimesheet = new LionEmployeeTimesheet();
		return lionEmployeeTimesheet;
	}

	@Override
	public void save(LionEmployeeTimesheet lionEmployeeTimesheet) {
		super.save(lionEmployeeTimesheet);

	}

	@Override
	public void update(LionEmployeeTimesheet lionEmployeeTimesheet) {
		super.update(lionEmployeeTimesheet);

	}

	@Override
	public List<LionEmployeeTimesheet> findByEmployeeTimesheetApplication(
			long timesheetId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LionEmployeeTimesheet> criteriaQuery = cb
				.createQuery(LionEmployeeTimesheet.class);
		Root<LionEmployeeTimesheet> root = criteriaQuery
				.from(LionEmployeeTimesheet.class);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(
				root.get(LionEmployeeTimesheet_.employeeTimesheetApplication),
				timesheetId));

		criteriaQuery.select(root).where(restriction);
		TypedQuery<LionEmployeeTimesheet> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		return typedQuery.getResultList();

	}
}
