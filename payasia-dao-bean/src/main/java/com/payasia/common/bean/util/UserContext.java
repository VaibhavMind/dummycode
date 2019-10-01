package com.payasia.common.bean.util;

import java.util.Locale;

import org.apache.commons.lang.StringUtils;

public class UserContext {

	public static final InheritableThreadLocal<String> userIdThreadLocal = new InheritableThreadLocal<>();
	public static final InheritableThreadLocal<String> workingCompanyIdThreadLocal = new InheritableThreadLocal<>();
	public static final InheritableThreadLocal<String> workingCompanyTimeZoneGMTOffsetThreadLocal = new InheritableThreadLocal<>();
	public static final InheritableThreadLocal<String> workingCompanyDateFormatThreadLocal = new InheritableThreadLocal<>();
	public static final InheritableThreadLocal<Long> languageIdThreadLocal = new InheritableThreadLocal<>();
	public static final InheritableThreadLocal<Locale> localeThreadLocal = new InheritableThreadLocal<>();
	public static final InheritableThreadLocal<String> privateKeyThreadLocal = new InheritableThreadLocal<>();

	/* New ADDED */
	public static final InheritableThreadLocal<String> ipAddressThreadLocal = new InheritableThreadLocal<>();
	public static final InheritableThreadLocal<String> companyCodeThreadLocal = new InheritableThreadLocal<>();
	public static final InheritableThreadLocal<String> loginIdThreadLocal = new InheritableThreadLocal<>();
	public static final InheritableThreadLocal<String> passwordThreadLocal = new InheritableThreadLocal<>();
	public static final InheritableThreadLocal<String> tabThreadLocal = new InheritableThreadLocal<>();

	/* Module */
	public static final InheritableThreadLocal<Boolean> hasHrisModuleThreadLocal = new InheritableThreadLocal<>();
	public static final InheritableThreadLocal<Boolean> hasClaimModuleThreadLocal = new InheritableThreadLocal<>();
	public static final InheritableThreadLocal<Boolean> hasLeaveModuleThreadLocal = new InheritableThreadLocal<>();
	public static final InheritableThreadLocal<Boolean> hasMobileModuleThreadLocal = new InheritableThreadLocal<>();
	public static final InheritableThreadLocal<Boolean> hasLundinTimesheetModuleThreadLocal = new InheritableThreadLocal<>();
	public static final InheritableThreadLocal<Boolean> hasLionTimesheetModuleThreadLocal = new InheritableThreadLocal<>();
	public static final InheritableThreadLocal<Boolean> hasCoherentTimesheetModuleThreadLocal = new InheritableThreadLocal<>();
	
	public static final InheritableThreadLocal<String> deviceThreadLocal = new InheritableThreadLocal<>();
	
	public static final InheritableThreadLocal<Long> clientAdminIdThreadLocal = new InheritableThreadLocal<>();

	public static void setUserId(String userid) {
		userIdThreadLocal.set(userid);
	}

	public static String getUserId() {
		String userId = userIdThreadLocal.get();
		if (StringUtils.isBlank(userId) || "anonymousUser".equalsIgnoreCase(userId)) {
			userId = "0";
		}
		return userId;
	}

	public static void setLocale(Locale locale) {
		localeThreadLocal.set(locale);
	}

	public static Locale getLocale() {
		Locale locale = localeThreadLocal.get();
		if (locale == null) {
			locale = new Locale("en_US");
		}
		return locale;
	}

	public static void setLanguageId(Long languageId) {
		languageIdThreadLocal.set(languageId);
	}

	public static Long getLanguageId() {
		Long languageId = languageIdThreadLocal.get();
		if (languageId == null) {
			languageId = 1l;
		}
		return languageId;
	}

	public static void setWorkingCompanyId(String workingCompanyId) {
		workingCompanyIdThreadLocal.set(workingCompanyId);
	}

	public static void setWorkingCompanyTimeZoneGMTOffset(String workingCompanyTimeZoneGMTOffset) {
		workingCompanyTimeZoneGMTOffsetThreadLocal.set(workingCompanyTimeZoneGMTOffset);
	}

	public static String getWorkingCompanyId() {
		String workingCompanyId = workingCompanyIdThreadLocal.get();
		if (StringUtils.isBlank(workingCompanyId) || "null".equalsIgnoreCase(workingCompanyId)) {
			workingCompanyId = "0";
		}
		return workingCompanyId;
	}

	public static String getWorkingCompanyTimeZoneGMTOffset() {
		String workingCompanyTimeZoneGMTOffset = workingCompanyTimeZoneGMTOffsetThreadLocal.get();
		if (StringUtils.isBlank(workingCompanyTimeZoneGMTOffset) && !"null".equals(workingCompanyTimeZoneGMTOffset)) {

			workingCompanyTimeZoneGMTOffset = "+00:00";
		}
		return workingCompanyTimeZoneGMTOffset;
	}

	public static void setWorkingCompanyDateFormat(String workingCompanyDateFormat) {
		workingCompanyDateFormatThreadLocal.set(workingCompanyDateFormat);
	}

	public static String getWorkingCompanyDateFormat() {
		String workingCompanyDateFormat = workingCompanyDateFormatThreadLocal.get();
		if (StringUtils.isBlank(workingCompanyDateFormat) && !"null".equals(workingCompanyDateFormat)) {

			workingCompanyDateFormat = "";
		}

		return workingCompanyDateFormat;
	}

	public static String getKey() {
		String key = privateKeyThreadLocal.get();
		return key;
	}

	public static void setKey(String key) {
		privateKeyThreadLocal.set(key);
	}

	public static String getIpAddress() {
		String ipAddress = ipAddressThreadLocal.get();
		return ipAddress;
	}

	public static void setIpAddress(String ipAddress) {
		ipAddressThreadLocal.set(ipAddress);
	}

	public static String getCompanyCode() {
		String companyCode = companyCodeThreadLocal.get();
		return companyCode;
	}

	public static void setCompanyCode(String companyCode) {
		companyCodeThreadLocal.set(companyCode);
	}

	public static String getLoginId() {
		String loginId = loginIdThreadLocal.get();
		return loginId;
	}

	public static void setLoginId(String loginId) {
		loginIdThreadLocal.set(loginId);
	}

	public static String getPassword() {
		String password = passwordThreadLocal.get();
		return password;
	}

	public static void setPassword(String password) {
		passwordThreadLocal.set(password);

	}

	public static String getTab() {
		String password = tabThreadLocal.get();
		return password;
	}

	public static void setTab(String token) {
		tabThreadLocal.set(token);

	}

	public static boolean isHrisModule() {
		return hasHrisModuleThreadLocal.get();

	}

	public static void setHrisModule(boolean isHrisModule) {
		hasHrisModuleThreadLocal.set(isHrisModule);
	}

	public static boolean isClaimModule() {
		return hasClaimModuleThreadLocal.get();

	}

	public static void setClaimModule(boolean isClaimModule) {
		hasClaimModuleThreadLocal.set(isClaimModule);
	}

	public static boolean isLeaveModule() {
		return hasLeaveModuleThreadLocal.get();

	}

	public static void setLeaveModule(boolean isLeaveModule) {
		hasLeaveModuleThreadLocal.set(isLeaveModule);
	}

	public static boolean isMobileModule() {
		return hasMobileModuleThreadLocal.get();

	}

	public static void setMobileModule(boolean isMobileModule) {
		hasMobileModuleThreadLocal.set(isMobileModule);
	}

	public static boolean isLundinTimesheetModule() {
		return hasLundinTimesheetModuleThreadLocal.get();

	}

	public static void setLundinTimesheetModule(boolean isLundinTimesheetModule) {
		hasLundinTimesheetModuleThreadLocal.set(isLundinTimesheetModule);
	}

	public static boolean isLionTimesheetModule() {
		return hasLionTimesheetModuleThreadLocal.get();

	}

	public static void setLionTimesheetModule(boolean isLionTimesheetModule) {
		hasLionTimesheetModuleThreadLocal.set(isLionTimesheetModule);
	}

	public static boolean isCoherentTimesheetModule() {
		return hasCoherentTimesheetModuleThreadLocal.get();

	}

	public static void setCoherentTimesheetModule(boolean isCoherentTimesheetModule) {
		hasCoherentTimesheetModuleThreadLocal.set(isCoherentTimesheetModule);
	}
	
	public static String getDevice() {
	  return deviceThreadLocal.get();

	}

	public static void setDevice(String device) {
		deviceThreadLocal.set(device);
	}
	
	public static Long getClientAdminId() {
		  return  clientAdminIdThreadLocal.get();
	}

	public static void setClientAdminId(Long clientAdminId) {
		clientAdminIdThreadLocal.set(clientAdminId);
	}

}
