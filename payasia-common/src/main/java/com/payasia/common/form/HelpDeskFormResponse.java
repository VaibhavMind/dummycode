package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

import com.payasia.common.dto.HelpDeskDTO;

public class HelpDeskFormResponse implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String screenName;
	
	private List<HelpDeskDTO> rows;

	public String getScreenName() {
		return screenName;
	}

	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}

	public List<HelpDeskDTO> getRows() {
		return rows;
	}

	public void setRows(List<HelpDeskDTO> rows) {
		this.rows = rows;
	}

	
}
