package com.payasia.dao.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.util.NullAwareBeanUtilsBean;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.DataImportHistoryDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.DataImportHistory;
import com.payasia.dao.bean.DataImportHistory_;

@Repository
public class DataImportHistoryDAOImpl extends BaseDAO implements
		DataImportHistoryDAO {

	@Override
	public DataImportHistory saveHistory(DataImportHistory dataImportHistory) {
		DataImportHistory persistObj = dataImportHistory;
		try {
			NullAwareBeanUtilsBean beanUtil = new NullAwareBeanUtilsBean();
			persistObj = (DataImportHistory) getBaseEntity();
			beanUtil.copyProperties(persistObj, dataImportHistory);
		} catch (IllegalAccessException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		} catch (InvocationTargetException e) {
			throw new PayAsiaSystemException("errors.dao", e);
		}
		this.entityManagerFactory.persist(persistObj);
		return persistObj;
	}

	@Override
	protected Object getBaseEntity() {
		DataImportHistory dataImportHistory = new DataImportHistory();
		return dataImportHistory;
	}

	@Override
	public List<DataImportHistory> findAll(Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<DataImportHistory> criteriaQuery = cb
				.createQuery(DataImportHistory.class);

		Root<DataImportHistory> historyRoot = criteriaQuery
				.from(DataImportHistory.class);
		Join<DataImportHistory, Company> empCompanyJoin = historyRoot
				.join(DataImportHistory_.company);
		criteriaQuery.select(historyRoot);
		criteriaQuery.where(cb.equal(empCompanyJoin.get(Company_.companyId),
				companyId));

		TypedQuery<DataImportHistory> typedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		return typedQuery.getResultList();
	}

}
