package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

public class CalendarTempShortListResponse implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2511939219347799386L;
	private long calTempId;
	private List<CalendarTemplateShortListForm> shortListRows;
	
	
	public long getCalTempId() {
		return calTempId;
	}
	public void setCalTempId(long calTempId) {
		this.calTempId = calTempId;
	}
	public List<CalendarTemplateShortListForm> getShortListRows() {
		return shortListRows;
	}
	public void setShortListRows(List<CalendarTemplateShortListForm> shortListRows) {
		this.shortListRows = shortListRows;
	}

}

