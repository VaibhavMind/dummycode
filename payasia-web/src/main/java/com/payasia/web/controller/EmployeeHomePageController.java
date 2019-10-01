package com.payasia.web.controller;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

public interface EmployeeHomePageController {

	String getPayslipDetails(HttpServletRequest request,
			HttpServletResponse response);

	String getLeaveDetails(HttpServletRequest request,
			HttpServletResponse response);

	String getClaimDetails(HttpServletRequest request,
			HttpServletResponse response);

	String getPasswordExpiryReminder(HttpServletRequest request,
			HttpServletResponse response);

	byte[] downloadMobileApplication(String applicationType,
			HttpServletRequest request, HttpServletResponse response);

	String getAllRecentActivityList(HttpServletRequest request,
			HttpServletResponse response, Locale locale);

	ModelAndView getHomePendingLundin(ModelMap model);

	ModelAndView getHomeSubmittedLundin(ModelMap model);

	String getDefaultEmailCCListByEmployee(String moduleName,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale);

	void saveDefaultEmailCCByEmployee(String ccEmailIds, String moduleName,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale);

	String getDefaultEmailCCByEmp(String moduleName,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale);

	ModelAndView getHomePendingLion(ModelMap model);

	ModelAndView getHomeSubmittedIngersoll(ModelMap model);

	ModelAndView getHomeSubmittedLion(ModelMap model);

	ModelAndView getHomeSubmittedCoherent(ModelMap model);

	ModelAndView getHomePendingCoherent(ModelMap model);

	ModelAndView getHomeSubmittedShiftCoherent(ModelMap model);

	ModelAndView getHomePendingShiftCoherent(ModelMap model);

}
