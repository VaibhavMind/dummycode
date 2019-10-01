package com.payasia.common.dto;

import java.io.Serializable;

public class CodeDescDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2383089890135858791L;
	private long codeDescId;
	private long formId;
	private String code;
	private String description;
	private String methodName;
	private boolean childVal;
	private String tablePosition;
	
	
	
	
	public String getTablePosition() {
		return tablePosition;
	}
	public void setTablePosition(String tablePosition) {
		this.tablePosition = tablePosition;
	}
	public long getCodeDescId() {
		return codeDescId;
	}
	public void setCodeDescId(long codeDescId) {
		this.codeDescId = codeDescId;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public long getFormId() {
		return formId;
	}
	public void setFormId(long formId) {
		this.formId = formId;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public boolean isChildVal() {
		return childVal;
	}
	public void setChildVal(boolean childVal) {
		this.childVal = childVal;
	}
	
	
	
	
	
}
