package com.payasia.dao.bean;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * The persistent class for the Email_Template_Sub_Category_Master database
 * table.
 * 
 */
@Entity
@Table(name = "Email_Template_Sub_Category_Master")
public class EmailTemplateSubCategoryMaster extends BaseEntity implements
		Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "Email_Template_Sub_Category_ID")
	private long emailTemplateSubCategoryId;

	@Column(name = "Sub_Category_Desc")
	private String subCategoryDesc;

	@Column(name = "Sub_Category_Name")
	private String subCategoryName;

	@Column(name = "Label_Key")
	private String labelKey;

	 
	@OneToMany(mappedBy = "emailTemplateSubCategoryMaster")
	private Set<EmailTemplate> emailTemplates;

	 
	@ManyToOne
	@JoinColumn(name = "Email_Template_Category_ID")
	private EmailTemplateCategoryMaster emailTemplateCategoryMaster;

	public EmailTemplateSubCategoryMaster() {
	}

	public long getEmailTemplateSubCategoryId() {
		return this.emailTemplateSubCategoryId;
	}

	public void setEmailTemplateSubCategoryId(long emailTemplateSubCategoryId) {
		this.emailTemplateSubCategoryId = emailTemplateSubCategoryId;
	}

	public String getSubCategoryDesc() {
		return this.subCategoryDesc;
	}

	public void setSubCategoryDesc(String subCategoryDesc) {
		this.subCategoryDesc = subCategoryDesc;
	}

	public String getSubCategoryName() {
		return this.subCategoryName;
	}

	public void setSubCategoryName(String subCategoryName) {
		this.subCategoryName = subCategoryName;
	}

	public Set<EmailTemplate> getEmailTemplates() {
		return this.emailTemplates;
	}

	public void setEmailTemplates(Set<EmailTemplate> emailTemplates) {
		this.emailTemplates = emailTemplates;
	}

	public EmailTemplateCategoryMaster getEmailTemplateCategoryMaster() {
		return this.emailTemplateCategoryMaster;
	}

	public void setEmailTemplateCategoryMaster(
			EmailTemplateCategoryMaster emailTemplateCategoryMaster) {
		this.emailTemplateCategoryMaster = emailTemplateCategoryMaster;
	}

	public String getLabelKey() {
		return labelKey;
	}

	public void setLabelKey(String labelKey) {
		this.labelKey = labelKey;
	}

}