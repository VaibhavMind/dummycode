package com.payasia.dao.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.util.NullAwareBeanUtilsBean;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.LeaveSchemeTypeGrantingDAO;
import com.payasia.dao.bean.LeaveSchemeType;
import com.payasia.dao.bean.LeaveSchemeTypeGranting;
import com.payasia.dao.bean.LeaveSchemeTypeGranting_;
import com.payasia.dao.bean.LeaveSchemeType_;

@Repository
public class LeaveSchemeTypeGrantingDAOImpl extends BaseDAO implements
		LeaveSchemeTypeGrantingDAO {

	@Override
	protected Object getBaseEntity() {
		LeaveSchemeTypeGranting leaveSchemeTypeGranting = new LeaveSchemeTypeGranting();
		return leaveSchemeTypeGranting;
	}

	@Override
	public LeaveSchemeTypeGranting findById(Long leaveSchemeTypeGrantingId) {
		LeaveSchemeTypeGranting leaveSchemeTypeGranting = super.findById(
				LeaveSchemeTypeGranting.class, leaveSchemeTypeGrantingId);
		return leaveSchemeTypeGranting;
	}

	@Override
	public void update(LeaveSchemeTypeGranting leaveSchemeTypeGranting) {
		super.update(leaveSchemeTypeGranting);
	}

	@Override
	public void save(LeaveSchemeTypeGranting leaveSchemeTypeGranting) {
		super.save(leaveSchemeTypeGranting);

	}

	@Override
	public void delete(LeaveSchemeTypeGranting leaveSchemeTypeGranting) {
		super.delete(leaveSchemeTypeGranting);
	}

	@Override
	public LeaveSchemeTypeGranting saveObj(
			LeaveSchemeTypeGranting leaveSchemeTypeGranting) {
		LeaveSchemeTypeGranting persistObj = leaveSchemeTypeGranting;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (LeaveSchemeTypeGranting) getBaseEntity();
			beanUtil.copyProperties(persistObj, leaveSchemeTypeGranting);
		} catch (IllegalAccessException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		} catch (InvocationTargetException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		}
		this.entityManagerFactory.persist(persistObj);
		return persistObj;
	}

	@Override
	public List<LeaveSchemeTypeGranting> findByCondition(Long leaveSchemeTypeId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveSchemeTypeGranting> criteriaQuery = cb
				.createQuery(LeaveSchemeTypeGranting.class);
		Root<LeaveSchemeTypeGranting> grantingRoot = criteriaQuery
				.from(LeaveSchemeTypeGranting.class);
		criteriaQuery.select(grantingRoot);
		Join<LeaveSchemeTypeGranting, LeaveSchemeType> leaveTypeJoin = grantingRoot
				.join(LeaveSchemeTypeGranting_.leaveSchemeType);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(
				leaveTypeJoin.get(LeaveSchemeType_.leaveSchemeTypeId),
				leaveSchemeTypeId));

		criteriaQuery.where(restriction);

		TypedQuery<LeaveSchemeTypeGranting> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		List<LeaveSchemeTypeGranting> leaveTypeGranting = typedQuery
				.getResultList();
		return leaveTypeGranting;

	}

}
