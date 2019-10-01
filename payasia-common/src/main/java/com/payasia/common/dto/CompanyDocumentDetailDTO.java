package com.payasia.common.dto;

import java.io.Serializable;

public class CompanyDocumentDetailDTO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8781307587762721440L;
	private Long documentId;
	private String documentName;
	public Long getDocumentId() {
		return documentId;
	}
	public void setDocumentId(Long documentId) {
		this.documentId = documentId;
	}
	public String getDocumentName() {
		return documentName;
	}
	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}
	
	
}
