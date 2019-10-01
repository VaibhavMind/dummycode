package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

public class EmpModifyClaimsResponse implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -369281579863492005L;

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
	private List<EmpModifyClaimsForm> rows;

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

	public List<EmpModifyClaimsForm> getRows() {
		return rows;
	}

	public void setRows(List<EmpModifyClaimsForm> rows) {
		this.rows = rows;
	}

}
