package com.payasia.common.dto;

import java.io.Serializable;

public class LeaveReportCustomDataDTO implements Serializable {
	private static final long serialVersionUID = 6083504707284690235L;
	private String customFieldHeaderName ; 
	private String customFieldKeyName ;
	private String customFieldValueName ;
	public String getCustomFieldHeaderName() {
		return customFieldHeaderName;
	}
	public void setCustomFieldHeaderName(String customFieldHeaderName) {
		this.customFieldHeaderName = customFieldHeaderName;
	}
	public String getCustomFieldKeyName() {
		return customFieldKeyName;
	}
	public void setCustomFieldKeyName(String customFieldKeyName) {
		this.customFieldKeyName = customFieldKeyName;
	}
	public String getCustomFieldValueName() {
		return customFieldValueName;
	}
	public void setCustomFieldValueName(String customFieldValueName) {
		this.customFieldValueName = customFieldValueName;
	}

}
