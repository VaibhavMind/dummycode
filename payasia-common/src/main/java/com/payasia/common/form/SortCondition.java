package com.payasia.common.form;

/**
 * The Class SortCondition.
 */
public class SortCondition {

	/** The column name. */
	private String columnName;
	
	/** The sort order. */
	private String sortOrder;

	/**
	 * Instantiates a new sort condition.
	 */
	public SortCondition() {

	}

	/**
	 * Instantiates a new sort condition.
	 *
	 * @param colName the col name
	 * @param sortOrder the sort order
	 */
	public SortCondition(String colName, String sortOrder) {
		this.columnName = colName;
		this.sortOrder = sortOrder;
	}

	/**
	 * Gets the column name.
	 *
	 * @return the column name
	 */
	public String getColumnName() {
		return columnName;
	}

	/**
	 * Sets the column name.
	 *
	 * @param columnName the new column name
	 */
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	/**
	 * Gets the order type.
	 *
	 * @return the order type
	 */
	public String getOrderType() {
		return sortOrder;
	}

	/**
	 * Sets the order type.
	 *
	 * @param orderType the new order type
	 */
	public void setOrderType(String orderType) {
		this.sortOrder = orderType;
	}

}
