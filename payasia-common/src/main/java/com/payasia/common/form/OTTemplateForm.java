package com.payasia.common.form;

import java.io.Serializable;

public class OTTemplateForm implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1290899452396787112L;
	private String templateName;
	private Long otTemplateId;
	private Long otTemplateItemId;
	private String noOfItems;
	private String active;
	private Boolean visibility;
	private String configure;
	private String accountCode;
	
	private String workFlowLevelOT;
	private Boolean allowOverrideOTL1;
	private Boolean allowOverrideOTL2;
	private Boolean allowOverrideOTL3;
	private Boolean allowRejectOTL1;
	private Boolean allowRejectOTL2;
	private Boolean allowRejectOTL3;
	
	private String otItem;
	private String otItemDesc;
	private Boolean otItemVisibility;
	private Long otItemId;
	public String getTemplateName() {
		return templateName;
	}
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}
	public Long getOtTemplateId() {
		return otTemplateId;
	}
	public void setOtTemplateId(Long otTemplateId) {
		this.otTemplateId = otTemplateId;
	}
	public Long getOtTemplateItemId() {
		return otTemplateItemId;
	}
	public void setOtTemplateItemId(Long otTemplateItemId) {
		this.otTemplateItemId = otTemplateItemId;
	}
	public String getNoOfItems() {
		return noOfItems;
	}
	public void setNoOfItems(String noOfItems) {
		this.noOfItems = noOfItems;
	}
	public String getActive() {
		return active;
	}
	public void setActive(String active) {
		this.active = active;
	}
	public Boolean getVisibility() {
		return visibility;
	}
	public void setVisibility(Boolean visibility) {
		this.visibility = visibility;
	}
	public String getConfigure() {
		return configure;
	}
	public void setConfigure(String configure) {
		this.configure = configure;
	}
	public String getWorkFlowLevelOT() {
		return workFlowLevelOT;
	}
	public void setWorkFlowLevelOT(String workFlowLevelOT) {
		this.workFlowLevelOT = workFlowLevelOT;
	}
	public Boolean getAllowOverrideOTL1() {
		return allowOverrideOTL1;
	}
	public void setAllowOverrideOTL1(Boolean allowOverrideOTL1) {
		this.allowOverrideOTL1 = allowOverrideOTL1;
	}
	public Boolean getAllowOverrideOTL2() {
		return allowOverrideOTL2;
	}
	public void setAllowOverrideOTL2(Boolean allowOverrideOTL2) {
		this.allowOverrideOTL2 = allowOverrideOTL2;
	}
	public Boolean getAllowOverrideOTL3() {
		return allowOverrideOTL3;
	}
	public void setAllowOverrideOTL3(Boolean allowOverrideOTL3) {
		this.allowOverrideOTL3 = allowOverrideOTL3;
	}
	public Boolean getAllowRejectOTL1() {
		return allowRejectOTL1;
	}
	public void setAllowRejectOTL1(Boolean allowRejectOTL1) {
		this.allowRejectOTL1 = allowRejectOTL1;
	}
	public Boolean getAllowRejectOTL2() {
		return allowRejectOTL2;
	}
	public void setAllowRejectOTL2(Boolean allowRejectOTL2) {
		this.allowRejectOTL2 = allowRejectOTL2;
	}
	public Boolean getAllowRejectOTL3() {
		return allowRejectOTL3;
	}
	public void setAllowRejectOTL3(Boolean allowRejectOTL3) {
		this.allowRejectOTL3 = allowRejectOTL3;
	}
	public String getOtItem() {
		return otItem;
	}
	public void setOtItem(String otItem) {
		this.otItem = otItem;
	}
	public String getOtItemDesc() {
		return otItemDesc;
	}
	public void setOtItemDesc(String otItemDesc) {
		this.otItemDesc = otItemDesc;
	}
	public Boolean getOtItemVisibility() {
		return otItemVisibility;
	}
	public void setOtItemVisibility(Boolean otItemVisibility) {
		this.otItemVisibility = otItemVisibility;
	}
	public Long getOtItemId() {
		return otItemId;
	}
	public void setOtItemId(Long otItemId) {
		this.otItemId = otItemId;
	}
	public String getAccountCode() {
		return accountCode;
	}
	public void setAccountCode(String accountCode) {
		this.accountCode = accountCode;
	}
}
