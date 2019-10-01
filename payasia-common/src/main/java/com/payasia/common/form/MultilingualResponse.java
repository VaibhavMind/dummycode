package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

public class MultilingualResponse extends PageResponse implements Serializable{
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -262202376305335023L;
	private List<MultilingualForm> rows;

	

	public List<MultilingualForm> getRows() {
		return rows;
	}

	public void setRows(List<MultilingualForm> rows) {
		this.rows = rows;
	}

	
}
