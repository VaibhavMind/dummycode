package com.payasia.common.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public class CustomRoundingDTO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7611425944891065977L;
	private Long customRoundingId;
	private BigDecimal fromRange;
	private BigDecimal toRange;
	private BigDecimal value;
	
	public BigDecimal getFromRange() {
		return fromRange;
	}
	public void setFromRange(BigDecimal fromRange) {
		this.fromRange = fromRange;
	}
	public BigDecimal getToRange() {
		return toRange;
	}
	public void setToRange(BigDecimal toRange) {
		this.toRange = toRange;
	}
	public BigDecimal getValue() {
		return value;
	}
	public void setValue(BigDecimal value) {
		this.value = value;
	}
	public Long getCustomRoundingId() {
		return customRoundingId;
	}
	public void setCustomRoundingId(Long customRoundingId) {
		this.customRoundingId = customRoundingId;
	}
	
	
	
	

}
