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
import com.payasia.dao.LeaveSchemeWorkflowDAO;
import com.payasia.dao.bean.LeaveScheme;
import com.payasia.dao.bean.LeaveSchemeWorkflow;
import com.payasia.dao.bean.LeaveSchemeWorkflow_;
import com.payasia.dao.bean.LeaveScheme_;
import com.payasia.dao.bean.WorkFlowRuleMaster;
import com.payasia.dao.bean.WorkFlowRuleMaster_;

@Repository
public class LeaveSchemeWorkflowDAOImpl extends BaseDAO implements
		LeaveSchemeWorkflowDAO {

	@Override
	protected Object getBaseEntity() {
		LeaveSchemeWorkflow leaveSchemeWorkflow = new LeaveSchemeWorkflow();
		return leaveSchemeWorkflow;
	}

	@Override
	public void update(LeaveSchemeWorkflow leaveSchemeWorkflow) {
		super.update(leaveSchemeWorkflow);

	}

	@Override
	public void save(LeaveSchemeWorkflow leaveSchemeWorkflow) {
		super.save(leaveSchemeWorkflow);
	}

	@Override
	public void delete(LeaveSchemeWorkflow leaveSchemeWorkflow) {
		super.delete(leaveSchemeWorkflow);

	}

	@Override
	public LeaveSchemeWorkflow findByID(long leaveSchemeWorkflowId) {
		return super.findById(LeaveSchemeWorkflow.class, leaveSchemeWorkflowId);
	}

	@Override
	public List<LeaveSchemeWorkflow> findByCondition(Long leaveSchemeId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveSchemeWorkflow> criteriaQuery = cb
				.createQuery(LeaveSchemeWorkflow.class);
		Root<LeaveSchemeWorkflow> leaveSchemeRoot = criteriaQuery
				.from(LeaveSchemeWorkflow.class);

		criteriaQuery.select(leaveSchemeRoot);

		Join<LeaveSchemeWorkflow, LeaveScheme> leaveSchemeWorkFlowJoin = leaveSchemeRoot
				.join(LeaveSchemeWorkflow_.leaveScheme);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(
				leaveSchemeWorkFlowJoin.get(LeaveScheme_.leaveSchemeId),
				leaveSchemeId));
		criteriaQuery.where(restriction);

		TypedQuery<LeaveSchemeWorkflow> leaveSchemeTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		List<LeaveSchemeWorkflow> leaveSchemeList = leaveSchemeTypedQuery
				.getResultList();
		return leaveSchemeList;
	}

	@Override
	public void deleteByCondition(long leaveSchemeId) {

		String queryString = "DELETE FROM LeaveSchemeWorkflow e WHERE e.leaveScheme.leaveSchemeId = :leaveSchemeId";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("leaveSchemeId", leaveSchemeId);

		q.executeUpdate();

	}

	@Override
	public LeaveSchemeWorkflow findByLeaveSchemeIdRuleName(Long leaveSchemeId,
			String leaveReviewerRule) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveSchemeWorkflow> criteriaQuery = cb
				.createQuery(LeaveSchemeWorkflow.class);
		Root<LeaveSchemeWorkflow> leaveSchemeRoot = criteriaQuery
				.from(LeaveSchemeWorkflow.class);

		criteriaQuery.select(leaveSchemeRoot);

		Join<LeaveSchemeWorkflow, LeaveScheme> leaveSchemeWorkFlowJoin = leaveSchemeRoot
				.join(LeaveSchemeWorkflow_.leaveScheme);

		Join<LeaveSchemeWorkflow, WorkFlowRuleMaster> leaveSchemeRuleMasterJoin = leaveSchemeRoot
				.join(LeaveSchemeWorkflow_.workFlowRuleMaster);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(
				leaveSchemeWorkFlowJoin.get(LeaveScheme_.leaveSchemeId),
				leaveSchemeId));

		restriction = cb.and(restriction, cb.equal(
				leaveSchemeRuleMasterJoin.get(WorkFlowRuleMaster_.ruleName),
				leaveReviewerRule));

		criteriaQuery.where(restriction);

		TypedQuery<LeaveSchemeWorkflow> leaveSchemeTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<LeaveSchemeWorkflow> leaveSchemeList = leaveSchemeTypedQuery
				.getResultList();
		if (leaveSchemeList != null &&  !leaveSchemeList.isEmpty()) {
			return leaveSchemeList.get(0);
		}
		return null;
	}

	
}
