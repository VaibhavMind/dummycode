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
import com.payasia.dao.CoherentOvertimeApplicationWorkflowDAO;
import com.payasia.dao.bean.CoherentOvertimeApplication;
import com.payasia.dao.bean.CoherentOvertimeApplicationWorkflow;
import com.payasia.dao.bean.CoherentOvertimeApplicationWorkflow_;
import com.payasia.dao.bean.CoherentOvertimeApplication_;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.Employee_;

@Repository
public class CoherentOvertimeApplicationWorkflowDAOImpl extends BaseDAO
		implements CoherentOvertimeApplicationWorkflowDAO {
	@Override
	protected Object getBaseEntity() {
		CoherentOvertimeApplicationWorkflow timesheet = new CoherentOvertimeApplicationWorkflow();
		return timesheet;
	}

	@Override
	public CoherentOvertimeApplicationWorkflow findById(long id) {
		return super.findById(CoherentOvertimeApplicationWorkflow.class, id);
	}

	@Override
	public void save(
			CoherentOvertimeApplicationWorkflow coherentOvertimeApplicationWorkflow) {
		super.save(coherentOvertimeApplicationWorkflow);
	}

	@Override
	public void delete(
			CoherentOvertimeApplicationWorkflow coherentOvertimeApplicationWorkflow) {
		super.delete(coherentOvertimeApplicationWorkflow);
	}

	@Override
	public void update(
			CoherentOvertimeApplicationWorkflow coherentOvertimeApplicationWorkflow) {
		super.update(coherentOvertimeApplicationWorkflow);

	}

	@Override
	public CoherentOvertimeApplicationWorkflow saveAndReturn(
			CoherentOvertimeApplicationWorkflow coherentOvertimeApplicationWorkflow) {
		CoherentOvertimeApplicationWorkflow persistObj = coherentOvertimeApplicationWorkflow;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (CoherentOvertimeApplicationWorkflow) getBaseEntity();
			beanUtil.copyProperties(persistObj,
					coherentOvertimeApplicationWorkflow);
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
	public CoherentOvertimeApplicationWorkflow findByCondition(
			Long otTimesheetId, Long createdById) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CoherentOvertimeApplicationWorkflow> criteriaQuery = cb
				.createQuery(CoherentOvertimeApplicationWorkflow.class);
		Root<CoherentOvertimeApplicationWorkflow> otTimesheetWorkflowRoot = criteriaQuery
				.from(CoherentOvertimeApplicationWorkflow.class);
		criteriaQuery.select(otTimesheetWorkflowRoot);

		Predicate restriction = cb.conjunction();

		Join<CoherentOvertimeApplicationWorkflow, Employee> empLeaveJoin = otTimesheetWorkflowRoot
				.join(CoherentOvertimeApplicationWorkflow_.createdBy);

		Join<CoherentOvertimeApplicationWorkflow, CoherentOvertimeApplication> otTimesheetJoin = otTimesheetWorkflowRoot
				.join(CoherentOvertimeApplicationWorkflow_.coherentOvertimeApplication);

		restriction = cb.and(restriction,
				cb.equal(empLeaveJoin.get(Employee_.employeeId), createdById));

		restriction = cb.and(restriction, cb.equal(otTimesheetJoin
				.get(CoherentOvertimeApplication_.overtimeApplicationID),
				otTimesheetId));
		criteriaQuery.where(restriction);
		criteriaQuery
				.orderBy(cb.desc(otTimesheetWorkflowRoot
						.get(CoherentOvertimeApplicationWorkflow_.overtimeApplicationWorkflowID)));
		TypedQuery<CoherentOvertimeApplicationWorkflow> otWorkflowTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		otWorkflowTypedQuery.setMaxResults(1);
		List<CoherentOvertimeApplicationWorkflow> otworkflowList = otWorkflowTypedQuery
				.getResultList();
		if (otworkflowList != null && !otworkflowList.isEmpty()) {
			return otworkflowList.get(0);
		}
		return null;

	}

}
