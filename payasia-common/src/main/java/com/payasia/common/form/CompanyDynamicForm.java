package com.payasia.common.form;

import java.io.Serializable;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 * The Class CompanyDynamicForm.
 */
public class CompanyDynamicForm implements Serializable{

/**
	 * 
	 */
	private static final long serialVersionUID = -859598522117532254L;

private String metaData;
	
	private long formId;
	
	private String tabName;
	
	private CommonsMultipartFile attachment;
	
	private String options;
	
	private boolean addedNewTab;
	
	private long basicFormId;
	
	private String saveResponse;
	private Long dictionaryId;
	private String dictionaryName;

	/**
	 * Gets the meta data.
	 *
	 * @return the meta data
	 */
	public String getMetaData() {
		return metaData;
	}

	/**
	 * Sets the meta data.
	 *
	 * @param metaData the new meta data
	 */
	public void setMetaData(String metaData) {
		this.metaData = metaData;
	}

	/**
	 * Gets the tab name.
	 *
	 * @return the tab name
	 */
	public String getTabName() {
		return tabName;
	}

	/**
	 * Sets the tab name.
	 *
	 * @param tabName the new tab name
	 */
	public void setTabName(String tabName) {
		this.tabName = tabName;
	}

	public long getFormId() {
		return formId;
	}

	public void setFormId(long formId) {
		this.formId = formId;
	}

	public CommonsMultipartFile getAttachment() {
		return attachment;
	}

	public void setAttachment(CommonsMultipartFile attachment) {
		this.attachment = attachment;
	}

	public String getOptions() {
		return options;
	}

	public void setOptions(String options) {
		this.options = options;
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
