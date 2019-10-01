package com.payasia.dao;

import java.util.List;

import com.payasia.dao.bean.DocumentCategoryMaster;

/**
 * The Interface DocumentCategoryMasterDAO.
 */
/**
 * @author vivekjain
 * 
 */
public interface DocumentCategoryMasterDAO {

	/**
	 * purpose : Find all DocumentCategoryMaster Objects List.
	 * 
	 * @return the DocumentCategoryMaster list
	 */
	List<DocumentCategoryMaster> findAll();

	/**
	 * purpose : Find DocumentCategoryMaster Object by condition entityName.
	 * 
	 * @param entityName
	 *            the entity name
	 * @return the document category master
	 */
	DocumentCategoryMaster findByCondition(String entityName);

	/**
	 * purpose : Find DocumentCategoryMaster Object by docId.
	 * 
	 * @param docId
	 *            the doc id
	 * @return the document category master
	 */
	DocumentCategoryMaster findById(long docId);

}
