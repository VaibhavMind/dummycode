package com.payasia.dao;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.CompanyExchangeRate;
import com.payasia.dao.bean.CurrencyMaster;

/**
 * The Interface CompanyExchangeRateDAO.
 */
public interface CompanyExchangeRateDAO {

	/**
	 * Gets the sort path for company exchange rate.
	 * 
	 * @param sortDTO
	 *            the sort dto
	 * @param companyExchangeRateRoot
	 *            the company exchange rate root
	 * @param compCurrencyJoin
	 *            the comp currency join
	 * @param compExcJoin
	 *            the comp exc join
	 * @return the sort path for company exchange rate
	 */
	Path<String> getSortPathForCompanyExchangeRate(SortCondition sortDTO,
			Root<CompanyExchangeRate> companyExchangeRateRoot,
			Join<CompanyExchangeRate, CurrencyMaster> compCurrencyJoin,
			Join<CompanyExchangeRate, Company> compExcJoin);

	/**
	 * Gets the sort path for currency list.
	 * 
	 * @param sortDTO
	 *            the sort dto
	 * @param companyExchangeRateRoot
	 *            the company exchange rate root
	 * @param compCurrencyJoin
	 *            the comp currency join
	 * @return the sort path for currency list
	 */
	Path<String> getSortPathForCurrencyList(SortCondition sortDTO,
			Root<CompanyExchangeRate> companyExchangeRateRoot,
			Join<CompanyExchangeRate, CurrencyMaster> compCurrencyJoin);

	/**
	 * Save CompanyExchangeRate Object.
	 * 
	 * @param companyExchangeRate
	 *            the company exchange rate
	 */
	void save(CompanyExchangeRate companyExchangeRate);

	/**
	 * Update CompanyExchangeRate Object.
	 * 
	 * @param companyExchangeRate
	 *            the company exchange rate
	 */
	void update(CompanyExchangeRate companyExchangeRate);

	/**
	 * Delete CompanyExchangeRate Object.
	 * 
	 * @param companyExchangeRate
	 *            the company exchange rate
	 */
	void delete(CompanyExchangeRate companyExchangeRate);

	List<CompanyExchangeRate> findByCondition(Long companyId, Long currencyId,
			Timestamp currencyDate, PageRequest pageDTO, SortCondition sortDTO);

	Integer getCountForCondition(Long companyId, Long currencyId,
			Timestamp currencyDate);

	CompanyExchangeRate findById(Long companyExchangeRateId);
	
	CompanyExchangeRate findByCompanyExchangeRateId(Long companyExchangeRateId,Long companyId); 

	CompanyExchangeRate findExchangeRate(Date currencyDate, Long currencyId,
			Long companyId);

	CompanyExchangeRate CheckExchangeRate(Date currencyDate, Long currencyId,
			Long companyId, Long companyExchangeRateId);

}
