package com.payasia.common.form;

import java.io.Serializable;

public class DynamicTabPrivilgeForm implements Serializable{

	private static final long serialVersionUID = 1952142223438974145L;
	private String tabName;
	private Long tabId;
	
	public String getTabName() {
		return tabName;
	}
	public void setTabName(String tabName) {
		this.tabName = tabName;
	}
	public Long getTabId() {
		return tabId;
	}
	public void setTabId(Long tabId) {
		this.tabId = tabId;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
