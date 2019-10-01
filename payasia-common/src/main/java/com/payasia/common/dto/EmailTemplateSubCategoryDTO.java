package com.payasia.common.dto;

import java.io.Serializable;


/**
 * The Class EmailTemplateCategoryDTO.
 */
public class EmailTemplateSubCategoryDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7176097202332933942L;

	/** The email template category id. */
	private long emailTemplateSubCategoryId;

	/** The category desc. */
	private String subCategoryDesc;

	/** The category name. */
	private String subCategoryName;

	public long getEmailTemplateSubCategoryId() {
		return emailTemplateSubCategoryId;
	}

	public void setEmailTemplateSubCategoryId(long emailTemplateSubCategoryId) {
		this.emailTemplateSubCategoryId = emailTemplateSubCategoryId;
	}

	public String getSubCategoryDesc() {
		return subCategoryDesc;
	}

	public void setSubCategoryDesc(String subCategoryDesc) {
		this.subCategoryDesc = subCategoryDesc;
	}

	public String getSubCategoryName() {
		return subCategoryName;
	}

	public void setSubCategoryName(String subCategoryName) {
		this.subCategoryName = subCategoryName;
	}
	

	
	
	
}
