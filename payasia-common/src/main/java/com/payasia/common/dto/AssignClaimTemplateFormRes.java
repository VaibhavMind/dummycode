package com.payasia.common.dto;

import java.io.Serializable;
import java.util.List;

import com.payasia.common.form.AssignClaimTemplateForm;
import com.payasia.common.form.PageResponse;

public class AssignClaimTemplateFormRes extends PageResponse implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4085439137260528076L;
	
	 
	private List<AssignClaimTemplateForm> assignClaimTemplateList;


	public List<AssignClaimTemplateForm> getAssignClaimTemplateList() {
		return assignClaimTemplateList;
	}


	public void setAssignClaimTemplateList(
			List<AssignClaimTemplateForm> assignClaimTemplateList) {
		this.assignClaimTemplateList = assignClaimTemplateList;
	}
	
	

}
