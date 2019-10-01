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
import com.payasia.dao.ClaimTemplateItemWorkflowDAO;
import com.payasia.dao.bean.ClaimTemplateItem;
import com.payasia.dao.bean.ClaimTemplateItemWorkflow;
import com.payasia.dao.bean.ClaimTemplateItemWorkflow_;
import com.payasia.dao.bean.ClaimTemplateItem_;

@Repository
public class ClaimTemplateItemWorkflowDAOImpl extends BaseDAO implements
		ClaimTemplateItemWorkflowDAO {

	@Override
	protected Object getBaseEntity() {
		ClaimTemplateItemWorkflow claimTemplateItemWorkflow = new ClaimTemplateItemWorkflow();
		return claimTemplateItemWorkflow;
	}

	@Override
	public void update(ClaimTemplateItemWorkflow claimTemplateItemWorkflow) {
		super.update(claimTemplateItemWorkflow);

	}

	@Override
	public void save(ClaimTemplateItemWorkflow claimTemplateItemWorkflow) {
		super.save(claimTemplateItemWorkflow);
	}

	@Override
	public void delete(ClaimTemplateItemWorkflow claimTemplateItemWorkflow) {
		super.delete(claimTemplateItemWorkflow);

	}

	@Override
	public ClaimTemplateItemWorkflow findByID(long claimTemplateItemWorkflow) {
		return super.findById(ClaimTemplateItemWorkflow.class,
				claimTemplateItemWorkflow);
	}

	@Override
	public ClaimTemplateItemWorkflow saveReturn(
			ClaimTemplateItemWorkflow claimTemplateItemWorkflow) {

		ClaimTemplateItemWorkflow persistObj = claimTemplateItemWorkflow;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (ClaimTemplateItemWorkflow) getBaseEntity();
			beanUtil.copyProperties(persistObj, claimTemplateItemWorkflow);
		} catch (IllegalAccessException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		} catch (InvocationTargetException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		}
		this.entityManagerFactory.persist(persistObj);
		return persistObj;
	}

	@Override
	public void deleteByCondition(long claimTemplateItemId) {

		String queryString = "DELETE FROM ClaimTemplateItemWorkflow e WHERE e.claimTemplateItem.claimTemplateItemId = :claimTemplateItemId";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("claimTemplateItemId", claimTemplateItemId);

		q.executeUpdate();

	}

	@Override
	public List<ClaimTemplateItemWorkflow> findByCondition(
			Long claimTemplateItemId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ClaimTemplateItemWorkflow> criteriaQuery = cb
				.createQuery(ClaimTemplateItemWorkflow.class);
		Root<ClaimTemplateItemWorkflow> claimTemWFRoot = criteriaQuery
				.from(ClaimTemplateItemWorkflow.class);

		criteriaQuery.select(claimTemWFRoot);

		Join<ClaimTemplateItemWorkflow, ClaimTemplateItem> claimTemplateItemJoin = claimTemWFRoot
				.join(ClaimTemplateItemWorkflow_.claimTemplateItem);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(claimTemplateItemJoin
				.get(ClaimTemplateItem_.claimTemplateItemId),
				claimTemplateItemId));
		criteriaQuery.where(restriction);

		TypedQuery<ClaimTemplateItemWorkflow> claimTemTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		List<ClaimTemplateItemWorkflow> claimTemList = claimTemTypedQuery
				.getResultList();
		return claimTemList;
	}

}
