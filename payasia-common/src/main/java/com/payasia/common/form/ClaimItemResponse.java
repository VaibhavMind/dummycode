package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

public class ClaimItemResponse extends PageResponse implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7969236060098580068L;
	private List<ClaimItemForm> rows;

	public List<ClaimItemForm> getRows() {
		return rows;
	}

	public void setRows(List<ClaimItemForm> rows) {
		this.rows = rows;
	}

}
