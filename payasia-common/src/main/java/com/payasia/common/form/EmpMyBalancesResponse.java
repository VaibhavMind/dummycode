package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

public class EmpMyBalancesResponse implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8349031206404257680L;

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
	private List<EmpMyBalancesForm> rows;

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

	public List<EmpMyBalancesForm> getRows() {
		return rows;
	}

	public void setRows(List<EmpMyBalancesForm> rows) {
		this.rows = rows;
	}

}
