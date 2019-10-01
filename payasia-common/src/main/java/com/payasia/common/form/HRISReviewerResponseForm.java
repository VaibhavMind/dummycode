package com.payasia.common.form;

import java.util.List;

public class HRISReviewerResponseForm extends PageResponse {
	private List<HRISReviewerForm> rows;
	
	/** The search employee list. */
	private List<EmployeeListForm> searchEmployeeList;
	
	private String ruleName;
	private String ruleValue;

	public List<HRISReviewerForm> getRows() {
		return rows;
	}

	public void setRows(List<HRISReviewerForm> rows) {
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
