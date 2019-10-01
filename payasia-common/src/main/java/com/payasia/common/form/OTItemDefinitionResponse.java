package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

public class OTItemDefinitionResponse extends PageResponse implements Serializable{
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 7918935352316480145L;
	private List<OTItemDefinitionForm> rows;


	public List<OTItemDefinitionForm> getRows() {
		return rows;
	}

	public void setRows(List<OTItemDefinitionForm> rows) {
		this.rows = rows;
	}

}
