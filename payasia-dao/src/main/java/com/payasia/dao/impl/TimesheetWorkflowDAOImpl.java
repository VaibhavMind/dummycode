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
import com.payasia.dao.TimesheetWorkflowDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.TimesheetWorkflow;
import com.payasia.dao.bean.TimesheetWorkflow_;

@Repository
public class TimesheetWorkflowDAOImpl extends BaseDAO implements
		TimesheetWorkflowDAO {

	@Override
	protected Object getBaseEntity() {
		TimesheetWorkflow lundinWorkflow = new TimesheetWorkflow();
		return lundinWorkflow;
	}

	@Override
	public void update(TimesheetWorkflow lundinWorkflow) {
		this.entityManagerFactory.merge(lundinWorkflow);
	}

	@Override
	public void delete(TimesheetWorkflow lundinWorkflow) {
		this.entityManagerFactory.remove(lundinWorkflow);
	}

	@Override
	public void save(TimesheetWorkflow lundinWorkflow) {
		super.save(lundinWorkflow);
	}

	@Override
	public TimesheetWorkflow findById(Long lundinWorkflowId) {
		return super.findById(TimesheetWorkflow.class, lundinWorkflowId);

	}

	@Override
	public List<TimesheetWorkflow> findByCompanyId(Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<TimesheetWorkflow> criteriaQuery = cb
				.createQuery(TimesheetWorkflow.class);
		Root<TimesheetWorkflow> root = criteriaQuery
				.from(TimesheetWorkflow.class);
		criteriaQuery.select(root);

		Predicate restriction = cb.conjunction();
		Join<TimesheetWorkflow, Company> compJoin = root
				.join(TimesheetWorkflow_.company);

		restriction = cb.and(restriction,
				cb.equal(compJoin.get(Company_.companyId), companyId));
		criteriaQuery.where(restriction);

		TypedQuery<TimesheetWorkflow> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<TimesheetWorkflow> changeRequestList = typedQuery.getResultList();

		return changeRequestList;

	}

	@Override
	public void deleteByCondition(Long workflowId) {
		String queryString = "DELETE FROM TimesheetWorkflow e WHERE e.workflowId = :workflowId";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("workflowId", workflowId);

		q.executeUpdate();
	}

}
