package com.payasia.dao.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.SortConstants;
import com.payasia.dao.BaseDAO;
import com.payasia.dao.CompanyExchangeRateDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.CompanyExchangeRate;
import com.payasia.dao.bean.CompanyExchangeRate_;
import com.payasia.dao.bean.Company_;
import com.payasia.dao.bean.CurrencyMaster;
import com.payasia.dao.bean.CurrencyMaster_;

/**
 * The Class CompanyExchangeRateDAOImpl.
 */
/**
 * @author vivekjain
 * 
 */
@Repository
public class CompanyExchangeRateDAOImpl extends BaseDAO implements
		CompanyExchangeRateDAO {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.CompanyExchangeRateDAO#findByYear(java.lang.Integer,
	 * java.lang.Long, java.lang.Long, com.payasia.common.form.PageRequest,
	 * com.payasia.common.form.SortCondition)
	 */
	@Override
	public List<CompanyExchangeRate> findByCondition(Long companyId,
			Long currencyId, Timestamp currencyDate, PageRequest pageDTO,
			SortCondition sortDTO) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CompanyExchangeRate> criteriaQuery = cb
				.createQuery(CompanyExchangeRate.class);
		Root<CompanyExchangeRate> companyExchangeRateRoot = criteriaQuery
				.from(CompanyExchangeRate.class);

		criteriaQuery.select(companyExchangeRateRoot);

		Join<CompanyExchangeRate, CurrencyMaster> compCurrencyJoin = companyExchangeRateRoot
				.join(CompanyExchangeRate_.currencyMaster);

		Join<CompanyExchangeRate, Company> compExcJoin = companyExchangeRateRoot
				.join(CompanyExchangeRate_.company);

		Predicate restriction = cb.conjunction();
		if (currencyId != null) {
			restriction = cb.and(restriction, cb.equal(
					compCurrencyJoin.get(CurrencyMaster_.currencyId),
					currencyId));
		}
		if (currencyDate != null) {
			restriction = cb
					.and(restriction, cb.lessThanOrEqualTo(
							companyExchangeRateRoot
									.get(CompanyExchangeRate_.startDate),
							currencyDate));
			restriction = cb.and(restriction, cb.greaterThanOrEqualTo(
					companyExchangeRateRoot.get(CompanyExchangeRate_.endDate),
					currencyDate));
		}

		restriction = cb.and(restriction,
				cb.equal(compExcJoin.get(Company_.companyId), companyId));

		criteriaQuery.where(restriction);

		if (sortDTO != null) {

			Path<String> sortPath = getSortPathForCompanyExchangeRate(sortDTO,
					companyExchangeRateRoot, compCurrencyJoin, compExcJoin);
			if (sortPath != null) {
				if (SortConstants.DB_ORDER_BY_ASC.equalsIgnoreCase(sortDTO
						.getOrderType())) {
					criteriaQuery.orderBy(cb.asc(sortPath));
				}
				if (SortConstants.DB_ORDER_BY_DESC.equalsIgnoreCase(sortDTO
						.getOrderType())) {
					criteriaQuery.orderBy(cb.desc(sortPath));
				}
			}

		}

		TypedQuery<CompanyExchangeRate> companyExchangeRateTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		if (pageDTO != null && (!(pageDTO.getPageSize() == -1))) {
			companyExchangeRateTypedQuery
					.setFirstResult(getStartPosition(pageDTO));
			companyExchangeRateTypedQuery.setMaxResults(pageDTO.getPageSize());
		}
		return companyExchangeRateTypedQuery.getResultList();
	}

	@Override
	public Integer getCountForCondition(Long companyId, Long currencyId,
			Timestamp currencyDate) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<Integer> criteriaQuery = cb.createQuery(Integer.class);
		Root<CompanyExchangeRate> companyExchangeRateRoot = criteriaQuery
				.from(CompanyExchangeRate.class);

		criteriaQuery.select(cb.count(companyExchangeRateRoot)
				.as(Integer.class));

		Join<CompanyExchangeRate, CurrencyMaster> compCurrencyJoin = companyExchangeRateRoot
				.join(CompanyExchangeRate_.currencyMaster);

		Join<CompanyExchangeRate, Company> compExcJoin = companyExchangeRateRoot
				.join(CompanyExchangeRate_.company);

		Predicate restriction = cb.conjunction();
		if (currencyId != null) {
			restriction = cb.and(restriction, cb.equal(
					compCurrencyJoin.get(CurrencyMaster_.currencyId),
					currencyId));
		}
		if (currencyDate != null) {
			restriction = cb
					.and(restriction, cb.lessThanOrEqualTo(
							companyExchangeRateRoot
									.get(CompanyExchangeRate_.startDate),
							currencyDate));
			restriction = cb.and(restriction, cb.greaterThanOrEqualTo(
					companyExchangeRateRoot.get(CompanyExchangeRate_.endDate),
					currencyDate));
		}

		restriction = cb.and(restriction,
				cb.equal(compExcJoin.get(Company_.companyId), companyId));

		criteriaQuery.where(restriction);

		TypedQuery<Integer> companyExchangeRateTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		return companyExchangeRateTypedQuery.getSingleResult();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.CompanyExchangeRateDAO#getSortPathForCompanyExchangeRate
	 * (com.payasia.common.form.SortCondition, javax.persistence.criteria.Root,
	 * javax.persistence.criteria.Join, javax.persistence.criteria.Join)
	 */
	@Override
	public Path<String> getSortPathForCompanyExchangeRate(
			SortCondition sortDTO,
			Root<CompanyExchangeRate> companyExchangeRateRoot,
			Join<CompanyExchangeRate, CurrencyMaster> compCurrencyJoin,
			Join<CompanyExchangeRate, Company> compExcJoin) {

		List<String> currencyIsIdList = new ArrayList<String>();
		currencyIsIdList.add(SortConstants.CUREENCY_ID);

		List<String> currencyIsColList = new ArrayList<String>();
		currencyIsColList.add(SortConstants.CURRENCY_NAME);

		List<String> exchangeRateList = new ArrayList<String>();
		exchangeRateList.add(SortConstants.CUREENCY_START_DATE);
		exchangeRateList.add(SortConstants.CUREENCY_END_DATE);
		exchangeRateList.add(SortConstants.CUREENCY_EXCHANGE_RATE);

		Path<String> sortPath = null;

		if (currencyIsColList.contains(sortDTO.getColumnName())) {
			sortPath = compCurrencyJoin.get(colMap.get(CurrencyMaster.class
					+ sortDTO.getColumnName()));
		}
		if (exchangeRateList.contains(sortDTO.getColumnName())) {
			sortPath = companyExchangeRateRoot.get(colMap
					.get(CompanyExchangeRate.class + sortDTO.getColumnName()));
		}
		return sortPath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.BaseDAO#getBaseEntity()
	 */
	@Override
	protected Object getBaseEntity() {
		CompanyExchangeRate companyExchangeRate = new CompanyExchangeRate();
		return companyExchangeRate;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.dao.CompanyExchangeRateDAO#getSortPathForCurrencyList(com
	 * .payasia.common.form.SortCondition, javax.persistence.criteria.Root,
	 * javax.persistence.criteria.Join)
	 */
	@Override
	public Path<String> getSortPathForCurrencyList(SortCondition sortDTO,
			Root<CompanyExchangeRate> companyExchangeRateRoot,
			Join<CompanyExchangeRate, CurrencyMaster> compCurrencyJoin) {

		List<String> currencyIsIdList = new ArrayList<String>();
		currencyIsIdList.add(SortConstants.CUREENCY_ID);

		List<String> currencyIsColList = new ArrayList<String>();
		currencyIsColList.add(SortConstants.CURRENCY_NAME);

		Path<String> sortPath = null;

		if (currencyIsColList.contains(sortDTO.getColumnName())) {
			sortPath = compCurrencyJoin.get(colMap.get(CurrencyMaster.class
					+ sortDTO.getColumnName()));
		}
		return sortPath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.CompanyExchangeRateDAO#save(com.payasia.dao.bean.
	 * CompanyExchangeRate)
	 */
	@Override
	public void save(CompanyExchangeRate companyExchangeRate) {
		super.save(companyExchangeRate);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.CompanyExchangeRateDAO#update(com.payasia.dao.bean.
	 * CompanyExchangeRate)
	 */
	@Override
	public void update(CompanyExchangeRate companyExchangeRate) {
		super.update(companyExchangeRate);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.dao.CompanyExchangeRateDAO#delete(com.payasia.dao.bean.
	 * CompanyExchangeRate)
	 */
	@Override
	public void delete(CompanyExchangeRate companyExchangeRate) {
		super.delete(companyExchangeRate);
	}

	@Override
	public CompanyExchangeRate findExchangeRate(Date currencyDate,
			Long currencyId, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CompanyExchangeRate> criteriaQuery = cb
				.createQuery(CompanyExchangeRate.class);
		Root<CompanyExchangeRate> companyExchangeRateRoot = criteriaQuery
				.from(CompanyExchangeRate.class);

		criteriaQuery.select(companyExchangeRateRoot);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.lessThanOrEqualTo(
				companyExchangeRateRoot.get(CompanyExchangeRate_.startDate),
				currencyDate));
		restriction = cb.and(restriction, cb.greaterThanOrEqualTo(
				companyExchangeRateRoot.get(CompanyExchangeRate_.endDate),
				currencyDate));

		restriction = cb.and(
				restriction,
				cb.equal(
						companyExchangeRateRoot
								.get(CompanyExchangeRate_.company)
								.get("companyId").as(Long.class), companyId));

		restriction = cb.and(
				restriction,
				cb.equal(
						companyExchangeRateRoot
								.get(CompanyExchangeRate_.currencyMaster)
								.get("currencyId").as(Long.class), currencyId));

		criteriaQuery.where(restriction);

		TypedQuery<CompanyExchangeRate> companyExchangeRateTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		List<CompanyExchangeRate> companyExchangeRates = companyExchangeRateTypedQuery
				.getResultList();
		CompanyExchangeRate companyExchangeRate = null;
		if (!companyExchangeRates.isEmpty()) {
			companyExchangeRate = companyExchangeRates.get(0);
		}

		return companyExchangeRate;
	}

	@Override
	public CompanyExchangeRate findById(Long companyExchangeRateId) {
		return super.findById(CompanyExchangeRate.class, companyExchangeRateId);
	}
	
	@Override
	public CompanyExchangeRate findByCompanyExchangeRateId(Long companyExchangeRateId, Long companyId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CompanyExchangeRate> criteriaQuery = cb
				.createQuery(CompanyExchangeRate.class);
		Root<CompanyExchangeRate> companyExchangeRateRoot = criteriaQuery
				.from(CompanyExchangeRate.class);

		criteriaQuery.select(companyExchangeRateRoot);

		Join<CompanyExchangeRate, Company> compExcJoin = companyExchangeRateRoot
				.join(CompanyExchangeRate_.company);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction,
				cb.equal(companyExchangeRateRoot.get(CompanyExchangeRate_.compExchangeRateId), companyExchangeRateId));

		restriction = cb.and(restriction,
		cb.equal(compExcJoin.get(Company_.companyId), companyId));

		criteriaQuery.where(restriction);

		

		TypedQuery<CompanyExchangeRate> companyExchangeRateTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		
		CompanyExchangeRate companyExchangeRate=null;
		
		if(companyExchangeRateTypedQuery.getResultList()!=null&&!companyExchangeRateTypedQuery.getResultList().isEmpty())
		{
			 companyExchangeRate = companyExchangeRateTypedQuery.getResultList().get(0);
		}
		
		return companyExchangeRate;
		
		
		
	}
	
	

	@Override
	public CompanyExchangeRate CheckExchangeRate(Date currencyDate,
			Long currencyId, Long companyId, Long companyExchangeRateId) {
		CriteriaBuilder cb = entityManagerFactory.getCriteriaBuilder();
		CriteriaQuery<CompanyExchangeRate> criteriaQuery = cb
				.createQuery(CompanyExchangeRate.class);
		Root<CompanyExchangeRate> companyExchangeRateRoot = criteriaQuery
				.from(CompanyExchangeRate.class);

		criteriaQuery.select(companyExchangeRateRoot);

		Predicate restriction = cb.conjunction();
		restriction = cb.and(restriction, cb.lessThanOrEqualTo(
				companyExchangeRateRoot.get(CompanyExchangeRate_.startDate),
				currencyDate));
		restriction = cb.and(restriction, cb.greaterThanOrEqualTo(
				companyExchangeRateRoot.get(CompanyExchangeRate_.endDate),
				currencyDate));

		restriction = cb.and(
				restriction,
				cb.equal(
						companyExchangeRateRoot
								.get(CompanyExchangeRate_.company)
								.get("companyId").as(Long.class), companyId));

		restriction = cb.and(
				restriction,
				cb.equal(
						companyExchangeRateRoot
								.get(CompanyExchangeRate_.currencyMaster)
								.get("currencyId").as(Long.class), currencyId));
		if (companyExchangeRateId != null) {
			restriction = cb.and(restriction, cb.notEqual(
					companyExchangeRateRoot
							.get(CompanyExchangeRate_.compExchangeRateId),
					companyExchangeRateId));
		}
		criteriaQuery.where(restriction);

		TypedQuery<CompanyExchangeRate> companyExchangeRateTypedQuery = entityManagerFactory
				.createQuery(criteriaQuery);
		List<CompanyExchangeRate> companyExchangeRates = companyExchangeRateTypedQuery
				.getResultList();
		CompanyExchangeRate companyExchangeRate = null;
		if (!companyExchangeRates.isEmpty()) {
			companyExchangeRate = companyExchangeRates.get(0);
		}

		return companyExchangeRate;
	}

}
