package com.payasia.common.dto;

import java.io.Serializable;

public class DynamicFormDataForm implements Serializable{
	
	private static final long serialVersionUID = -1177017580055132570L;
	
	private String formId;
	private Boolean isDefault;
	private String formRecordId;
	private String dataDictionaryId;
	private String formTableRecordId;
	private String seqNo;
	private Long employeeId;
	private Long companyId;
	private Long languageId;
	
	public String getDataDictionaryId() {
		return dataDictionaryId;
	}
	public void setDataDictionaryId(String dataDictionaryId) {
		this.dataDictionaryId = dataDictionaryId;
	}
	public String getFormTableRecordId() {
		return formTableRecordId;
	}
	public void setFormTableRecordId(String formTableRecordId) {
		this.formTableRecordId = formTableRecordId;
	}
	public String getSeqNo() {
		return seqNo;
	}
	public void setSeqNo(String seqNo) {
		this.seqNo = seqNo;
	}
	public Long getLanguageId() {
		return languageId;
	}
	public void setLanguageId(Long languageId) {
		this.languageId = languageId;
	}
	public String getFormRecordId() {
		return formRecordId;
	}
	public void setFormRecordId(String formRecordId) {
		this.formRecordId = formRecordId;
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
	public Boolean getIsDefault() {
		return isDefault;
	}
	public void setIsDefault(Boolean isDefault) {
		this.isDefault = isDefault;
	}
	
	

}
