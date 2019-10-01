package com.payasia.common.dto;

import com.payasia.common.form.ClaimTemplateForm;

public class EmployeeClaimTemplateDataDTO {

	private Long employeeClaimTemplateId;
	private Long claimTemplateId;
	private String templateName;
	private String entitlementType;
	private Float totalClaimEntitlement;
	private Float usedClaimEntitlement;
	private Float leftClaimEntitlement;
	private Long employeeId;
	
	private ClaimTemplateForm claimTemplateConfig;

	public Long getEmployeeClaimTemplateId() {
		return employeeClaimTemplateId;
	}
	public void setEmployeeClaimTemplateId(Long employeeClaimTemplateId) {
		this.employeeClaimTemplateId = employeeClaimTemplateId;
	}
	public Long getClaimTemplateId() {
		return claimTemplateId;
	}
	public void setClaimTemplateId(Long claimTemplateId) {
		this.claimTemplateId = claimTemplateId;
	}
	public String getTemplateName() {
		return templateName;
	}
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}
	public String getEntitlementType() {
		return entitlementType;
	}
	public void setEntitlementType(String entitlementType) {
		this.entitlementType = entitlementType;
	}
	public Float getTotalClaimEntitlement() {
		return totalClaimEntitlement;
	}
	public void setTotalClaimEntitlement(Float totalClaimEntitlement) {
		this.totalClaimEntitlement = totalClaimEntitlement;
	}
	public Float getUsedClaimEntitlement() {
		return usedClaimEntitlement;
	}
	public void setUsedClaimEntitlement(Float usedClaimEntitlement) {
		this.usedClaimEntitlement = usedClaimEntitlement;
	}
	public Float getLeftClaimEntitlement() {
		return leftClaimEntitlement;
	}
	public void setLeftClaimEntitlement(Float leftClaimEntitlement) {
		this.leftClaimEntitlement = leftClaimEntitlement;
	}
	public Long getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}
	public ClaimTemplateForm getClaimTemplateConfig() {
		return claimTemplateConfig;
	}
	public void setClaimTemplateConfig(ClaimTemplateForm claimTemplateConfig) {
		this.claimTemplateConfig = claimTemplateConfig;
	}
	
}
