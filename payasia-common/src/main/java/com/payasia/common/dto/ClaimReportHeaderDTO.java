package com.payasia.common.dto;

import java.io.Serializable;

public class ClaimReportHeaderDTO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
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
	@Override
	public boolean equals(Object obj) {
		if(obj !=null && obj instanceof ClaimReportHeaderDTO){
			ClaimReportHeaderDTO comp=(ClaimReportHeaderDTO)obj;
			if(comp.mDataProp!=null && comp.mDataProp.equals(this.mDataProp)){
				if(comp.sTitle!=null && comp.sTitle.equals(this.sTitle)){
					return true;
				}
				
			}
			
		}
		return false;
	}
	
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + ((int) (this.mDataProp.length() ^ (this.mDataProp.length() >>> 29)));
		hash = hash * prime + ((int) (this.sTitle.length() ^ (this.sTitle.length() >>> 29)));
		
		
		return hash;
    }

}
