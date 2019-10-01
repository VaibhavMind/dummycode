package com.payasia.common.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public class FirstMonthCustomDTO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5545341931827445155L;
	private Long customProrationId;
	private Integer fromRange;
	private Integer toRange;
	private BigDecimal value;
	
	public Long getCustomProrationId() {
		return customProrationId;
	}
	public void setCustomProrationId(Long customProrationId) {
		this.customProrationId = customProrationId;
	}
	public Integer getFromRange() {
		return fromRange;
	}
	public void setFromRange(Integer fromRange) {
		this.fromRange = fromRange;
	}
	public Integer getToRange() {
		return toRange;
	}
	public void setToRange(Integer toRange) {
		this.toRange = toRange;
	}
	public BigDecimal getValue() {
		return value;
	}
	public void setValue(BigDecimal value) {
		this.value = value;
	}

	
}
