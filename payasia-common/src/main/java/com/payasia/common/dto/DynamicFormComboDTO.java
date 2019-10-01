package com.payasia.common.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Set;

public class DynamicFormComboDTO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7334570926327227491L;
	private Long entityId;
	private Long formId;
	private Boolean isTableDropDown;
	private Long companyId;
	private Integer maxVersion;
	private HashMap<String, String> comboValue;
	private String custFieldName;
	private Set<String> dropDownValues;
	
	
	
	
	
	
	
	
	
	
	public String getCustFieldName() {
		return custFieldName;
	}
	public void setCustFieldName(String custFieldName) {
		this.custFieldName = custFieldName;
	}
	public Set<String> getDropDownValues() {
		return dropDownValues;
	}
	public void setDropDownValues(Set<String> dropDownValues) {
		this.dropDownValues = dropDownValues;
	}
	public HashMap<String, String> getComboValue() {
		return comboValue;
	}
	public void setComboValue(HashMap<String, String> comboValue) {
		this.comboValue = comboValue;
	}
	public Long getEntityId() {
		return entityId;
	}
	public void setEntityId(Long entityId) {
		this.entityId = entityId;
	}
	public Long getFormId() {
		return formId;
	}
	public void setFormId(Long formId) {
		this.formId = formId;
	}
	public Boolean getIsTableDropDown() {
		return isTableDropDown;
	}
	public void setIsTableDropDown(Boolean isTableDropDown) {
		this.isTableDropDown = isTableDropDown;
	}
	public Long getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
	public Integer getMaxVersion() {
		return maxVersion;
	}
	public void setMaxVersion(Integer maxVersion) {
		this.maxVersion = maxVersion;
	}
	
	
	

}
