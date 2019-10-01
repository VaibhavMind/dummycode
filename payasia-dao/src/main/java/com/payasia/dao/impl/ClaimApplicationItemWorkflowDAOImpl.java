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
import com.payasia.dao.ClaimApplicationItemWorkflowDAO;
import com.payasia.dao.bean.AppCodeMaster;
import com.payasia.dao.bean.AppCodeMaster_;
import com.payasia.dao.bean.ClaimApplicationItem;
import com.payasia.dao.bean.ClaimApplicationItemWorkflow;
import com.payasia.dao.bean.ClaimApplicationItemWorkflow_;
import com.payasia.dao.bean.ClaimApplicationItem_;

@Repository
public class ClaimApplicationItemWorkflowDAOImpl extends BaseDAO implements
		ClaimApplicationItemWorkflowDAO {

	@Override
	protected Object getBaseEntity() {
		ClaimApplicationItemWorkflow claimApplicationItemWorkflow = new ClaimApplicationItemWorkflow();
		return claimApplicationItemWorkflow;
	}

	@Override
	public void save(ClaimApplicationItemWorkflow claimApplicationItemWorkflow) {
		super.save(claimApplicationItemWorkflow);
	}

	@Override
	public ClaimApplicationItemWorkflow findClaimItemStatus(
			ClaimApplicationItem claimApplicationItem, Long employeeId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ClaimApplicationItemWorkflow> criteriaQuery = cb
				.createQuery(ClaimApplicationItemWorkflow.class);
		Root<ClaimApplicationItemWorkflow> claimAppItemWorkFlow = criteriaQuery
				.from(ClaimApplicationItemWorkflow.class);
		criteriaQuery.select(claimAppItemWorkFlow);

		Predicate restriction = cb.conjunction();

		Join<ClaimApplicationItemWorkflow, ClaimApplicationItem> claimApplicationItemJoin = claimAppItemWorkFlow
				.join(ClaimApplicationItemWorkflow_.claimApplicationItem);

		restriction = cb.and(restriction, cb.equal(claimApplicationItemJoin
				.get(ClaimApplicationItem_.claimApplicationItemId),
				claimApplicationItem.getClaimApplicationItemId()));

		restriction = cb.and(
				restriction,
				cb.notEqual(
						claimAppItemWorkFlow
								.get(ClaimApplicationItemWorkflow_.createdBy)
								.get("employeeId").as(Long.class), employeeId));

		criteriaQuery.where(restriction);
		criteriaQuery.orderBy(cb.desc(claimAppItemWorkFlow
				.get(ClaimApplicationItemWorkflow_.createdDate)));
		TypedQuery<ClaimApplicationItemWorkflow> claimAppTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		claimAppTypedQuery.setMaxResults(1);
		List<ClaimApplicationItemWorkflow> claimAppList = claimAppTypedQuery
				.getResultList();
		if (claimAppList != null && !claimAppList.isEmpty()) {
			return claimAppList.get(0);
		}
		return null;
	}

	@Override
	public ClaimApplicationItemWorkflow findByClaimItem(
			Long claimApplicationItemId, Long appCodeIdForOverrideAction) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ClaimApplicationItemWorkflow> criteriaQuery = cb
				.createQuery(ClaimApplicationItemWorkflow.class);
		Root<ClaimApplicationItemWorkflow> claimAppItemWorkFlow = criteriaQuery
				.from(ClaimApplicationItemWorkflow.class);
		criteriaQuery.select(claimAppItemWorkFlow);

		Predicate restriction = cb.conjunction();

		Join<ClaimApplicationItemWorkflow, ClaimApplicationItem> claimApplicationItemJoin = claimAppItemWorkFlow
				.join(ClaimApplicationItemWorkflow_.claimApplicationItem);
		Join<ClaimApplicationItemWorkflow, AppCodeMaster> appCodeJoin = claimAppItemWorkFlow
				.join(ClaimApplicationItemWorkflow_.claimItemWorkflowAction);

		restriction = cb.and(restriction, cb.equal(claimApplicationItemJoin
				.get(ClaimApplicationItem_.claimApplicationItemId),
				claimApplicationItemId));

		restriction = cb.and(restriction, cb.equal(
				appCodeJoin.get(AppCodeMaster_.appCodeID),
				appCodeIdForOverrideAction));

		criteriaQuery.where(restriction);
		criteriaQuery
				.orderBy(cb.asc(claimAppItemWorkFlow
						.get(ClaimApplicationItemWorkflow_.claimApplicationItemWorkflowId)));
		TypedQuery<ClaimApplicationItemWorkflow> claimAppTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		claimAppTypedQuery.setMaxResults(1);
		List<ClaimApplicationItemWorkflow> claimAppList = claimAppTypedQuery
				.getResultList();
		if (claimAppList != null && !claimAppList.isEmpty()) {
			return claimAppList.get(0);
		}
		return null;
	}

	@Override
	public void deleteByCondition(Long claimApplicationItemId) {

		String queryString = "DELETE FROM ClaimApplicationItemWorkflow caiw WHERE caiw.claimApplicationItem.claimApplicationItemId = :claimApplicationItemId ";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("claimApplicationItemId", claimApplicationItemId);
		q.executeUpdate();
	}

}
