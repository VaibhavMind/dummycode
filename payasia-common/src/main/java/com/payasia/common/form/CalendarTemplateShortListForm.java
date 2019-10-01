package com.payasia.common.form;

import java.io.Serializable;

public class CalendarTemplateShortListForm implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2916338437218591733L;
	private long calTempFilterId;
	private long dataDictionaryId;
	private long entityId;
	private String openBracket;
	private String closeBracket;
	private String equalityOperator;
	private String logicalOperator;
	private String value;
	
	
	public long getCalTempFilterId() {
		return calTempFilterId;
	}
	public void setCalTempFilterId(long calTempFilterId) {
		this.calTempFilterId = calTempFilterId;
	}
	public long getDataDictionaryId() {
		return dataDictionaryId;
	}
	public void setDataDictionaryId(long dataDictionaryId) {
		this.dataDictionaryId = dataDictionaryId;
	}
	public long getEntityId() {
		return entityId;
	}
	public void setEntityId(long entityId) {
		this.entityId = entityId;
	}
	public String getOpenBracket() {
		return openBracket;
	}
	public void setOpenBracket(String openBracket) {
		this.openBracket = openBracket;
	}
	public String getCloseBracket() {
		return closeBracket;
	}
	public void setCloseBracket(String closeBracket) {
		this.closeBracket = closeBracket;
	}
	public String getEqualityOperator() {
		return equalityOperator;
	}
	public void setEqualityOperator(String equalityOperator) {
		this.equalityOperator = equalityOperator;
	}
	public String getLogicalOperator() {
		return logicalOperator;
	}
	public void setLogicalOperator(String logicalOperator) {
		this.logicalOperator = logicalOperator;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
}
