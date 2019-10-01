package com.payasia.dao.impl;

import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.payasia.dao.BaseDAO;
import com.payasia.dao.KeyPayIntLeaveApplicationDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.IntegrationMaster;
import com.payasia.dao.bean.IntegrationMaster_;
import com.payasia.dao.bean.KeyPayIntLeaveApplication;
import com.payasia.dao.bean.KeyPayIntLeaveApplication_;
import com.payasia.dao.bean.LeaveApplication;
import com.payasia.dao.bean.LeaveApplication_;

@Repository
public class KeyPayIntLeaveApplicationDAOImpl extends BaseDAO implements KeyPayIntLeaveApplicationDAO {
	private static final Logger LOGGER = Logger.getLogger(KeyPayIntLeaveApplicationDAOImpl.class);

	@Override
	protected Object getBaseEntity() {
		KeyPayIntLeaveApplication keyPayIntLeaveApplication = new KeyPayIntLeaveApplication();
		return keyPayIntLeaveApplication;
	}

	@Override
	public void save(KeyPayIntLeaveApplication keyPayIntLeaveApplication) {
		super.save(keyPayIntLeaveApplication);
	}

	@Override
	public void update(KeyPayIntLeaveApplication keyPayIntLeaveApplication) {
		super.update(keyPayIntLeaveApplication);
	}

	@Override
	public Long getMaxApprovedLeaveAppId(Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
		Root<KeyPayIntLeaveApplication> root = criteriaQuery.from(KeyPayIntLeaveApplication.class);
		criteriaQuery.select(
				cb.max(root.get(KeyPayIntLeaveApplication_.leaveApplication).get("leaveApplicationId").as(Long.class)));

		Join<KeyPayIntLeaveApplication, Company> companyJoin = root.join(KeyPayIntLeaveApplication_.company);

		Predicate restriction = cb.conjunction();

		restriction = cb.and(restriction, cb.equal(companyJoin.get(Company_.companyId), companyId));

		criteriaQuery.where(restriction);

		TypedQuery<Long> dynamicFormTypedQuery = entityManagerFactory.createQuery(criteriaQuery);
		if (dynamicFormTypedQuery.getResultList().size() > 0) {
			try {
				return dynamicFormTypedQuery.getResultList().get(0);
			} catch (NullPointerException e) {
				LOGGER.error(e.getMessage(), e);
				return 0l;
			}
		} else {
			return 0l;
		}
	}

	@Override
	public List<KeyPayIntLeaveApplication> findByCondition(int syncStatus, String leaveStatus, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<KeyPayIntLeaveApplication> criteriaQuery = cb.createQuery(KeyPayIntLeaveApplication.class);
		Root<KeyPayIntLeaveApplication> root = criteriaQuery.from(KeyPayIntLeaveApplication.class);
		criteriaQuery.select(root);

		Predicate restriction = cb.conjunction();
		Join<KeyPayIntLeaveApplication, Company> companyJoin = root.join(KeyPayIntLeaveApplication_.company);

		restriction = cb.and(restriction, cb.notEqual(root.get(KeyPayIntLeaveApplication_.syncStatus), syncStatus));
		restriction = cb.and(restriction, cb.equal(root.get(KeyPayIntLeaveApplication_.leaveStatus), leaveStatus));
		restriction = cb.and(restriction, cb.equal(companyJoin.get(Company_.companyId), companyId));
		criteriaQuery.where(restriction);

		TypedQuery<KeyPayIntLeaveApplication> typedQuery = entityManagerFactory.createQuery(criteriaQuery);
		return typedQuery.getResultList();
	}

	@Override
	public KeyPayIntLeaveApplication findByLeaveAppId(Long companyId, Long leaveApplicationId, String leaveStatus) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<KeyPayIntLeaveApplication> criteriaQuery = cb.createQuery(KeyPayIntLeaveApplication.class);
		Root<KeyPayIntLeaveApplication> root = criteriaQuery.from(KeyPayIntLeaveApplication.class);
		criteriaQuery.select(root);

		Join<KeyPayIntLeaveApplication, Company> companyJoin = root.join(KeyPayIntLeaveApplication_.company);

		Join<KeyPayIntLeaveApplication, LeaveApplication> leaveAppJoin = root
				.join(KeyPayIntLeaveApplication_.leaveApplication);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction,
				cb.equal(leaveAppJoin.get(LeaveApplication_.leaveApplicationId), leaveApplicationId));
		restriction = cb.and(restriction, cb.notEqual(root.get(KeyPayIntLeaveApplication_.leaveStatus), leaveStatus));
		restriction = cb.and(restriction, cb.equal(companyJoin.get(Company_.companyId), companyId));
		criteriaQuery.where(restriction);

		TypedQuery<KeyPayIntLeaveApplication> typedQuery = entityManagerFactory.createQuery(criteriaQuery);
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
	public IntegrationMaster findByKeyPayDetailByCompanyId(Long companyId) {
		Boolean active = true;
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();

		CriteriaQuery<IntegrationMaster> criteriaQuery = cb.createQuery(IntegrationMaster.class);
		Root<IntegrationMaster> root = criteriaQuery.from(IntegrationMaster.class);
		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.equal(root.get(IntegrationMaster_.companyId), companyId));
		restriction = cb.and(restriction, cb.equal(root.get(IntegrationMaster_.active), active));
		criteriaQuery.where(restriction);
		TypedQuery<IntegrationMaster> typedQuery = entityManagerFactory.createQuery(criteriaQuery);

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
}
