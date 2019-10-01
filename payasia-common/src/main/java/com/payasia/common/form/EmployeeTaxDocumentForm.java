package com.payasia.common.form;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import com.payasia.common.dto.CompanyDocumentDetailDTO;

public class EmployeeTaxDocumentForm implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1440916529616008739L;
	private int year;
	private List<CompanyDocumentDetailDTO> taxDocumentList;
	private String verifyResponse;
	private boolean documentExist;
	private Object[] messageParam;

	public List<CompanyDocumentDetailDTO> getTaxDocumentList() {
		return taxDocumentList;
	}

	public void setTaxDocumentList(
			List<CompanyDocumentDetailDTO> taxDocumentList) {
		this.taxDocumentList = taxDocumentList;
	}

	public String getVerifyResponse() {
		return verifyResponse;
	}

	public void setVerifyResponse(String verifyResponse) {
		this.verifyResponse = verifyResponse;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public boolean isDocumentExist() {
		return documentExist;
	}

	public void setDocumentExist(boolean documentExist) {
		this.documentExist = documentExist;
	}

	public Object[] getMessageParam() {
		return messageParam;
	}

	public void setMessageParam(Object[] messageParam) {
		if (messageParam != null) {
			this.messageParam = Arrays.copyOf(messageParam, messageParam.length);
		}
	}

}
