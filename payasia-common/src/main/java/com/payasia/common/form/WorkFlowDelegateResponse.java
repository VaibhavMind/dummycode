package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

/**
 * The Class WorkFlowDelegateList.
 */
public class WorkFlowDelegateResponse extends PageResponse implements Serializable{


	/**
	 * 
	 */
	private static final long serialVersionUID = 5894015776379226617L;

	/** The work flow delegate form. */
	private List<WorkFlowDelegateForm> workFlowDelegateForm;

	/** The search employee list. */
	List<EmployeeListForm> searchEmployeeList;

	/**
	 * Gets the work flow delegate form.
	 *
	 * @return the work flow delegate form
	 */
	public List<WorkFlowDelegateForm> getWorkFlowDelegateForm() {
		return workFlowDelegateForm;
	}

	/**
	 * Sets the work flow delegate form.
	 *
	 * @param workFlowDelegateForm the new work flow delegate form
	 */
	public void setWorkFlowDelegateForm(
			List<WorkFlowDelegateForm> workFlowDelegateForm) {
		this.workFlowDelegateForm = workFlowDelegateForm;
	}

	/**
	 * Gets the search employee list.
	 *
	 * @return the search employee list
	 */
	public List<EmployeeListForm> getSearchEmployeeList() {
		return searchEmployeeList;
	}

	/**
	 * Sets the search employee list.
	 *
	 * @param searchEmployeeList the new search employee list
	 */
	public void setSearchEmployeeList(List<EmployeeListForm> searchEmployeeList) {
		this.searchEmployeeList = searchEmployeeList;
	}
	
	

}
