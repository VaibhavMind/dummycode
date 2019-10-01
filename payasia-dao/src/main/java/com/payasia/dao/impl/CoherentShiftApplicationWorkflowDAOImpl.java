package com.payasia.dao.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.util.NullAwareBeanUtilsBean;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.CoherentShiftApplicationWorkflowDAO;
import com.payasia.dao.bean.CoherentShiftApplication;
import com.payasia.dao.bean.CoherentShiftApplicationWorkflow;
import com.payasia.dao.bean.CoherentShiftApplicationWorkflow_;
import com.payasia.dao.bean.CoherentShiftApplication_;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.Employee_;

@Repository
public class CoherentShiftApplicationWorkflowDAOImpl extends BaseDAO implements
		CoherentShiftApplicationWorkflowDAO {
	@Override
	protected Object getBaseEntity() {
		CoherentShiftApplicationWorkflow timesheet = new CoherentShiftApplicationWorkflow();
		return timesheet;
	}

	@Override
	public CoherentShiftApplicationWorkflow findById(long id) {
		return super.findById(CoherentShiftApplicationWorkflow.class, id);
	}

	@Override
	public void save(
			CoherentShiftApplicationWorkflow coherentShiftApplicationWorkflow) {
		super.save(coherentShiftApplicationWorkflow);
	}

	@Override
	public void update(
			CoherentShiftApplicationWorkflow coherentShiftApplicationWorkflow) {
		super.update(coherentShiftApplicationWorkflow);

	}

	@Override
	public CoherentShiftApplicationWorkflow saveAndReturn(
			CoherentShiftApplicationWorkflow coherentShiftApplicationWorkflow) {
		CoherentShiftApplicationWorkflow persistObj = coherentShiftApplicationWorkflow;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (CoherentShiftApplicationWorkflow) getBaseEntity();
			beanUtil.copyProperties(persistObj,
					coherentShiftApplicationWorkflow);
		} catch (IllegalAccessException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		} catch (InvocationTargetException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		}
		this.entityManagerFactory.persist(persistObj);
		this.entityManagerFactory.flush();
		return persistObj;
	}

	@Override
	public CoherentShiftApplicationWorkflow findByCondition(
			Long shiftApplicationID, Long createdById) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CoherentShiftApplicationWorkflow> criteriaQuery = cb
				.createQuery(CoherentShiftApplicationWorkflow.class);
		Root<CoherentShiftApplicationWorkflow> otTimesheetWorkflowRoot = criteriaQuery
				.from(CoherentShiftApplicationWorkflow.class);
		criteriaQuery.select(otTimesheetWorkflowRoot);

		Predicate restriction = cb.conjunction();

		Join<CoherentShiftApplicationWorkflow, Employee> empLeaveJoin = otTimesheetWorkflowRoot
				.join(CoherentShiftApplicationWorkflow_.createdBy);

		Join<CoherentShiftApplicationWorkflow, CoherentShiftApplication> otTimesheetJoin = otTimesheetWorkflowRoot
				.join(CoherentShiftApplicationWorkflow_.coherentShiftApplication);

		restriction = cb.and(restriction,
				cb.equal(empLeaveJoin.get(Employee_.employeeId), createdById));

		restriction = cb.and(restriction, cb.equal(otTimesheetJoin
				.get(CoherentShiftApplication_.shiftApplicationID),
				shiftApplicationID));
		criteriaQuery.where(restriction);
		criteriaQuery
				.orderBy(cb.desc(otTimesheetWorkflowRoot
						.get(CoherentShiftApplicationWorkflow_.shiftApplicationWorkflowID)));
		TypedQuery<CoherentShiftApplicationWorkflow> otWorkflowTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		otWorkflowTypedQuery.setMaxResults(1);
		List<CoherentShiftApplicationWorkflow> otworkflowList = otWorkflowTypedQuery
				.getResultList();
		if (otworkflowList != null && !otworkflowList.isEmpty()) {
			return otworkflowList.get(0);
		}
		return null;

	}

	@Override
	public void delete(
			CoherentShiftApplicationWorkflow coherentShiftApplicationWorkflow) {
		super.delete(coherentShiftApplicationWorkflow);

	}
}
