package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

public class OTTemplateResponse extends PageResponse implements Serializable{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4426460471296079548L;
	private List<OTTemplateForm> rows;
	private Set<OTTemplateForm> otTemplateSet;

	

	public List<OTTemplateForm> getRows() {
		return rows;
	}

	public void setRows(List<OTTemplateForm> rows) {
		this.rows = rows;
	}

	public Set<OTTemplateForm> getOtTemplateSet() {
		return otTemplateSet;
	}

	public void setOtTemplateSet(Set<OTTemplateForm> otTemplateSet) {
		this.otTemplateSet = otTemplateSet;
	}

}
