package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

public class PayCodeDataFormResponse extends PageResponse implements
		Serializable {

	private static final long serialVersionUID = 1L;
	private List<PayCodeDataForm> payCodeDataFormList;

	public List<PayCodeDataForm> getPayCodeDataFormList() {
		return payCodeDataFormList;
	}

	public void setPayCodeDataFormList(List<PayCodeDataForm> payCodeDataFormList) {
		this.payCodeDataFormList = payCodeDataFormList;
	}

}
