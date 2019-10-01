package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;


/**
 * The Class HRLetterResponse.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HRLetterResponse extends PageResponse implements Serializable{

	

	/**
	 * 
	 */
	private static final long serialVersionUID = -470159266584460880L;
	/** The rows. */
	private List<HRLetterForm> rows;
	/** The search employee list. */
	
	private List<EmployeeListForm> searchEmployeeList;
	
	
	/**
	 * Gets the rows.
	 * 
	 * @return the rows
	 */
	public List<HRLetterForm> getRows() {
		return rows;
	}

	/**
	 * Sets the rows.
	 * 
	 * @param rows
	 *            the new rows
	 */
	public void setRows(List<HRLetterForm> rows) {
		this.rows = rows;
	}

	public List<EmployeeListForm> getSearchEmployeeList() {
		return searchEmployeeList;
	}

	public void setSearchEmployeeList(List<EmployeeListForm> searchEmployeeList) {
		this.searchEmployeeList = searchEmployeeList;
	}
	

}
