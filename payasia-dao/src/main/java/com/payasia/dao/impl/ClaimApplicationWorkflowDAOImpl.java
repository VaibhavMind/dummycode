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
import com.payasia.dao.ClaimApplicationWorkflowDAO;
import com.payasia.dao.bean.ClaimApplication;
import com.payasia.dao.bean.ClaimApplicationReviewer;
import com.payasia.dao.bean.ClaimApplicationReviewer_;
import com.payasia.dao.bean.ClaimApplicationWorkflow;
import com.payasia.dao.bean.ClaimApplicationWorkflow_;
import com.payasia.dao.bean.ClaimApplication_;
import com.payasia.dao.bean.ClaimStatusMaster;
import com.payasia.dao.bean.ClaimStatusMaster_;
import com.payasia.dao.bean.Employee;
import com.payasia.dao.bean.Employee_;

@Repository
public class ClaimApplicationWorkflowDAOImpl extends BaseDAO implements
		ClaimApplicationWorkflowDAO {

	@Override
	public void save(ClaimApplicationWorkflow claimApplicationWorkflow) {
		super.save(claimApplicationWorkflow);
	}

	@Override
	public void deleteByCondition(Long claimApplicationId) {

		String queryString = "DELETE FROM ClaimApplicationWorkflow caw WHERE caw.claimApplication.claimApplicationId = :claimApplicationId ";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("claimApplicationId", claimApplicationId);
		q.executeUpdate();
	}

	@Override
	protected Object getBaseEntity() {
		ClaimApplicationWorkflow claimApplicationWorkflow = new ClaimApplicationWorkflow();

		return claimApplicationWorkflow;
	}

	@Override
	public ClaimApplicationWorkflow saveReturn(
			ClaimApplicationWorkflow claimApplicationWorkflow) {

		ClaimApplicationWorkflow persistObj = claimApplicationWorkflow;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (ClaimApplicationWorkflow) getBaseEntity();
			beanUtil.copyProperties(persistObj, claimApplicationWorkflow);
		} catch (IllegalAccessException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		} catch (InvocationTargetException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		}
		this.entityManagerFactory.persist(persistObj);
		return persistObj;
	}

	@Override
	public ClaimApplicationWorkflow findByCondition(Long claimApplicationId,
			Long createdById) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ClaimApplicationWorkflow> criteriaQuery = cb
				.createQuery(ClaimApplicationWorkflow.class);
		Root<ClaimApplicationWorkflow> claimRoot = criteriaQuery
				.from(ClaimApplicationWorkflow.class);
		criteriaQuery.select(claimRoot);

		Predicate restriction = cb.conjunction();

		Join<ClaimApplicationWorkflow, Employee> empLeaveJoin = claimRoot
				.join(ClaimApplicationWorkflow_.employee);

		Join<ClaimApplicationWorkflow, ClaimApplication> claimApplicationJoin = claimRoot
				.join(ClaimApplicationWorkflow_.claimApplication);

		restriction = cb.and(restriction,
				cb.equal(empLeaveJoin.get(Employee_.employeeId), createdById));

		restriction = cb.and(restriction, cb.equal(
				claimApplicationJoin.get(ClaimApplication_.claimApplicationId),
				claimApplicationId));
		criteriaQuery.where(restriction);
		criteriaQuery.orderBy(cb.desc(claimRoot
				.get(ClaimApplicationWorkflow_.claimApplicationWorkflowId)));
		TypedQuery<ClaimApplicationWorkflow> claimAppTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		claimAppTypedQuery.setMaxResults(1);
		List<ClaimApplicationWorkflow> claimAppList = claimAppTypedQuery
				.getResultList();
		if (claimAppList != null && !claimAppList.isEmpty()) {
			return claimAppList.get(0);
		}
		return null;

	}

	@Override
	public ClaimApplicationWorkflow findByAppIdAndStatus(
			Long claimApplicationId, String StatusName) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ClaimApplicationWorkflow> criteriaQuery = cb
				.createQuery(ClaimApplicationWorkflow.class);
		Root<ClaimApplicationWorkflow> claimRoot = criteriaQuery
				.from(ClaimApplicationWorkflow.class);
		criteriaQuery.select(claimRoot);

		Predicate restriction = cb.conjunction();

		Join<ClaimApplicationWorkflow, ClaimApplication> claimApplicationJoin = claimRoot
				.join(ClaimApplicationWorkflow_.claimApplication);
		Join<ClaimApplicationWorkflow, ClaimStatusMaster> claimStatusJoin = claimRoot
				.join(ClaimApplicationWorkflow_.claimStatusMaster);

		restriction = cb.and(restriction, cb.equal(
				claimApplicationJoin.get(ClaimApplication_.claimApplicationId),
				claimApplicationId));
		restriction = cb.and(restriction, cb.equal(
				claimStatusJoin.get(ClaimStatusMaster_.claimStatusName),
				StatusName));
		criteriaQuery.where(restriction);
		criteriaQuery.orderBy(cb.desc(claimRoot
				.get(ClaimApplicationWorkflow_.claimApplicationWorkflowId)));
		TypedQuery<ClaimApplicationWorkflow> claimAppTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		claimAppTypedQuery.setMaxResults(1);
		List<ClaimApplicationWorkflow> claimAppList = claimAppTypedQuery
				.getResultList();
		if (claimAppList != null && !claimAppList.isEmpty()) {
			return claimAppList.get(0);
		}
		return null;

	}
	
	
	
	@Override
	public ClaimApplicationWorkflow findByReviewerCondition(Long claimApplicationId,
			Long createdById) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ClaimApplicationWorkflow> criteriaQuery = cb
				.createQuery(ClaimApplicationWorkflow.class);
		Root<ClaimApplicationWorkflow> claimRoot = criteriaQuery
				.from(ClaimApplicationWorkflow.class);
		criteriaQuery.select(claimRoot);

		Predicate restriction = cb.conjunction();

		Join<ClaimApplicationWorkflow, Employee> empLeaveJoin = claimRoot
				.join(ClaimApplicationWorkflow_.employee);

		Join<ClaimApplicationWorkflow, ClaimApplication> claimApplicationJoin = claimRoot
				.join(ClaimApplicationWorkflow_.claimApplication);
		
		Join<ClaimApplication , ClaimApplicationReviewer> claimApplicationRevJoin = claimApplicationJoin
				.join(ClaimApplication_.claimApplicationReviewers);
		Join<ClaimApplicationReviewer , Employee> claimApplicationEmpJoin = claimApplicationRevJoin
				.join(ClaimApplicationReviewer_.employee);

		restriction = cb.and(restriction,
				cb.equal(empLeaveJoin.get(Employee_.employeeId), createdById));
		
		restriction = cb.or(restriction,
				cb.equal(claimApplicationRevJoin.get(ClaimApplicationReviewer_.employee), createdById));

		restriction = cb.and(restriction, cb.equal(
				claimApplicationJoin.get(ClaimApplication_.claimApplicationId),
				claimApplicationId));
		criteriaQuery.where(restriction);
		criteriaQuery.orderBy(cb.desc(claimRoot
				.get(ClaimApplicationWorkflow_.claimApplicationWorkflowId)));
		TypedQuery<ClaimApplicationWorkflow> claimAppTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		claimAppTypedQuery.setMaxResults(1);
		List<ClaimApplicationWorkflow> claimAppList = claimAppTypedQuery
				.getResultList();
		if (claimAppList != null && !claimAppList.isEmpty()) {
			return claimAppList.get(0);
		}
		return null;

	}
	
	@Override
	public List<ClaimApplicationWorkflow> findWorkFlowByClaimAppId(Long claimApplicationId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ClaimApplicationWorkflow> criteriaQuery = cb
				.createQuery(ClaimApplicationWorkflow.class);
		Root<ClaimApplicationWorkflow> claimRoot = criteriaQuery
				.from(ClaimApplicationWorkflow.class);
		criteriaQuery.select(claimRoot);

		Predicate restriction = cb.conjunction();

		Join<ClaimApplicationWorkflow, ClaimApplication> claimApplicationJoin = claimRoot
				.join(ClaimApplicationWorkflow_.claimApplication);
		Join<ClaimApplicationWorkflow, ClaimStatusMaster> claimStatusJoin = claimRoot
				.join(ClaimApplicationWorkflow_.claimStatusMaster);

		restriction = cb.and(restriction, cb.equal(
				claimApplicationJoin.get(ClaimApplication_.claimApplicationId),
				claimApplicationId));
		
		criteriaQuery.where(restriction);
		criteriaQuery.orderBy(cb.desc(claimRoot
				.get(ClaimApplicationWorkflow_.claimApplicationWorkflowId)));
		TypedQuery<ClaimApplicationWorkflow> claimAppTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		List<ClaimApplicationWorkflow> claimAppList = claimAppTypedQuery
				.getResultList();
		if (claimAppList != null && !claimAppList.isEmpty()) {
			return claimAppList;
		}
		return null;

	}
	

}
