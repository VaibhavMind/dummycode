package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

/**
 * The Class PasswordSecurityQuestionResponse.
 */
public class PasswordSecurityQuestionResponse extends PageResponse implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4312972248470902994L;
	/** The rows. */
	private List<PasswordSecurityQuestionForm> rows;

	
	/**
	 * Gets the rows.
	 * 
	 * @return the rows
	 */
	public List<PasswordSecurityQuestionForm> getRows() {
		return rows;
	}

	/**
	 * Sets the rows.
	 * 
	 * @param rows
	 *            the new rows
	 */
	public void setRows(List<PasswordSecurityQuestionForm> rows) {
		this.rows = rows;
	}

}
