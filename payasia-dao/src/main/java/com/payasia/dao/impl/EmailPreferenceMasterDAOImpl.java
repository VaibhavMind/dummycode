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
import com.payasia.dao.EmailPreferenceMasterDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.EmailPreferenceMaster;
import com.payasia.dao.bean.EmailPreferenceMaster_;

/**
 * The Class EmailPreferenceMasterDAOImpl.
 */
/**
 * @author vivekjain
 * 
 */
@Repository
public class EmailPreferenceMasterDAOImpl extends BaseDAO implements
		EmailPreferenceMasterDAO {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.BaseDAO#getBaseEntity()
	 */
	@Override
	protected Object getBaseEntity() {
		EmailPreferenceMaster emailPreferenceMaster = new EmailPreferenceMaster();
		return emailPreferenceMaster;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.EmailPreferenceMasterDAO#findByConditionCompany(java.
	 * lang.Long)
	 */
	@Override
	public EmailPreferenceMaster findByConditionCompany(Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<EmailPreferenceMaster> criteriaQuery = cb
				.createQuery(EmailPreferenceMaster.class);
		Root<EmailPreferenceMaster> emailTypeRoot = criteriaQuery
				.from(EmailPreferenceMaster.class);

		criteriaQuery.select(emailTypeRoot);

		Join<EmailPreferenceMaster, Company> emailTypeRootJoin = emailTypeRoot
				.join(EmailPreferenceMaster_.company);

		Path<Long> companyID = emailTypeRootJoin.get(Company_.companyId);

		criteriaQuery.where(cb.equal(companyID, companyId));

		criteriaQuery.orderBy(cb.asc(emailTypeRoot
				.get(EmailPreferenceMaster_.emailPrefId)));

		TypedQuery<EmailPreferenceMaster> emailTypeTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<EmailPreferenceMaster> emailTypeList = emailTypeTypedQuery
				.getResultList();
		if (emailTypeList != null &&  !emailTypeList.isEmpty()) {
			return emailTypeList.get(0);
		}
		return null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.EmailPreferenceMasterDAO#save(com.payasia.dao.bean.
	 * EmailPreferenceMaster)
	 */
	@Override
	public void save(EmailPreferenceMaster emailPreferenceMaster) {
		super.save(emailPreferenceMaster);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.EmailPreferenceMasterDAO#update(com.payasia.dao.bean.
	 * EmailPreferenceMaster)
	 */
	@Override
	public void update(EmailPreferenceMaster emailPreferenceMaster) {
		super.update(emailPreferenceMaster);

	}

}
