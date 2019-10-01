package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

public class LundinReviewerResponseForm extends PageResponse implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4966091208939218590L;

	private List<LundinTimesheetReviewerForm> rows;
	
	/** The search employee list. */
	private List<EmployeeListForm> searchEmployeeList;
	
	private String ruleName;
	private String ruleValue;


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

	public List<LundinTimesheetReviewerForm> getRows() {
		return rows;
	}

	public void setRows(List<LundinTimesheetReviewerForm> rows) {
		this.rows = rows;
	}


}
