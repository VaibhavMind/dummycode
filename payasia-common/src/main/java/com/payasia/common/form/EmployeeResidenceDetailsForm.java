package com.payasia.common.form;

import java.io.Serializable;

public class EmployeeResidenceDetailsForm implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5877635730775389826L;
	private String identityType;
	private String identityNumber;
	private String residenceStatus;
	private String fwlClass;
	private String effectiveFrom;

	public String getIdentityType() {
		return identityType;
	}

	public void setIdentityType(String identityType) {
		this.identityType = identityType;
	}

	public String getIdentityNumber() {
		return identityNumber;
	}

	public void setIdentityNumber(String identityNumber) {
		this.identityNumber = identityNumber;
	}

	public String getResidenceStatus() {
		return residenceStatus;
	}

	public void setResidenceStatus(String residenceStatus) {
		this.residenceStatus = residenceStatus;
	}

	public String getFwlClass() {
		return fwlClass;
	}

	public void setFwlClass(String fwlClass) {
		this.fwlClass = fwlClass;
	}

	public String getEffectiveFrom() {
		return effectiveFrom;
	}

	public void setEffectiveFrom(String effectiveFrom) {
		this.effectiveFrom = effectiveFrom;
	}

}
