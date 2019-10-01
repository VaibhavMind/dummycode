package com.payasia.dao;

import java.util.List;

import com.payasia.dao.bean.EmailTemplateCategoryMaster;

/**
 * @author vivekjain
 * 
 */
/**
 * The Interface EmailTemplateCategoryMasterDAO.
 */
public interface EmailTemplateCategoryMasterDAO {

	/**
	 * Find all EmailTemplateCategoryMaster Objects List.
	 * 
	 * @return the list
	 */
	List<EmailTemplateCategoryMaster> findAll();

	/**
	 * Find EmailTemplateCategoryMaster Object by categoryId.
	 * 
	 * @param categoryId
	 *            the category id
	 * @return the email template category master
	 */
	EmailTemplateCategoryMaster findbyId(long categoryId);

}
