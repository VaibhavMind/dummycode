package com.payasia.dao.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * The persistent class for the Claim_Application_Item_Workflow database table.
 * 
 */
@Entity
@Table(name = "Claim_Application_Item_Custom_Field")
public class ClaimApplicationItemCustomField extends CompanyBaseEntity
		implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Claim_Item_Custom_Field_ID")
	private long claimItemCustomFieldId;

	 
	@ManyToOne
	@JoinColumn(name = "Claim_Application_Item_ID")
	private ClaimApplicationItem claimApplicationItem;

	 
	@ManyToOne
	@JoinColumn(name = "Custom_Field_ID")
	private ClaimTemplateItemCustomField claimTemplateItemCustomField;

	@Column(name = "Value")
	private String value;

	public ClaimApplicationItemCustomField() {
	}

	public long getClaimItemCustomFieldId() {
		return claimItemCustomFieldId;
	}

	public void setClaimItemCustomFieldId(long claimItemCustomFieldId) {
		this.claimItemCustomFieldId = claimItemCustomFieldId;
	}

	public ClaimApplicationItem getClaimApplicationItem() {
		return claimApplicationItem;
	}

	public void setClaimApplicationItem(
			ClaimApplicationItem claimApplicationItem) {
		this.claimApplicationItem = claimApplicationItem;
	}

	public ClaimTemplateItemCustomField getClaimTemplateItemCustomField() {
		return claimTemplateItemCustomField;
	}

	public void setClaimTemplateItemCustomField(
			ClaimTemplateItemCustomField claimTemplateItemCustomField) {
		this.claimTemplateItemCustomField = claimTemplateItemCustomField;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}