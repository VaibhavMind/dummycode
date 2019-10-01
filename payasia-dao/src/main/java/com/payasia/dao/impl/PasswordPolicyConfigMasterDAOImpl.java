package com.payasia.dao.impl;

import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.payasia.dao.BaseDAO;
import com.payasia.dao.PasswordPolicyConfigMasterDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.PasswordPolicyConfigMaster;
import com.payasia.dao.bean.PasswordPolicyConfigMaster_;

/**
 * The Class PasswordPolicyConfigMasterDAOImpl.
 */
/**
 * @author vivekjain
 * 
 */
@Repository
public class PasswordPolicyConfigMasterDAOImpl extends BaseDAO implements
		PasswordPolicyConfigMasterDAO {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.BaseDAO#getBaseEntity()
	 */
	@Override
	protected Object getBaseEntity() {
		PasswordPolicyConfigMaster passwordPolicyConfigMaster = new PasswordPolicyConfigMaster();
		return passwordPolicyConfigMaster;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.PasswordPolicyConfigMasterDAO#save(com.payasia.dao.bean
	 * .PasswordPolicyConfigMaster)
	 */
	@Override
	public void save(PasswordPolicyConfigMaster passwordPolicyConfigMaster) {
		super.save(passwordPolicyConfigMaster);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.PasswordPolicyConfigMasterDAO#update(com.payasia.dao.
	 * bean.PasswordPolicyConfigMaster)
	 */
	@Override
	public void update(PasswordPolicyConfigMaster passwordPolicyConfigMaster) {
		super.update(passwordPolicyConfigMaster);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.PasswordPolicyConfigMasterDAO#findByConditionCompany(
	 * java.lang.Long)
	 */
	@Override
	public PasswordPolicyConfigMaster findByConditionCompany(Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<PasswordPolicyConfigMaster> criteriaQuery = cb
				.createQuery(PasswordPolicyConfigMaster.class);
		Root<PasswordPolicyConfigMaster> passTypeRoot = criteriaQuery
				.from(PasswordPolicyConfigMaster.class);

		criteriaQuery.select(passTypeRoot);

		Join<PasswordPolicyConfigMaster, Company> passTypeRootJoin = passTypeRoot
				.join(PasswordPolicyConfigMaster_.company);

		Path<Long> companyID = passTypeRootJoin.get(Company_.companyId);

		criteriaQuery.where(cb.equal(companyID, companyId));

		TypedQuery<PasswordPolicyConfigMaster> passTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		List<PasswordPolicyConfigMaster> passwordList = passTypedQuery
				.getResultList();
		if (passwordList != null &&  !passwordList.isEmpty()) {
			return passwordList.get(0);
		}
		return null;

	}

	@Override
	public PasswordPolicyConfigMaster findByConditionCompanyCode(
			String companyCode) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<PasswordPolicyConfigMaster> criteriaQuery = cb
				.createQuery(PasswordPolicyConfigMaster.class);
		Root<PasswordPolicyConfigMaster> passTypeRoot = criteriaQuery
				.from(PasswordPolicyConfigMaster.class);

		criteriaQuery.select(passTypeRoot);

		Join<PasswordPolicyConfigMaster, Company> passTypeRootJoin = passTypeRoot
				.join(PasswordPolicyConfigMaster_.company);

		Path<String> companyCODE = passTypeRootJoin.get(Company_.companyCode);

		criteriaQuery.where(cb.equal(companyCODE, companyCode));

		TypedQuery<PasswordPolicyConfigMaster> passTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		List<PasswordPolicyConfigMaster> passwordList = passTypedQuery
				.getResultList();
		if (passwordList != null &&  !passwordList.isEmpty()) {
			return passwordList.get(0);
		}
		return null;

	}

}
