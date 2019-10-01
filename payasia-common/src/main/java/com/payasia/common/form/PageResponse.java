package com.payasia.common.form;

/**
 * The Class PageResponse is Base Class for all Response which handling grid
 * functionality .
 */
public class PageResponse {

	/** * Grid page no. */
	private int page;

	/** * Total pages for the Grid. */
	private int total;

	/** * Total number of records for the Grid. */
	private int records;

	public int getTotal() {
		return total;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	/**
	 * Sets the total pages.
	 * 
	 * @param total
	 *            the new total pages in grid
	 */
	public void setTotal(int total) {
		this.total = total;
	}

	/**
	 * Gets the records .
	 * 
	 * @return the records in grid
	 */
	public int getRecords() {
		return records;
	}

	/**
	 * Sets the records.
	 * 
	 * @param records
	 *            set records
	 */
	public void setRecords(int records) {
		this.records = records;
	}

}
