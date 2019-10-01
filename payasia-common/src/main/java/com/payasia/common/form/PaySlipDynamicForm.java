package com.payasia.common.form;

import java.io.Serializable;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

public class PaySlipDynamicForm implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2738311505059128741L;

	private String metaData;
	
	private long formId;
	
	private String tabName;
	
	private CommonsMultipartFile attachment;
	
	private String options;

	private boolean addedNewTab;
	
	private long basicFormId;
	
	private String saveResponse;
	
	private Integer payslipPart;
	
	private String paySlipFrequency;
	
	private int effectiveYear;
	
	private long effectiveMonth;
	
	private int effectivePart;
	private Long dictionaryId;
	private String dictionaryName;
	
	public int getEffectiveYear() {
		return effectiveYear;
	}

	public void setEffectiveYear(int effectiveYear) {
		this.effectiveYear = effectiveYear;
	}

	public long getEffectiveMonth() {
		return effectiveMonth;
	}

	public void setEffectiveMonth(long effectiveMonth) {
		this.effectiveMonth = effectiveMonth;
	}

	public int getEffectivePart() {
		return effectivePart;
	}

	public void setEffectivePart(int effectivePart) {
		this.effectivePart = effectivePart;
	}
	
	public Integer getPayslipPart() {
		return payslipPart;
	}

	public void setPayslipPart(Integer payslipPart) {
		this.payslipPart = payslipPart;
	}

	public String getPaySlipFrequency() {
		return paySlipFrequency;
	}

	public void setPaySlipFrequency(String paySlipFrequency) {
		this.paySlipFrequency = paySlipFrequency;
	}

	public boolean isAddedNewTab() {
		return addedNewTab;
	}

	public void setAddedNewTab(boolean addedNewTab) {
		this.addedNewTab = addedNewTab;
	}

	public long getBasicFormId() {
		return basicFormId;
	}

	public void setBasicFormId(long basicFormId) {
		this.basicFormId = basicFormId;
	}

	public String getSaveResponse() {
		return saveResponse;
	}

	public void setSaveResponse(String saveResponse) {
		this.saveResponse = saveResponse;
	}

	public long getFormId() {
		return formId;
	}

	public void setFormId(long formId) {
		this.formId = formId;
	}

	public String getOptions() {
		return options;
	}

	public void setOptions(String options) {
		this.options = options;
	}

	public CommonsMultipartFile getAttachment() {
		return attachment;
	}

	public void setAttachment(CommonsMultipartFile attachment) {
		this.attachment = attachment;
	}

	public String getMetaData() {
		return metaData;
	}

	public void setMetaData(String metaData) {
		this.metaData = metaData;
	}

	public String getTabName() {
		return tabName;
	}

	public void setTabName(String tabName) {
		this.tabName = tabName;
	}

	public Long getDictionaryId() {
		return dictionaryId;
	}

	public void setDictionaryId(Long dictionaryId) {
		this.dictionaryId = dictionaryId;
	}

	public String getDictionaryName() {
		return dictionaryName;
	}

	public void setDictionaryName(String dictionaryName) {
		this.dictionaryName = dictionaryName;
	}
	
}
