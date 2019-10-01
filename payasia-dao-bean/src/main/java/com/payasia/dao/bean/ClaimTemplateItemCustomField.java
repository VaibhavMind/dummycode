package com.payasia.dao.bean;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * The persistent class for the Claim_Template_Item database table.
 * 
 */
@Entity
@Table(name = "Claim_Template_Item_Custom_Field")
public class ClaimTemplateItemCustomField extends CompanyBaseEntity implements
		Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "Custom_Field_ID")
	private long customFieldId;

	@ManyToOne
	@JoinColumn(name = "Claim_Template_Item_ID")
	private ClaimTemplateItem claimTemplateItem;

	@Column(name = "Field_Name")
	private String fieldName;

	@Column(name = "Mandatory")
	private boolean mandatory;

	@OneToMany(mappedBy = "claimTemplateItemCustomField")
	private Set<ClaimApplicationItemCustomField> claimApplicationItemCustomFields;

	@OneToMany(mappedBy = "claimTemplateItemCustomField", cascade = { CascadeType.REMOVE })
	private Set<ClaimTemplateItemCustomFieldDropDown> claimTemplateItemCustomFieldDropDowns;

	@ManyToOne
	@JoinColumn(name = "Field_Type")
	private AppCodeMaster fieldType;

	public ClaimTemplateItemCustomField() {
	}

	public long getCustomFieldId() {
		return customFieldId;
	}

	public void setCustomFieldId(long customFieldId) {
		this.customFieldId = customFieldId;
	}

	public ClaimTemplateItem getClaimTemplateItem() {
		return claimTemplateItem;
	}

	public void setClaimTemplateItem(ClaimTemplateItem claimTemplateItem) {
		this.claimTemplateItem = claimTemplateItem;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public boolean isMandatory() {
		return mandatory;
	}

	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}

	public Set<ClaimApplicationItemCustomField> getClaimApplicationItemCustomFields() {
		return claimApplicationItemCustomFields;
	}

	public void setClaimApplicationItemCustomFields(
			Set<ClaimApplicationItemCustomField> claimApplicationItemCustomFields) {
		this.claimApplicationItemCustomFields = claimApplicationItemCustomFields;
	}

	public AppCodeMaster getFieldType() {
		return fieldType;
	}

	public void setFieldType(AppCodeMaster fieldType) {
		this.fieldType = fieldType;
	}

	public Set<ClaimTemplateItemCustomFieldDropDown> getClaimTemplateItemCustomFieldDropDowns() {
		return claimTemplateItemCustomFieldDropDowns;
	}

	public void setClaimTemplateItemCustomFieldDropDowns(
			Set<ClaimTemplateItemCustomFieldDropDown> claimTemplateItemCustomFieldDropDowns) {
		this.claimTemplateItemCustomFieldDropDowns = claimTemplateItemCustomFieldDropDowns;
	}

}