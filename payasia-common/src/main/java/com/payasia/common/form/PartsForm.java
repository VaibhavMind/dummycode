package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

import com.payasia.common.dto.PartDTO;

public class PartsForm implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String verifyResponse;
	private List<PartDTO> parts;
	public List<PartDTO> getParts() {
		return parts;
	}
	public void setParts(List<PartDTO> parts) {
		this.parts = parts;
	}
	public String getVerifyResponse() {
		return verifyResponse;
	}
	public void setVerifyResponse(String verifyResponse) {
		this.verifyResponse = verifyResponse;
	}

	
	
	
	
	

}
