package com.payasia.common.dto;

import java.io.Serializable;

public class WorkdayFieldMasterDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private long workdayFieldId;
	
	private String sectionName;

	private String fieldName;
	
	private String fieldDesc;
	
	private String entityName;
	
	private boolean isMandatory;
	
	private boolean isCommon;
	
	private boolean isActive;
	
	private boolean isDisplayable;

	public long getWorkdayFieldId() {
		return workdayFieldId;
	}

	public void setWorkdayFieldId(long workdayFieldId) {
		this.workdayFieldId = workdayFieldId;
	}

	public String getSectionName() {
		return sectionName;
	}

	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getFieldDesc() {
		return fieldDesc;
	}

	public void setFieldDesc(String fieldDesc) {
		this.fieldDesc = fieldDesc;
	}

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	public boolean isMandatory() {
		return isMandatory;
	}

	public void setMandatory(boolean isMandatory) {
		this.isMandatory = isMandatory;
	}

	public boolean isCommon() {
		return isCommon;
	}

	public void setCommon(boolean isCommon) {
		this.isCommon = isCommon;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public boolean isDisplayable() {
		return isDisplayable;
	}

	public void setDisplayable(boolean isDisplayable) {
		this.isDisplayable = isDisplayable;
	}
}
