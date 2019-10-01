package com.payasia.common.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PayslipFrequencyDTO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1850740810903142832L;

	private long payslipFrequencyID;

	private String frequency;

	private String frequency_Desc;

	public long getPayslipFrequencyID() {
		return payslipFrequencyID;
	}

	public void setPayslipFrequencyID(long payslipFrequencyID) {
		this.payslipFrequencyID = payslipFrequencyID;
	}

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public String getFrequency_Desc() {
		return frequency_Desc;
	}

	public void setFrequency_Desc(String frequency_Desc) {
		this.frequency_Desc = frequency_Desc;
	}
	
	
}
