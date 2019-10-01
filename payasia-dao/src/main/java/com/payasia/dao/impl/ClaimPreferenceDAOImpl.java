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
import com.payasia.dao.ClaimPreferenceDAO;
import com.payasia.dao.bean.ClaimPreference;
import com.payasia.dao.bean.ClaimPreference_;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Company_;

@Repository
public class ClaimPreferenceDAOImpl extends BaseDAO implements
		ClaimPreferenceDAO {

	@Override
	protected Object getBaseEntity() {
		ClaimPreference claimPreference = new ClaimPreference();
		return claimPreference;
	}

	@Override
	public void update(ClaimPreference claimPreference) {
		super.update(claimPreference);
	}

	@Override
	public void delete(ClaimPreference claimPreference) {
		super.delete(claimPreference);
	}

	@Override
	public void save(ClaimPreference claimPreference) {
		super.save(claimPreference);
	}

	@Override
	public ClaimPreference findByID(Long claimPreferenceId) {
		return super.findById(ClaimPreference.class, claimPreferenceId);
	}

	@Override
	public ClaimPreference findByCompanyId(Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<ClaimPreference> criteriaQuery = cb
				.createQuery(ClaimPreference.class);
		Root<ClaimPreference> claimPreferenceRoot = criteriaQuery
				.from(ClaimPreference.class);

		criteriaQuery.select(claimPreferenceRoot);
		Join<ClaimPreference, Company> companyJoin = claimPreferenceRoot
				.join(ClaimPreference_.company);
		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction,
				cb.equal(companyJoin.get(Company_.companyId), companyId));
		criteriaQuery.where(restriction);
		TypedQuery<ClaimPreference> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<ClaimPreference> claimPrefList = typedQuery.getResultList();
		if (claimPrefList != null && !claimPrefList.isEmpty()) {
			return claimPrefList.get(0);
		}
		return null;
	}

	@Override
	public ClaimPreference saveReturn(ClaimPreference claimPreference) {
		ClaimPreference persistObj = claimPreference;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (ClaimPreference) getBaseEntity();
			beanUtil.copyProperties(persistObj, claimPreference);
		} catch (IllegalAccessException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		} catch (InvocationTargetException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		}
		this.entityManagerFactory.persist(persistObj);
		return persistObj;
	}

}
