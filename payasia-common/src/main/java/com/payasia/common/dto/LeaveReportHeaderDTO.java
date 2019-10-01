package com.payasia.common.dto;

import java.io.Serializable;

public class LeaveReportHeaderDTO implements Serializable {
	
	private String mDataProp;
	private String sClass;
	private String sTitle;
	
	
	public String getsTitle() {
		return sTitle;
	}
	public void setsTitle(String sTitle) {
		this.sTitle = sTitle;
	}
	public String getsClass() {
		return sClass;
	}
	public void setsClass(String sClass) {
		this.sClass = sClass;
	}
	public String getmDataProp() {
		return mDataProp;
	}
	public void setmDataProp(String mDataProp) {
		this.mDataProp = mDataProp;
	}
	
	

}
