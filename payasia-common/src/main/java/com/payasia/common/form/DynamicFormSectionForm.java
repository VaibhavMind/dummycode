package com.payasia.common.form;

import java.io.Serializable;

/**
 * The Class PrevilageForm.
 */
public class DynamicFormSectionForm implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6363915448730647736L;

	private Long formId;
	private String formIdStr;
	private String sectionDesc;
	
	private String sectionName;
	
	private boolean roleAssigned;
	private String moduleName;
	public Long getFormId() {
		return formId;
	}
	public void setFormId(Long formId) {
		this.formId = formId;
	}
	public String getSectionDesc() {
		return sectionDesc;
	}
	public void setSectionDesc(String sectionDesc) {
		this.sectionDesc = sectionDesc;
	}
	public String getSectionName() {
		return sectionName;
	}
	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}
	public boolean isRoleAssigned() {
		return roleAssigned;
	}
	public void setRoleAssigned(boolean roleAssigned) {
		this.roleAssigned = roleAssigned;
	}
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	public String getFormIdStr() {
		return formIdStr;
	}
	public void setFormIdStr(String formIdStr) {
		this.formIdStr = formIdStr;
	}
	
	
	
	
}
