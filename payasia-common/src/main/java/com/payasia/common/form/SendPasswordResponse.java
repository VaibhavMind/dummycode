package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

public class SendPasswordResponse extends PageResponse implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3130355787877444875L;
	/**
	 * An array that contains the actual objects
	 */
	private List<SendPasswordForm> rows;


	public List<SendPasswordForm> getRows() {
		return rows;
	}

	public void setRows(List<SendPasswordForm> rows) {
		this.rows = rows;
	}
}
