package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

public class ClaimBatchResponse extends PageResponse implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6540140870699985578L;
	private List<ClaimBatchForm> rows;

	public List<ClaimBatchForm> getRows() {
		return rows;
	}

	public void setRows(List<ClaimBatchForm> rows) {
		this.rows = rows;
	}


}
