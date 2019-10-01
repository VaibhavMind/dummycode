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
import com.payasia.dao.LeaveSchemeTypeCustomFieldDAO;
import com.payasia.dao.bean.LeaveSchemeTypeAvailingLeave;
import com.payasia.dao.bean.LeaveSchemeTypeAvailingLeave_;
import com.payasia.dao.bean.LeaveSchemeTypeCustomField;
import com.payasia.dao.bean.LeaveSchemeTypeCustomField_;

@Repository
public class LeaveSchemeTypeCustomFieldDAOImpl extends BaseDAO implements
		LeaveSchemeTypeCustomFieldDAO {

	@Override
	protected Object getBaseEntity() {
		LeaveSchemeTypeCustomField leaveSchemeTypeCustomField = new LeaveSchemeTypeCustomField();
		return leaveSchemeTypeCustomField;
	}

	@Override
	public void save(LeaveSchemeTypeCustomField leaveSchemeTypeCustomField) {
		super.save(leaveSchemeTypeCustomField);
	}

	@Override
	public List<LeaveSchemeTypeCustomField> findByLeaveSchemeTypeAvailingLeaveId(
			long leaveSchemeTypeAvailingLeaveId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveSchemeTypeCustomField> criteriaQuery = cb
				.createQuery(LeaveSchemeTypeCustomField.class);
		Root<LeaveSchemeTypeCustomField> leaveSchemeTypeRoot = criteriaQuery
				.from(LeaveSchemeTypeCustomField.class);

		criteriaQuery.select(leaveSchemeTypeRoot);

		Join<LeaveSchemeTypeCustomField, LeaveSchemeTypeAvailingLeave> leaveSchemeTypeJoin = leaveSchemeTypeRoot
				.join(LeaveSchemeTypeCustomField_.leaveSchemeTypeAvailingLeave);

		Predicate restriction = cb.conjunction();
		restriction = cb
				.and(restriction,
						cb.equal(
								leaveSchemeTypeJoin
										.get(LeaveSchemeTypeAvailingLeave_.leaveSchemeTypeAvailingLeaveId),
								leaveSchemeTypeAvailingLeaveId));

		criteriaQuery.where(restriction);

		TypedQuery<LeaveSchemeTypeCustomField> leaveTypeDefinitionTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		List<LeaveSchemeTypeCustomField> LeaveSchemeTypeDetail = leaveTypeDefinitionTypedQuery
				.getResultList();
		return LeaveSchemeTypeDetail;

	}

	@Override
	public void delete(LeaveSchemeTypeCustomField leaveSchemeTypeCustomField) {
		super.delete(leaveSchemeTypeCustomField);

	}

	@Override
	public void update(LeaveSchemeTypeCustomField leaveSchemeTypeCustomField) {
		super.update(leaveSchemeTypeCustomField);

	}

	@Override
	public LeaveSchemeTypeCustomField findById(long customRoundFieldId) {

		return super.findById(LeaveSchemeTypeCustomField.class,
				customRoundFieldId);
	}

}
