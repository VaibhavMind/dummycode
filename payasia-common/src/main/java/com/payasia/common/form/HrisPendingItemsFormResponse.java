package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

public class HrisPendingItemsFormResponse extends PageResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<HrisPendingItemsForm> rows;
	public List<HrisPendingItemsForm> getRows() {
		return rows;
	}
	public void setRows(List<HrisPendingItemsForm> rows) {
		this.rows = rows;
	}
	
	

}
