package com.payasia.dao;

import java.util.List;

import com.payasia.dao.bean.CurrencyMaster;

/**
 * The Interface CurrencyMasterDAO.
 */
/**
 * @author vivekjain
 * 
 */
public interface CurrencyMasterDAO {

	/**
	 * purpose : Save CurrencyMaster Object.
	 * 
	 * @param currencyMaster
	 *            the currency master
	 */
	void save(CurrencyMaster currencyMaster);

	/**
	 * purpose : Find CurrencyMaster Object by currencyId.
	 * 
	 * @param currencyId
	 *            the currency id
	 * @return the currency master
	 */
	CurrencyMaster findById(Long currencyId);

	/**
	 * purpose : Find CurrencyMaster Objects List.
	 * 
	 * @return the list
	 */
	List<CurrencyMaster> findAll();

	CurrencyMaster findByCurrencyCode(String code);
}
