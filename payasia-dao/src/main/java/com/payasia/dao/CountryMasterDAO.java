package com.payasia.dao;

import java.util.List;

import com.payasia.dao.bean.CountryMaster;

/**
 * The Interface CountryMasterDAO.
 */
/**
 * @author vivekjain
 * 
 */
public interface CountryMasterDAO {

	/**
	 * purpose : Find CountryMaster Objects List all CountryMaster.
	 * 
	 * @return the list
	 */
	List<CountryMaster> findAll();

	/**
	 * purpose : Find CountryMaster Object by countryId.
	 * 
	 * @param countryId
	 *            the country id
	 * @return the country master
	 */
	CountryMaster findById(long countryId);

	/**
	 * purpose : Save CountryMaster Object.
	 * 
	 * @param countryMaster
	 *            the country master
	 */
	void save(CountryMaster countryMaster);

	/**
	 * purpose : Save and persist CountryMaster Object and Returns a Generated
	 * Identity.
	 * 
	 * @param countryMaster
	 *            the country master
	 * @return the country master
	 */
	CountryMaster saveAndPersist(CountryMaster countryMaster);

	/**
	 * purpose : Find CountryMaster Object by countryName.
	 * 
	 * @param countryName
	 *            the country name
	 * @return the country master
	 */
	CountryMaster findByName(String countryName);
}
