/**
 * @author ragulapraveen
 *
 */
package com.payasia.web.controller;

import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

import com.payasia.common.form.AddLeaveForm;
import com.payasia.common.form.EmployeePaySlipForm;
import com.payasia.common.form.EmployeeWorkFlowForm;
import com.payasia.common.form.PendingItemsForm;

/**
 * The Interface EmployeeHomeController.
 */
public interface EmployeeHomeController {

	/**
	 * Purpose : Opens modifyClaims page.
	 * 
	 * @return the model and view
	 */
	ModelAndView modifyClaimsPage();

	/**
	 * Purpose : Opens withdrawClaims page.
	 * 
	 * @return the model and view
	 */
	ModelAndView withdrawClaimsPage();

	/**
	 * Purpose : Opens changePassword page.
	 * 
	 * @return the model and view
	 */
	ModelAndView changePasswordPage(ModelMap model, HttpServletRequest request,
			Locale locale);

	/**
	 * Purpose : Opens myBalances page.
	 * 
	 * @return the model and view
	 */
	ModelAndView myBalancesPage();

	/**
	 * Purpose : Opens switchUser page.
	 * 
	 * @return the model and view
	 */
	ModelAndView switchUserPage();

	/**
	 * Purpose : Opens modifyLeaves page.
	 * 
	 * @return the model and view
	 */
	ModelAndView swithDrawLeaves();

	/**
	 * Purpose : Opens withDrawLeaves page.
	 * 
	 * @return the model and view
	 */
	ModelAndView withDrawLeaves();

	/**
	 * Purpose : Opens oTModify page.
	 * 
	 * @return the model and view
	 */
	ModelAndView oTModifyPage();

	/**
	 * Purpose : Opens oTWithdraw page.
	 * 
	 * @return the model and view
	 */
	ModelAndView oTWithdrawPage();

	/**
	 * Purpose : Opens myRequest page.
	 * 
	 * @return the model and view
	 */
	ModelAndView showMyRequest();

	/**
	 * Purpose : Opens contactUs page.
	 * 
	 * @param model
	 *            the model
	 * @return the model and view
	 */
	ModelAndView showContactUs(ModelMap model);

	/**
	 * Purpose : Opens paySlip page.
	 * 
	 * @param model
	 *            the model
	 * @param employeePaySlip
	 *            the employee pay slip
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @param locale
	 *            the locale
	 * @return the model and view
	 */
	ModelAndView paySlipPage(ModelMap model,
			EmployeePaySlipForm employeePaySlip, HttpServletRequest request,
			HttpServletResponse response, Locale locale);

	/**
	 * Purpose : Opens Employee Home page.
	 * 
	 * @param model
	 *            the model
	 * @param request
	 *            the request
	 * @param locale
	 *            the locale
	 * @return the model and view
	 */
	ModelAndView homePage(ModelMap model, HttpServletRequest request,
			Locale locale);

	/**
	 * Purpose : Opens addLeaves page.
	 * 
	 * @param model
	 *            the model
	 * @param addLeaveForm
	 *            the add leave form
	 * @param request
	 *            the request
	 * @return the model and view
	 */
	ModelAndView showEmpaddLeave(ModelMap model, AddLeaveForm addLeaveForm,
			HttpServletRequest request, Locale locale);

	/**
	 * Purpose : Opens addLeaves page.
	 * 
	 * @param model
	 *            the model
	 * @param pendingItemsForm
	 *            the pending items form
	 * @return the model and view
	 */
	ModelAndView showPendingItems(ModelMap model,
			PendingItemsForm pendingItemsForm, Locale locale);

	/**
	 * Purpose : Opens claimListPage page.
	 * 
	 * @param model
	 *            the model
	 * @param request
	 *            the request
	 * @return the model and view
	 */
	ModelAndView claimListPage(ModelMap model, HttpServletRequest request);

	/**
	 * Purpose : Opens oTAddPage .
	 * 
	 * @param model
	 *            the model
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @return the model and view
	 */
	ModelAndView oTAddPage(ModelMap model, HttpServletRequest request,
			HttpServletResponse response);

	/**
	 * Employee change password.
	 * 
	 * @param model
	 *            the model
	 * @param request
	 *            the request
	 * @param locale
	 *            the locale
	 * @return the model and view
	 */
	ModelAndView employeeChangePassword(ModelMap model,
			HttpServletRequest request, Locale locale);

	ModelAndView employeeLeaveBalanceSummary(ModelMap model,
			HttpServletRequest request, Locale locale);

	ModelAndView showPendingClaims(ModelMap model);

	ModelAndView myClaimsPage(ModelMap model, HttpServletRequest request);

	ModelAndView leaveReports(ModelMap model);

	ModelAndView hrisMyRequest(ModelMap model, HttpServletRequest request,
			Locale locale);

	ModelAndView hrisPendingItems(ModelMap model, HttpServletRequest request,
			Locale locale);

	/**
	 * Purpose : Opens information page.
	 * 
	 * @return the model and view
	 */

	ModelAndView employeeHomePage(ModelMap model);

	ModelAndView employeHrLetters(Map model, HttpServletRequest request,
			HttpServletResponse response);

	ModelAndView showWorkFlowDelegate(ModelMap model,
			EmployeeWorkFlowForm employeeWorkFlowForm,
			HttpServletRequest request, Locale locale);

	/**
	 * Purpose : Opens employee document center page.
	 * 
	 * @param map
	 *            the map
	 * @return the model and view
	 */
	ModelAndView documentCenterPage(ModelMap model, Locale locale);

	/**
	 * Purpose : To Know Employee Claim Summary Details.
	 * 
	 * @param map
	 *            the map
	 * @return the model and view
	 */
	ModelAndView employeeClaimSummary(Map model, HttpServletRequest request,
			HttpServletResponse response);

	ModelAndView getLundinTimesheet(Map model, HttpServletRequest request,
			HttpServletResponse response);

	ModelAndView getLundinPendingTimesheet(Map model,
			HttpServletRequest request, HttpServletResponse response);

	ModelAndView lionhkMyTimesheet(ModelMap model, HttpServletRequest request,
			HttpServletResponse response);

	ModelAndView lionhkPendingTimesheet(ModelMap model,
			HttpServletRequest request, HttpServletResponse response);

	ModelAndView lionTimesheetReports(ModelMap model,
			HttpServletRequest request, HttpServletResponse response);

	ModelAndView coherentOvertimeTimesheet(ModelMap model,
			HttpServletRequest request, HttpServletResponse response);

	ModelAndView myShift(Map model, HttpServletRequest request,
			HttpServletResponse response);

	ModelAndView coherentEmployeeOvertime(ModelMap model,
			HttpServletRequest request, HttpServletResponse response);

	ModelAndView coherentEmployeeShift(Map model, HttpServletRequest request,
			HttpServletResponse response);

	ModelAndView coherentTimesheetReports(ModelMap model,
			HttpServletRequest request, HttpServletResponse response);

	ModelAndView lundinTimesheetReports(ModelMap model,
			HttpServletRequest request, HttpServletResponse response);

	ModelAndView payasiaEmployeeOvertime(ModelMap model, 
			HttpServletRequest request, HttpServletResponse response);

	ModelAndView payasiaOvertimeTimesheet(ModelMap model, 
			HttpServletRequest request, HttpServletResponse response);
	
	ModelAndView managerClaimReports(ModelMap model);

}
