package com.payasia.dao;

import java.util.List;

import com.payasia.dao.bean.EmailTemplateSubCategoryMaster;

/**
 * The Interface EmailTemplateSubCategoryMasterDAO.
 */
/**
 * @author vivekjain
 * 
 */
public interface EmailTemplateSubCategoryMasterDAO {

	/**
	 * purpose : Find EmailTemplateSubCategoryMaster Object by subcategoryId.
	 * 
	 * @param subcategoryId
	 *            the subcategory id
	 * @return the email template sub category master
	 */
	EmailTemplateSubCategoryMaster findbyId(long subcategoryId);

	/**
	 * purpose : Gets EmailTemplateSubCategoryMaster Objects List by
	 * emailTemplateCategoryId.
	 * 
	 * @param emailTemplateCategoryId
	 *            the email template category id
	 * @return the sub category list
	 */
	List<EmailTemplateSubCategoryMaster> getSubCategoryList(
			Long emailTemplateCategoryId);

	/**
	 * purpose : Find all EmailTemplateSubCategoryMaster Objects List.
	 * 
	 * @return the list
	 */
	List<EmailTemplateSubCategoryMaster> findAll();
	
	

}
