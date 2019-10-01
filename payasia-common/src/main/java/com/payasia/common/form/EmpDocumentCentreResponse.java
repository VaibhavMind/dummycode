package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

public class EmpDocumentCentreResponse extends PageResponse implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 92533690921093650L;
	private List<EmpDocumentCenterForm> empDocumentCentreForm;

	public List<EmpDocumentCenterForm> getEmpDocumentCentreForm() {
		return empDocumentCentreForm;
	}

	public void setEmpDocumentCentreForm(
			List<EmpDocumentCenterForm> empDocumentCentreForm) {
		this.empDocumentCentreForm = empDocumentCentreForm;
	}

}
