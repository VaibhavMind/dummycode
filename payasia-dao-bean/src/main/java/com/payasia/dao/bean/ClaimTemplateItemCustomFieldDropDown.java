package com.payasia.dao.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * The persistent class for the Claim_Template_Item_Custom_Field_DropDown
 * database table.
 * 
 */
@Entity
@Table(name = "Claim_Template_Item_Custom_Field_DropDown")
public class ClaimTemplateItemCustomFieldDropDown extends CompanyBaseEntity
		implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "Custom_Field_DropDown_ID")
	private long customFieldDropDownId;

	@ManyToOne
	@JoinColumn(name = "Custom_Field_ID")
	private ClaimTemplateItemCustomField claimTemplateItemCustomField;

	@Column(name = "Field_Value")
	private String fieldValue;

	public ClaimTemplateItemCustomFieldDropDown() {
	}

	public long getCustomFieldDropDownId() {
		return customFieldDropDownId;
	}

	public void setCustomFieldDropDownId(long customFieldDropDownId) {
		this.customFieldDropDownId = customFieldDropDownId;
	}

	public ClaimTemplateItemCustomField getClaimTemplateItemCustomField() {
		return claimTemplateItemCustomField;
	}

	public void setClaimTemplateItemCustomField(
			ClaimTemplateItemCustomField claimTemplateItemCustomField) {
		this.claimTemplateItemCustomField = claimTemplateItemCustomField;
	}

	public String getFieldValue() {
		return fieldValue;
	}

	public void setFieldValue(String fieldValue) {
		this.fieldValue = fieldValue;
	}

}