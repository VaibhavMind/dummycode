package com.payasia.common.dto;

import java.util.List;

public class HrisPendingItemsConditionDTO {
	
	private Long employeeId;
	
	private String createdDate;
	private String createdBy;
	private String oldValue;
	private String newValue;
	private String field;
	
	private List<String> hrisStatusNames;
	
	public Long getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getOldValue() {
		return oldValue;
	}

	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}

	public String getNewValue() {
		return newValue;
	}

	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public List<String> getHrisStatusNames() {
		return hrisStatusNames;
	}

	public void setHrisStatusNames(List<String> hrisStatusNames) {
		this.hrisStatusNames = hrisStatusNames;
	}
	
	

}
