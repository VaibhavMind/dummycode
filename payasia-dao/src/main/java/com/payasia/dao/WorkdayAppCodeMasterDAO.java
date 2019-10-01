package com.payasia.dao;

import java.util.List;

import com.payasia.dao.bean.WorkdayAppCodeMaster;

/**
 * The Interface WorkdayAppCodeMasterDAO.
 *
 * @author peeyushpratap
 */

/**
 * The Interface WorkdayAppCodeMasterDAO.
 */
public interface WorkdayAppCodeMasterDAO {

	/**
	 * purpose : find By Condition category.
	 * 
	 * @param category
	 *            the category
	 * @return WorkdayAppCodeMaster List
	 */
	List<WorkdayAppCodeMaster> findByCategory(String category);

	/**
	 * purpose : find By appCodeID.
	 * 
	 * @param appCodeID
	 *            the app code id
	 * @return WorkdayAppCodeMaster Entity Bean
	 */
	WorkdayAppCodeMaster findById(long appCodeID);

	/**
	 * purpose : find By Condition category and Code value.
	 * 
	 * @param category
	 *            the category
	 * @param value
	 *            the value
	 * @return WorkdayAppCodeMaster List
	 */
	WorkdayAppCodeMaster findByCondition(String category, String value);

	/**
	 * Find by category and desc.
	 * 
	 * @param category
	 *            the category
	 * @param desc
	 *            the desc
	 * @return the app code master
	 */
	WorkdayAppCodeMaster findByCategoryAndDesc(String category, String desc);

	/**
	 * 
	 * @param category
	 * @param value
	 * @param countryId
	 * @return
	 */
	WorkdayAppCodeMaster findByConditionAndCountry(String category, String value, long countryId);
	
	/**
	 * 
	 * @param countryId
	 * @return
	 */
	List<WorkdayAppCodeMaster> findByCountryId(Long countryId);

	/**
	 * 
	 * @param batchTypeId
	 * @return
	 */
	WorkdayAppCodeMaster findPaygroupTypeByBatchType(Long batchTypeId);

	/**
	 * 
	 * @param paygroupTypeId
	 * @return
	 */
	WorkdayAppCodeMaster findBatchTypeByPaygroupType(Long paygroupTypeId);


}
