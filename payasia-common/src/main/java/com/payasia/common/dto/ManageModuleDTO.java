package com.payasia.common.dto;

import java.io.Serializable;

public class ManageModuleDTO implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	
	private String companyName;
	private String companyCode;
	private Long companyId;
	private String groupName;
	private Long groupId;
	private String groupCode;
	private Long moduleId;
	private String moduleName;
	private boolean hasHrisModule;
	private boolean hasClaimModule;
	private boolean hasLeaveModule;
	private boolean hasOTTimesheetModule;
	private boolean hasLundinTimesheetModule;
	private boolean hasLionTimesheetModule;
	private boolean hasMobile;
	private boolean hasCoherentTimesheetModule;

	
	public boolean isHasCoherentTimesheetModule() {
		return hasCoherentTimesheetModule;
	}
	public void setHasCoherentTimesheetModule(boolean hasCoherentTimesheetModule) {
		this.hasCoherentTimesheetModule = hasCoherentTimesheetModule;
	}
	
	

	public ManageModuleDTO() {
	
	}
	
	
	public ManageModuleDTO(String companyName, String companyCode,
			Long companyId, String groupName, Long groupId, String groupCode,
			Long moduleId, String moduleName) {
		super();
		this.companyName = companyName;
		this.companyCode = companyCode;
		this.companyId = companyId;
		this.groupName = groupName;
		this.groupId = groupId;
		this.groupCode = groupCode;
		this.moduleId = moduleId;
		this.moduleName = moduleName;
	}
	
	
	
	
	
	
	public Long getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
	public Long getGroupId() {
		return groupId;
	}
	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}
	public String getGroupCode() {
		return groupCode;
	}
	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	public Long getModuleId() {
		return moduleId;
	}
	public void setModuleId(Long moduleId) {
		this.moduleId = moduleId;
	}
	
	
	public boolean isHasMobile() {
		return hasMobile;
	}
	public void setHasMobile(boolean hasMobile) {
		this.hasMobile = hasMobile;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getCompanyCode() {
		return companyCode;
	}
	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}
	public boolean isHasHrisModule() {
		return hasHrisModule;
	}
	public void setHasHrisModule(boolean hasHrisModule) {
		this.hasHrisModule = hasHrisModule;
	}
	public boolean isHasClaimModule() {
		return hasClaimModule;
	}
	public void setHasClaimModule(boolean hasClaimModule) {
		this.hasClaimModule = hasClaimModule;
	}
	public boolean isHasLeaveModule() {
		return hasLeaveModule;
	}
	public void setHasLeaveModule(boolean hasLeaveModule) {
		this.hasLeaveModule = hasLeaveModule;
	}
	public boolean isHasOTTimesheetModule() {
		return hasOTTimesheetModule;
	}
	public void setHasOTTimesheetModule(boolean hasOTTimesheetModule) {
		this.hasOTTimesheetModule = hasOTTimesheetModule;
	}
	public boolean isHasLundinTimesheetModule() {
		return hasLundinTimesheetModule;
	}
	public void setHasLundinTimesheetModule(boolean hasLundinTimesheetModule) {
		this.hasLundinTimesheetModule = hasLundinTimesheetModule;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((companyId == null) ? 0 : companyId.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ManageModuleDTO other = (ManageModuleDTO) obj;
		if (companyId == null) {
			if (other.companyId != null)
				return false;
		} else if (!companyId.equals(other.companyId))
			return false;
		return true;
	}

	public boolean isHasLionTimesheetModule() {
		return hasLionTimesheetModule;
	}


	public void setHasLionTimesheetModule(boolean hasLionTimesheetModule) {
		this.hasLionTimesheetModule = hasLionTimesheetModule;
	}
	
}
