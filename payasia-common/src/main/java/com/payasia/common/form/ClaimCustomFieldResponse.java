package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

public class ClaimCustomFieldResponse implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5823060748801810226L;

	private String page;

	/**
	 * Total pages for the query
	 */
	private String total;

	/**
	 * Total number of records for the query
	 */
	private String records;

	/**
	 * An array that contains the actual objects
	 */
	private List<ClaimCustomFieldForm> rows;

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}

	public String getRecords() {
		return records;
	}

	public void setRecords(String records) {
		this.records = records;
	}

	public List<ClaimCustomFieldForm> getRows() {
		return rows;
	}

	public void setRows(List<ClaimCustomFieldForm> rows) {
		this.rows = rows;
	}

}
