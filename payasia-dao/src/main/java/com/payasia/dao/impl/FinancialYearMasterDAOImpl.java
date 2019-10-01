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
import com.payasia.dao.FinancialYearMasterDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.FinancialYearMaster;
import com.payasia.dao.bean.FinancialYearMaster_;

/**
 * The Class FinancialYearMasterDAOImpl.
 */
/**
 * @author vivekjain
 * 
 */
@Repository
public class FinancialYearMasterDAOImpl extends BaseDAO implements
		FinancialYearMasterDAO {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.FinancialYearMasterDAO#saveRole(com.payasia.dao.bean.
	 * FinancialYearMaster)
	 */
	@Override
	public void saveRole(FinancialYearMaster financialYearMaster) {
		super.save(financialYearMaster);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.FinancialYearMasterDAO#findById(long)
	 */
	@Override
	public FinancialYearMaster findById(long financialYearId) {
		return super.findById(FinancialYearMaster.class, financialYearId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.BaseDAO#getBaseEntity()
	 */
	@Override
	protected Object getBaseEntity() {
		FinancialYearMaster financialYearMaster = new FinancialYearMaster();
		return financialYearMaster;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.FinancialYearMasterDAO#findAll()
	 */
	@Override
	public List<FinancialYearMaster> findAll() {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<FinancialYearMaster> criteriaQuery = cb
				.createQuery(FinancialYearMaster.class);
		Root<FinancialYearMaster> financialYearMasterRoot = criteriaQuery
				.from(FinancialYearMaster.class);

		criteriaQuery.select(financialYearMasterRoot);

		criteriaQuery.orderBy(cb.asc(financialYearMasterRoot
				.get(FinancialYearMaster_.finYearId)));

		TypedQuery<FinancialYearMaster> financialYearMasterTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<FinancialYearMaster> allFinancialYearMasterList = financialYearMasterTypedQuery
				.getResultList();
		return allFinancialYearMasterList;
	}

	@Override
	public FinancialYearMaster findByCompany(Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<FinancialYearMaster> criteriaQuery = cb
				.createQuery(FinancialYearMaster.class);
		Root<FinancialYearMaster> financialYearMasterRoot = criteriaQuery
				.from(FinancialYearMaster.class);
		criteriaQuery.select(financialYearMasterRoot);
		Predicate restriction = cb.conjunction();
		Join<FinancialYearMaster, Company> companypJoin = financialYearMasterRoot
				.join(FinancialYearMaster_.companies);

		restriction = cb.and(restriction,
				cb.equal(companypJoin.get(Company_.companyId), companyId));

		criteriaQuery.where(restriction);

		TypedQuery<FinancialYearMaster> financialYearMasterTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		FinancialYearMaster financialYearMaster = financialYearMasterTypedQuery
				.getSingleResult();
		return financialYearMaster;
	}

}
