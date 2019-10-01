package com.payasia.dao.bean;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * The persistent class for the Email_Template_Category_Master database table.
 * 
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Table(name = "Email_Template_Category_Master")
public class EmailTemplateCategoryMaster extends BaseEntity implements
		Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "Email_Template_Category_ID")
	private long emailTemplateCategoryId;

	@Column(name = "Category_Desc")
	private String categoryDesc;

	@Column(name = "Category_Name")
	private String categoryName;

	@Column(name = "Label_Key")
	private String labelKey;

	 
	@OneToMany(mappedBy = "emailTemplateCategoryMaster")
	private Set<EmailTemplateSubCategoryMaster> emailTemplateSubCategoryMasters;

	public EmailTemplateCategoryMaster() {
	}

	public long getEmailTemplateCategoryId() {
		return this.emailTemplateCategoryId;
	}

	public void setEmailTemplateCategoryId(long emailTemplateCategoryId) {
		this.emailTemplateCategoryId = emailTemplateCategoryId;
	}

	public String getCategoryDesc() {
		return this.categoryDesc;
	}

	public void setCategoryDesc(String categoryDesc) {
		this.categoryDesc = categoryDesc;
	}

	public String getCategoryName() {
		return this.categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public Set<EmailTemplateSubCategoryMaster> getEmailTemplateSubCategoryMasters() {
		return this.emailTemplateSubCategoryMasters;
	}

	public void setEmailTemplateSubCategoryMasters(
			Set<EmailTemplateSubCategoryMaster> emailTemplateSubCategoryMasters) {
		this.emailTemplateSubCategoryMasters = emailTemplateSubCategoryMasters;
	}

	public String getLabelKey() {
		return labelKey;
	}

	public void setLabelKey(String labelKey) {
		this.labelKey = labelKey;
	}

}