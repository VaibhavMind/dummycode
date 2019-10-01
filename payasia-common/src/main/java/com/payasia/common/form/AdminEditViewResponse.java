package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

public class AdminEditViewResponse implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7741509901570036001L;
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

	public List<EntityListViewFieldForm> getRows() {
		return rows;
	}

	public void setRows(List<EntityListViewFieldForm> rows) {
		this.rows = rows;
	}

	public void setEntityListView(List<EntityListViewForm> entityListView) {
		this.entityListView = entityListView;
	}

	public List<EntityListViewForm> getEntityListView() {
		return entityListView;
	}

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
	private List<EntityListViewFieldForm> rows;
	private List<EntityListViewForm> entityListView;

}
