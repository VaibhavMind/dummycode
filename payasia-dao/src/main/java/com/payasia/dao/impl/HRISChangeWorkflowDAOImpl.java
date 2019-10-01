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
import com.payasia.dao.HRISChangeWorkflowDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.HRISChangeWorkflow;
import com.payasia.dao.bean.HRISChangeWorkflow_;

@Repository
public class HRISChangeWorkflowDAOImpl extends BaseDAO implements
		HRISChangeWorkflowDAO {

	@Override
	protected Object getBaseEntity() {
		HRISChangeWorkflow hrisChangeWorkflow = new HRISChangeWorkflow();
		return hrisChangeWorkflow;
	}

	@Override
	public void update(HRISChangeWorkflow hrisChangeWorkflow) {
		this.entityManagerFactory.merge(hrisChangeWorkflow);
	}

	@Override
	public void delete(HRISChangeWorkflow hrisChangeWorkflow) {
		this.entityManagerFactory.remove(hrisChangeWorkflow);
	}

	@Override
	public void save(HRISChangeWorkflow hrisChangeWorkflow) {
		super.save(hrisChangeWorkflow);
	}

	@Override
	public HRISChangeWorkflow findById(Long hrisChangeWorkflowId) {
		return super.findById(HRISChangeWorkflow.class, hrisChangeWorkflowId);

	}

	@Override
	public List<HRISChangeWorkflow> findByCompanyId(Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<HRISChangeWorkflow> criteriaQuery = cb
				.createQuery(HRISChangeWorkflow.class);
		Root<HRISChangeWorkflow> root = criteriaQuery
				.from(HRISChangeWorkflow.class);
		criteriaQuery.select(root);

		Predicate restriction = cb.conjunction();
		Join<HRISChangeWorkflow, Company> compJoin = root
				.join(HRISChangeWorkflow_.company);

		restriction = cb.and(restriction,
				cb.equal(compJoin.get(Company_.companyId), companyId));
		criteriaQuery.where(restriction);

		TypedQuery<HRISChangeWorkflow> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<HRISChangeWorkflow> changeRequestList = typedQuery.getResultList();

		return changeRequestList;

	}

	@Override
	public void deleteByCondition(Long hrisChangeWorkflowId) {
		String queryString = "DELETE FROM HRISChangeWorkflow e WHERE e.hrisChangeWorkflowId = :hrisChangeWorkflowId";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("hrisChangeWorkflowId", hrisChangeWorkflowId);

		q.executeUpdate();
	}

}
