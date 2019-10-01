package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddLeaveFormResponse extends PageResponse implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1982729121829278744L;
	private List<AddLeaveForm> rows;
	private List<AddLeaveForm> addLeaveFormList;
	private AddLeaveForm addLeaveForm;
	
	
	
	

	public List<AddLeaveForm> getAddLeaveFormList() {
		return addLeaveFormList;
	}

	public void setAddLeaveFormList(List<AddLeaveForm> addLeaveFormList) {
		this.addLeaveFormList = addLeaveFormList;
	}

	public AddLeaveForm getAddLeaveForm() {
		return addLeaveForm;
	}

	public void setAddLeaveForm(AddLeaveForm addLeaveForm) {
		this.addLeaveForm = addLeaveForm;
	}

	public List<AddLeaveForm> getRows() {
		return rows;
	}

	public void setRows(List<AddLeaveForm> rows) {
		this.rows = rows;
	}
	
	/*public List<AddLeaveForm> getAddLeaveFormList() {
		return addLeaveFormList;
	}

	public void setAddLeaveFormList(List<AddLeaveForm> addLeaveFormList) {
		this.addLeaveFormList = addLeaveFormList;
	}
	*/
	
	
	
}
