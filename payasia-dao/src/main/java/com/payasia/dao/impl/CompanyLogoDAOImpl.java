package com.payasia.dao.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.util.NullAwareBeanUtilsBean;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.CompanyLogoDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.CompanyLogo;
import com.payasia.dao.bean.CompanyLogo_;
import com.payasia.dao.bean.Company_;

/**
 * The Class CompanyLogoDAOImpl.
 */
/**
 * @author vivekjain
 * 
 */
@Repository
public class CompanyLogoDAOImpl extends BaseDAO implements CompanyLogoDAO {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.CompanyLogoDAO#findById(long)
	 */
	@Override
	public CompanyLogo findById(long companyLogoId) {
		return super.findById(CompanyLogo.class, companyLogoId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.CompanyLogoDAO#save(com.payasia.dao.bean.CompanyLogo)
	 */
	@Override
	public void save(CompanyLogo companyLogo) {
		super.save(companyLogo);

	}

	@Override
	public CompanyLogo saveReturn(CompanyLogo companyLogo) {
		CompanyLogo persistObj = companyLogo;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (CompanyLogo) getBaseEntity();
			beanUtil.copyProperties(persistObj, companyLogo);
		} catch (IllegalAccessException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		} catch (InvocationTargetException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		}
		this.entityManagerFactory.persist(persistObj);
		return persistObj;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.CompanyLogoDAO#update(com.payasia.dao.bean.CompanyLogo)
	 */
	@Override
	public void update(CompanyLogo companyLogo) {
		super.update(companyLogo);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.BaseDAO#getBaseEntity()
	 */
	@Override
	protected Object getBaseEntity() {
		CompanyLogo companyLogo = new CompanyLogo();
		return companyLogo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.CompanyLogoDAO#findByConditionCompany(java.lang.Long)
	 */
	@Override
	public CompanyLogo findByConditionCompany(Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CompanyLogo> criteriaQuery = cb
				.createQuery(CompanyLogo.class);
		Root<CompanyLogo> compLogoRoot = criteriaQuery.from(CompanyLogo.class);

		criteriaQuery.select(compLogoRoot);

		Join<CompanyLogo, Company> compLogoRootJoin = compLogoRoot
				.join(CompanyLogo_.company);

		Path<Long> companyID = compLogoRootJoin.get(Company_.companyId);

		criteriaQuery.where(cb.equal(companyID, companyId));

		TypedQuery<CompanyLogo> compLogoTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		List<CompanyLogo> companyLogoList = compLogoTypedQuery.getResultList();
		if (companyLogoList != null && !companyLogoList.isEmpty()) {
			return companyLogoList.get(0);
		}
		return null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.CompanyLogoDAO#findByConditionCompanyCode(java.lang.String
	 * )
	 */
	@Override
	public CompanyLogo findByConditionCompanyCode(String companyCode) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CompanyLogo> criteriaQuery = cb
				.createQuery(CompanyLogo.class);
		Root<CompanyLogo> compLogoRoot = criteriaQuery.from(CompanyLogo.class);

		criteriaQuery.select(compLogoRoot);

		Join<CompanyLogo, Company> compLogoRootJoin = compLogoRoot
				.join(CompanyLogo_.company);

		Path<String> companyCodeString = compLogoRootJoin
				.get(Company_.companyCode);

		criteriaQuery.where(cb.equal(cb.upper(companyCodeString),
				companyCode.toUpperCase()));

		TypedQuery<CompanyLogo> compLogoTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		List<CompanyLogo> companyLogoList = compLogoTypedQuery.getResultList();
		if (companyLogoList != null && !companyLogoList.isEmpty()) {
			return companyLogoList.get(0);
		}
		return null;

	}

}
