package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

public class LeaveBatchResponse extends PageResponse implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8048945218526341114L;
	private List<LeaveBatchForm> rows;


	public List<LeaveBatchForm> getRows() {
		return rows;
	}

	public void setRows(List<LeaveBatchForm> rows) {
		this.rows = rows;
	}

}
