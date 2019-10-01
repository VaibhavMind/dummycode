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
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.LeaveApplicationWorkflowDAO;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.Employee_;
import com.payasia.dao.bean.LeaveApplication;
import com.payasia.dao.bean.LeaveApplicationWorkflow;
import com.payasia.dao.bean.LeaveApplicationWorkflow_;
import com.payasia.dao.bean.LeaveApplication_;
import com.payasia.dao.bean.LeaveStatusMaster;
import com.payasia.dao.bean.LeaveStatusMaster_;

@Repository
public class LeaveApplicationWorkflowDAOImpl extends BaseDAO implements
		LeaveApplicationWorkflowDAO {

	@Override
	public void save(LeaveApplicationWorkflow leaveApplicationWorkflow) {
		super.save(leaveApplicationWorkflow);
	}

	@Override
	public LeaveApplicationWorkflow saveReturn(
			LeaveApplicationWorkflow leaveApplicationWorkflow) {
		LeaveApplicationWorkflow persistObj = leaveApplicationWorkflow;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (LeaveApplicationWorkflow) getBaseEntity();
			beanUtil.copyProperties(persistObj, leaveApplicationWorkflow);
		} catch (IllegalAccessException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		} catch (InvocationTargetException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		}
		this.entityManagerFactory.persist(persistObj);
		return persistObj;
	}

	@Override
	public LeaveApplicationWorkflow findByCondition(Long leaveApplicationId,
			Long createdById) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveApplicationWorkflow> criteriaQuery = cb
				.createQuery(LeaveApplicationWorkflow.class);
		Root<LeaveApplicationWorkflow> leaveApplicationWorkflowRoot = criteriaQuery
				.from(LeaveApplicationWorkflow.class);
		criteriaQuery.select(leaveApplicationWorkflowRoot);

		Predicate restriction = cb.conjunction();

		Join<LeaveApplicationWorkflow, Employee> empLeaveJoin = leaveApplicationWorkflowRoot
				.join(LeaveApplicationWorkflow_.employee);

		Join<LeaveApplicationWorkflow, LeaveApplication> leaveApplicationJoin = leaveApplicationWorkflowRoot
				.join(LeaveApplicationWorkflow_.leaveApplication);

		Join<LeaveApplicationWorkflow, LeaveStatusMaster> leaveStatusMasterJOin = leaveApplicationWorkflowRoot
				.join(LeaveApplicationWorkflow_.leaveStatusMaster);

		restriction = cb.and(restriction,
				cb.equal(empLeaveJoin.get(Employee_.employeeId), createdById));

		restriction = cb.and(restriction, cb.equal(
				leaveApplicationJoin.get(LeaveApplication_.leaveApplicationId),
				leaveApplicationId));

		restriction = cb.and(restriction, cb.notEqual(
				leaveStatusMasterJOin.get(LeaveStatusMaster_.leaveStatusName),
				PayAsiaConstants.LEAVE_STATUS_SUBMITTED));

		criteriaQuery.where(restriction);
		TypedQuery<LeaveApplicationWorkflow> leaveAppTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		List<LeaveApplicationWorkflow> leaveAppList = leaveAppTypedQuery
				.getResultList();
		if (leaveAppList != null &&  !leaveAppList.isEmpty()) {
			return leaveAppList.get(0);
		}
		return null;

	}

	@Override
	public LeaveApplicationWorkflow findByConditionLeaveStatus(
			Long leaveApplicationId, Long createdById, Long leaveStatusId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveApplicationWorkflow> criteriaQuery = cb
				.createQuery(LeaveApplicationWorkflow.class);
		Root<LeaveApplicationWorkflow> empRoot = criteriaQuery
				.from(LeaveApplicationWorkflow.class);
		criteriaQuery.select(empRoot);

		Predicate restriction = cb.conjunction();

		Join<LeaveApplicationWorkflow, Employee> empLeaveJoin = empRoot
				.join(LeaveApplicationWorkflow_.employee);

		Join<LeaveApplicationWorkflow, LeaveStatusMaster> leaveStatusJoin = empRoot
				.join(LeaveApplicationWorkflow_.leaveStatusMaster);

		Join<LeaveApplicationWorkflow, LeaveApplication> leaveApplicationJoin = empRoot
				.join(LeaveApplicationWorkflow_.leaveApplication);

		restriction = cb.and(restriction,
				cb.equal(empLeaveJoin.get(Employee_.employeeId), createdById));

		restriction = cb.and(restriction, cb.equal(
				leaveApplicationJoin.get(LeaveApplication_.leaveApplicationId),
				leaveApplicationId));

		restriction = cb.and(restriction, cb.equal(
				leaveStatusJoin.get(LeaveStatusMaster_.leaveStatusID),
				leaveStatusId));

		criteriaQuery.where(restriction);
		TypedQuery<LeaveApplicationWorkflow> leaveAppTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		List<LeaveApplicationWorkflow> leaveAppList = leaveAppTypedQuery
				.getResultList();
		if (leaveAppList != null &&  !leaveAppList.isEmpty()) {
			return leaveAppList.get(0);
		}
		return null;

	}

	@Override
	protected Object getBaseEntity() {
		LeaveApplicationWorkflow leaveApplicationWorkflow = new LeaveApplicationWorkflow();
		return leaveApplicationWorkflow;
	}

	@Override
	public void deleteByCondition(Long leaveApplicationId) {

		String queryString = "DELETE FROM LeaveApplicationWorkflow lea WHERE  lea.leaveApplication.leaveApplicationId = :leaveApplicationId ";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("leaveApplicationId", leaveApplicationId);

		q.executeUpdate();
	}

}
