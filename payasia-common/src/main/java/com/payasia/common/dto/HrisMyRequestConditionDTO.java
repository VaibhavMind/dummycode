package com.payasia.common.dto;

import java.io.Serializable;
import java.util.List;

public class HrisMyRequestConditionDTO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long employeeId;
	List<String> hrisStatusNames;
	private String createdDate;
	private String createdBy;
	private String oldValue;
	private String newValue;
	private String field;

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

	public Long getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}

	public List<String> getHrisStatusNames() {
		return hrisStatusNames;
	}

	public void setHrisStatusNames(List<String> hrisStatusNames) {
		this.hrisStatusNames = hrisStatusNames;
	}
	
	
	

}
