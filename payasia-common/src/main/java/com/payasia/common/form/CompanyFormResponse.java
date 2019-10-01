package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

public class CompanyFormResponse extends PageResponse implements Serializable{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -983269108815773820L;
	/** An array that contains the actual objects. */
	private List<CompanyForm> rows;

	public List<CompanyForm> getRows() {
		return rows;
	}

	public void setRows(List<CompanyForm> rows) {
		this.rows = rows;
	}

	
	
	
	

}
