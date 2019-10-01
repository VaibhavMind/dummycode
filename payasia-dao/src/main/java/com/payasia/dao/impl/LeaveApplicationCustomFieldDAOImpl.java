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
import com.payasia.dao.LeaveApplicationCustomFieldDAO;
import com.payasia.dao.bean.LeaveApplication;
import com.payasia.dao.bean.LeaveApplicationCustomField;
import com.payasia.dao.bean.LeaveApplicationCustomField_;
import com.payasia.dao.bean.LeaveApplication_;
import com.payasia.dao.bean.LeaveSchemeTypeCustomField;
import com.payasia.dao.bean.LeaveSchemeTypeCustomField_;

@Repository
public class LeaveApplicationCustomFieldDAOImpl extends BaseDAO implements
		LeaveApplicationCustomFieldDAO {

	@Override
	protected Object getBaseEntity() {
		LeaveApplicationCustomField leaveApplicationCustomField = new LeaveApplicationCustomField();
		return leaveApplicationCustomField;
	}

	@Override
	public LeaveApplicationCustomField findById(Long empLeaveSchemeTypeId) {
		return super.findById(LeaveApplicationCustomField.class,
				empLeaveSchemeTypeId);
	}

	@Override
	public void update(LeaveApplicationCustomField leaveApplicationCustomField) {
		super.update(leaveApplicationCustomField);
	}

	@Override
	public void save(LeaveApplicationCustomField leaveApplicationCustomField) {
		super.save(leaveApplicationCustomField);
	}

	@Override
	public List<LeaveApplicationCustomField> findByCondition(
			Long leaveApplicationId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveApplicationCustomField> criteriaQuery = cb
				.createQuery(LeaveApplicationCustomField.class);
		Root<LeaveApplicationCustomField> root = criteriaQuery
				.from(LeaveApplicationCustomField.class);
		criteriaQuery.select(root);

		Predicate restriction = cb.conjunction();

		Join<LeaveApplicationCustomField, LeaveApplication> leaveAppJoin = root
				.join(LeaveApplicationCustomField_.leaveApplication);
		Join<LeaveApplicationCustomField, LeaveSchemeTypeCustomField> leaveSchemeTypeJoin = root
				.join(LeaveApplicationCustomField_.leaveSchemeTypeCustomField);
		restriction = cb.and(restriction, cb.equal(
				leaveAppJoin.get(LeaveApplication_.leaveApplicationId),
				leaveApplicationId));

		criteriaQuery.where(restriction);
		criteriaQuery.orderBy(cb.asc(leaveSchemeTypeJoin
				.get(LeaveSchemeTypeCustomField_.customFieldId)));
		TypedQuery<LeaveApplicationCustomField> leaveAppTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		return leaveAppTypedQuery.getResultList();
	}

}
