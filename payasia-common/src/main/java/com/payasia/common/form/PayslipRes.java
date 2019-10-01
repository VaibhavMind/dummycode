package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

import com.payasia.common.dto.PayslipErrorLog;

public class PayslipRes implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<PayslipErrorLog> errors;

	public List<PayslipErrorLog> getErrors() {
		return errors;
	}

	public void setErrors(List<PayslipErrorLog> errors) {
		this.errors = errors;
	}
	
	

}
