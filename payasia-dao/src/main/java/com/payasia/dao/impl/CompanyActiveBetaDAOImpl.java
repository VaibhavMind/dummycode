/**
 * @author peeyushpratap
 *
 */
package com.payasia.dao.impl;

import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.payasia.dao.BaseDAO;
import com.payasia.dao.CompanyActiveBetaDAO;
import com.payasia.dao.bean.CompanyActiveBeta;
import com.payasia.dao.bean.CompanyActiveBeta_;
import com.payasia.dao.bean.Employee;

/**
 * The Class CompanyActiveBetaDAOImpl.
 */

@Repository
public class CompanyActiveBetaDAOImpl extends BaseDAO implements CompanyActiveBetaDAO {

	@Override
	public CompanyActiveBeta findByID(long id) {
		return super.findById(CompanyActiveBeta.class, id);
	}
	
	@Override
	public CompanyActiveBeta findByCompanyId(long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CompanyActiveBeta> criteriaQuery = cb.createQuery(CompanyActiveBeta.class);
		Root<CompanyActiveBeta> companyActiveBetaRoot = criteriaQuery.from(CompanyActiveBeta.class);
		criteriaQuery.select(companyActiveBetaRoot);
		
		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(
				companyActiveBetaRoot.get(CompanyActiveBeta_.companyId), companyId));
		criteriaQuery.where(restriction);
		List<CompanyActiveBeta> empTypedQuery = entityManagerFactory.createQuery(criteriaQuery).getResultList();
		if(!empTypedQuery.isEmpty())
			return empTypedQuery.get(0);
		return null;
	}

	@Override
	public List<CompanyActiveBeta> findAllActive() {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CompanyActiveBeta> criteriaQuery = cb.createQuery(CompanyActiveBeta.class);
		Root<CompanyActiveBeta> companyActiveBetaRoot = criteriaQuery.from(CompanyActiveBeta.class);
		criteriaQuery.select(companyActiveBetaRoot);
		
		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(
				companyActiveBetaRoot.get(CompanyActiveBeta_.active), true));
		criteriaQuery.where(restriction);
		return entityManagerFactory.createQuery(criteriaQuery).getResultList();
	}
	

	@Override
	protected Object getBaseEntity() {
		return new CompanyActiveBeta();
	}

}
