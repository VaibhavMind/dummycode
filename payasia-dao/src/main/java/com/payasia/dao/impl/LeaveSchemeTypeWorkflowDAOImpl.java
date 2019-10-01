package com.payasia.dao.impl;

import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.springframework.stereotype.Repository;

import com.payasia.dao.BaseDAO;
import com.payasia.dao.LeaveSchemeTypeWorkflowDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.LeaveScheme;
import com.payasia.dao.bean.LeaveSchemeType;
import com.payasia.dao.bean.LeaveSchemeTypeWorkflow;
import com.payasia.dao.bean.LeaveSchemeTypeWorkflow_;
import com.payasia.dao.bean.LeaveSchemeType_;
import com.payasia.dao.bean.LeaveScheme_;
import com.payasia.dao.bean.WorkFlowRuleMaster;
import com.payasia.dao.bean.WorkFlowRuleMaster_;

@Repository
public class LeaveSchemeTypeWorkflowDAOImpl extends BaseDAO implements
		LeaveSchemeTypeWorkflowDAO {

	@Override
	protected Object getBaseEntity() {

		LeaveSchemeTypeWorkflow leaveSchemeTypeWorkflow = new LeaveSchemeTypeWorkflow();
		return leaveSchemeTypeWorkflow;
	}

	@Override
	public List<LeaveSchemeTypeWorkflow> findByCondition(Long leaveTypeId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveSchemeTypeWorkflow> criteriaQuery = cb
				.createQuery(LeaveSchemeTypeWorkflow.class);
		Root<LeaveSchemeTypeWorkflow> leaveSchemeTypeWorkflowRoot = criteriaQuery
				.from(LeaveSchemeTypeWorkflow.class);

		criteriaQuery.select(leaveSchemeTypeWorkflowRoot);

		Join<LeaveSchemeTypeWorkflow, LeaveSchemeType> leaveTypeJoin = leaveSchemeTypeWorkflowRoot
				.join(LeaveSchemeTypeWorkflow_.leaveSchemeType);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(
				leaveTypeJoin.get(LeaveSchemeType_.leaveSchemeTypeId),
				leaveTypeId));
		criteriaQuery.where(restriction);

		TypedQuery<LeaveSchemeTypeWorkflow> leaveTypeTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		List<LeaveSchemeTypeWorkflow> leaveTypeWorkFlowList = leaveTypeTypedQuery
				.getResultList();
		return leaveTypeWorkFlowList;
	}

	@Override
	public LeaveSchemeTypeWorkflow findByID(long leaveSchemeTypeId) {
		return super.findById(LeaveSchemeTypeWorkflow.class, leaveSchemeTypeId);
	}

	@Override
	public LeaveSchemeTypeWorkflow findByLeaveSchemeIdRuleName(
			Long leaveSchemeTypeId, String leaveReviewerRule) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveSchemeTypeWorkflow> criteriaQuery = cb
				.createQuery(LeaveSchemeTypeWorkflow.class);
		Root<LeaveSchemeTypeWorkflow> leaveSchemeTypeRoot = criteriaQuery
				.from(LeaveSchemeTypeWorkflow.class);

		criteriaQuery.select(leaveSchemeTypeRoot);

		Join<LeaveSchemeTypeWorkflow, LeaveSchemeType> leaveSchemeWorkFlowJoin = leaveSchemeTypeRoot
				.join(LeaveSchemeTypeWorkflow_.leaveSchemeType);

		Join<LeaveSchemeTypeWorkflow, WorkFlowRuleMaster> leaveSchemeRuleMasterJoin = leaveSchemeTypeRoot
				.join(LeaveSchemeTypeWorkflow_.workFlowRuleMaster);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction,
				cb.equal(leaveSchemeWorkFlowJoin
						.get(LeaveSchemeType_.leaveSchemeTypeId),
						leaveSchemeTypeId));

		restriction = cb.and(restriction, cb.equal(
				leaveSchemeRuleMasterJoin.get(WorkFlowRuleMaster_.ruleName),
				leaveReviewerRule));

		criteriaQuery.where(restriction);

		TypedQuery<LeaveSchemeTypeWorkflow> leaveSchemeTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<LeaveSchemeTypeWorkflow> leaveSchemeList = leaveSchemeTypedQuery
				.getResultList();
		if (leaveSchemeList != null &&  !leaveSchemeList.isEmpty()) {
			return leaveSchemeList.get(0);
		}
		return null;
	}

	@Override
	public LeaveSchemeTypeWorkflow findMaxWorkFlowRuleValByLeaveScheme(
			Long leaveSchemeId, String leaveReviewerRule) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveSchemeTypeWorkflow> criteriaQuery = cb
				.createQuery(LeaveSchemeTypeWorkflow.class);
		Root<LeaveSchemeTypeWorkflow> leaveSchemeTypeRoot = criteriaQuery
				.from(LeaveSchemeTypeWorkflow.class);

		criteriaQuery.select(leaveSchemeTypeRoot);

		Join<LeaveSchemeTypeWorkflow, LeaveSchemeType> leaveSchemeWorkFlowJoin = leaveSchemeTypeRoot
				.join(LeaveSchemeTypeWorkflow_.leaveSchemeType);
		Join<LeaveSchemeType, LeaveScheme> leaveSchemeJoin = leaveSchemeWorkFlowJoin
				.join(LeaveSchemeType_.leaveScheme);

		Join<LeaveSchemeTypeWorkflow, WorkFlowRuleMaster> leaveSchemeRuleMasterJoin = leaveSchemeTypeRoot
				.join(LeaveSchemeTypeWorkflow_.workFlowRuleMaster);

		Predicate restriction = cb.conjunction();

		restriction = cb
				.and(restriction, cb.equal(
						leaveSchemeJoin.get(LeaveScheme_.leaveSchemeId),
						leaveSchemeId));

		restriction = cb.and(restriction, cb.equal(
				leaveSchemeRuleMasterJoin.get(WorkFlowRuleMaster_.ruleName),
				leaveReviewerRule));

		Subquery<Long> subquery = criteriaQuery.subquery(Long.class);
		Root<WorkFlowRuleMaster> workFlowRulSubRoot = subquery
				.from(WorkFlowRuleMaster.class);
		subquery.select(cb.max(workFlowRulSubRoot.get(
				WorkFlowRuleMaster_.workFlowRuleId).as(Long.class)));
		Join<WorkFlowRuleMaster, LeaveSchemeTypeWorkflow> subleaveSchemeTypeWrkJoin = workFlowRulSubRoot
				.join(WorkFlowRuleMaster_.leaveSchemeTypeWorkflows);

		Join<LeaveSchemeTypeWorkflow, LeaveSchemeType> subLeaveSchemeTypeJoin = subleaveSchemeTypeWrkJoin
				.join(LeaveSchemeTypeWorkflow_.leaveSchemeType);

		Join<LeaveSchemeType, LeaveScheme> subLeaveschemeJoin = subLeaveSchemeTypeJoin
				.join(LeaveSchemeType_.leaveScheme);

		Predicate subRestriction = cb.conjunction();

		subRestriction = cb.and(subRestriction, cb.equal(
				subLeaveschemeJoin.get(LeaveScheme_.leaveSchemeId),
				leaveSchemeId));

		subRestriction = cb.and(subRestriction, cb.equal(
				workFlowRulSubRoot.get(WorkFlowRuleMaster_.ruleName),
				leaveReviewerRule));
		subquery.where(subRestriction);

		restriction = cb.and(
				restriction,
				cb.in(leaveSchemeRuleMasterJoin
						.get(WorkFlowRuleMaster_.workFlowRuleId)).value(
						subquery));

		criteriaQuery.where(restriction);

		TypedQuery<LeaveSchemeTypeWorkflow> leaveSchemeTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<LeaveSchemeTypeWorkflow> leaveSchemeList = leaveSchemeTypedQuery
				.getResultList();
		if (leaveSchemeList != null &&  !leaveSchemeList.isEmpty()) {
			return leaveSchemeList.get(0);
		}
		return null;
	}

	@Override
	public void update(LeaveSchemeTypeWorkflow leaveSchemeTypeWorkflow) {
		super.update(leaveSchemeTypeWorkflow);

	}

	@Override
	public void save(LeaveSchemeTypeWorkflow leaveSchemeTypeWorkflow) {
		super.save(leaveSchemeTypeWorkflow);
	}

	@Override
	public void delete(LeaveSchemeTypeWorkflow leaveSchemeTypeWorkflow) {
		super.delete(leaveSchemeTypeWorkflow);

	}

	@Override
	public void deleteByCondition(long leaveSchemeTypeId) {

		String queryString = "DELETE FROM LeaveSchemeTypeWorkflow e WHERE e.leaveSchemeType.leaveSchemeTypeId = :leaveSchemeTypeId";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("leaveSchemeTypeId", leaveSchemeTypeId);

		q.executeUpdate();

	}

	@Override
	public List<LeaveSchemeTypeWorkflow> findByCondition(Long leaveTypeId, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveSchemeTypeWorkflow> criteriaQuery = cb
				.createQuery(LeaveSchemeTypeWorkflow.class);
		Root<LeaveSchemeTypeWorkflow> leaveSchemeTypeWorkflowRoot = criteriaQuery
				.from(LeaveSchemeTypeWorkflow.class);

		criteriaQuery.select(leaveSchemeTypeWorkflowRoot);

		Join<LeaveSchemeTypeWorkflow, LeaveSchemeType> leaveTypeJoin = leaveSchemeTypeWorkflowRoot
				.join(LeaveSchemeTypeWorkflow_.leaveSchemeType);
		Join<LeaveSchemeType, LeaveScheme> leaveSchemeTypeJoin = leaveTypeJoin
				.join(LeaveSchemeType_.leaveScheme);
		Join<LeaveScheme, Company> leaveCompTypeJoin = leaveSchemeTypeJoin
				.join(LeaveScheme_.company);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(
				leaveCompTypeJoin.get(Company_.companyId),
				companyId));
			
		restriction = cb.and(restriction, cb.equal(
				leaveTypeJoin.get(LeaveSchemeType_.leaveSchemeTypeId),
				leaveTypeId));
		criteriaQuery.where(restriction);

		TypedQuery<LeaveSchemeTypeWorkflow> leaveTypeTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		List<LeaveSchemeTypeWorkflow> leaveTypeWorkFlowList = leaveTypeTypedQuery
				.getResultList();
		return leaveTypeWorkFlowList;
	}

}
