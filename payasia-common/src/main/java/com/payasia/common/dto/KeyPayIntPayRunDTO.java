package com.payasia.common.dto;

import java.io.Serializable;
import java.sql.Timestamp;

public class KeyPayIntPayRunDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6577742005459831739L;
	private Long payRunId;
	private Timestamp dateFinalised;
	private Timestamp payPeriodStarting;
	private Timestamp payPeriodEnding;
	private Timestamp payRunParameterDate;
	
	
	public Timestamp getDateFinalised() {
		return dateFinalised;
	}
	public void setDateFinalised(Timestamp dateFinalised) {
		this.dateFinalised = dateFinalised;
	}
	public Timestamp getPayPeriodStarting() {
		return payPeriodStarting;
	}
	public void setPayPeriodStarting(Timestamp payPeriodStarting) {
		this.payPeriodStarting = payPeriodStarting;
	}
	public Timestamp getPayPeriodEnding() {
		return payPeriodEnding;
	}
	public void setPayPeriodEnding(Timestamp payPeriodEnding) {
		this.payPeriodEnding = payPeriodEnding;
	}
	public Long getPayRunId() {
		return payRunId;
	}
	public void setPayRunId(Long payRunId) {
		this.payRunId = payRunId;
	}
	public Timestamp getPayRunParameterDate() {
		return payRunParameterDate;
	}
	public void setPayRunParameterDate(Timestamp payRunParameterDate) {
		this.payRunParameterDate = payRunParameterDate;
	}
	
	

}
