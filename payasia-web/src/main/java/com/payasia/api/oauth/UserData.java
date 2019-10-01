package com.payasia.api.oauth;

import java.io.Serializable;
import java.util.Locale;

/**
 * @author manojkumar2
 * @param :
 *            This class used to put all required information related to
 *            employee
 */
public class UserData implements Serializable {

	private static final long serialVersionUID = 1L;

	private String employeeNumber;
	private String companyCode;
	private String userId;
	private String workingCompanyId;
	private String workingCompanyTimeZone;
	private String workingCompanyDateFormat;
	private Long languageId;
	private String privateKey;
	private String tabID;
	private boolean hasHrisModule;
	private boolean hasClaimModule;
	private boolean hasLeaveModule;
	private boolean hasMobileModule;
	private boolean hasLundinTimesheetModule;
	private boolean hasLionTimesheetModule;
	private boolean hasCoherentTimesheetModule;
	private Locale locale;
	private String deviceName;
	private String ipAddress;
	private Long clientAdminId;

	public UserData() {
	}

	public UserData(String employeeNumber, String companyCode, String userId, String workingCompanyId,
			String workingCompanyTimeZone, String workingCompanyDateFormat, Long languageId, String privateKey,
			String tabID, boolean hasHrisModule, boolean hasClaimModule, boolean hasLeaveModule,
			boolean hasMobileModule, boolean hasLundinTimesheetModule, boolean hasLionTimesheetModule,
			boolean hasCoherentTimesheetModule, Locale locale,String deviceName,String ipAddress,Long clientAdminId) {
		super();
		this.employeeNumber = employeeNumber;
		this.companyCode = companyCode;
		this.userId = userId;
		this.workingCompanyId = workingCompanyId;
		this.workingCompanyTimeZone = workingCompanyTimeZone;
		this.workingCompanyDateFormat = workingCompanyDateFormat;
		this.languageId = languageId;
		this.privateKey = privateKey;
		this.tabID = tabID;
		this.hasHrisModule = hasHrisModule;
		this.hasClaimModule = hasClaimModule;
		this.hasLeaveModule = hasLeaveModule;
		this.hasMobileModule = hasMobileModule;
		this.hasLundinTimesheetModule = hasLundinTimesheetModule;
		this.hasLionTimesheetModule = hasLionTimesheetModule;
		this.hasCoherentTimesheetModule = hasCoherentTimesheetModule;
		this.locale=locale;
		this.deviceName=deviceName;
		this.ipAddress=ipAddress;
		this.clientAdminId=clientAdminId;
	}

	public String getEmployeeNumber() {
		return employeeNumber;
	}

	public void setEmployeeNumber(String employeeNumber) {
		this.employeeNumber = employeeNumber;
	}

	public String getCompanyCode() {
		return companyCode;
	}

	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getWorkingCompanyId() {
		return workingCompanyId;
	}

	public void setWorkingCompanyId(String workingCompanyId) {
		this.workingCompanyId = workingCompanyId;
	}

	public String getWorkingCompanyTimeZone() {
		return workingCompanyTimeZone;
	}

	public void setWorkingCompanyTimeZone(String workingCompanyTimeZone) {
		this.workingCompanyTimeZone = workingCompanyTimeZone;
	}

	public String getWorkingCompanyDateFormat() {
		return workingCompanyDateFormat;
	}

	public void setWorkingCompanyDateFormat(String workingCompanyDateFormat) {
		this.workingCompanyDateFormat = workingCompanyDateFormat;
	}

	public Long getLanguageId() {
		return languageId;
	}

	public void setLanguageId(Long languageId) {
		this.languageId = languageId;
	}

	public String getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}

	public String getTabID() {
		return tabID;
	}

	public void setTabID(String tabID) {
		this.tabID = tabID;
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

	public boolean isHasMobileModule() {
		return hasMobileModule;
	}

	public void setHasMobileModule(boolean hasMobileModule) {
		this.hasMobileModule = hasMobileModule;
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

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}
	
	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	
	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public Long getClientAdminId() {
		return clientAdminId;
	}

	public void setClientAdminId(Long clientAdminId) {
		this.clientAdminId = clientAdminId;
	}

	@Override
	public String toString() {
		return "UserData [employeeNumber=" + employeeNumber + ", companyCode=" + companyCode + ", userId=" + userId
				+ ", workingCompanyId=" + workingCompanyId + ", workingCompanyTimeZone=" + workingCompanyTimeZone
				+ ", workingCompanyDateFormat=" + workingCompanyDateFormat + ", languageId=" + languageId
				+ ", privateKey=" + privateKey + "]";
	}

}
