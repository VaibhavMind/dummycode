package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;


/**
 * The Class CurrencyDefinitionResponse.
 */
public class CurrencyDefinitionResponse extends PageResponse implements Serializable{
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 2726700635966438444L;
	/** An array that contains the actual objects. */
	private List<CurrencyDefinitionForm> rows;

	

	/**
	 * Gets the rows.
	 *
	 * @return the rows
	 */
	public List<CurrencyDefinitionForm> getRows() {
		return rows;
	}

	/**
	 * Sets the rows.
	 *
	 * @param rows the new rows
	 */
	public void setRows(List<CurrencyDefinitionForm> rows) {
		this.rows = rows;
	}

}
