package com.payasia.common.dto;

import java.io.Serializable;
import java.util.Map;

public class LundinDepartmentDTO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -845201697727964211L;

	private long departmentId;

	private String effectiveDate;

	private boolean status;

	private String departmentCode;

	private String departmentName;
	
	private long defaultBlockId;
	private long defaultAFEId;

	public long getDynamicFieldId() {
		return dynamicFieldId;
	}

	public void setDynamicFieldId(long dynamicFieldId) {
		this.dynamicFieldId = dynamicFieldId;
	}

	private String departmentType;
	private long dynamicFieldId;
	
	private int order;
	
	private long selectedValue;
	
	private Map<Long,String> appCodeMaster;

	private long departmentTypeId;

	public Map<Long, String> getAppCodeMaster() {
		
		return appCodeMaster;
	}

	public void setAppCodeMaster(Map<Long, String> appCodeMaster) {
		this.appCodeMaster = appCodeMaster;
	}

	
	public long getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(long departmentId) {
		this.departmentId = departmentId;
	}

	public String getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(String effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public String getDepartmentCode() {
		return departmentCode;
	}

	public void setDepartmentCode(String departmentCode) {
		this.departmentCode = departmentCode;
	}

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	public String getDepartmentType() {
		return departmentType;
	}

	public void setDepartmentType(String departmentType) {
		this.departmentType = departmentType;
	}


	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public long getDepartmentTypeId() {
		return departmentTypeId;
	}

	public void setDepartmentTypeId(long departmentTypeId) {
		this.departmentTypeId = departmentTypeId;
	}

	public long getSelectedValue() {
		return selectedValue;
	}

	public void setSelectedValue(long selectedValue) {
		this.selectedValue = selectedValue;
	}

	public long getDefaultBlockId() {
		return defaultBlockId;
	}

	public void setDefaultBlockId(long defaultBlockId) {
		this.defaultBlockId = defaultBlockId;
	}

	public long getDefaultAFEId() {
		return defaultAFEId;
	}

	public void setDefaultAFEId(long defaultAFEId) {
		this.defaultAFEId = defaultAFEId;
	}

}
