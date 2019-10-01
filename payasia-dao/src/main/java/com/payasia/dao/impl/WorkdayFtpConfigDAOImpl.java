package com.payasia.dao.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.persistence.NoResultException;
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
import com.payasia.dao.WorkdayFtpConfigDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.WorkdayFtpConfig;
import com.payasia.dao.bean.WorkdayFtpConfig_;

@Repository
public class WorkdayFtpConfigDAOImpl extends BaseDAO implements WorkdayFtpConfigDAO {

	@Override
	protected Object getBaseEntity() {
		return new WorkdayFtpConfig();
	}
	
	@Override
	public WorkdayFtpConfig saveReturn(WorkdayFtpConfig workdayFtpConfig) {
		WorkdayFtpConfig persistObj = workdayFtpConfig;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (WorkdayFtpConfig) getBaseEntity();
			beanUtil.copyProperties(persistObj, workdayFtpConfig);
		} catch (IllegalAccessException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		} catch (InvocationTargetException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		}
		this.entityManagerFactory.persist(persistObj);
		return persistObj;
	}

	@Override
	public void save(WorkdayFtpConfig workdayFtpConfig) {
		super.save(workdayFtpConfig);
	}
	
	@Override
	public void update(WorkdayFtpConfig ftpConfig) {
		super.update(ftpConfig);
	}

	@Override
	public WorkdayFtpConfig findByCompanyId(Long companyId) {
		
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<WorkdayFtpConfig> criteriaQuery = cb
				.createQuery(WorkdayFtpConfig.class);
		Root<WorkdayFtpConfig> ftpConfigRoot = criteriaQuery.from(WorkdayFtpConfig.class);

		criteriaQuery.select(ftpConfigRoot);
		Join<WorkdayFtpConfig, Company> companyJoin = ftpConfigRoot
				.join(WorkdayFtpConfig_.company);
		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction,
				cb.equal(companyJoin.get(Company_.companyId), companyId));
		criteriaQuery.where(restriction);
		TypedQuery<WorkdayFtpConfig> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		WorkdayFtpConfig ftpConfig = null;
		try {
			ftpConfig = typedQuery.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}
		return ftpConfig;
	}
	
	@Override
	public WorkdayFtpConfig findActiveFtpConfigByCompanyId(Long companyId) {
		
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<WorkdayFtpConfig> criteriaQuery = cb
				.createQuery(WorkdayFtpConfig.class);
		Root<WorkdayFtpConfig> ftpConfigRoot = criteriaQuery.from(WorkdayFtpConfig.class);

		criteriaQuery.select(ftpConfigRoot);
		Join<WorkdayFtpConfig, Company> companyJoin = ftpConfigRoot
				.join(WorkdayFtpConfig_.company);
		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction,
				cb.equal(companyJoin.get(Company_.companyId), companyId));
		restriction = cb.and(restriction,
				cb.equal(ftpConfigRoot.get(WorkdayFtpConfig_.employeeDataIsActive), true));
		criteriaQuery.where(restriction);
		TypedQuery<WorkdayFtpConfig> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		WorkdayFtpConfig ftpConfig = null;
		try {
			ftpConfig = typedQuery.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}
		return ftpConfig;
	}


	@Override
	public List<WorkdayFtpConfig> findAll() {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<WorkdayFtpConfig> cq = cb.createQuery(WorkdayFtpConfig.class);
		Root<WorkdayFtpConfig> rootEntry = cq.from(WorkdayFtpConfig.class);
		CriteriaQuery<WorkdayFtpConfig> all = cq.select(rootEntry);
		TypedQuery<WorkdayFtpConfig> allQuery = entityManagerFactory.createQuery(all);
		return allQuery.getResultList();
	}

}
