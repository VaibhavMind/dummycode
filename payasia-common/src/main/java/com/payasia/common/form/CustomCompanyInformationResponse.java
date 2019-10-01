package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

/**
 * The Class CustomCompanyInformationResponse.
 */
public class CustomCompanyInformationResponse implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3042192101596696392L;

	/** The page. */
	private String page;

	/** Total pages for the query. */
	private String total;

	/** Total number of records for the query. */
	private String records;

	/** An array that contains the actual objects. */
	private List<CompanyListForm> rows;

	/**
	 * Gets the page.
	 *
	 * @return the page
	 */
	public String getPage() {
		return page;
	}

	/**
	 * Sets the page.
	 *
	 * @param page the new page
	 */
	public void setPage(String page) {
		this.page = page;
	}

	/**
	 * Gets the total.
	 *
	 * @return the total
	 */
	public String getTotal() {
		return total;
	}

	/**
	 * Sets the total.
	 *
	 * @param total the new total
	 */
	public void setTotal(String total) {
		this.total = total;
	}

	/**
	 * Gets the records.
	 *
	 * @return the records
	 */
	public String getRecords() {
		return records;
	}

	/**
	 * Sets the records.
	 *
	 * @param records the new records
	 */
	public void setRecords(String records) {
		this.records = records;
	}

	/**
	 * Gets the rows.
	 *
	 * @return the rows
	 */
	public List<CompanyListForm> getRows() {
		return rows;
	}

	/**
	 * Sets the rows.
	 *
	 * @param rows the new rows
	 */
	public void setRows(List<CompanyListForm> rows) {
		this.rows = rows;
	}

}
