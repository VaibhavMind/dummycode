package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

public class ClaimItemsCategoryResponse extends PageResponse implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4997772719292494708L;
	private List<ClaimItemsCategoryForm> rows;
	

	public List<ClaimItemsCategoryForm> getRows() {
		return rows;
	}

	public void setRows(List<ClaimItemsCategoryForm> rows) {
		this.rows = rows;
	}

}
