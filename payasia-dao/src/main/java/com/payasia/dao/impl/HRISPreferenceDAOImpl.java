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
import com.payasia.dao.HRISPreferenceDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.HRISPreference;
import com.payasia.dao.bean.HRISPreference_;

@Repository
public class HRISPreferenceDAOImpl extends BaseDAO implements HRISPreferenceDAO {

	@Override
	protected Object getBaseEntity() {
		HRISPreference hrisPreference = new HRISPreference();
		return hrisPreference;
	}

	@Override
	public void update(HRISPreference hrisPreference) {
		this.entityManagerFactory.merge(hrisPreference);
	}

	@Override
	public void delete(HRISPreference hrisPreference) {
		this.entityManagerFactory.remove(hrisPreference);
	}

	@Override
	public void save(HRISPreference hrisPreference) {
		super.save(hrisPreference);
	}

	@Override
	public HRISPreference findById(Long hrisPreferenceId) {
		return super.findById(HRISPreference.class, hrisPreferenceId);

	}

	@Override
	public HRISPreference findByCompanyId(Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<HRISPreference> criteriaQuery = cb
				.createQuery(HRISPreference.class);
		Root<HRISPreference> hrisPreferenceRoot = criteriaQuery
				.from(HRISPreference.class);

		criteriaQuery.select(hrisPreferenceRoot);
		Join<HRISPreference, Company> companyJoin = hrisPreferenceRoot
				.join(HRISPreference_.company);
		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction,
				cb.equal(companyJoin.get(Company_.companyId), companyId));
		criteriaQuery.where(restriction);
		TypedQuery<HRISPreference> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<HRISPreference> hrisPrefList = typedQuery.getResultList();
		if (hrisPrefList != null &&  !hrisPrefList.isEmpty()) {
			return hrisPrefList.get(0);
		}
		return null;
	}

}
