package com.payasia.dao.impl;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.payasia.dao.BaseDAO;
import com.payasia.dao.IntegrationMasterDAO;
import com.payasia.dao.bean.IntegrationMaster;
import com.payasia.dao.bean.IntegrationMaster_;

@Repository
public class IntegrationMasterDAOImpl extends BaseDAO implements
		IntegrationMasterDAO {

	private static final Logger LOGGER = Logger
			.getLogger(IntegrationMasterDAOImpl.class);

	@Override
	public IntegrationMaster findByCondition(long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<IntegrationMaster> criteriaQuery = cb
				.createQuery(IntegrationMaster.class);
		Root<IntegrationMaster> root = criteriaQuery
				.from(IntegrationMaster.class);
		criteriaQuery.select(root);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction,
				cb.equal(root.get(IntegrationMaster_.companyId), companyId));
		restriction = cb.and(restriction,
				cb.equal(root.get(IntegrationMaster_.active), true));
		criteriaQuery.where(restriction);

		TypedQuery<IntegrationMaster> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		if (typedQuery.getResultList().size() > 0) {
			try {
				return typedQuery.getResultList().get(0);
			} catch (NullPointerException e) {
				LOGGER.error(e.getMessage(), e);
				return null;
			}
		} else {
			return null;
		}
	}

	@Override
	protected Object getBaseEntity() {
		IntegrationMaster integrationMaster = new IntegrationMaster();
		return integrationMaster;
	}

}
