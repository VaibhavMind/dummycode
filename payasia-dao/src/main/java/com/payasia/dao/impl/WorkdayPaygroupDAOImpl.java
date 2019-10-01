package com.payasia.dao.impl;

import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.payasia.dao.BaseDAO;
import com.payasia.dao.WorkdayPaygroupDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.WorkdayPaygroup;
import com.payasia.dao.bean.WorkdayPaygroup_;

@Repository
public class WorkdayPaygroupDAOImpl extends BaseDAO implements WorkdayPaygroupDAO {
	
	@Override
	protected Object getBaseEntity() {
		return new WorkdayPaygroup();
	}

	@Override
	public WorkdayPaygroup findById(long workdayPayGroupId) {
		return super.findById(WorkdayPaygroup.class, workdayPayGroupId);
	}
	
	@Override
	public WorkdayPaygroup findByPaygroupName(String workdayPaygroupName, long companyId) {
		
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<WorkdayPaygroup> criteriaQuery = cb.createQuery(WorkdayPaygroup.class);
		Root<WorkdayPaygroup> payGroupRoot = criteriaQuery.from(WorkdayPaygroup.class);
		criteriaQuery.select(payGroupRoot);
		
		Join<WorkdayPaygroup, Company> companyJoin = payGroupRoot.join(WorkdayPaygroup_.company);
		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(cb.upper(payGroupRoot.get(WorkdayPaygroup_.paygroupName)), workdayPaygroupName.toUpperCase()));
		restriction = cb.and(restriction, cb.equal(companyJoin.get(Company_.companyId), companyId));
		criteriaQuery.where(restriction);
		TypedQuery<WorkdayPaygroup> typedQuery = entityManagerFactory.createQuery(criteriaQuery);
		List<WorkdayPaygroup> resultList = typedQuery.getResultList();
		if(resultList != null && !resultList.isEmpty())
			return resultList.get(0);
		else return null;
	}
	
	@Override
	public WorkdayPaygroup findByPaygroupId(String workdayPaygroupId, long companyId) {
		
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<WorkdayPaygroup> criteriaQuery = cb.createQuery(WorkdayPaygroup.class);
		Root<WorkdayPaygroup> payGroupRoot = criteriaQuery.from(WorkdayPaygroup.class);
		criteriaQuery.select(payGroupRoot);
		
		Join<WorkdayPaygroup, Company> companyJoin = payGroupRoot.join(WorkdayPaygroup_.company);
		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(cb.upper(payGroupRoot.get(WorkdayPaygroup_.paygroupId)), workdayPaygroupId.toUpperCase()));
		restriction = cb.and(restriction, cb.equal(companyJoin.get(Company_.companyId), companyId));
		criteriaQuery.where(restriction);
		TypedQuery<WorkdayPaygroup> typedQuery = entityManagerFactory.createQuery(criteriaQuery);
		List<WorkdayPaygroup> resultList = typedQuery.getResultList();
		if(resultList != null && !resultList.isEmpty())
			return resultList.get(0);
		else return null;
	}

	@Override
	public List<WorkdayPaygroup> findByCompanyId(long companyId) {
		
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<WorkdayPaygroup> criteriaQuery = cb.createQuery(WorkdayPaygroup.class);
		Root<WorkdayPaygroup> payGroupRoot = criteriaQuery.from(WorkdayPaygroup.class);
		criteriaQuery.select(payGroupRoot);
		Join<WorkdayPaygroup, Company> companyJoin = payGroupRoot.join(WorkdayPaygroup_.company);
		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(companyJoin.get(Company_.companyId), companyId));
		criteriaQuery.where(restriction);
		TypedQuery<WorkdayPaygroup> typedQuery = entityManagerFactory.createQuery(criteriaQuery);
		return typedQuery.getResultList();
	}
}
