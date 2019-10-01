package com.payasia.common.form;


import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class OTTimesheetForm implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6055882811610217238L;
	private Long otTimesheetId;
	private Long companyId;
	private Long employeeId;
	private Long otTimesheetTemplateId;
	private String otTimesheetTemplateName;
	private String createdDate;
	private String updatedDate;
	private Integer totalItems;
	private BigDecimal totalAmount;
	private Long otTimesheetStatusId;
	private String remarks;
	private String otTimesheetFormName;
	private Long otTimesheetNumber;
	
	private boolean canOverride;
	private boolean canReject;
	private boolean canApprove;
	private boolean canForward;
	
	
	private List<OTMyRequestForm> otMyRequestForm;
	public List<OTMyRequestForm> getOtMyRequestForm() {
		return otMyRequestForm;
	}
	public void setOtMyRequestForm(List<OTMyRequestForm> otMyRequestForm) {
		this.otMyRequestForm = otMyRequestForm;
	}
	public List<OTTimesheetWorkflowForm> getOtTimesheetWorkflowForm() {
		return otTimesheetWorkflowForm;
	}
	public void setOtTimesheetWorkflowForm(
			List<OTTimesheetWorkflowForm> otTimesheetWorkflowForm) {
		this.otTimesheetWorkflowForm = otTimesheetWorkflowForm;
	}
	private List<OTTimesheetWorkflowForm> otTimesheetWorkflowForm;
	
	
	public boolean isCanOverride() {
		return canOverride;
	}
	public void setCanOverride(boolean canOverride) {
		this.canOverride = canOverride;
	}
	public boolean isCanReject() {
		return canReject;
	}
	public void setCanReject(boolean canReject) {
		this.canReject = canReject;
	}
	public boolean isCanApprove() {
		return canApprove;
	}
	public void setCanApprove(boolean canApprove) {
		this.canApprove = canApprove;
	}
	public boolean isCanForward() {
		return canForward;
	}
	public void setCanForward(boolean canForward) {
		this.canForward = canForward;
	}
	
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
	public Long getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	public String getUpdatedDate() {
		return updatedDate;
	}
	public void setUpdatedDate(String updatedDate) {
		this.updatedDate = updatedDate;
	}
	public Integer getTotalItems() {
		return totalItems;
	}
	public void setTotalItems(Integer totalItems) {
		this.totalItems = totalItems;
	}
	public BigDecimal getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}
	public Long getOtTimesheetId() {
		return otTimesheetId;
	}
	public void setOtTimesheetId(Long otTimesheetId) {
		this.otTimesheetId = otTimesheetId;
	}
	public Long getOtTimesheetTemplateId() {
		return otTimesheetTemplateId;
	}
	public void setOtTimesheetTemplateId(Long otTimesheetTemplateId) {
		this.otTimesheetTemplateId = otTimesheetTemplateId;
	}
	public String getOtTimesheetTemplateName() {
		return otTimesheetTemplateName;
	}
	public void setOtTimesheetTemplateName(String otTimesheetTemplateName) {
		this.otTimesheetTemplateName = otTimesheetTemplateName;
	}
	public Long getOtTimesheetStatusId() {
		return otTimesheetStatusId;
	}
	public void setOtTimesheetStatusId(Long otTimesheetStatusId) {
		this.otTimesheetStatusId = otTimesheetStatusId;
	}
	public String getOtTimesheetFormName() {
		return otTimesheetFormName;
	}
	public void setOtTimesheetFormName(String otTimesheetFormName) {
		this.otTimesheetFormName = otTimesheetFormName;
	}
	public Long getOtTimesheetNumber() {
		return otTimesheetNumber;
	}
	public void setOtTimesheetNumber(Long otTimesheetNumber) {
		this.otTimesheetNumber = otTimesheetNumber;
	}
	public Long getCompanyId() {
		return companyId;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
	

}

