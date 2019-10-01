package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

public class ClaimBatchFormPage implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2668899447512386848L;
	private String page;
	/**
	 * Total pages for the query
	 */
	private String total;
	/**
	 * Total number of records for the query
	 */
	private String records;

	private List<ClaimBatchForm> claimBatchForm;

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}

	public String getRecords() {
		return records;
	}

	public void setRecords(String records) {
		this.records = records;
	}

	public List<ClaimBatchForm> getClaimsBatchForm() {
		return claimBatchForm;
	}

	public void setClaimsBatchForm(List<ClaimBatchForm> claimsBatchForm) {
		this.claimBatchForm = claimsBatchForm;
	}

}
