package com.payasia.common.form;

import java.io.Serializable;

public class EmpSwitchUserForm implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -528888560477365177L;
	private String switchTo;
	private String userType;
	private String fromDate;
	private String toDate;

	public String getSwitchTo() {
		return switchTo;
	}

	public void setSwitchTo(String switchTo) {
		this.switchTo = switchTo;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getFromDate() {
		return fromDate;
	}

	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}

	public String getToDate() {
		return toDate;
	}

	public void setToDate(String toDate) {
		this.toDate = toDate;
	}

}
