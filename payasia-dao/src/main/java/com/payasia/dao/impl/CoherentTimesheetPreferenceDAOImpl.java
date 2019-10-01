package com.payasia.dao.impl;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.payasia.dao.BaseDAO;
import com.payasia.dao.CoherentTimesheetPreferenceDAO;
import com.payasia.dao.bean.CoherentTimesheetPreference;
import com.payasia.dao.bean.CoherentTimesheetPreference_;

@Repository
public class CoherentTimesheetPreferenceDAOImpl extends BaseDAO implements
		CoherentTimesheetPreferenceDAO {

	private static final Logger LOGGER = Logger
			.getLogger(LundinTimesheetPreferenceDAOImpl.class);

	@Override
	protected Object getBaseEntity() {
		CoherentTimesheetPreference coherentTimesheetPreference = new CoherentTimesheetPreference();
		return coherentTimesheetPreference;
	}

	@Override
	public void save(CoherentTimesheetPreference coherentTimesheetPreference) {
		super.save(coherentTimesheetPreference);
	}

	@Override
	public void delete(CoherentTimesheetPreference coherentTimesheetPreference) {
		super.delete(coherentTimesheetPreference);
	}

	@Override
	public void update(CoherentTimesheetPreference coherentTimesheetPreference) {
		super.update(coherentTimesheetPreference);

	}

	@Override
	public CoherentTimesheetPreference findByID(Long coherentTimesheetPreference) {
		return super.findById(CoherentTimesheetPreference.class,
				coherentTimesheetPreference);
	}

	@Override
	public CoherentTimesheetPreference findByCompanyId(Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CoherentTimesheetPreference> criteriaQuery = cb
				.createQuery(CoherentTimesheetPreference.class);

		Root<CoherentTimesheetPreference> root = criteriaQuery
				.from(CoherentTimesheetPreference.class);
		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(
				root.get(CoherentTimesheetPreference_.company), companyId));
		criteriaQuery.select(root).where(restriction);
		TypedQuery<CoherentTimesheetPreference> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		try {

			return typedQuery.getSingleResult();
		} catch (Exception ex) {
			return null;
		}
	}
}
