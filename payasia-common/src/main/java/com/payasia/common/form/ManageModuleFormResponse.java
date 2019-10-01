package com.payasia.common.form;

import java.util.List;

import com.payasia.common.dto.ManageModuleDTO;

public class ManageModuleFormResponse extends PageResponse{

	private List<ManageModuleDTO> response;

	public List<ManageModuleDTO> getResponse() {
		return response;
	}

	public void setResponse(List<ManageModuleDTO> response) {
		this.response = response;
	}
}
