package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

public class OTBatchForm extends PageResponse implements Serializable{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8696528952535101655L;
	private String description;
	private String fromDate;
	private String toDate;
	private String status;
	private Long otBatchId;
	
	public Long getOtBatchId() {
		return otBatchId;
	}

	public void setOtBatchId(Long otBatchId) {
		this.otBatchId = otBatchId;
	}
	private List<OTBatchForm> rows;
 

	public List<OTBatchForm> getRows() {
		return rows;
	}

	public void setRows(List<OTBatchForm> rows) {
		this.rows = rows;
	}
	
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getFromDate() {
		return fromDate;
	}
	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}
	public String getToDate() {
		return toDate;
	}
	public void setToDate(String toDate) {
		this.toDate = toDate;
	}

	

}
