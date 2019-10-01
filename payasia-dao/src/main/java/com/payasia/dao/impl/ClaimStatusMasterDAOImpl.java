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
import com.payasia.dao.ClaimStatusMasterDAO;
import com.payasia.dao.bean.ClaimStatusMaster;
import com.payasia.dao.bean.ClaimStatusMaster_;

@Repository
public class ClaimStatusMasterDAOImpl extends BaseDAO implements
		ClaimStatusMasterDAO {

	@Override
	protected Object getBaseEntity() {
		ClaimStatusMaster claimStatusMaster = new ClaimStatusMaster();
		return claimStatusMaster;
	}

	@Override
	public ClaimStatusMaster findByCondition(String claimStatusName) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ClaimStatusMaster> criteriaQuery = cb
				.createQuery(ClaimStatusMaster.class);
		Root<ClaimStatusMaster> claimStatusRoot = criteriaQuery
				.from(ClaimStatusMaster.class);

		criteriaQuery.select(claimStatusRoot);

		Predicate restriction = cb.conjunction();

		if (StringUtils.isNotBlank(claimStatusName)) {
			restriction = cb.and(restriction, cb.equal(cb.upper(claimStatusRoot
					.get(ClaimStatusMaster_.claimStatusName)), claimStatusName
					.toUpperCase()));

		}

		criteriaQuery.where(restriction);

		TypedQuery<ClaimStatusMaster> claimStatusTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<ClaimStatusMaster> claimStatusList = claimStatusTypedQuery
				.getResultList();
		if (claimStatusList != null && !claimStatusList.isEmpty()) {
			return claimStatusList.get(0);
		}
		return null;
	}

}
