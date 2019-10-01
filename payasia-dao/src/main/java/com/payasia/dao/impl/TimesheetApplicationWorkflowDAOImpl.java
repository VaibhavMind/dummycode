package com.payasia.dao.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.persistence.Query;
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
import com.payasia.dao.TimesheetApplicationWorkflowDAO;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.EmployeeTimesheetApplication;
import com.payasia.dao.bean.EmployeeTimesheetApplication_;
import com.payasia.dao.bean.Employee_;
import com.payasia.dao.bean.TimesheetApplicationWorkflow;
import com.payasia.dao.bean.TimesheetApplicationWorkflow_;

@Repository
public class TimesheetApplicationWorkflowDAOImpl extends BaseDAO implements
		TimesheetApplicationWorkflowDAO {
	@Override
	public void save(TimesheetApplicationWorkflow workflow) {
		super.save(workflow);
	}

	@Override
	public void deleteByCondition(Long timesheetId) {

		String queryString = "DELETE FROM TimesheetApplicationWorkflow caw WHERE caw.LundinTimesheet.timesheetId = :timesheetId ";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("timesheetId", timesheetId);
		q.executeUpdate();
	}

	@Override
	protected Object getBaseEntity() {
		TimesheetApplicationWorkflow workflow = new TimesheetApplicationWorkflow();
		return workflow;
	}

	@Override
	public TimesheetApplicationWorkflow saveReturn(
			TimesheetApplicationWorkflow workflow) {

		TimesheetApplicationWorkflow persistObj = workflow;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (TimesheetApplicationWorkflow) getBaseEntity();
			beanUtil.copyProperties(persistObj, workflow);
		} catch (IllegalAccessException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		} catch (InvocationTargetException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		}
		this.entityManagerFactory.persist(persistObj);
		return persistObj;
	}

	@Override
	public TimesheetApplicationWorkflow findByCondition(Long otTimesheetId,
			Long createdById) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<TimesheetApplicationWorkflow> criteriaQuery = cb
				.createQuery(TimesheetApplicationWorkflow.class);
		Root<TimesheetApplicationWorkflow> otTimesheetWorkflowRoot = criteriaQuery
				.from(TimesheetApplicationWorkflow.class);
		criteriaQuery.select(otTimesheetWorkflowRoot);

		Predicate restriction = cb.conjunction();

		Join<TimesheetApplicationWorkflow, Employee> empLeaveJoin = otTimesheetWorkflowRoot
				.join(TimesheetApplicationWorkflow_.createdBy);

		Join<TimesheetApplicationWorkflow, EmployeeTimesheetApplication> otTimesheetJoin = otTimesheetWorkflowRoot
				.join(TimesheetApplicationWorkflow_.employeeTimesheetApplication);

		restriction = cb.and(restriction,
				cb.equal(empLeaveJoin.get(Employee_.employeeId), createdById));

		restriction = cb.and(restriction, cb.equal(
				otTimesheetJoin.get(EmployeeTimesheetApplication_.timesheetId),
				otTimesheetId));
		criteriaQuery.where(restriction);
		criteriaQuery.orderBy(cb.desc(otTimesheetWorkflowRoot
				.get(TimesheetApplicationWorkflow_.timesheetWorkflowId)));
		TypedQuery<TimesheetApplicationWorkflow> otWorkflowTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		otWorkflowTypedQuery.setMaxResults(1);
		List<TimesheetApplicationWorkflow> otworkflowList = otWorkflowTypedQuery
				.getResultList();
		if (otworkflowList != null && !otworkflowList.isEmpty()) {
			return otworkflowList.get(0);
		}
		return null;

	}

	@Override
	public void delete(TimesheetApplicationWorkflow workflow) {
		super.delete(workflow);

	}

}
