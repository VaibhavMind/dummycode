package com.payasia.common.dto;

import java.io.Serializable;

public class DynamicFormTableDataForm implements Serializable{
	
	private static final long serialVersionUID = -1177017580055132570L;
	
	private String formId;
	private String formtableid;
	private String dataDictionaryId;
	private String formtype;
	private Long employeeId;
	private Long companyId;
	private String sortBy;
	private String sortOrder;
	
	public String getSortBy() {
		return sortBy;
	}
	public void setSortBy(String sortBy) {
		this.sortBy = sortBy;
	}
	public String getSortOrder() {
		return sortOrder;
	}
	public void setSortOrder(String sortOrder) {
		this.sortOrder = sortOrder;
	}
	public String getDataDictionaryId() {
		return dataDictionaryId;
	}
	public void setDataDictionaryId(String dataDictionaryId) {
		this.dataDictionaryId = dataDictionaryId;
	}
	public String getFormtype() {
		return formtype;
	}
	public void setFormtype(String formtype) {
		this.formtype = formtype;
	}
	public Long getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}
	public Long getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
	public String getFormId() {
		return formId;
	}
	public void setFormId(String formId) {
		this.formId = formId;
	}
	public String getFormtableid() {
		return formtableid;
	}
	public void setFormtableid(String formtableid) {
		this.formtableid = formtableid;
	}
	
	
	

}
