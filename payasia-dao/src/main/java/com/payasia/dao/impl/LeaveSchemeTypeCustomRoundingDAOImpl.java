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
import com.payasia.dao.LeaveSchemeTypeCustomRoundingDAO;
import com.payasia.dao.bean.LeaveSchemeTypeCustomRounding;
import com.payasia.dao.bean.LeaveSchemeTypeCustomRounding_;
import com.payasia.dao.bean.LeaveSchemeTypeProration;
import com.payasia.dao.bean.LeaveSchemeTypeProration_;

@Repository
public class LeaveSchemeTypeCustomRoundingDAOImpl extends BaseDAO implements
		LeaveSchemeTypeCustomRoundingDAO {

	@Override
	protected Object getBaseEntity() {
		LeaveSchemeTypeCustomRounding leaveSchemeTypeCustomRounding = new LeaveSchemeTypeCustomRounding();
		return leaveSchemeTypeCustomRounding;
	}

	@Override
	public void save(LeaveSchemeTypeCustomRounding leaveSchemeTypeCustomRounding) {
		super.save(leaveSchemeTypeCustomRounding);
	}

	@Override
	public List<LeaveSchemeTypeCustomRounding> findByLeaveSchemeTypeProrationId(
			long leaveSchemeTypeProrationId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveSchemeTypeCustomRounding> criteriaQuery = cb
				.createQuery(LeaveSchemeTypeCustomRounding.class);
		Root<LeaveSchemeTypeCustomRounding> leaveSchemeTypeRoot = criteriaQuery
				.from(LeaveSchemeTypeCustomRounding.class);

		criteriaQuery.select(leaveSchemeTypeRoot);

		Join<LeaveSchemeTypeCustomRounding, LeaveSchemeTypeProration> leaveSchemeTypeJoin = leaveSchemeTypeRoot
				.join(LeaveSchemeTypeCustomRounding_.leaveSchemeTypeProration);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(leaveSchemeTypeJoin
				.get(LeaveSchemeTypeProration_.leaveSchemeTypeProrationId),
				leaveSchemeTypeProrationId));

		criteriaQuery.where(restriction);

		TypedQuery<LeaveSchemeTypeCustomRounding> leaveTypeDefinitionTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		List<LeaveSchemeTypeCustomRounding> LeaveSchemeTypeDetail = leaveTypeDefinitionTypedQuery
				.getResultList();
		return LeaveSchemeTypeDetail;
	}

	@Override
	public void deleteByCondition(Long leaveSchemeTypeProrationId) {
		String queryString = "DELETE FROM LeaveSchemeTypeCustomRounding e WHERE e.leaveSchemeTypeProration.leaveSchemeTypeProrationId = :leaveSchemeTypeProrationId";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("leaveSchemeTypeProrationId", leaveSchemeTypeProrationId);

		q.executeUpdate();

	}

}
