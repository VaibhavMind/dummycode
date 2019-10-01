package com.payasia.common.dto;

import java.io.Serializable;
import java.util.List;


/**
 * The Class EmailTemplateCategoryDTO.
 */
public class EmailTemplateCategoryDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5531785702462819890L;

	/** The email template category id. */
	private long emailTemplateCategoryId;

	/** The category desc. */
	private String categoryDesc;

	/** The category name. */
	private String categoryName;
	
	private List<EmailTemplateSubCategoryDTO> EmailTemplateSubCategoryDTO;

	/**
	 * Gets the email template category id.
	 *
	 * @return the email template category id
	 */
	public long getEmailTemplateCategoryId() {
		return emailTemplateCategoryId;
	}

	/**
	 * Sets the email template category id.
	 *
	 * @param emailTemplateCategoryId the new email template category id
	 */
	public void setEmailTemplateCategoryId(long emailTemplateCategoryId) {
		this.emailTemplateCategoryId = emailTemplateCategoryId;
	}

	/**
	 * Gets the category desc.
	 *
	 * @return the category desc
	 */
	public String getCategoryDesc() {
		return categoryDesc;
	}

	/**
	 * Sets the category desc.
	 *
	 * @param categoryDesc the new category desc
	 */
	public void setCategoryDesc(String categoryDesc) {
		this.categoryDesc = categoryDesc;
	}

	/**
	 * Gets the category name.
	 *
	 * @return the category name
	 */
	public String getCategoryName() {
		return categoryName;
	}

	/**
	 * Sets the category name.
	 *
	 * @param categoryName the new category name
	 */
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public List<EmailTemplateSubCategoryDTO> getEmailTemplateSubCategoryDTO() {
		return EmailTemplateSubCategoryDTO;
	}

	public void setEmailTemplateSubCategoryDTO(
			List<EmailTemplateSubCategoryDTO> emailTemplateSubCategoryDTO) {
		EmailTemplateSubCategoryDTO = emailTemplateSubCategoryDTO;
	}
	
	
}
