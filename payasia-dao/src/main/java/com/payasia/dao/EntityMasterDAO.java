package com.payasia.dao;

import java.util.List;

import com.payasia.dao.bean.EntityMaster;

/**
 * The Interface EntityMasterDAO.
 */
/**
 * @author vivekjain
 * 
 */
public interface EntityMasterDAO {

	/**
	 * Find all EntityMaster Objects List.
	 * 
	 * @return the list
	 */
	List<EntityMaster> findAll();

	/**
	 * Find EntityMaster Object by entityMasterId.
	 * 
	 * @param entityMasterId
	 *            the entity master id
	 * @return the entity master
	 */
	EntityMaster findById(long entityMasterId);

	/**
	 * Save EntityMaster Object.
	 * 
	 * @param entityMaster
	 *            the entity master
	 */
	void save(EntityMaster entityMaster);

	/**
	 * Find EntityMaster Object by entity name.
	 * 
	 * @param entityName
	 *            the entity name
	 * @return the entity master
	 */
	EntityMaster findByEntityName(String entityName);
}
