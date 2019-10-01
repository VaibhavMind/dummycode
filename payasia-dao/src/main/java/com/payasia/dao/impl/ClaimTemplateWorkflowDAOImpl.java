package com.payasia.dao.impl;

import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.payasia.dao.BaseDAO;
import com.payasia.dao.ClaimTemplateWorkflowDAO;
import com.payasia.dao.bean.ClaimTemplate;
import com.payasia.dao.bean.ClaimTemplateWorkflow;
import com.payasia.dao.bean.ClaimTemplateWorkflow_;
import com.payasia.dao.bean.ClaimTemplate_;
import com.payasia.dao.bean.CompanyBaseEntity_;
import com.payasia.dao.bean.WorkFlowRuleMaster;
import com.payasia.dao.bean.WorkFlowRuleMaster_;

@Repository
public class ClaimTemplateWorkflowDAOImpl extends BaseDAO implements
		ClaimTemplateWorkflowDAO {

	@Override
	protected Object getBaseEntity() {
		ClaimTemplateWorkflow claimTemplateWorkflow = new ClaimTemplateWorkflow();
		return claimTemplateWorkflow;
	}

	@Override
	public void deleteByCondition(Long claimTemplateId) {
		String queryString = "DELETE FROM ClaimTemplateWorkflow e WHERE e.claimTemplate.claimTemplateId = :claimTemplateId";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("claimTemplateId", claimTemplateId);

		q.executeUpdate();

	}

	@Override
	public List<ClaimTemplateWorkflow> findByCondition(Long claimTemplateId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ClaimTemplateWorkflow> criteriaQuery = cb
				.createQuery(ClaimTemplateWorkflow.class);
		Root<ClaimTemplateWorkflow> claimTemWFRoot = criteriaQuery
				.from(ClaimTemplateWorkflow.class);

		criteriaQuery.select(claimTemWFRoot);

		Join<ClaimTemplateWorkflow, ClaimTemplate> claimTemWorkFlowJoin = claimTemWFRoot
				.join(ClaimTemplateWorkflow_.claimTemplate);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(
				claimTemWorkFlowJoin.get(ClaimTemplate_.claimTemplateId),
				claimTemplateId));
		criteriaQuery.where(restriction);

		TypedQuery<ClaimTemplateWorkflow> claimTemTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		List<ClaimTemplateWorkflow> claimTemList = claimTemTypedQuery
				.getResultList();
		return claimTemList;
	}

	@Override
	public void save(ClaimTemplateWorkflow claimTemplateWorkflow) {
		super.save(claimTemplateWorkflow);

	}

	@Override
	public ClaimTemplateWorkflow findByTemplateIdRuleName(Long claimTemplateId,Long companyId,
			String leaveReviewerRule) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ClaimTemplateWorkflow> criteriaQuery = cb
				.createQuery(ClaimTemplateWorkflow.class);
		Root<ClaimTemplateWorkflow> claimTemplateWorkflowRoot = criteriaQuery
				.from(ClaimTemplateWorkflow.class);

		criteriaQuery.select(claimTemplateWorkflowRoot);

		Join<ClaimTemplateWorkflow, ClaimTemplate> claimTemplateJoin = claimTemplateWorkflowRoot
				.join(ClaimTemplateWorkflow_.claimTemplate);

		Join<ClaimTemplateWorkflow, WorkFlowRuleMaster> workFlowRuleMasterJoin = claimTemplateWorkflowRoot
				.join(ClaimTemplateWorkflow_.workFlowRuleMaster);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(
				claimTemplateWorkflowRoot.get(CompanyBaseEntity_.companyId),
				companyId));
		
		restriction = cb.and(restriction, cb.equal(
				claimTemplateJoin.get(ClaimTemplate_.claimTemplateId),
				claimTemplateId));

		restriction = cb.and(restriction, cb.equal(
				workFlowRuleMasterJoin.get(WorkFlowRuleMaster_.ruleName),
				leaveReviewerRule));

		criteriaQuery.where(restriction);

		TypedQuery<ClaimTemplateWorkflow> claimTemplateWorkflowTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<ClaimTemplateWorkflow> claimTemplateList = claimTemplateWorkflowTypedQuery
				.getResultList();
		if (claimTemplateList != null && !claimTemplateList.isEmpty()) {
			return claimTemplateList.get(0);
		}
		return null;
	}

}
