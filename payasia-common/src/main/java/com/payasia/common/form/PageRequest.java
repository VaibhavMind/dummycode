package com.payasia.common.form;


/**
 * The Class PageRequest.
 */
public class PageRequest {

	/** The page number. */
	private int pageNumber;
	
	/** The page size. */
	private int pageSize;

	/**
	 * Gets the page number.
	 *
	 * @return the page number
	 */
	public int getPageNumber() {
		return pageNumber; 
	}

	/**
	 * Sets the page number.
	 *
	 * @param pageNumber the new page number
	 */
	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	/**
	 * Gets the page size.
	 *
	 * @return the page size
	 */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * Sets the page size.
	 *
	 * @param pageSize the new page size
	 */
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

}
