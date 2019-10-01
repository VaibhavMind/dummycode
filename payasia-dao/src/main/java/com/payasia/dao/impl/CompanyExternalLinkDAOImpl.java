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
import com.payasia.dao.CompanyExternalLinkDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.CompanyExternalLink;
import com.payasia.dao.bean.CompanyExternalLink_;
import com.payasia.dao.bean.Company_;

/**
 * The Class CompanyExternalLinkDAOImpl.
 */
/**
 * @author vivekjain
 * 
 */
@Repository
public class CompanyExternalLinkDAOImpl extends BaseDAO implements
		CompanyExternalLinkDAO {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.BaseDAO#getBaseEntity()
	 */
	@Override
	protected Object getBaseEntity() {
		CompanyExternalLink companyExternalLink = new CompanyExternalLink();
		return companyExternalLink;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.CompanyExternalLinkDAO#save(com.payasia.dao.bean.
	 * CompanyExternalLink)
	 */
	@Override
	public void save(CompanyExternalLink companyExternalLink) {
		super.save(companyExternalLink);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.CompanyExternalLinkDAO#update(com.payasia.dao.bean.
	 * CompanyExternalLink)
	 */
	@Override
	public void update(CompanyExternalLink companyExternalLink) {
		super.update(companyExternalLink);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.CompanyExternalLinkDAO#findByConditionCompany(java.lang
	 * .Long)
	 */
	@Override
	public CompanyExternalLink findByConditionCompany(Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CompanyExternalLink> criteriaQuery = cb
				.createQuery(CompanyExternalLink.class);
		Root<CompanyExternalLink> compExtLinkTypeRoot = criteriaQuery
				.from(CompanyExternalLink.class);

		criteriaQuery.select(compExtLinkTypeRoot);

		Join<CompanyExternalLink, Company> compExtLinkTypeRootJoin = compExtLinkTypeRoot
				.join(CompanyExternalLink_.company);

		Path<Long> companyID = compExtLinkTypeRootJoin.get(Company_.companyId);

		criteriaQuery.where(cb.equal(companyID, companyId));

		TypedQuery<CompanyExternalLink> compExtLinkTypeTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);

		List<CompanyExternalLink> companyExternalLinkList = compExtLinkTypeTypedQuery
				.getResultList();
		if (companyExternalLinkList != null
				&& !companyExternalLinkList.isEmpty()) {
			return companyExternalLinkList.get(0);
		}
		return null;

	}

}
