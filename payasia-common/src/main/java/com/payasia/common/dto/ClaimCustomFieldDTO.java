package com.payasia.common.dto;

import java.io.Serializable;

public class ClaimCustomFieldDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4297007088531991325L;
	
	
	private Long customFieldId;
	private Long claimApplicationItemCustomFieldId;
	private Long customFieldTypeId;
	private String customFieldName;
	private Boolean customFieldMandatory;
	private String value;
	
	
	public Long getClaimApplicationItemCustomFieldId() {
		return claimApplicationItemCustomFieldId;
	}
	public void setClaimApplicationItemCustomFieldId(
			Long claimApplicationItemCustomFieldId) {
		this.claimApplicationItemCustomFieldId = claimApplicationItemCustomFieldId;
	}
	public Long getCustomFieldId() {
		return customFieldId;
	}
	public void setCustomFieldId(Long customFieldId) {
		this.customFieldId = customFieldId;
	}
	public Long getCustomFieldTypeId() {
		return customFieldTypeId;
	}
	public void setCustomFieldTypeId(Long customFieldTypeId) {
		this.customFieldTypeId = customFieldTypeId;
	}
	public String getCustomFieldName() {
		return customFieldName;
	}
	public void setCustomFieldName(String customFieldName) {
		this.customFieldName = customFieldName;
	}
	public Boolean getCustomFieldMandatory() {
		return customFieldMandatory;
	}
	public void setCustomFieldMandatory(Boolean customFieldMandatory) {
		this.customFieldMandatory = customFieldMandatory;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	

}
