package com.payasia.common.form;

import java.io.Serializable;
import java.util.List;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.payasia.common.dto.EmployeeFieldDTO;

public class EmployeeDynamicForm implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 58196064887968811L;

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
	private List<EmployeeFieldDTO> employeesList;
	
	
	
	public long getBasicFormId() {
		return basicFormId;
	}

	public void setBasicFormId(long basicFormId) {
		this.basicFormId = basicFormId;
	}

	public boolean isAddedNewTab() {
		return addedNewTab;
	}

	public void setAddedNewTab(boolean addedNewTab) {
		this.addedNewTab = addedNewTab;
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

	public List<EmployeeFieldDTO> getEmployeesList() {
		return employeesList;
	}

	public void setEmployeesList(List<EmployeeFieldDTO> employeesList) {
		this.employeesList = employeesList;
	}
	
}
