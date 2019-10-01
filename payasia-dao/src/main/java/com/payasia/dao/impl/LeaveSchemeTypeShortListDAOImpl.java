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
import com.payasia.dao.LeaveSchemeTypeShortListDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.LeaveScheme;
import com.payasia.dao.bean.LeaveSchemeType;
import com.payasia.dao.bean.LeaveSchemeTypeShortList;
import com.payasia.dao.bean.LeaveSchemeTypeShortList_;
import com.payasia.dao.bean.LeaveSchemeType_;
import com.payasia.dao.bean.LeaveScheme_;

@Repository
public class LeaveSchemeTypeShortListDAOImpl extends BaseDAO implements
		LeaveSchemeTypeShortListDAO {

	@Override
	public void save(LeaveSchemeTypeShortList leaveSchemeTypeShortList) {
		super.save(leaveSchemeTypeShortList);
	}

	@Override
	public void deleteByCondition(Long leaveSchemeTypeId) {

		String queryString = "DELETE FROM LeaveSchemeTypeShortList c WHERE c.leaveSchemeType.leaveSchemeTypeId = :leaveSchemeTypeId";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("leaveSchemeTypeId", leaveSchemeTypeId);

		q.executeUpdate();
	}

	@Override
	protected Object getBaseEntity() {
		LeaveSchemeTypeShortList leaveSchemeTypeShortList = new LeaveSchemeTypeShortList();
		return leaveSchemeTypeShortList;
	}

	@Override
	public List<LeaveSchemeTypeShortList> findByCondition(Long leaveSchemeTypeId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveSchemeTypeShortList> criteriaQuery = cb
				.createQuery(LeaveSchemeTypeShortList.class);
		Root<LeaveSchemeTypeShortList> shortListRoot = criteriaQuery
				.from(LeaveSchemeTypeShortList.class);
		criteriaQuery.select(shortListRoot);
		Join<LeaveSchemeTypeShortList, LeaveSchemeType> leaveTypeJoin = shortListRoot
				.join(LeaveSchemeTypeShortList_.leaveSchemeType);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(
				leaveTypeJoin.get(LeaveSchemeType_.leaveSchemeTypeId),
				leaveSchemeTypeId));

		criteriaQuery.where(restriction);

		criteriaQuery.orderBy(cb.asc(shortListRoot
				.get(LeaveSchemeTypeShortList_.shortListId)));

		TypedQuery<LeaveSchemeTypeShortList> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		List<LeaveSchemeTypeShortList> leaveTypeShortList = typedQuery
				.getResultList();
		return leaveTypeShortList;

	}

	@Override
	public LeaveSchemeTypeShortList findById(Long filterId) {
		return super.findById(LeaveSchemeTypeShortList.class, filterId);
	}

	@Override
	public void delete(LeaveSchemeTypeShortList leaveSchemeTypeShortList) {
		super.delete(leaveSchemeTypeShortList);

	}

	@Override
	public List<LeaveSchemeTypeShortList> findSchemeTypeByCompany(Long leaveSchemeTypeId, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveSchemeTypeShortList> criteriaQuery = cb
				.createQuery(LeaveSchemeTypeShortList.class);
		Root<LeaveSchemeTypeShortList> shortListRoot = criteriaQuery
				.from(LeaveSchemeTypeShortList.class);
		criteriaQuery.select(shortListRoot);
		Join<LeaveSchemeTypeShortList, LeaveSchemeType> leaveTypeJoin = shortListRoot
				.join(LeaveSchemeTypeShortList_.leaveSchemeType);
		Join<LeaveSchemeType, LeaveScheme> leaveSchemeTypeJoin = leaveTypeJoin
				.join(LeaveSchemeType_.leaveScheme);
		Join<LeaveScheme, Company> leaveTypeCompJoin = leaveSchemeTypeJoin
				.join(LeaveScheme_.company);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(
				leaveTypeJoin.get(LeaveSchemeType_.leaveSchemeTypeId),
				leaveSchemeTypeId));
		
		restriction = cb.and(restriction, cb.equal(
				leaveTypeCompJoin.get(Company_.companyId),
				companyId));

		criteriaQuery.where(restriction);

		criteriaQuery.orderBy(cb.asc(shortListRoot
				.get(LeaveSchemeTypeShortList_.shortListId)));

		TypedQuery<LeaveSchemeTypeShortList> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		List<LeaveSchemeTypeShortList> leaveTypeShortList = typedQuery
				.getResultList();
		return leaveTypeShortList;
	}
}
