package com.payasia.dao.impl;

import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import com.payasia.dao.BaseDAO;
import com.payasia.dao.SsoConfigurationDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.CompanyGroup;
import com.payasia.dao.bean.CompanyGroup_;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.SsoConfiguration;
import com.payasia.dao.bean.SsoConfiguration_;

@Repository
public class SsoConfigurationDAOImpl extends BaseDAO implements SsoConfigurationDAO {

	@Override
	protected Object getBaseEntity() {
		// TODO Auto-generated method stub

		return new SsoConfiguration();
	}

	@Override
	public void save(SsoConfiguration ssoConfiguration) {
		super.save(ssoConfiguration);

	}

	@Override
	public void update(SsoConfiguration ssoConfiguration) {
		super.update(ssoConfiguration);

	}

	@Override
	public SsoConfiguration findByCompanyId(Long companyId) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<SsoConfiguration> criteriaQuery = cb.createQuery(SsoConfiguration.class);
		Root<SsoConfiguration> ssoConfigurationRoot = criteriaQuery.from(SsoConfiguration.class);

		criteriaQuery.select(ssoConfigurationRoot);

		Predicate restriction = cb.conjunction();

		Join<SsoConfiguration, Company> ssoConfigurationTypeJoin = ssoConfigurationRoot.join(SsoConfiguration_.company);

		restriction = cb.and(restriction, cb.equal(ssoConfigurationTypeJoin.get(Company_.companyId), companyId));

		criteriaQuery.where(restriction);

		TypedQuery<SsoConfiguration> companyTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<SsoConfiguration> companyList = companyTypedQuery.getResultList();
		if (companyList != null && !companyList.isEmpty()) {
			return companyList.get(0);
		}

		return null;
	}
	
	@Override
	public SsoConfiguration findByCompanyCodeWithGroup(String companyCode) {

		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<SsoConfiguration> criteriaQuery = cb.createQuery(SsoConfiguration.class);
		Root<SsoConfiguration> ssoConfigurationRoot = criteriaQuery.from(SsoConfiguration.class);

		Predicate restriction = cb.conjunction();

		Join<SsoConfiguration, Company> ssoConfigurationTypeJoin = ssoConfigurationRoot.join(SsoConfiguration_.company);
		Join<Company, CompanyGroup> ssoConfigurationCompTypeJoin = ssoConfigurationTypeJoin.join(Company_.companyGroup);
		
		restriction = cb.and(restriction, cb.equal(ssoConfigurationRoot.get(SsoConfiguration_.isEnableSso), true));
		
		if (StringUtils.isNotBlank(companyCode)) {
			Subquery<Long> subquery = criteriaQuery.subquery(Long.class);
			Root<Company> cmp = subquery.from(Company.class);
			Join<Company, CompanyGroup> sqEmp = cmp.join(Company_.companyGroup);
			subquery.select(sqEmp.get(CompanyGroup_.groupId).as(Long.class));

			Path<String> role = cmp.get(Company_.companyCode);

			Predicate subRestriction = cb.conjunction();

			subRestriction = cb.and(subRestriction, cb.equal(role, companyCode));

			subquery.where(subRestriction);

			restriction = cb.and(restriction, cb.and(cb.in(ssoConfigurationCompTypeJoin.get(CompanyGroup_.groupId)).value(subquery)));
		}
		
		criteriaQuery.where(restriction);

		TypedQuery<SsoConfiguration> companyTypedQuery = entityManagerFactory.createQuery(criteriaQuery);

		List<SsoConfiguration> companyList = companyTypedQuery.getResultList();
		if (companyList != null && !companyList.isEmpty()) {
			return companyList.get(0);
		}

		return null;
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.SsoConfigurationDAO#findCompanyByIDPIssuer(java.lang.String)
	 */
	@Override
	public Company findCompanyByIDPIssuer(String idpIssuer) {
		String query = new StringBuilder().append("SELECT cmp FROM SsoConfiguration sso INNER JOIN sso.company cmp")
				.append(" WHERE sso.idpIssuer= :idpIssuer AND sso.isEnableSso=true AND cmp.active=true").toString();
		TypedQuery<Company> tq = entityManagerFactory.createQuery(query, Company.class);
		tq.setParameter("idpIssuer", idpIssuer);
		List<Company> companies = tq.getResultList();
		if (!CollectionUtils.isEmpty(companies)) {
			return companies.get(0);
		}
		return null;
	}

}
