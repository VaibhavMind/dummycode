package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

public class PayDataCollectionResponseForm extends PageResponse implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6864945006564699598L;
	private List<PayDataCollectionForm> payDataCollectionForm;

	public List<PayDataCollectionForm> getPayDataCollectionForm() {
		return payDataCollectionForm;
	}

	public void setPayDataCollectionForm(List<PayDataCollectionForm> payDataCollectionForm) {
		this.payDataCollectionForm = payDataCollectionForm;
	}

	
}
