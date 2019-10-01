package com.payasia.common.dto;

import java.io.Serializable;

public class CurrencyWSDTO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6296636194637805681L;
	private String currency;
	private Long currencyId;
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public Long getCurrencyId() {
		return currencyId;
	}
	public void setCurrencyId(Long currencyId) {
		this.currencyId = currencyId;
	}
	
	

}
