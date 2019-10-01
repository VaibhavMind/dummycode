package com.payasia.dao;

import java.util.List;

import com.payasia.dao.bean.StateMaster;

/**
 * The Interface StateMasterDAO.
 */
/**
 * @author vivekjain
 * 
 */
public interface StateMasterDAO {

	/**
	 * Find all StateMaster Objects list.
	 * 
	 * @return the list
	 */
	List<StateMaster> findAll();

	/**
	 * Find StateMaster Object by stateId.
	 * 
	 * @param stateId
	 *            the state id
	 * @return the state master
	 */
	StateMaster findById(long stateId);

	/**
	 * Find StateMaster Objects List by countryId.
	 * 
	 * @param countryId
	 *            the country id
	 * @return the list
	 */
	List<StateMaster> findByCountry(Long countryId);

	/**
	 * Save StateMaster Object.
	 * 
	 * @param stateMaster
	 *            the state master
	 */
	void save(StateMaster stateMaster);

	/**
	 * Save and persist StateMaster Object and returns a generated identity.
	 * 
	 * @param stateMaster
	 *            the state master
	 * @return the state master
	 */
	StateMaster saveAndPersist(StateMaster stateMaster);
}
