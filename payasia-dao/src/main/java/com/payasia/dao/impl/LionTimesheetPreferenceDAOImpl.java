package com.payasia.dao.impl;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.payasia.dao.BaseDAO;
import com.payasia.dao.LionTimesheetPreferenceDAO;
import com.payasia.dao.bean.LionTimesheetPreference;
import com.payasia.dao.bean.LionTimesheetPreference_;

@Repository
public class LionTimesheetPreferenceDAOImpl extends BaseDAO implements
		LionTimesheetPreferenceDAO {

	@Override
	public void save(LionTimesheetPreference lionTimesheetPreference) {
		super.save(lionTimesheetPreference);
	}
	
	@Override
	public void update(LionTimesheetPreference lionTimesheetPreference) {
		super.update(lionTimesheetPreference);
	}

	@Override
	protected Object getBaseEntity() {
		LionTimesheetPreference lionTimesheetPreference = new LionTimesheetPreference();
		return lionTimesheetPreference;
	}

	@Override
	public LionTimesheetPreference findByCompanyId(Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<LionTimesheetPreference> criteriaQuery = cb
				.createQuery(LionTimesheetPreference.class);

		Root<LionTimesheetPreference> root = criteriaQuery
				.from(LionTimesheetPreference.class);
		Predicate restriction = cb.conjunction();
		restriction = cb
				.and(restriction, cb.equal(
						root.get(LionTimesheetPreference_.company), companyId));
		criteriaQuery.select(root).where(restriction);
		TypedQuery<LionTimesheetPreference> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		try {

			return typedQuery.getSingleResult();
		} catch (Exception ex) {
			return null;
		}
	}
}
