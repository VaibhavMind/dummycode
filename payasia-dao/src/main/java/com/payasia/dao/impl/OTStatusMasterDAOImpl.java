package com.payasia.dao.impl;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.payasia.dao.BaseDAO;
import com.payasia.dao.OTStatusMasterDAO;
import com.payasia.dao.bean.OTStatusMaster;
import com.payasia.dao.bean.OTStatusMaster_;

@Repository
public class OTStatusMasterDAOImpl extends BaseDAO implements OTStatusMasterDAO {

	@Override
	protected Object getBaseEntity() {
		OTStatusMaster otStatusMaster = new OTStatusMaster();
		return otStatusMaster;
	}

	@Override
	public OTStatusMaster findByID(long otStatusMasterId) {
		return super.findById(OTStatusMaster.class, otStatusMasterId);
	}

	@Override
	public OTStatusMaster findByOTStatus(String otStatus) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<OTStatusMaster> criteriaQuery = cb
				.createQuery(OTStatusMaster.class);
		Root<OTStatusMaster> otStatusRoot = criteriaQuery
				.from(OTStatusMaster.class);

		criteriaQuery.select(otStatusRoot);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(
				cb.upper(otStatusRoot.get(OTStatusMaster_.otStatusName)),
				otStatus.toUpperCase()));

		criteriaQuery.where(restriction);

		TypedQuery<OTStatusMaster> otStatusTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		if (otStatusTypedQuery.getResultList().size() > 0) {
			OTStatusMaster otStatusMaster = otStatusTypedQuery
					.getSingleResult();

			return otStatusMaster;
		} else {
			return null;
		}
	}

}
