package com.payasia.common.dto;

import java.io.Serializable;

public class ExcelImportExportConditionDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1983672501759561070L;
	private Long entityId;
	private String searchString;
	private String descriptionSearchString;
	private String scopeSearchString;
	
	
	
	public String getScopeSearchString() {
		return scopeSearchString;
	}
	public void setScopeSearchString(String scopeSearchString) {
		this.scopeSearchString = scopeSearchString;
	}
	public Long getEntityId() {
		return entityId;
	}
	public void setEntityId(Long entityId) {
		this.entityId = entityId;
	}
	public String getSearchString() {
		return searchString;
	}
	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}
	public String getDescriptionSearchString() {
		return descriptionSearchString;
	}
	public void setDescriptionSearchString(String descriptionSearchString) {
		this.descriptionSearchString = descriptionSearchString;
	}
	
	
	
}
