package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

public class CalendarDefResponse extends PageResponse implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1939669581612669294L;
	private List<CalendarDefForm> rows;

	public List<CalendarDefForm> getRows() {
		return rows;
	}

	public void setRows(List<CalendarDefForm> rows) {
		this.rows = rows;
	}
}
