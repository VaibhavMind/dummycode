package com.payasia.dao.impl;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.payasia.dao.BaseDAO;
import com.payasia.dao.LundinTimesheetPreferenceDAO;
import com.payasia.dao.bean.LundinTimesheetPreference;
import com.payasia.dao.bean.LundinTimesheetPreference_;

@Repository
public class LundinTimesheetPreferenceDAOImpl extends BaseDAO implements
		LundinTimesheetPreferenceDAO {

	private static final Logger LOGGER = Logger
			.getLogger(LundinTimesheetPreferenceDAOImpl.class);

	@Override
	protected Object getBaseEntity() {
		LundinTimesheetPreference lundinTimesheetPreference = new LundinTimesheetPreference();
		return lundinTimesheetPreference;
	}

	@Override
	public void save(LundinTimesheetPreference lundinTimesheetPreference) {
		super.save(lundinTimesheetPreference);
	}

	@Override
	public void delete(LundinTimesheetPreference lundinTimesheetPreference) {
		super.delete(lundinTimesheetPreference);
	}

	@Override
	public void update(LundinTimesheetPreference lundinTimesheetPreference) {
		super.update(lundinTimesheetPreference);

	}

	@Override
	public LundinTimesheetPreference findByID(Long lundinTimesheetPreferenceId) {
		return super.findById(LundinTimesheetPreference.class,
				lundinTimesheetPreferenceId);
	}

	@Override
	public LundinTimesheetPreference findByCompanyId(Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LundinTimesheetPreference> criteriaQuery = cb
				.createQuery(LundinTimesheetPreference.class);

		Root<LundinTimesheetPreference> root = criteriaQuery
				.from(LundinTimesheetPreference.class);
		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(
				root.get(LundinTimesheetPreference_.company), companyId));
		criteriaQuery.select(root).where(restriction);
		TypedQuery<LundinTimesheetPreference> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		try {

			return typedQuery.getSingleResult();
		} catch (Exception ex) {
			return null;
		}
	}

}
