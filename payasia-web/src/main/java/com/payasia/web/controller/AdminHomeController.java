package com.payasia.web.controller;

import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

import com.payasia.common.dto.SsoConfigurationDTO;
import com.payasia.common.form.AdminPaySlipForm;
import com.payasia.common.form.ClaimBatchForm;
import com.payasia.common.form.CompanyDynamicForm;
import com.payasia.common.form.DataExportForm;
import com.payasia.common.form.DataImportForm;
import com.payasia.common.form.EmployeeDynamicForm;
import com.payasia.common.form.EmployeeNumberSrForm;
import com.payasia.common.form.EmployeeTaxDocumentForm;
import com.payasia.common.form.ExcelExportToolForm;
import com.payasia.common.form.ExcelImportToolForm;
import com.payasia.common.form.HolidayListForm;
import com.payasia.common.form.LeaveBatchForm;
import com.payasia.common.form.MailTemplateListForm;
import com.payasia.common.form.PaySlipDynamicForm;
import com.payasia.common.form.PendingItemsForm;

/**
 * The Interface AdminHomeController.
 */
/**
 * @author vivekjain
 * 
 */
public interface AdminHomeController {

	/**
	 * Pay code.
	 * 
	 * @param model
	 *            the model
	 * @return the model and view
	 */
	ModelAndView payCode(ModelMap model);

	/**
	 * Adds the employee.
	 * 
	 * @return the model and view
	 */
	ModelAndView addEmployee();

	/**
	 * Adminhome page.
	 * 
	 * @return the model and view
	 */
	ModelAndView adminhomePage();

	/**
	 * Employee page.
	 * 
	 * @param model
	 *            the model
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @return the model and view
	 */
	ModelAndView employeePage(ModelMap model, HttpServletRequest request, HttpServletResponse response);

	/**
	 * Payroll page.
	 * 
	 * @return the model and view
	 */
	ModelAndView payrollPage();

	/**
	 * Reporting page.
	 * 
	 * @return the model and view
	 */
	ModelAndView reportingPage();

	/**
	 * Tool page.
	 * 
	 * @return the model and view
	 */
	ModelAndView toolPage();

	/**
	 * Task page.
	 * 
	 * @return the model and view
	 */
	ModelAndView taskPage();

	/**
	 * Employee information new page.
	 * 
	 * @return the model and view
	 */
	ModelAndView employeeInformationNewPage();

	/**
	 * Pending task page.
	 * 
	 * @return the model and view
	 */
	ModelAndView pendingTaskPage();

	/**
	 * Edits the view.
	 * 
	 * @return the model and view
	 */
	ModelAndView editView();

	/**
	 * Claims batch process.
	 * 
	 * @param model
	 *            the model
	 * @param claimBatchForm
	 *            the claim batch form
	 * @return the model and view
	 */
	ModelAndView claimsBatchProcess(ModelMap model, ClaimBatchForm claimBatchForm);

	/**
	 * Send password.
	 * 
	 * @param model
	 *            the model
	 * @return the model and view
	 */
	ModelAndView sendPAssword(Map model);

	/**
	 * Claim item definition page.
	 * 
	 * @param model
	 *            the model
	 * @return the model and view
	 */
	ModelAndView claimItemDefinitionPage(ModelMap model);

	/**
	 * Delete employee.
	 * 
	 * @return the model and view
	 */
	ModelAndView deleteEmployee();

	/**
	 * Access control.
	 * 
	 * @return the model and view
	 */
	ModelAndView accessControl();

	/**
	 * Company information page.
	 * 
	 * @param ModelMap
	 *            the mod
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @return the model and view
	 */
	ModelAndView companyInformationPage(ModelMap mod, HttpServletRequest request, HttpServletResponse response);

	/**
	 * Ot type definition page.
	 * 
	 * @param model
	 *            the model
	 * @return the model and view
	 */
	ModelAndView otTypeDefinitionPage(ModelMap model);

	/**
	 * Email template page.
	 * 
	 * @return the model and view
	 */
	ModelAndView emailTemplatePage();

	/**
	 * Show switch user.
	 * 
	 * @param model
	 *            the model
	 * @return the model and view
	 */
	ModelAndView showSwitchUser(Map model);

	/**
	 * Show change employee Number page.
	 * 
	 * @param ModelMap
	 *            the mod
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @return the model and view
	 */
	ModelAndView showChangeEmpNo(ModelMap mod, HttpServletRequest request, HttpServletResponse response);

	/**
	 * Email preference page.
	 * 
	 * @param model
	 *            the model
	 * @return the model and view
	 */
	ModelAndView emailPreferencePage(Map model);

	/**
	 * Claims option preference page.
	 * 
	 * @return the model and view
	 */
	ModelAndView claimsOptionPreferencePage();

	/**
	 * Password policy preference page.
	 * 
	 * @param model
	 *            the model
	 * @return the model and view
	 */
	ModelAndView passwordPolicyPreferencePage(Map model);

	/**
	 * Report options preference page.
	 * 
	 * @param ModelMap
	 *            the mod
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @return the model and view
	 */
	ModelAndView reportOptionsPreferencePage(ModelMap mod, HttpServletRequest request, HttpServletResponse response);

	/**
	 * Logo upload page.
	 * 
	 * @param model
	 *            the model
	 * @param request
	 *            the request
	 * @return the model and view
	 */
	ModelAndView logoUploadPage(ModelMap model, HttpServletRequest request);

	/**
	 * Document center page.
	 * 
	 * @return the model and view
	 */
	ModelAndView documentCenterPage();

	/**
	 * Leave type definition page.
	 * 
	 * @param model
	 *            the model
	 * @return the model and view
	 */
	ModelAndView leaveTypeDefinitionPage(ModelMap model, HttpServletRequest request, HttpServletResponse response);

	/**
	 * Employee preference page.
	 * 
	 * @param mod
	 *            the mod
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @return the model and view
	 */
	ModelAndView employeePreferencePage(ModelMap mod, HttpServletRequest request, HttpServletResponse response);

	/**
	 * Pay slip designer page.
	 * 
	 * @param model
	 *            the model
	 * @return the model and view
	 */
	ModelAndView paySlipDesignerPage(ModelMap model, HttpServletRequest request, HttpServletResponse response);

	/**
	 * Show pay slip import.
	 * 
	 * @return the model and view
	 */
	ModelAndView showPaySlipImport();

	/**
	 * Pay data collection.
	 * 
	 * @param model
	 *            the model
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @return the model and view
	 */
	ModelAndView payDataCollection(ModelMap model, HttpServletRequest request, HttpServletResponse response);

	/**
	 * Manage employee form page.
	 * 
	 * @param model
	 *            the model
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @param employeeDynamicForm
	 *            the employee dynamic form
	 * @return the model and view
	 */
	ModelAndView manageEmployeeFormPage(ModelMap model, HttpServletRequest request, HttpServletResponse response,
			EmployeeDynamicForm employeeDynamicForm);

	/**
	 * Show holiday list master.
	 * 
	 * @param model
	 *            the model
	 * @return the model and view
	 */
	ModelAndView showHoliDayListMaster(ModelMap model);

	/**
	 * Adds the company.
	 * 
	 * @param ModelMap
	 *            the map
	 * @return the model and view
	 */
	ModelAndView addCompany(ModelMap map);

	/**
	 * Edits the company.
	 * 
	 * @param ModelMap
	 *            the model
	 * @return the model and view
	 */
	ModelAndView editCompany(ModelMap model);

	/**
	 * Show pay slip form designer.
	 * 
	 * @param ModelMap
	 *            the model
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @param paySlipDynamicForm
	 *            the pay slip dynamic form
	 * @return the model and view
	 */
	ModelAndView showPaySlipFormDesigner(ModelMap model, HttpServletRequest request, HttpServletResponse response,
			PaySlipDynamicForm paySlipDynamicForm);

	/**
	 * Company form designer.
	 * 
	 * @param ModelMap
	 *            the model
	 * @param companyDynamicForm
	 *            the company dynamic form
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @return the model and view
	 */
	ModelAndView companyFormDesigner(ModelMap model, CompanyDynamicForm companyDynamicForm, HttpServletRequest request,
			HttpServletResponse response);

	/**
	 * Leave batch definition page.
	 * 
	 * @param ModelMap
	 *            the model
	 * @param leaveBatchForm
	 *            the leave batch form
	 * @return the model and view
	 */
	ModelAndView leaveBatchDefinitionPage(ModelMap model, LeaveBatchForm leaveBatchForm);

	/**
	 * Payslip page.
	 * 
	 * @param ModelMap
	 *            the model
	 * @param adminPaySlipForm
	 *            the admin pay slip form
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @param Locale
	 *            the locale
	 * @return the model and view
	 */
	ModelAndView payslipPage(ModelMap model, AdminPaySlipForm adminPaySlipForm, HttpServletRequest request,
			HttpServletResponse response, Locale locale);

	/**
	 * Tax documents page.
	 * 
	 * @param model
	 *            the model
	 * @param employeeTaxDocumentForm
	 *            the employee tax document form
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @return the model and view
	 */
	ModelAndView taxDocumentsPage(ModelMap model, EmployeeTaxDocumentForm employeeTaxDocumentForm,
			HttpServletRequest request, HttpServletResponse response);

	/**
	 * Home page.
	 * 
	 * @param model
	 *            the model
	 * @param request
	 *            the request
	 * @param locale
	 *            the locale
	 * @return the model and view
	 */
	ModelAndView homePage(ModelMap model, HttpServletRequest request, Locale locale);

	/**
	 * Show external link.
	 * 
	 * @param model
	 *            the model
	 * @return the model and view
	 */
	ModelAndView showExternalLink(ModelMap model);

	/**
	 * Ot batch definition page.
	 * 
	 * @param model
	 *            the model
	 * @return the model and view
	 */
	ModelAndView otBatchDefinitionPage(ModelMap model);

	/**
	 * Leave reviewer page.
	 * 
	 * @param model
	 *            the model
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @return the model and view
	 */
	ModelAndView leaveReviewerPage(ModelMap model, HttpServletRequest request, HttpServletResponse response);

	/**
	 * Claim reviewer page.
	 * 
	 * @param map
	 *            the map
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @return the model and view
	 */
	ModelAndView claimReviewerPage(ModelMap map, HttpServletRequest request, HttpServletResponse response);

	/**
	 * Claim templates page.
	 * 
	 * @param model
	 *            the model
	 * @return the model and view
	 */
	ModelAndView claimTemplatesPage(ModelMap model, Locale locale, HttpServletRequest request);

	/**
	 * Ot reviewer page.
	 * 
	 * @param map
	 *            the map
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @return the model and view
	 */
	ModelAndView otReviewerPage(ModelMap map, HttpServletRequest request, HttpServletResponse response);

	/**
	 * Ot template definition page.
	 * 
	 * @param model
	 *            the model
	 * @return the model and view
	 */
	ModelAndView otTemplateDefinitionPage(ModelMap model);

	/**
	 * Currency definition page.
	 * 
	 * @param mod
	 *            the mod
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @return the model and view
	 */
	ModelAndView currencyDefinitionPage(ModelMap mod, HttpServletRequest request, HttpServletResponse response);

	/**
	 * Show holi day list.
	 * 
	 * @param model
	 *            the model
	 * @param holidayListForm
	 *            the holiday list form
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @return the model and view
	 */
	ModelAndView showHoliDayList(ModelMap model, HolidayListForm holidayListForm, HttpServletRequest request,
			HttpServletResponse response);

	/**
	 * Switch company page.
	 * 
	 * @param model
	 *            the model
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @return the model and view
	 */
	ModelAndView switchCompanyPage(ModelMap model, HttpServletRequest request, HttpServletResponse response);

	/**
	 * Employee number sr.
	 * 
	 * @param model
	 *            the model
	 * @param request
	 *            the request
	 * @param employeeNumberSrForm
	 *            the employee number sr form
	 * @return the model and view
	 */
	ModelAndView employeeNumberSr(ModelMap model, HttpServletRequest request,
			EmployeeNumberSrForm employeeNumberSrForm);

	/**
	 * Purpose: To show Employee Calendar Defination page.
	 * 
	 * @param model
	 *            the model
	 * @param request
	 *            the request
	 * @param employeeNumberSrForm
	 *            the employee number sr form
	 * @return the model and view
	 */
	ModelAndView showEmployeeCalendarDef(ModelMap model, HttpServletRequest request);

	/**
	 * Assign leave scheme.
	 * 
	 * @param model
	 *            the model
	 * @param request
	 *            the request
	 * @return the model and view
	 */

	ModelAndView assignLeaveScheme(ModelMap model, HttpServletRequest request);

	ModelAndView showCalTempDef(ModelMap mod, HttpServletRequest request);

	ModelAndView leaveEventReminder(ModelMap model, HttpServletRequest request);

	ModelAndView leaveReports(ModelMap model);

	ModelAndView assignClaimTemplate(ModelMap model);

	ModelAndView claimPreference(ModelMap model);

	ModelAndView employeeClaims(ModelMap model, HttpServletRequest request);

	ModelAndView leaveTranscationReport(ModelMap model, HttpServletRequest request);

	ModelAndView payslipRelease(ModelMap model, HttpServletRequest request, HttpServletResponse response);

	/**
	 * Managers users for Defining Role And Privileges to Users.
	 * 
	 * @param model
	 *            the model
	 * @return the model and view
	 */
	ModelAndView mgrUsers(ModelMap model, HttpServletRequest request, HttpServletResponse response);

	ModelAndView claimReports(ModelMap model, HttpServletRequest request, HttpServletResponse response);

	ModelAndView leaveBalanceSummary(ModelMap model, HttpServletRequest request, Locale locale);

	ModelAndView endYearProcessingPage(ModelMap model, HttpServletRequest request, HttpServletResponse response,
			Locale locale);

	ModelAndView showPendingItems(ModelMap model, PendingItemsForm pendingItemsForm, Locale locale);

	ModelAndView leavePreference(ModelMap model, Locale locale);

	ModelAndView editEmployee(ModelMap model);

	ModelAndView hrisReviewer(ModelMap model, HttpServletRequest request, HttpServletResponse response);

	/**
	 * Show hr letters.
	 * 
	 * @param model
	 *            the model
	 * @return the model and view
	 */
	ModelAndView showHRLetters(Map model, HttpServletRequest request, HttpServletResponse response);

	ModelAndView hrisPreference(ModelMap model);

	/**
	 * Show doc centre.
	 * 
	 * @param model
	 *            the model
	 * @return the model and view
	 */
	ModelAndView showDocCentre(ModelMap model, HttpServletRequest request, HttpServletResponse response, Locale locale);

	/**
	 * Show multilingual page.
	 * 
	 * @param model
	 *            the model
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @param paySlipDynamicForm
	 *            the pay slip dynamic form
	 * @return the model and view
	 */
	ModelAndView showMultilingualPage(ModelMap model, HttpServletRequest request, HttpServletResponse response,
			PaySlipDynamicForm paySlipDynamicForm, Locale locale);

	/**
	 * Show import template.
	 * 
	 * @param model
	 *            the model
	 * @param excelImportToolForm
	 *            the excel import tool form
	 * @return the model and view
	 */
	ModelAndView showImportTemplate(ModelMap model, ExcelImportToolForm excelImportToolForm, Locale locale);

	/**
	 * Show excel export.
	 * 
	 * @param model
	 *            the model
	 * @param excelExportToolForm
	 *            the excel export tool form
	 * @return the model and view
	 */
	ModelAndView showExcelExport(ModelMap model, ExcelExportToolForm excelExportToolForm, HttpServletRequest request,
			HttpServletResponse response, Locale locale);

	/**
	 * Show data export.
	 * 
	 * @param ModelMap
	 *            the model
	 * @param dataExportForm
	 *            the data export form
	 * @return the model and view
	 */
	ModelAndView showDataExport(ModelMap model, DataExportForm dataExportForm, HttpServletRequest request,
			Locale locale);

	/**
	 * Show data import.
	 * 
	 * @param model
	 *            the model
	 * @param dataImportForm
	 *            the data import form
	 * @return the model and view
	 */
	ModelAndView showDataImport(ModelMap model, DataImportForm dataImportForm, HttpServletRequest request,
			Locale locale);

	/**
	 * Mail template list page.
	 * 
	 * @param model
	 *            the model
	 * @param mailTemplateListForm
	 *            the mail template list form
	 * @return the model and view
	 */
	ModelAndView mailTemplateListPage(ModelMap model, MailTemplateListForm mailTemplateListForm, Locale locale);

	ModelAndView showWorkFlowDelegate(ModelMap model, HttpServletRequest request, HttpServletResponse response,
			Locale locale);

	/**
	 * 
	 * Function to redirect to Employee Entitlement page in Claim menu.
	 * 
	 * @param model
	 * @return
	 */

	ModelAndView employeeEntitlements(ModelMap model, HttpServletRequest request);

	ModelAndView lundinTimesheetPreference(ModelMap model, HttpServletRequest request, HttpServletResponse response);

	ModelAndView lundinTimesheetEventReminder(ModelMap model, HttpServletRequest request, HttpServletResponse response);

	ModelAndView lundinTimesheetReviewer(ModelMap model, HttpServletRequest request, HttpServletResponse response);

	ModelAndView lundinTimesheetReports(ModelMap model, HttpServletRequest request, HttpServletResponse response);

	ModelAndView getLundinDepartment(Map model, HttpServletRequest request, HttpServletResponse response);

	ModelAndView getLundinBlock(Map model, HttpServletRequest request, HttpServletResponse response);

	ModelAndView getLundinAFE(Map model, HttpServletRequest request, HttpServletResponse response);

	ModelAndView applicationOfEBenefits(ModelMap model);

	ModelAndView creditItems(ModelMap model);

	ModelAndView creditSetup(ModelMap model);

	ModelAndView leaveGranter(ModelMap model, HttpServletRequest request);

	/**
	 * Leave scheme definition page.
	 * 
	 * @param model
	 *            the model
	 * @return the model and view
	 */
	ModelAndView leaveSchemeDefinitionPage(ModelMap model, HttpServletRequest request, HttpServletResponse response);

	ModelAndView hrisReports(ModelMap model);

	ModelAndView discussionBoard(ModelMap model);

	ModelAndView discussionTopic(ModelMap model);

	ModelAndView manageModule(ModelMap model);

	ModelAndView lionhkTimesheetPreference(ModelMap model, HttpServletRequest request, HttpServletResponse response);

	ModelAndView lionTimesheetReviewer(ModelMap model, HttpServletRequest request, HttpServletResponse response);

	ModelAndView lionTimesheetReports(ModelMap model, HttpServletRequest request, HttpServletResponse response);

	ModelAndView lionhkPendingTimesheetScreen(ModelMap model, HttpServletRequest request, HttpServletResponse response);

	ModelAndView lionhkETimesheetScreen(ModelMap model, HttpServletRequest request, HttpServletResponse response);

	ModelAndView coherentEmployeeOvertimeAdmin(ModelMap model, HttpServletRequest request,
			HttpServletResponse response);

	ModelAndView coherentEmployeeShiftAdmin(ModelMap model, HttpServletRequest request, HttpServletResponse response);

	ModelAndView coherentTimesheetReports(ModelMap model, HttpServletRequest request, HttpServletResponse response);

	ModelAndView coherentTimesheetPreference(ModelMap model, HttpServletRequest request, HttpServletResponse response);

	ModelAndView EmployeeItemEntitlements(ModelMap model, HttpServletRequest request);

	ModelAndView viewSsoConfigure(ModelMap model, HttpServletRequest request);

	ModelAndView saveSsoConfiguration(ModelMap model, HttpServletRequest request,
			SsoConfigurationDTO ssoConfigurationDTO);
	
	ModelAndView hroWorkdayFtp(HttpServletRequest request, ModelMap model);

	ModelAndView hroWorkdayEmployeeData(HttpServletRequest request, ModelMap model);

	ModelAndView hroWorkdayPayrollData(HttpServletRequest request, ModelMap model);

	ModelAndView hroWorkdayReport(HttpServletRequest request, ModelMap model);

}
