package com.payasia.common.dto;

import java.io.Serializable;

public class CompanyModuleDTO implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3051199410098814463L;
	private boolean hasHrisModule;
	private boolean hasClaimModule;
	private boolean hasLeaveModule;
	private boolean hasLundinTimesheetModule;
	private boolean hasMobile;
	private boolean hasLionTimesheetModule;
	private boolean hasCoherentTimesheetModule;
private boolean ssoEnabled;
	
	
	public boolean isHasMobile() {
		return hasMobile;
	}
	public void setHasMobile(boolean hasMobile) {
		this.hasMobile = hasMobile;
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
	public boolean isHasLundinTimesheetModule() {
		return hasLundinTimesheetModule;
	}
	public void setHasLundinTimesheetModule(boolean hasLundinTimesheetModule) {
		this.hasLundinTimesheetModule = hasLundinTimesheetModule;
	}
	public boolean isHasLionTimesheetModule() {
		return hasLionTimesheetModule;
	}
	public void setHasLionTimesheetModule(boolean hasLionTimesheetModule) {
		this.hasLionTimesheetModule = hasLionTimesheetModule;
	}
	public boolean isHasCoherentTimesheetModule() {
		return hasCoherentTimesheetModule;
	}
	public void setHasCoherentTimesheetModule(boolean hasCoherentTimesheetModule) {
		this.hasCoherentTimesheetModule = hasCoherentTimesheetModule;
	}
	public boolean isSsoEnabled() {
		return ssoEnabled;
	}
	public void setSsoEnabled(boolean ssoEnabled) {
		this.ssoEnabled = ssoEnabled;
	}
	
}
