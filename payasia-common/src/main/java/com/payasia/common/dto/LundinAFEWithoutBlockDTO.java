package com.payasia.common.dto;


public class LundinAFEWithoutBlockDTO {
	private long afeId;

	public long getAfeId() {
		return afeId;
	}

	public void setAfeId(long afeId) {
		this.afeId = afeId;
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

	public String getAfeCode() {
		return afeCode;
	}

	public void setAfeCode(String afeCode) {
		this.afeCode = afeCode;
	}

	public String getAfeName() {
		return afeName;
	}

	public void setAfeName(String afeName) {
		this.afeName = afeName;
	}

	
	private String effectiveDate;
	
	private boolean status;

	private String afeCode;

	private String afeName;

}
