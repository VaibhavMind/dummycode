package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

public class LeaveReviewerResponseForm extends PageResponse implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4039259335327382109L;

	private List<LeaveReviewerForm> rows;
	
	/** The search employee list. */
	private List<EmployeeListForm> searchEmployeeList;
	
	private String ruleName;
	private String ruleValue;
	
	private String errorMsg;
	

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public List<LeaveReviewerForm> getRows() {
		return rows;
	}

	public void setRows(List<LeaveReviewerForm> rows) {
		this.rows = rows;
	}

	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	public String getRuleValue() {
		return ruleValue;
	}

	public void setRuleValue(String ruleValue) {
		this.ruleValue = ruleValue;
	}

	public List<EmployeeListForm> getSearchEmployeeList() {
		return searchEmployeeList;
	}

	public void setSearchEmployeeList(List<EmployeeListForm> searchEmployeeList) {
		this.searchEmployeeList = searchEmployeeList;
	}

}
