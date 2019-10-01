package com.payasia.common.form;

import java.util.List;

public class PaySlipReleaseFormResponse extends PageResponse {

	private List<PaySlipReleaseForm> response;

	public List<PaySlipReleaseForm> getResponse() {
		return response;
	}

	public void setResponse(List<PaySlipReleaseForm> response) {
		this.response = response;
	}

}
