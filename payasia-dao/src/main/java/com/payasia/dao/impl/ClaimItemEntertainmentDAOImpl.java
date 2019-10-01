package com.payasia.dao.impl;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.payasia.dao.BaseDAO;
import com.payasia.dao.ClaimItemEntertainmentDAO;
import com.payasia.dao.bean.ClaimItemEntertainment;
import com.payasia.dao.bean.ClaimItemEntertainment_;
import com.payasia.dao.bean.ClaimTemplateItem;
import com.payasia.dao.bean.ClaimTemplateItem_;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.Employee;

@Repository
public class ClaimItemEntertainmentDAOImpl extends BaseDAO implements ClaimItemEntertainmentDAO {

	@Override
	protected Object getBaseEntity() {
		ClaimItemEntertainment claimItemEntertainment = new ClaimItemEntertainment();
		return claimItemEntertainment;
	}

	@Override
	public void update(ClaimItemEntertainment claimItemEntertainment) {
		super.update(claimItemEntertainment);
	}

	@Override
	public void save(ClaimItemEntertainment claimItemEntertainment) {
		super.save(claimItemEntertainment);

	}

	@Override
	public ClaimItemEntertainment findByCondition(String litemEntitlementClaimTemplateTypeId, Employee employee,
			Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ClaimItemEntertainment> criteriaQuery = cb.createQuery(ClaimItemEntertainment.class);
		Root<ClaimItemEntertainment> claimApplicationRoot = criteriaQuery.from(ClaimItemEntertainment.class);
		criteriaQuery.select(claimApplicationRoot);

		Predicate restriction = cb.conjunction();

		Join<ClaimItemEntertainment, ClaimTemplateItem> claimItemJoin = claimApplicationRoot
				.join(ClaimItemEntertainment_.claimTemplateItem);

		Join<ClaimItemEntertainment, Company> companyJoin = claimApplicationRoot.join(ClaimItemEntertainment_.company);

		restriction = cb.and(restriction,
				cb.equal(claimApplicationRoot.get(ClaimItemEntertainment_.employee), employee.getEmployeeId()));
		restriction = cb.and(restriction, cb.equal(claimItemJoin.get(ClaimTemplateItem_.claimTemplateItemId),
				litemEntitlementClaimTemplateTypeId));
		restriction = cb.and(restriction, cb.equal(companyJoin.get(Company_.companyId), companyId));

		criteriaQuery.where(restriction);

		TypedQuery<ClaimItemEntertainment> claimAppTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		if (claimAppTypedQuery.getResultList().size() > 0) {
			return claimAppTypedQuery.getResultList().get(0);
		} else {
			return null;
		}

	}

	@Override
	public ClaimItemEntertainment findByCondition(Long claimItemId, Long employeeId, Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ClaimItemEntertainment> criteriaQuery = cb.createQuery(ClaimItemEntertainment.class);
		Root<ClaimItemEntertainment> claimApplicationRoot = criteriaQuery.from(ClaimItemEntertainment.class);
		criteriaQuery.select(claimApplicationRoot);

		Predicate restriction = cb.conjunction();

		Join<ClaimItemEntertainment, Company> companyJoin = claimApplicationRoot.join(ClaimItemEntertainment_.company);

		/*
		 * restriction = cb.and(restriction, cb.equal(
		 * claimApplicationRoot.get(ClaimItemEntertainment_.employee),
		 * employee.getEmployeeId()));
		 */

		restriction = cb.and(restriction, cb.equal(companyJoin.get(Company_.companyId), companyId));
		restriction = cb.and(restriction,
				cb.equal(claimApplicationRoot.get(ClaimItemEntertainment_.employee), employeeId));
		restriction = cb.and(restriction,
				cb.equal(claimApplicationRoot.get(ClaimItemEntertainment_.claimTemplateItem), claimItemId));
		criteriaQuery.where(restriction);

		TypedQuery<ClaimItemEntertainment> claimAppTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		if (claimAppTypedQuery.getResultList().size() > 0) {
			return claimAppTypedQuery.getResultList().get(0);
		} else {
			return null;
		}

	}

	@Override
	public void delete(long employeeId, Long claimTemplateItem) {
		String queryString = "DELETE FROM ClaimItemEntertainment CIE WHERE CIE.employee.employeeId = :employeeId and CIE.claimTemplateItem.claimTemplateItemId = :claimTemplateItem ";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("employeeId", employeeId);
		q.setParameter("claimTemplateItem", claimTemplateItem);
		q.executeUpdate();
	}

}
