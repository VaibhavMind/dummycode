package com.payasia.dao.impl;

import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.payasia.dao.BaseDAO;
import com.payasia.dao.LeaveSessionMasterDAO;
import com.payasia.dao.bean.LeaveSessionMaster;
import com.payasia.dao.bean.LeaveSessionMaster_;

@Repository
public class LeaveSessionMasterDAOImpl extends BaseDAO implements
		LeaveSessionMasterDAO {

	@Override
	public LeaveSessionMaster findById(Long leaveSessionMasterID) {
		return super.findById(LeaveSessionMaster.class, leaveSessionMasterID);
	}

	@Override
	public List<LeaveSessionMaster> findAll() {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveSessionMaster> criteriaQuery = cb
				.createQuery(LeaveSessionMaster.class);
		Root<LeaveSessionMaster> leaveSchemeRoot = criteriaQuery
				.from(LeaveSessionMaster.class);
		criteriaQuery.select(leaveSchemeRoot);
		TypedQuery<LeaveSessionMaster> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		return typedQuery.getResultList();
	}

	@Override
	protected Object getBaseEntity() {
		LeaveSessionMaster leaveSessionMaster = new LeaveSessionMaster();
		return leaveSessionMaster;
	}

	@Override
	public LeaveSessionMaster findByName(String SessionName) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveSessionMaster> criteriaQuery = cb
				.createQuery(LeaveSessionMaster.class);
		Root<LeaveSessionMaster> leaveSessionRoot = criteriaQuery
				.from(LeaveSessionMaster.class);
		criteriaQuery.select(leaveSessionRoot);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(
				cb.upper(leaveSessionRoot.get(LeaveSessionMaster_.session)),
				SessionName.toUpperCase()));
		criteriaQuery.where(restriction);

		TypedQuery<LeaveSessionMaster> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		LeaveSessionMaster leaveSession = typedQuery.getSingleResult();

		return leaveSession;

	}

}
