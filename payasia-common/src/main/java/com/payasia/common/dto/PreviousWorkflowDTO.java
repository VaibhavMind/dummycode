package com.payasia.common.dto;

import java.io.Serializable;

public class PreviousWorkflowDTO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3668773380521606864L;
	private String preFromDate;
	private String preToDate;
	private String preWorkflow;
	public String getPreFromDate() {
		return preFromDate;
	}
	public void setPreFromDate(String preFromDate) {
		this.preFromDate = preFromDate;
	}
	public String getPreToDate() {
		return preToDate;
	}
	public void setPreToDate(String preToDate) {
		this.preToDate = preToDate;
	}
	public String getPreWorkflow() {
		return preWorkflow;
	}
	public void setPreWorkflow(String preWorkflow) {
		this.preWorkflow = preWorkflow;
	}
	
	

}
