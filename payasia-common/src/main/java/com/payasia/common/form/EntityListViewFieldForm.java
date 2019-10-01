package com.payasia.common.form;

import java.io.Serializable;

/**
 * The Class EntityListViewFieldForm.
 */
public class EntityListViewFieldForm implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -525085584201630087L;

	/** The data dictionary id. */
	private Long dataDictionaryId;

	/** The field name. */
	private String fieldName;

	private String fieldLabel;

	/** The sequence. */
	private Integer sequence;

	private String status;

	private String viewName;

	private Integer records;

	public Integer getRecords() {
		return records;
	}

	public void setRecords(Integer records) {
		this.records = records;
	}

	public String getViewName() {
		return viewName;
	}

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getFieldLabel() {
		return fieldLabel;
	}

	public void setFieldLabel(String fieldLabel) {
		this.fieldLabel = fieldLabel;
	}

	/**
	 * Gets the field name.
	 * 
	 * @return the field name
	 */
	public String getFieldName() {
		return fieldName;
	}

	/**
	 * Sets the field name.
	 * 
	 * @param fieldName
	 *            the new field name
	 */
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	/**
	 * Sets the sequence.
	 * 
	 * @param sequence
	 *            the new sequence
	 */
	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	/**
	 * Gets the sequence.
	 * 
	 * @return the sequence
	 */
	public Integer getSequence() {
		return sequence;
	}

	/**
	 * Gets the data dictionary id.
	 * 
	 * @return the data dictionary id
	 */
	public Long getDataDictionaryId() {
		return dataDictionaryId;
	}

	/**
	 * Sets the data dictionary id.
	 * 
	 * @param dataDictionaryId
	 *            the new data dictionary id
	 */
	public void setDataDictionaryId(Long dataDictionaryId) {
		this.dataDictionaryId = dataDictionaryId;
	}

}
