package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

 
/**
 * The Class CustomAdminAccessControlResponse.
 */
public class CustomAdminAccessControlResponse extends PageResponse implements Serializable{
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 9086870631848909388L;
	/** An array that contains the actual objects. */
	private List<AdminAccessControlForm> rows;

	

	/**
	 * Gets the rows.
	 *
	 * @return the rows
	 */
	public List<AdminAccessControlForm> getRows() {
		return rows;
	}

	/**
	 * Sets the rows.
	 *
	 * @param rows the new rows
	 */
	public void setRows(List<AdminAccessControlForm> rows) {
		this.rows = rows;
	}

}
