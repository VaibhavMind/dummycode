package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

public class EmailTemplateCustomResponse implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7427284515329573207L;

	private String page;

	/**
	 * Total pages for the query
	 */
	private String total;

	/**
	 * Total number of records for the query
	 */
	private String records;

	/**
	 * An array that contains the actual objects
	 */
	private List<EmailTemplateForm> emailTemplatesList;

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

	public List<EmailTemplateForm> getEmailTemplatesList() {
		return emailTemplatesList;
	}

	public void setEmailTemplatesList(List<EmailTemplateForm> emailTemplatesList) {
		this.emailTemplatesList = emailTemplatesList;
	}

}
