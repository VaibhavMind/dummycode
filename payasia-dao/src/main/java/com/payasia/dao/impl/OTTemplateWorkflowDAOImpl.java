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
import com.payasia.dao.OTTemplateWorkflowDAO;
import com.payasia.dao.bean.OTTemplate;
import com.payasia.dao.bean.OTTemplateWorkflow;
import com.payasia.dao.bean.OTTemplateWorkflow_;
import com.payasia.dao.bean.OTTemplate_;
import com.payasia.dao.bean.WorkFlowRuleMaster;
import com.payasia.dao.bean.WorkFlowRuleMaster_;

/**
 * The Class OTTemplateWorkflowDAOImpl.
 */
/**
 * @author vivekjain
 * 
 */
@Repository
public class OTTemplateWorkflowDAOImpl extends BaseDAO implements
		OTTemplateWorkflowDAO {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.OTTemplateWorkflowDAO#findByTemplateIdRuleName(java.lang
	 * .Long, java.lang.String)
	 */
	@Override
	public OTTemplateWorkflow findByTemplateIdRuleName(Long otTemplateId,
			String otReviewerRule) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<OTTemplateWorkflow> criteriaQuery = cb
				.createQuery(OTTemplateWorkflow.class);
		Root<OTTemplateWorkflow> oTTemplateWorkflowRoot = criteriaQuery
				.from(OTTemplateWorkflow.class);

		criteriaQuery.select(oTTemplateWorkflowRoot);

		Join<OTTemplateWorkflow, OTTemplate> oTTemplateJoin = oTTemplateWorkflowRoot
				.join(OTTemplateWorkflow_.otTemplate);

		Join<OTTemplateWorkflow, WorkFlowRuleMaster> workFlowRuleMasterJoin = oTTemplateWorkflowRoot
				.join(OTTemplateWorkflow_.workFlowRuleMaster);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(
				oTTemplateJoin.get(OTTemplate_.otTemplateId), otTemplateId));

		restriction = cb.and(restriction, cb.equal(
				workFlowRuleMasterJoin.get(WorkFlowRuleMaster_.ruleName),
				otReviewerRule));

		criteriaQuery.where(restriction);

		TypedQuery<OTTemplateWorkflow> oTTemplateWorkflowTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		if (oTTemplateWorkflowTypedQuery.getResultList().size() > 0) {
			OTTemplateWorkflow otTemplateWorkflow = oTTemplateWorkflowTypedQuery
					.getSingleResult();
			return otTemplateWorkflow;
		} else {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.BaseDAO#getBaseEntity()
	 */
	@Override
	protected Object getBaseEntity() {

		OTTemplateWorkflow oTTemplateWorkflow = new OTTemplateWorkflow();
		return oTTemplateWorkflow;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.OTTemplateWorkflowDAO#deleteByCondition(java.lang.Long)
	 */
	@Override
	public void deleteByCondition(Long otTemplateId) {
		String queryString = "DELETE FROM OTTemplateWorkflow e WHERE e.otTemplate.otTemplateId = :otTemplateId";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("otTemplateId", otTemplateId);

		q.executeUpdate();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.OTTemplateWorkflowDAO#findByCondition(java.lang.Long)
	 */
	@Override
	public List<OTTemplateWorkflow> findByCondition(Long otTemplateId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<OTTemplateWorkflow> criteriaQuery = cb
				.createQuery(OTTemplateWorkflow.class);
		Root<OTTemplateWorkflow> otTemWFRoot = criteriaQuery
				.from(OTTemplateWorkflow.class);

		criteriaQuery.select(otTemWFRoot);

		Join<OTTemplateWorkflow, OTTemplate> otTemWorkFlowJoin = otTemWFRoot
				.join(OTTemplateWorkflow_.otTemplate);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(
				otTemWorkFlowJoin.get(OTTemplate_.otTemplateId), otTemplateId));
		criteriaQuery.where(restriction);

		TypedQuery<OTTemplateWorkflow> otTemTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		List<OTTemplateWorkflow> otTemList = otTemTypedQuery.getResultList();
		return otTemList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.OTTemplateWorkflowDAO#save(com.payasia.dao.bean.
	 * OTTemplateWorkflow)
	 */
	@Override
	public void save(OTTemplateWorkflow otTemplateWorkflow) {
		super.save(otTemplateWorkflow);

	}

}
