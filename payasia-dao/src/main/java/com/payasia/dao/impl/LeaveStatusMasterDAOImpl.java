package com.payasia.dao.impl;

import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.payasia.dao.BaseDAO;
import com.payasia.dao.LeaveStatusMasterDAO;
import com.payasia.dao.bean.LeaveStatusMaster;
import com.payasia.dao.bean.LeaveStatusMaster_;

@Repository
public class LeaveStatusMasterDAOImpl extends BaseDAO implements
		LeaveStatusMasterDAO {

	@Override
	public LeaveStatusMaster findByCondition(String leaveStatusName) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveStatusMaster> criteriaQuery = cb
				.createQuery(LeaveStatusMaster.class);
		Root<LeaveStatusMaster> leaveStatusRoot = criteriaQuery
				.from(LeaveStatusMaster.class);

		criteriaQuery.select(leaveStatusRoot);

		Predicate restriction = cb.conjunction();

		if (StringUtils.isNotBlank(leaveStatusName)) {
			restriction = cb.and(restriction, cb.equal(cb.upper(leaveStatusRoot
					.get(LeaveStatusMaster_.leaveStatusName)), leaveStatusName
					.toUpperCase()));

		}

		criteriaQuery.where(restriction);

		TypedQuery<LeaveStatusMaster> leaveStatusTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<LeaveStatusMaster> leaveStatusList = leaveStatusTypedQuery
				.getResultList();
		if (leaveStatusList != null &&  !leaveStatusList.isEmpty()) {
			return leaveStatusList.get(0);
		}
		return null;
	}

	@Override
	public List<LeaveStatusMaster> findAll() {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LeaveStatusMaster> criteriaQuery = cb
				.createQuery(LeaveStatusMaster.class);
		Root<LeaveStatusMaster> leaveStatusRoot = criteriaQuery
				.from(LeaveStatusMaster.class);

		criteriaQuery.select(leaveStatusRoot);

		Predicate restriction = cb.conjunction();

		criteriaQuery.where(restriction);

		TypedQuery<LeaveStatusMaster> leaveStatusTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<LeaveStatusMaster> leaveStatusList = leaveStatusTypedQuery
				.getResultList();
		if (leaveStatusList != null &&  !leaveStatusList.isEmpty()) {
			return leaveStatusList;
		}
		return null;
	}

	@Override
	protected Object getBaseEntity() {
		LeaveStatusMaster leaveStatusMaster = new LeaveStatusMaster();
		return leaveStatusMaster;
	}

}
