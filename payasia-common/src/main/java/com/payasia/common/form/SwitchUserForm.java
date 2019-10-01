package com.payasia.common.form;

import java.io.Serializable;

public class SwitchUserForm implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6072315589345635962L;
	private long configId;
	private long userID;
	private String user;
	private String switchUser;
	private long switchUserID;
	private String fromDate;
	private String toDate;

	public long getConfigId() {
		return configId;
	}

	public void setConfigId(long configId) {
		this.configId = configId;
	}

	public long getUserID() {
		return userID;
	}

	public void setUserID(long userID) {
		this.userID = userID;
	}

	public long getSwitchUserID() {
		return switchUserID;
	}

	public void setSwitchUserID(long switchUserID) {
		this.switchUserID = switchUserID;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getSwitchUser() {
		return switchUser;
	}

	public void setSwitchUser(String switchUser) {
		this.switchUser = switchUser;
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
