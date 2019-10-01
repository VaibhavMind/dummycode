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
import com.payasia.dao.LeaveSchemeTypeCustomProrationDAO;
import com.payasia.dao.bean.LeaveSchemeTypeCustomProration;
import com.payasia.dao.bean.LeaveSchemeTypeCustomProration_;
import com.payasia.dao.bean.LeaveSchemeTypeProration;
import com.payasia.dao.bean.LeaveSchemeTypeProration_;

@Repository
public class LeaveSchemeTypeCustomProrationDAOImpl extends BaseDAO implements
		LeaveSchemeTypeCustomProrationDAO {

	@Override
	protected Object getBaseEntity() {
		LeaveSchemeTypeCustomProration leaveSchemeTypeCustomProration = new LeaveSchemeTypeCustomProration();
		return leaveSchemeTypeCustomProration;
	}

	@Override
	public void update(
			LeaveSchemeTypeCustomProration leaveSchemeTypeCustomProration) {
		super.update(leaveSchemeTypeCustomProration);

	}

	@Override
	public void save(
			LeaveSchemeTypeCustomProration leaveSchemeTypeCustomProration) {
		super.save(leaveSchemeTypeCustomProration);
	}

	@Override
	public void delete(
			LeaveSchemeTypeCustomProration leaveSchemeTypeCustomProration) {
		super.delete(leaveSchemeTypeCustomProration);

	}

	@Override
	public LeaveSchemeTypeCustomProration findByID(
			long leaveSchemeTypeCustomProrationId) {
		return super.findById(LeaveSchemeTypeCustomProration.class,
				leaveSchemeTypeCustomProrationId);
	}

	@Override
	public void deleteByCondition(Long leaveSchemeTypeProrationId) {
		String queryString = "DELETE FROM LeaveSchemeTypeCustomProration e WHERE e.leaveSchemeTypeProration.leaveSchemeTypeProrationId = :leaveSchemeTypeProrationId";
		Query q = entityManagerFactory.createQuery(queryString);
		q.setParameter("leaveSchemeTypeProrationId", leaveSchemeTypeProrationId);

		q.executeUpdate();

	}

	@Override
	public List<LeaveSchemeTypeCustomProration> findByLeaveSchemeTypeProrationId(
			long leaveSchemeTypeProrationId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveSchemeTypeCustomProration> criteriaQuery = cb
				.createQuery(LeaveSchemeTypeCustomProration.class);
		Root<LeaveSchemeTypeCustomProration> leaveSchemeTypeRoot = criteriaQuery
				.from(LeaveSchemeTypeCustomProration.class);

		criteriaQuery.select(leaveSchemeTypeRoot);

		Join<LeaveSchemeTypeCustomProration, LeaveSchemeTypeProration> leaveSchemeTypeJoin = leaveSchemeTypeRoot
				.join(LeaveSchemeTypeCustomProration_.leaveSchemeTypeProration);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(leaveSchemeTypeJoin
				.get(LeaveSchemeTypeProration_.leaveSchemeTypeProrationId),
				leaveSchemeTypeProrationId));

		criteriaQuery.where(restriction);

		TypedQuery<LeaveSchemeTypeCustomProration> leaveTypeDefinitionTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		List<LeaveSchemeTypeCustomProration> LeaveSchemeTypeDetail = leaveTypeDefinitionTypedQuery
				.getResultList();
		return LeaveSchemeTypeDetail;
	}

}
