package com.payasia.common.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public class ForexRateResponse implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5367535586784882157L;
	private BigDecimal forexRate;

	public BigDecimal getForexRate() {
		return forexRate;
	}

	public void setForexRate(BigDecimal forexRate) {
		this.forexRate = forexRate;
	}
	
	

}
