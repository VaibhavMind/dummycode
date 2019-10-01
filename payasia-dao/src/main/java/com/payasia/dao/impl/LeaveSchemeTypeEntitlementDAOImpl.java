package com.payasia.dao.impl;

import java.math.BigDecimal;
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
import com.payasia.dao.LeaveSchemeTypeEntitlementDAO;
import com.payasia.dao.bean.LeaveSchemeTypeEntitlement;
import com.payasia.dao.bean.LeaveSchemeTypeEntitlement_;
import com.payasia.dao.bean.LeaveSchemeTypeGranting;
import com.payasia.dao.bean.LeaveSchemeTypeGranting_;

@Repository
public class LeaveSchemeTypeEntitlementDAOImpl extends BaseDAO implements
		LeaveSchemeTypeEntitlementDAO {

	@Override
	protected Object getBaseEntity() {
		LeaveSchemeTypeEntitlement leaveSchemeTypeEntitlement = new LeaveSchemeTypeEntitlement();
		return leaveSchemeTypeEntitlement;
	}

	@Override
	public void save(LeaveSchemeTypeEntitlement leaveSchemeTypeEntitlement) {
		super.save(leaveSchemeTypeEntitlement);
	}

	@Override
	public List<LeaveSchemeTypeEntitlement> findByLeaveSchemeTypeGrantingId(
			long leaveSchemeTypeGrantingId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveSchemeTypeEntitlement> criteriaQuery = cb
				.createQuery(LeaveSchemeTypeEntitlement.class);
		Root<LeaveSchemeTypeEntitlement> leaveSchemeTypeEntRoot = criteriaQuery
				.from(LeaveSchemeTypeEntitlement.class);

		criteriaQuery.select(leaveSchemeTypeEntRoot);

		Join<LeaveSchemeTypeEntitlement, LeaveSchemeTypeGranting> leaveSchemeTypeJoin = leaveSchemeTypeEntRoot
				.join(LeaveSchemeTypeEntitlement_.leaveSchemeTypeGranting);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(leaveSchemeTypeJoin
				.get(LeaveSchemeTypeGranting_.leaveSchemeTypeGrantingId),
				leaveSchemeTypeGrantingId));

		criteriaQuery.where(restriction);

		TypedQuery<LeaveSchemeTypeEntitlement> leaveTypeDefinitionTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		List<LeaveSchemeTypeEntitlement> LeaveSchemeTypeEntDetailList = leaveTypeDefinitionTypedQuery
				.getResultList();
		return LeaveSchemeTypeEntDetailList;
	}

	@Override
	public void deleteByCondition(long leaveSchemeTypeGrantingId) {

		String queryString = "DELETE FROM LeaveSchemeTypeEntitlement e WHERE e.leaveSchemeTypeGranting.leaveSchemeTypeGrantingId = :leaveSchemeTypeGrantingId";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("leaveSchemeTypeGrantingId", leaveSchemeTypeGrantingId);

		q.executeUpdate();

	}

	@Override
	public LeaveSchemeTypeEntitlement findById(Long leaveSchemeTypeDetailId) {
		return super.findById(LeaveSchemeTypeEntitlement.class,
				leaveSchemeTypeDetailId);
	}

	@Override
	public void update(LeaveSchemeTypeEntitlement leaveSchemeTypeEntitlement) {
		super.update(leaveSchemeTypeEntitlement);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.CompanyDocumentDAO#findMaxCompanyDocumentId()
	 */
	@Override
	public BigDecimal findMaxDays(Long leaveGrantingId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<BigDecimal> criteriaQuery = cb
				.createQuery(BigDecimal.class);
		Root<LeaveSchemeTypeEntitlement> leaveSchemeTypeEntitlementRoot = criteriaQuery
				.from(LeaveSchemeTypeEntitlement.class);
		criteriaQuery.select(cb.max(
				leaveSchemeTypeEntitlementRoot
						.get(LeaveSchemeTypeEntitlement_.value)).as(
				BigDecimal.class));

		Predicate restriction = cb.conjunction();
		restriction = cb
				.and(restriction,
						cb.equal(
								leaveSchemeTypeEntitlementRoot
										.get(LeaveSchemeTypeEntitlement_.leaveSchemeTypeGranting)
										.get("leaveSchemeTypeGrantingId")
										.as(Long.class), leaveGrantingId));

		criteriaQuery.where(restriction);

		TypedQuery<BigDecimal> maxCompanyDocumentQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		BigDecimal maxDay = maxCompanyDocumentQuery.getSingleResult();
		if (maxDay == null) {
			maxDay = new BigDecimal(0);
		}
		return maxDay;

	}

}
