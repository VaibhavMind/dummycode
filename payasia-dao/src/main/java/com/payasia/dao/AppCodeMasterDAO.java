package com.payasia.dao;

import java.util.List;

import com.payasia.dao.bean.AppCodeMaster;

/**
 * The Interface AppCodeMasterDAO.
 *
 * @author vivekjain
 */
/**
 * The Interface AppCodeMasterDAO.
 */
public interface AppCodeMasterDAO {

	/**
	 * purpose : find By Condition category.
	 * 
	 * @param category
	 *            the category
	 * @return AppCodeMaster List
	 */
	List<AppCodeMaster> findByCondition(String category);

	/**
	 * purpose : find By appCodeID.
	 * 
	 * @param appCodeID
	 *            the app code id
	 * @return AppCodeMaster Entity Bean
	 */
	AppCodeMaster findById(long appCodeID);

	/**
	 * purpose : find By Condition category and Code value.
	 * 
	 * @param category
	 *            the category
	 * @param value
	 *            the value
	 * @return AppCodeMaster List
	 */
	AppCodeMaster findByCondition(String category, String value);

	/**
	 * Find by category and desc.
	 * 
	 * @param category
	 *            the category
	 * @param desc
	 *            the desc
	 * @return the app code master
	 */
	AppCodeMaster findByCategoryAndDesc(String category, String desc);

	/**
	 * Find by condition pending items.
	 * 
	 * @param category
	 *            the category
	 * @return the list
	 */
	List<AppCodeMaster> findByConditionPendingItems(String category);

	List<AppCodeMaster> findByConditionWorkFlowDelegate(String category);
}
