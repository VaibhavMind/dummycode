package com.payasia.web.controller.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.AppCodeDTO;
import com.payasia.common.dto.ComboValueDTO;
import com.payasia.common.dto.CompanyModuleDTO;
import com.payasia.common.dto.DataDictionaryDTO;
import com.payasia.common.dto.DateFormatDTO;
import com.payasia.common.dto.EmailTemplateCategoryDTO;
import com.payasia.common.dto.EmployeeFieldDTO;
import com.payasia.common.dto.EmployeeLeaveSchemeTypeDTO;
import com.payasia.common.dto.EntityMasterDTO;
import com.payasia.common.dto.LanguageListDTO;
import com.payasia.common.dto.LanguageMasterDTO;
import com.payasia.common.dto.LundinAFEDTO;
import com.payasia.common.dto.LundinBlockDTO;
import com.payasia.common.dto.LundinDepartmentDTO;
import com.payasia.common.dto.ManageModuleDTO;
import com.payasia.common.dto.MonthMasterDTO;
import com.payasia.common.dto.PayslipFrequencyDTO;
import com.payasia.common.dto.SsoConfigurationDTO;
import com.payasia.common.dto.WorkdayFieldMappingDTO;
import com.payasia.common.form.AddClaimForm;
import com.payasia.common.form.AddLeaveForm;
import com.payasia.common.form.AdminPaySlipForm;
import com.payasia.common.form.AssignClaimTemplateForm;
import com.payasia.common.form.AssignLeaveSchemeForm;
import com.payasia.common.form.CalendarDefForm;
import com.payasia.common.form.CalendarTemplateMonthForm;
import com.payasia.common.form.ChangeEmployeeNameListForm;
import com.payasia.common.form.ClaimApplicationItemAttach;
import com.payasia.common.form.ClaimApplicationItemForm;
import com.payasia.common.form.ClaimBatchForm;
import com.payasia.common.form.ClaimItemForm;
import com.payasia.common.form.ClaimItemsCategoryForm;
import com.payasia.common.form.ClaimPreferenceForm;
import com.payasia.common.form.ClaimReportsForm;
import com.payasia.common.form.ClaimReviewerForm;
import com.payasia.common.form.ClaimTemplateForm;
import com.payasia.common.form.ClaimTemplateItemForm;
import com.payasia.common.form.CoherentOvertimeDetailForm;
import com.payasia.common.form.CoherentShiftDetailForm;
import com.payasia.common.form.CoherentTimesheetPreferenceForm;
import com.payasia.common.form.CoherentTimesheetReportsForm;
import com.payasia.common.form.CompanyCopyForm;
import com.payasia.common.form.CompanyDocumentCenterForm;
import com.payasia.common.form.CompanyDynamicForm;
import com.payasia.common.form.CompanyForm;
import com.payasia.common.form.CompanyGroupForm;
import com.payasia.common.form.CompanyHolidayCalendarForm;
import com.payasia.common.form.CompanyListForm;
import com.payasia.common.form.CurrencyDefinitionForm;
import com.payasia.common.form.DataExportForm;
import com.payasia.common.form.DataImportForm;
import com.payasia.common.form.DiscussionBoardForm;
import com.payasia.common.form.DiscussionTopicCommentForm;
import com.payasia.common.form.DynamicFormTableDocumentDTO;
import com.payasia.common.form.EmailPreferenceForm;
import com.payasia.common.form.EmployeeCalendarDefForm;
import com.payasia.common.form.EmployeeClaimAdjustmentForm;
import com.payasia.common.form.EmployeeDetailForm;
import com.payasia.common.form.EmployeeDynamicForm;
import com.payasia.common.form.EmployeeFilterListForm;
import com.payasia.common.form.EmployeeItemEntitlementForm;
import com.payasia.common.form.EmployeeListForm;
import com.payasia.common.form.EmployeeNumberSrForm;
import com.payasia.common.form.EmployeePaySlipDesignForm;
import com.payasia.common.form.EmployeePreferencesForm;
import com.payasia.common.form.EmployeeTaxDocumentForm;
import com.payasia.common.form.EmployeeTypeForm;
import com.payasia.common.form.EntityListViewForm;
import com.payasia.common.form.ExcelExportToolForm;
import com.payasia.common.form.ExcelImportToolForm;
import com.payasia.common.form.ExternalLinkForm;
import com.payasia.common.form.HRISPreferenceForm;
import com.payasia.common.form.HRISReportsForm;
import com.payasia.common.form.HRISReviewerForm;
import com.payasia.common.form.HRLetterForm;
import com.payasia.common.form.HolidayListForm;
import com.payasia.common.form.HolidayListMasterForm;
import com.payasia.common.form.ImportEmployeeClaimForm;
import com.payasia.common.form.ImportEmployeeOvertimeShiftForm;
import com.payasia.common.form.LeaveBalanceSummaryForm;
import com.payasia.common.form.LeaveBatchForm;
import com.payasia.common.form.LeaveEventReminderForm;
import com.payasia.common.form.LeavePreferenceForm;
import com.payasia.common.form.LeaveReportsForm;
import com.payasia.common.form.LeaveReviewerForm;
import com.payasia.common.form.LeaveSchemeForm;
import com.payasia.common.form.LeaveTypeForm;
import com.payasia.common.form.LeaveYearEndEmployeeDetailForm;
import com.payasia.common.form.LionTimesheetPreferenceForm;
import com.payasia.common.form.LionTimesheetReportsForm;
import com.payasia.common.form.LogoUploadForm;
import com.payasia.common.form.LundinTimesheetEventReminderForm;
import com.payasia.common.form.LundinTimesheetPreferenceForm;
import com.payasia.common.form.LundinTimesheetReportsForm;
import com.payasia.common.form.LundinTimesheetReviewerForm;
import com.payasia.common.form.MailTemplateListForm;
import com.payasia.common.form.MultilingualForm;
import com.payasia.common.form.OTBatchForm;
import com.payasia.common.form.OTItemDefinitionForm;
import com.payasia.common.form.OTReviewerForm;
import com.payasia.common.form.OTTemplateForm;
import com.payasia.common.form.PasswordPolicyPreferenceForm;
import com.payasia.common.form.PayCodeDataForm;
import com.payasia.common.form.PayDataCollectionForm;
import com.payasia.common.form.PaySlipDynamicForm;
import com.payasia.common.form.PaySlipReleaseForm;
import com.payasia.common.form.PendingItemsForm;
import com.payasia.common.form.ReportOptionsPreferenceForm;
import com.payasia.common.form.SendPasswordForm;
import com.payasia.common.form.SwitchCompanyForm;
import com.payasia.common.form.SwitchUserForm;
import com.payasia.common.form.UserRoleListForm;
import com.payasia.common.form.WorkFlowDelegateForm;
import com.payasia.common.form.WorkdayFtpConfigForm;
import com.payasia.common.form.WorkdayGenerateReportForm;
import com.payasia.common.form.YearEndProcessForm;
import com.payasia.common.form.YearEndProcessingForm;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.dao.AppCodeMasterDAO;
import com.payasia.dao.LanguageMasterDAO;
import com.payasia.dao.bean.LanguageMaster;
import com.payasia.logic.AddClaimLogic;
import com.payasia.logic.AddLeaveLogic;
import com.payasia.logic.CalendarDefLogic;
import com.payasia.logic.ChangeEmployeeNumberLogic;
import com.payasia.logic.ClaimReviewerLogic;
import com.payasia.logic.ClaimTemplateLogic;
import com.payasia.logic.CompanyDocumentCenterLogic;
import com.payasia.logic.CompanyDynamicFormLogic;
import com.payasia.logic.CompanyInformationLogic;
import com.payasia.logic.CurrencyDefinitionLogic;
import com.payasia.logic.DataImportLogic;
import com.payasia.logic.EmployeeCalendarDefLogic;
import com.payasia.logic.EmployeeDetailLogic;
import com.payasia.logic.EmployeeDocumentLogic;
import com.payasia.logic.EmployeeDynamicFormLogic;
import com.payasia.logic.EmployeeNumberSrLogic;
import com.payasia.logic.EmployeePreferencesLogic;
import com.payasia.logic.ExcelExportToolLogic;
import com.payasia.logic.ExcelImportToolLogic;
import com.payasia.logic.ExternalLinkLogic;
import com.payasia.logic.GeneralLogic;
import com.payasia.logic.HRISReviewerLogic;
import com.payasia.logic.HolidayListMasterLogic;
import com.payasia.logic.LanguageMasterLogic;
import com.payasia.logic.LeaveBalanceSummaryLogic;
import com.payasia.logic.LeaveEventReminderLogic;
import com.payasia.logic.LeavePreferenceLogic;
import com.payasia.logic.LeaveReviewerLogic;
import com.payasia.logic.LeaveSchemeLogic;
import com.payasia.logic.LeaveTypeLogic;
import com.payasia.logic.LionTimesheetPreferenceLogic;
import com.payasia.logic.LogoUploadLogic;
import com.payasia.logic.LundinDepartmentLogic;
import com.payasia.logic.LundinTimesheetEventReminderLogic;
import com.payasia.logic.LundinTimesheetReviewerLogic;
import com.payasia.logic.MailTemplateListLogic;
import com.payasia.logic.MultilingualLogic;
import com.payasia.logic.OTReviewerLogic;
import com.payasia.logic.PayCodeDataLogic;
import com.payasia.logic.PayDataCollectionLogic;
import com.payasia.logic.PaySlipDesignerLogic;
import com.payasia.logic.PaySlipDynamicFormLogic;
import com.payasia.logic.PendingItemsLogic;
import com.payasia.logic.ReportOptionsPreferenceLogic;
import com.payasia.logic.SsoConfigurationLogic;
import com.payasia.logic.SwitchCompanyLogic;
import com.payasia.logic.WorkFlowDelegateLogic;
import com.payasia.logic.WorkdayFtpIntegrationLogic;
import com.payasia.logic.YearEndProcessingLogic;
import com.payasia.web.controller.AdminHomeController;
import com.payasia.web.util.PayAsiaSessionAttributes;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

@Controller
@RequestMapping(value = "/admin")
public class AdminHomeControllerImpl implements AdminHomeController {

	/** The Constant logger. */
	private static final Logger LOGGER = Logger
			.getLogger(AdminHomeControllerImpl.class);

	/** The download path. */
	@Value("#{payasiaptProperties['payasia.protocol']}")
	private String payasiaProtocol;

	/** The download path. */
	@Value("#{payasiaptProperties['payasia.payslip.url']}")
	private String payslipURL;

	@Resource
	SwitchCompanyLogic switchCompanyLogic;
	@Resource
	ReportOptionsPreferenceLogic reportOptionsPreferenceLogic;

	@Resource
	CompanyInformationLogic companyInformationLogic;
	@Resource
	ChangeEmployeeNumberLogic changeEmployeeNumberLogic;

	@Resource
	CurrencyDefinitionLogic currencyDefinitionLogic;

	@Resource
	WorkFlowDelegateLogic workflowDelegateLogic;

	@Resource
	EmployeeNumberSrLogic employeeNumberSrLogic;
	@Resource
	AddClaimLogic addClaimLogic;
	@Resource
	EmployeePreferencesLogic employeePreferencesLogic;

	@Resource
	LeaveSchemeLogic leaveSchemeLogic;

	@Resource
	LogoUploadLogic logoUploadLogic;
	@Resource
	MessageSource messageSource;

	@Resource
	LeavePreferenceLogic leavePreferenceLogic;

	@Resource
	PayCodeDataLogic payCodeDataLogic;

	@Resource
	MailTemplateListLogic mailTemplateListLogic;
	@Resource
	LundinDepartmentLogic lundinDepartmentLogic;
	@Resource
	EmployeeDynamicFormLogic employeeDynamicFormLogic;
	@Resource
	HRISReviewerLogic hrisReviewerLogic;
	@Resource
	CompanyDynamicFormLogic companyDynamicFormLogic;

	@Resource
	ExcelImportToolLogic excelImportToolLogic;

	@Resource
	ExcelExportToolLogic excelExportToolLogic;

	@Resource
	DataImportLogic dataImportLogic;

	@Resource
	EmployeeDetailLogic employeeDetailLogic;

	@Resource
	HolidayListMasterLogic holidayListMasterLogic;

	@Resource
	PayDataCollectionLogic payDataCollectionLogic;

	@Resource
	PaySlipDynamicFormLogic paySlipDynamicFormLogic;
	@Resource
	GeneralLogic generalLogic;

	@Resource
	EmployeeDocumentLogic employeeDocumentLogic;

	@Resource
	MultilingualLogic multilingualLogic;

	@Resource
	CompanyDocumentCenterLogic companyDocumentCenterLogic;
	@Resource
	LanguageMasterLogic languageMasterLogic;

	@Resource
	ExternalLinkLogic externalLinkLogic;

	@Resource
	LeaveReviewerLogic leaveReviewerLogic;

	@Resource
	ClaimReviewerLogic claimReviewerLogic;

	@Resource
	OTReviewerLogic otReviewerLogic;

	@Resource
	CalendarDefLogic calendarDefLogic;

	@Resource
	PaySlipDesignerLogic paySlipDesignerLogic;

	@Resource
	LeaveBalanceSummaryLogic leaveBalanceSummaryLogic;

	@Resource
	EmployeeCalendarDefLogic employeeCalendarDefLogic;

	@Resource
	LeaveTypeLogic leaveTypeLogic;

	@Resource
	LeaveEventReminderLogic leaveEventReminderLogic;
	@Resource
	AddLeaveLogic addLeaveLogic;
	@Resource
	YearEndProcessingLogic yearEndProcessingLogic;
	@Resource
	ClaimTemplateLogic claimTemplateLogic;
	@Resource
	PendingItemsLogic pendingItemsLogic;
	@Resource
	private LanguageMasterDAO languageMasterDAO;
	@Resource
	LundinTimesheetEventReminderLogic lundinTimesheetEventReminderLogic;
	@Resource
	LundinTimesheetReviewerLogic lundinTimesheetReviewerLogic;
	@Resource
	AppCodeMasterDAO appCodeMasterDAO;
	@Resource
	LionTimesheetPreferenceLogic lionTimesheetPreferenceLogic;

	@Resource
	SsoConfigurationLogic ssoConfigurationLogic;
	
	@Resource
	WorkdayFtpIntegrationLogic workdayFtpIntegrationLogic;

	@Override
	@RequestMapping(value = "/home.html", method = RequestMethod.GET)
	public ModelAndView homePage(ModelMap model, HttpServletRequest request,
			Locale locale) {

		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		SwitchCompanyForm switchCompanyForm = new SwitchCompanyForm();
		model.put("switchCompanyForm", switchCompanyForm);

		ExternalLinkForm externalLinkForm = externalLinkLogic
				.getExternalLink(companyId);

		if (externalLinkForm.getImage() == null) {
			externalLinkForm.setResponseString("ERROR");
		} else {
			externalLinkForm.setResponseString("SUCCESS");
		}
		model.put("externalLink", externalLinkForm);

		List<SwitchCompanyForm> switchCompanyList = switchCompanyLogic
				.getSwitchCompanyList(employeeId);
		List<LanguageListDTO> languageList = languageMasterLogic.getLanguages();
		model.put("languageList", languageList);

		String sessionLocale = (String) request.getSession().getAttribute(
				PayAsiaSessionAttributes.PAYASIA_LOCALE_LABEL);
		if (StringUtils.isNotBlank(sessionLocale)) {
			request.getSession().setAttribute(
					PayAsiaSessionAttributes.PAYASIA_LOCALE_LABEL,
					sessionLocale);
		} else {
			LanguageMaster languageMaster = languageMasterDAO
					.getDefaultLanguage();
			if (languageMaster != null) {
				request.getSession().setAttribute(
						PayAsiaSessionAttributes.PAYASIA_LOCALE_LABEL,
						languageMaster.getLanguageCode());
			}

		}

		String loginHistory = employeeDetailLogic
				.getEmployeeLoginHistory(companyId);

		model.put("chartData", loginHistory);

		model.put("switchCompanyList", switchCompanyList);
		return new ModelAndView("admin/home");
	}

	@Override
	@RequestMapping(value = "/adminHomePage.html", method = RequestMethod.GET)
	public ModelAndView adminhomePage() {

		return new ModelAndView("admin/adminHomePage");
	}

	@Override
	@RequestMapping(value = "/addEmployee.html", method = RequestMethod.GET)
	public ModelAndView addEmployee() {

		return new ModelAndView("admin/addEmployee");
	}

	@Override
	@RequestMapping(value = "/addCompany.html", method = RequestMethod.GET)
	public ModelAndView addCompany(ModelMap model) {
		CompanyForm companyForm = new CompanyForm();
		model.put("companyForm", companyForm);

		CompanyGroupForm companyGroupForm = new CompanyGroupForm();
		model.put("companyGroupForm", companyGroupForm);

		List<CompanyForm> groupNameList = companyInformationLogic
				.getCompanyGroup();
		model.put("groupNameList", groupNameList);

		List<CompanyForm> financialYearList = companyInformationLogic
				.getFinancialYearList();
		model.put("financialYearList", financialYearList);

		List<CompanyForm> paySlipFrequencyList = companyInformationLogic
				.getPaySlipFrequencyList();
		model.put("paySlipFrequencyList", paySlipFrequencyList);

		List<CompanyForm> dateFormatList = companyInformationLogic
				.getDateFormatList();
		model.put("dateFormatList", dateFormatList);

		List<CompanyForm> currencyNameList = companyInformationLogic
				.getCurrencyName();
		model.put("currencyNameList", currencyNameList);

		List<CompanyForm> countryList = companyInformationLogic
				.getCountryList();
		model.put("countryList", countryList);

		List<CompanyForm> timeZoneList = companyInformationLogic
				.getTimeZoneList();
		model.put("timeZoneList", timeZoneList);

		return new ModelAndView("admin/addCompany", model);
	}

	@Override
	@RequestMapping(value = "/editEmployee.html", method = RequestMethod.GET)
	public ModelAndView editEmployee(ModelMap model) {
		EmployeeListForm employeeListForm = new EmployeeListForm();
		model.put("employeeListForm", employeeListForm);

		DynamicFormTableDocumentDTO dynamicFormDocumentDTO = new DynamicFormTableDocumentDTO();
		model.put("addEmpDynDocumentForm", dynamicFormDocumentDTO);
		return new ModelAndView("admin/editEmployee", model);
	}

	@Override
	@RequestMapping(value = "/editCompany.html", method = RequestMethod.GET)
	public ModelAndView editCompany(ModelMap model) {

		CompanyForm companyForm = new CompanyForm();
		model.put("companyForm", companyForm);

		List<CompanyForm> groupNameList = companyInformationLogic
				.getCompanyGroup();
		model.put("groupNameList", groupNameList);

		List<CompanyForm> financialYearList = companyInformationLogic
				.getFinancialYearList();
		model.put("financialYearList", financialYearList);

		List<CompanyForm> paySlipFrequencyList = companyInformationLogic
				.getPaySlipFrequencyList();
		model.put("paySlipFrequencyList", paySlipFrequencyList);

		List<CompanyForm> dateFormatList = companyInformationLogic
				.getDateFormatList();
		model.put("dateFormatList", dateFormatList);

		List<CompanyForm> countryList = companyInformationLogic
				.getCountryList();
		model.put("countryList", countryList);

		List<CompanyForm> currencyNameList = companyInformationLogic
				.getCurrencyName();
		model.put("currencyNameList", currencyNameList);

		List<CompanyForm> timeZoneList = companyInformationLogic
				.getTimeZoneList();
		model.put("timeZoneList", timeZoneList);

		return new ModelAndView("admin/editCompany", model);
	}

	@Override
	@RequestMapping(value = "/employee.html", method = RequestMethod.GET)
	public ModelAndView employeePage(ModelMap model,
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		EmployeeListForm employeeListForm = new EmployeeListForm();
		model.put("employeeListForm", employeeListForm);

		List<EntityListViewForm> viewNameList = employeeDetailLogic
				.getViewName(companyId);
		model.put("viewNameList", viewNameList);

		EntityListViewForm entityListView = new EntityListViewForm();
		model.put("entityListView", entityListView);

		DynamicFormTableDocumentDTO dynamicFormDocumentDTO = new DynamicFormTableDocumentDTO();
		model.put("dynamicFormDocumentDTO", dynamicFormDocumentDTO);

		EmployeeDetailForm employeeDetailForm = new EmployeeDetailForm();
		model.put("employeeDetailForm", employeeDetailForm);
		return new ModelAndView("admin/employee", model);
	}

	@Override
	@RequestMapping(value = "/payDataCollection.html", method = RequestMethod.GET)
	public ModelAndView payDataCollection(ModelMap model,
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		EmployeeListForm employeeListForm = new EmployeeListForm();
		model.put("employeeListForm", employeeListForm);

		PayDataCollectionForm payDataCollectionForm = new PayDataCollectionForm();
		model.put("payDataCollectionForm", payDataCollectionForm);

		List<PayDataCollectionForm> payCodeList = payDataCollectionLogic
				.getAllPayCode(companyId);
		model.put("payCodeList", payCodeList);

		return new ModelAndView("admin/payDataCollection", model);
	}

	@Override
	@RequestMapping(value = "/payCode.html", method = RequestMethod.GET)
	public ModelAndView payCode(ModelMap model) {

		PayCodeDataForm payCodeDataForm = new PayCodeDataForm();
		model.put("payCodeDataForm", payCodeDataForm);
		return new ModelAndView("admin/payCode", model);

	}

	@Override
	@RequestMapping(value = "/payroll.html", method = RequestMethod.GET)
	public ModelAndView payrollPage() {

		return new ModelAndView("admin/payroll");
	}

	@Override
	@RequestMapping(value = "/reporting.html", method = RequestMethod.GET)
	public ModelAndView reportingPage() {

		return new ModelAndView("admin/reporting");
	}

	@Override
	@RequestMapping(value = "/tool.html", method = RequestMethod.GET)
	public ModelAndView toolPage() {

		return new ModelAndView("admin/tool");
	}

	@Override
	@RequestMapping(value = "/userRoles.html", method = RequestMethod.GET)
	public ModelAndView taskPage() {

		return new ModelAndView("admin/userRoles");
	}

	@Override
	@RequestMapping(value = "/managerUsers.html", method = RequestMethod.GET)
	public ModelAndView mgrUsers(ModelMap model, HttpServletRequest request,
			HttpServletResponse response) {

		UserRoleListForm userRoleListForm = new UserRoleListForm();
		model.put("userRoleListForm", userRoleListForm);

		return new ModelAndView("admin/managerUsers", model);
	}

	@Override
	@RequestMapping(value = "/information.html", method = RequestMethod.GET)
	public ModelAndView employeeInformationNewPage() {

		return new ModelAndView("admin/information1");
	}

	@Override
	@RequestMapping(value = "/changeEmployeeNumber.html", method = RequestMethod.GET)
	public ModelAndView pendingTaskPage() {

		return new ModelAndView("admin/changeEmployeeNumber");
	}

	@Override
	@RequestMapping(value = "/editView.html", method = RequestMethod.GET)
	public ModelAndView editView() {

		return new ModelAndView("admin/editView");
	}

	@Override
	@RequestMapping(value = "claimReviewer.html", method = RequestMethod.GET)
	public ModelAndView claimReviewerPage(ModelMap map,
			HttpServletRequest request, HttpServletResponse response) {

		ClaimReviewerForm claimReviewerForm = new ClaimReviewerForm();
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		List<ClaimReviewerForm> claimTemplateList = claimReviewerLogic
				.getClaimTemplateList(companyId);

		List<ClaimReviewerForm> workFlowRuleList = claimReviewerLogic
				.getWorkFlowRuleList();

		claimReviewerForm.setClaimReviewerRuleId1(workFlowRuleList.get(0)
				.getClaimReviewerRuleId());
		claimReviewerForm.setClaimReviewerRuleId2(workFlowRuleList.get(1)
				.getClaimReviewerRuleId());
		claimReviewerForm.setClaimReviewerRuleId3(workFlowRuleList.get(2)
				.getClaimReviewerRuleId());

		map.put("claimTemplateList", claimTemplateList);

		map.put("claimReviewerForm", claimReviewerForm);

		return new ModelAndView("admin/claimReviewer");
	}

	@Override
	@RequestMapping(value = "employeeNumberSr.html", method = RequestMethod.GET)
	public ModelAndView employeeNumberSr(
			ModelMap model,
			HttpServletRequest request,
			@ModelAttribute(value = "employeeNumberSrForm") EmployeeNumberSrForm employeeNumberSrForm) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		List<EmployeeTypeForm> employeeTypeFormList = employeeNumberSrLogic
				.getEmployeeTypeList(companyId);
		model.addAttribute("employeeTypeFormList", employeeTypeFormList);
		return new ModelAndView("admin/employeeNumberSr", model);
	}

	@Override
	@RequestMapping(value = "claimBatch.html", method = RequestMethod.GET)
	public ModelAndView claimsBatchProcess(
			ModelMap model,
			@ModelAttribute(value = "claimBatchForm") ClaimBatchForm claimBatchForm) {
		return new ModelAndView("admin/claimBatch");
	}

	@Override
	@RequestMapping(value = "sendPassword.html", method = RequestMethod.GET)
	public ModelAndView sendPAssword(Map model) {

		SendPasswordForm sendPasswordForm = new SendPasswordForm();
		model.put("sendPasswordForm", sendPasswordForm);
		return new ModelAndView("admin/sendPassword");
	}

	@Override
	@RequestMapping(value = "claimItemDefinition.html", method = RequestMethod.GET)
	public ModelAndView claimItemDefinitionPage(ModelMap model) {

		ClaimItemForm claimItemForm = new ClaimItemForm();
		model.put("claimItemForm", claimItemForm);

		ClaimItemsCategoryForm claimItemsCategoryForm = new ClaimItemsCategoryForm();
		model.put("claimCategoryForm", claimItemsCategoryForm);

		return new ModelAndView("admin/claimItemDefinition", model);
	}

	@Override
	@RequestMapping(value = "/claimTemplates.html", method = RequestMethod.GET)
	public ModelAndView claimTemplatesPage(ModelMap model, Locale locale, HttpServletRequest request) {
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		ClaimTemplateForm claimTemplateForm = new ClaimTemplateForm();
		ClaimTemplateItemForm claimTemplateItemForm = new ClaimTemplateItemForm();
		model.put("claimTemplateForm", claimTemplateForm);
		model.put("claimTemplateItemForm", claimTemplateItemForm);

		List<AppCodeDTO> claimTypeList = claimTemplateLogic
				.getClaimTypeItemList(locale);
		model.put("claimTypeList", claimTypeList);

		List<AppCodeDTO> customFieldList = claimTemplateLogic.getCustomFieldTypes();
		model.put("customFieldList", customFieldList);

		ClaimTemplateForm appCodeList = claimTemplateLogic
				.getClaimTemplateAppCodeList(locale);
		model.put("appCodeList", appCodeList);

		List<ClaimTemplateForm> allowedNoOfTimesAppCodeList = claimTemplateLogic
				.getAllowedNoOfTimesAppCodeList();
		model.put("allowedNoOfTimesAppCodeList", allowedNoOfTimesAppCodeList);
		
		List<ClaimTemplateForm> claimTemplateList = claimTemplateLogic.getClaimTemplateList(companyId, null);
		model.put("claimTemplateList", claimTemplateList);

		List<ClaimTemplateForm> prorationBasedOnAppCodeList = claimTemplateLogic.getProrationBasedOnAppCodeList();
		model.put("prorationBasedOnAppCodeList", prorationBasedOnAppCodeList);

		List<CompanyForm> currencyNameList = companyInformationLogic
				.getCurrencyName();
		model.put("currencyNameList", currencyNameList);

		return new ModelAndView("admin/claimTemplates", model);
	}

	@Override
	@RequestMapping(value = "/deleteEmployee.html", method = RequestMethod.GET)
	public ModelAndView deleteEmployee() {

		return new ModelAndView("admin/deleteEmployee");
	}

	@Override
	@RequestMapping(value = "/accessControlEmployee.html", method = RequestMethod.GET)
	public ModelAndView accessControl() {

		return new ModelAndView("admin/accessControlEmployee");
	}

	@Override
	@RequestMapping(value = "/employeeDynamicForm.html", method = RequestMethod.GET)
	public ModelAndView manageEmployeeFormPage(
			ModelMap model,
			HttpServletRequest request,
			HttpServletResponse response,
			@ModelAttribute(value = "employeeDynamicForm") EmployeeDynamicForm employeeDynamicForm) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		List<EmployeeDynamicForm> tabNameList = employeeDynamicFormLogic
				.getTabList(companyId);
		model.addAttribute("tabNameList", tabNameList);

		return new ModelAndView("admin/employeeDynamicForm", model);
	}

	@Override
	@RequestMapping(value = "/payslip.html", method = RequestMethod.GET)
	public ModelAndView payslipPage(
			ModelMap model,
			@ModelAttribute(value = "adminPaySlipForm") AdminPaySlipForm adminPaySlipFormArg,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) {
		UserContext.setLocale(locale);

		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		AdminPaySlipForm adminPaySlipForm = employeeDocumentLogic
				.getPaySlipFrequencyDetails(companyId);

		ExternalLinkForm externalLinkForm = externalLinkLogic
				.getExternalLink(companyId);

		if (externalLinkForm.getImage() == null) {
			externalLinkForm.setResponseString("ERROR");
		} else {
			externalLinkForm.setResponseString("SUCCESS");
		}
		model.put("externalLink", externalLinkForm);
		model.addAttribute(adminPaySlipForm);
		List<MonthMasterDTO> monthList = generalLogic.getMonthList();
		model.put("monthList", monthList);

		Map<String, String> advanceFilterCombosHashMap = generalLogic
				.getEmployeeFilterComboList(companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(
				advanceFilterCombosHashMap, jsonConfig);
		model.put("advanceFilterCombosHashMap", jsonObject.toString());
		model.put("payasiaProtocol", payasiaProtocol);
		model.put("payasiaURL", payslipURL);

		return new ModelAndView("admin/payslip", model);
	}

	@Override
	@RequestMapping(value = "/taxDocuments.html", method = RequestMethod.GET)
	public ModelAndView taxDocumentsPage(
			ModelMap model,
			@ModelAttribute(value = "employeeTaxDocumentForm") EmployeeTaxDocumentForm employeeTaxDocumentForm,
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		List<Integer> yearList = employeeDocumentLogic.getYearList(companyId);
		model.put("yearList", yearList);
		return new ModelAndView("admin/taxDocuments", model);
	}

	@Override
	@RequestMapping(value = "/otItemDefinition.html", method = RequestMethod.GET)
	public ModelAndView otTypeDefinitionPage(ModelMap model) {
		OTItemDefinitionForm otItemDefinitionForm = new OTItemDefinitionForm();
		model.put("otItemDefinitionForm", otItemDefinitionForm);
		return new ModelAndView("admin/otItemDefinition", model);
	}

	@Override
	@RequestMapping(value = "/otTemplateDefinition.html", method = RequestMethod.GET)
	public ModelAndView otTemplateDefinitionPage(ModelMap model) {
		OTTemplateForm otTemplateForm = new OTTemplateForm();
		model.put("otTemplateForm", otTemplateForm);
		return new ModelAndView("admin/otTemplateDefinition", model);
	}

	@Override
	@RequestMapping(value = "/otReviewer.html", method = RequestMethod.GET)
	public ModelAndView otReviewerPage(ModelMap map,
			HttpServletRequest request, HttpServletResponse response) {

		OTReviewerForm otReviewerForm = new OTReviewerForm();

		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		List<OTReviewerForm> otTemplateList = otReviewerLogic
				.getOTTemplateList(companyId);

		List<OTReviewerForm> workFlowRuleList = otReviewerLogic
				.getWorkFlowRuleList();

		otReviewerForm.setOtReviewerRuleId1(workFlowRuleList.get(0)
				.getOtReviewerRuleId());
		otReviewerForm.setOtReviewerRuleId2(workFlowRuleList.get(1)
				.getOtReviewerRuleId());
		otReviewerForm.setOtReviewerRuleId3(workFlowRuleList.get(2)
				.getOtReviewerRuleId());

		map.put("otTemplateList", otTemplateList);

		map.put("otReviewerForm", otReviewerForm);

		return new ModelAndView("admin/otReviewer", map);
	}

	@Override
	@RequestMapping(value = "/otBatchDefinition.html", method = RequestMethod.GET)
	public ModelAndView otBatchDefinitionPage(ModelMap model) {

		OTBatchForm oTBatchForm = new OTBatchForm();
		model.put("oTBatchForm", oTBatchForm);

		return new ModelAndView("admin/otBatchDefinition", model);
	}

	@Override
	@RequestMapping(value = "emailTemplate.html", method = RequestMethod.GET)
	public ModelAndView emailTemplatePage() {

		return new ModelAndView("admin/emailTemplate");
	}

	@Override
	@RequestMapping(value = "switchUser.html", method = RequestMethod.GET)
	public ModelAndView showSwitchUser(Map model) {
		SwitchUserForm switchUserForm = new SwitchUserForm();
		model.put("switchUserForm", switchUserForm);
		return new ModelAndView("admin/switchUser");
	}

	@Override
	@RequestMapping(value = "workFlowDelegate.html", method = RequestMethod.GET)
	public ModelAndView showWorkFlowDelegate(ModelMap model,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) {
		UserContext.setLocale(locale);
		WorkFlowDelegateForm workFlowDelegateForm = new WorkFlowDelegateForm();
		model.addAttribute("workFlowDelegateForm", workFlowDelegateForm);
		CompanyModuleDTO companyModuleDTO = new CompanyModuleDTO();
		companyModuleDTO
				.setHasClaimModule((boolean) request.getSession().getAttribute(
						PayAsiaSessionAttributes.COMPANY_HAS_CLAIM_MODULE));
		companyModuleDTO
				.setHasHrisModule((boolean) request.getSession().getAttribute(
						PayAsiaSessionAttributes.COMPANY_HAS_HRIS_MODULE));
		companyModuleDTO
				.setHasLeaveModule((boolean) request.getSession().getAttribute(
						PayAsiaSessionAttributes.COMPANY_HAS_LEAVE_MODULE));
		companyModuleDTO
				.setHasLundinTimesheetModule((boolean) request
						.getSession()
						.getAttribute(
								PayAsiaSessionAttributes.COMPANY_HAS_LUNDIN_TIMESHEET_MODULE));
		companyModuleDTO
				.setHasLionTimesheetModule((boolean) request
						.getSession()
						.getAttribute(
								PayAsiaSessionAttributes.COMPANY_HAS_LION_TIMESHEET_MODULE));
		companyModuleDTO
				.setHasCoherentTimesheetModule((boolean) request
						.getSession()
						.getAttribute(
								PayAsiaSessionAttributes.COMPANY_HAS_COHERENT_TIMESHEET_MODULE));

		List<WorkFlowDelegateForm> workflowTypeList = workflowDelegateLogic
				.getWorkflowTypeList(companyModuleDTO);
		model.addAttribute("workflowTypeList", workflowTypeList);
		return new ModelAndView("admin/workFlowDelegate", model);
	}

	@Override
	@RequestMapping(value = "holiDayCalendar.html", method = RequestMethod.GET)
	public ModelAndView showHoliDayList(ModelMap model,
			@ModelAttribute("holidayListForm") HolidayListForm holidayListForm,
			HttpServletRequest request, HttpServletResponse response) {

		CompanyHolidayCalendarForm companyHolidayCalendarForm = new CompanyHolidayCalendarForm();
		model.addAttribute("companyHolidayCalendarForm",
				companyHolidayCalendarForm);

		return new ModelAndView("admin/holiDayCalendar", model);
	}

	@Override
	@RequestMapping(value = "changeEmpNo.html", method = RequestMethod.GET)
	public ModelAndView showChangeEmpNo(ModelMap mod,
			HttpServletRequest request, HttpServletResponse response) {
		EmployeeNumberSrForm employeeNumberSrForm = new EmployeeNumberSrForm();
		mod.put("employeeNumberSrForm", employeeNumberSrForm);
		ChangeEmployeeNameListForm changeEmployeeNameListForm = new ChangeEmployeeNameListForm();
		mod.put("changeEmployeeNameListForm", changeEmployeeNameListForm);
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		List<EmployeeNumberSrForm> EmployeeNumberSrFormList = changeEmployeeNumberLogic
				.getEmployeeNumSeries(companyId);
		mod.put("EmployeeNumberSrFormList", EmployeeNumberSrFormList);
		return new ModelAndView("admin/changeEmpNo", mod);
	}

	@Override
	@RequestMapping(value = "emailPreference.html", method = RequestMethod.GET)
	public ModelAndView emailPreferencePage(Map model) {
		EmailPreferenceForm emailPreferenceForm = new EmailPreferenceForm();
		model.put("emailPreferenceForm", emailPreferenceForm);
		return new ModelAndView("admin/emailPreference");
	}

	@Override
	@RequestMapping(value = "claimsOptionPreference.html", method = RequestMethod.GET)
	public ModelAndView claimsOptionPreferencePage() {

		return new ModelAndView("admin/claimsOptionPreference");
	}

	@Override
	@RequestMapping(value = "passwordPolicyPreference.html", method = RequestMethod.GET)
	public ModelAndView passwordPolicyPreferencePage(Map model) {
		PasswordPolicyPreferenceForm passwordPolicyPreferenceForm = new PasswordPolicyPreferenceForm();
		model.put("passwordPolicyPreferenceForm", passwordPolicyPreferenceForm);
		return new ModelAndView("admin/passwordPolicyPreference");
	}

	@Override
	@RequestMapping(value = "reportOptionsPreference.html", method = RequestMethod.GET)
	public ModelAndView reportOptionsPreferencePage(ModelMap mod,
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		List<ReportOptionsPreferenceForm> reportOptionsList = reportOptionsPreferenceLogic
				.getReportFormatOptions(companyId);
		mod.put("reportOptionsList", reportOptionsList);
		return new ModelAndView("admin/reportOptionsPreference", mod);
	}

	@Override
	@RequestMapping(value = "/currencyDefinition.html", method = RequestMethod.GET)
	public ModelAndView currencyDefinitionPage(ModelMap mod,
			HttpServletRequest request, HttpServletResponse response) {
		CurrencyDefinitionForm currencyDefinitionForm = new CurrencyDefinitionForm();
		mod.put("currencyDefinitionForm", currencyDefinitionForm);

		List<CurrencyDefinitionForm> currencyNameList = currencyDefinitionLogic
				.getCurrencyName();
		mod.put("currencyNameList", currencyNameList);

		return new ModelAndView("admin/currencyDefinition", mod);
	}

	@Override
	@RequestMapping(value = "/documentCenter.html", method = RequestMethod.GET)
	public ModelAndView documentCenterPage() {

		return new ModelAndView("admin/documentCenter");
	}

	@Override
	@RequestMapping(value = "/switchCompany.html", method = RequestMethod.GET)
	public ModelAndView switchCompanyPage(ModelMap model,
			HttpServletRequest request, HttpServletResponse response) {
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		SwitchCompanyForm switchCompanyFormInner = new SwitchCompanyForm();
		model.put("switchCompanyFormInner", switchCompanyFormInner);

		List<SwitchCompanyForm> switchCompanyListInner = switchCompanyLogic
				.getSwitchCompanyList(employeeId);
		model.put("switchCompanyListInner", switchCompanyListInner);
		return new ModelAndView("admin/switchCompany", model);
	}

	@Override
	@RequestMapping(value = "/leaveTypeDefinition.html", method = RequestMethod.GET)
	public ModelAndView leaveTypeDefinitionPage(ModelMap model,
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		LeaveTypeForm leaveTypeForm = new LeaveTypeForm();
		model.put("leaveTypeForm", leaveTypeForm);

		LeaveTypeForm appCodeList = leaveTypeLogic.getLeaveTypeAppcodeList();
		model.put("appCodeList", appCodeList);

		List<AppCodeDTO> applicationModeList = excelImportToolLogic
				.getAppCodeList("Leave Type Application Mode");
		model.put("applicationModeList", applicationModeList);

		LeavePreferenceForm leavePreferenceForm = leaveBalanceSummaryLogic
				.isEncashedVisible(companyId);
		model.put("leaveUnitDefined", leavePreferenceForm.getLeaveUnit());
		model.put("companyCountryName",
				leavePreferenceForm.getCompanyCountryName());
		model.put("companyCountryName",
				leavePreferenceForm.getCompanyCountryName());

		return new ModelAndView("admin/leaveTypeDefinition", model);
	}

	@Override
	@RequestMapping(value = "/leaveSchemeDefinition.html", method = RequestMethod.GET)
	public ModelAndView leaveSchemeDefinitionPage(ModelMap model,
			HttpServletRequest request, HttpServletResponse response) {
		LeaveSchemeForm leaveSchemeForm = leaveSchemeLogic
				.getLeaveSchemeAppcodeList();
		model.put("leaveSchemeForm", leaveSchemeForm);

		List<LeaveSchemeForm> appCodeForDistMeth = leaveSchemeLogic
				.getAppcodeListForLeaveDistMeth();
		model.put("appCodeForDistMeth", appCodeForDistMeth);

		List<LeaveSchemeForm> appCodeForRoundingMeth = leaveSchemeLogic
				.getAppcodeListForLeaveRoundingMeth();
		model.put("appCodeForRoundingMeth", appCodeForRoundingMeth);

		List<LeaveSchemeForm> appCodeForApplyAfterFrom = leaveSchemeLogic
				.getAppcodeListForApplyAfterFrom();
		model.put("appCodeForApplyAfterFrom", appCodeForApplyAfterFrom);

		List<LeaveSchemeForm> appCodeForProrationBasedOnMeth = leaveSchemeLogic
				.getAppcodeListForProrationBasedOnMeth();

		List<LeaveSchemeForm> appCodeForRoundOffRuleMeth = leaveSchemeLogic
				.getAppcodeListForRoundOffRuleMeth();
		model.put("appCodeForProrationBasedOnMeth",
				appCodeForProrationBasedOnMeth);

		model.put("appCodeForRoundOffRuleMeth", appCodeForRoundOffRuleMeth);

		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		LeavePreferenceForm leavePreferenceForm = leaveBalanceSummaryLogic
				.isEncashedVisible(companyId);
		model.put("leaveUnitDefined", leavePreferenceForm.getLeaveUnit());

		return new ModelAndView("admin/leaveSchemeDefinition");
	}

	@Override
	@RequestMapping(value = "/leaveReviewer.html", method = RequestMethod.GET)
	public ModelAndView leaveReviewerPage(ModelMap model,
			HttpServletRequest request, HttpServletResponse response) {

		LeaveReviewerForm leaveReviewerForm = new LeaveReviewerForm();

		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		List<LeaveReviewerForm> leaveSchemeList = leaveReviewerLogic
				.getLeaveSchemeList(companyId);

		List<LeaveReviewerForm> workFlowRuleList = leaveReviewerLogic
				.getWorkFlowRuleList();

		leaveReviewerForm
				.setLeaveReviewerRuleId1(workFlowRuleList.get(0) == null ? 0
						: workFlowRuleList.get(0).getLeaveReviewerRuleId());
		leaveReviewerForm
				.setLeaveReviewerRuleId2(workFlowRuleList.get(0) == null ? 0
						: workFlowRuleList.get(1).getLeaveReviewerRuleId());
		leaveReviewerForm
				.setLeaveReviewerRuleId3(workFlowRuleList.get(0) == null ? 0
						: workFlowRuleList.get(2).getLeaveReviewerRuleId());

		model.put("leaveSchemeList", leaveSchemeList);
		model.put("leaveReviewerForm", leaveReviewerForm);

		return new ModelAndView("admin/leaveReviewer");
	}

	@Override
	@RequestMapping(value = "/leaveBatchDefinition.html", method = RequestMethod.GET)
	public ModelAndView leaveBatchDefinitionPage(
			ModelMap model,
			@ModelAttribute(value = "leaveBatchForm") LeaveBatchForm leaveBatchForm) {

		return new ModelAndView("admin/leaveBatchDefinition", model);
	}

	@Override
	@RequestMapping(value = "excelImport.html", method = RequestMethod.GET)
	public ModelAndView showImportTemplate(
			ModelMap model,
			@ModelAttribute(value = "excelImportToolForm") ExcelImportToolForm excelImportToolForm,
			Locale locale) {
		UserContext.setLocale(locale);
		List<EntityMasterDTO> entityList = excelImportToolLogic.getEntityList();
		List<AppCodeDTO> transactionTypeList = excelImportToolLogic
				.getAppCodeList("Transaction Type");
		List<AppCodeDTO> uploadTypeList = excelImportToolLogic
				.getAppCodeList("Upload Type");
		model.put("entityList", entityList);
		model.put("transactionTypeList", transactionTypeList);
		model.put("uploadTypeList", uploadTypeList);
		return new ModelAndView("admin/excelImport", model);
	}

	@Override
	@RequestMapping(value = "paySlipDesigner.html", method = RequestMethod.GET)
	public ModelAndView paySlipDesignerPage(ModelMap model,
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		EmployeePaySlipDesignForm employeePaySlipDesignForm = new EmployeePaySlipDesignForm();
		PaySlipDynamicForm paySlipForm = paySlipDynamicFormLogic
				.getPaySlipFrequencyDetails(companyId);
		PaySlipDynamicForm effectiveForm = paySlipDesignerLogic
				.getEffectiveFrom(companyId);
		PaySlipDynamicForm currentPayslip = paySlipDynamicFormLogic
				.getCurrentPayslipInfo(companyId);
		model.addAttribute("paySlipForm", paySlipForm);
		model.addAttribute("effectiveForm", effectiveForm);
		model.addAttribute("currentPayslip", currentPayslip);
		model.put("employeePaySlipDesignForm", employeePaySlipDesignForm);
		return new ModelAndView("admin/paySlipDesigner", model);
	}

	@Override
	@RequestMapping(value = "excelExport.html", method = RequestMethod.GET)
	public ModelAndView showExcelExport(
			ModelMap model,
			@ModelAttribute(value = "excelExportToolForm") ExcelExportToolForm excelExportToolForm,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) {
		UserContext.setLocale(locale);
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		List<EntityMasterDTO> entityList = excelExportToolLogic.getEntityList();
		model.put("entityList", entityList);
		List<DataDictionaryDTO> dataDictionaryList = excelExportToolLogic
				.getStaticEmployeeFieldList();
		model.put("dataDictionaryEmpStaticList", dataDictionaryList);

		Map<String, String> advanceFilterCombosHashMap = generalLogic
				.getEmployeeFilterComboList(companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(
				advanceFilterCombosHashMap, jsonConfig);
		model.put("advanceFilterCombosHashMap", jsonObject.toString());

		List<EmployeeFieldDTO> employeeList = generalLogic.returnEmployeesList(
				employeeId, companyId);
		model.put("employeeList", employeeList);

		return new ModelAndView("admin/excelExport", model);
	}

	@Override
	@RequestMapping(value = "mailTemplateList.html", method = RequestMethod.GET)
	public ModelAndView mailTemplateListPage(
			ModelMap model,
			@ModelAttribute(value = "mailTemplateListForm") MailTemplateListForm mailTemplateListForm,
			Locale locale) {
		UserContext.setLocale(locale);
		List<EmailTemplateCategoryDTO> emailTemplateCategoryList = mailTemplateListLogic
				.getCategoryList();
		model.addAttribute("emailTemplateCategoryList",
				emailTemplateCategoryList);
		model.put("mailTemplateListForm", mailTemplateListForm);

		return new ModelAndView("admin/mailTemplateList");
	}

	@Override
	@RequestMapping(value = "endYearProcessing.html", method = RequestMethod.GET)
	public ModelAndView endYearProcessingPage(ModelMap model,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) {

		YearEndProcessingForm yearEndProcessingForm = new YearEndProcessingForm();
		model.put("yearEndProcessingForm", yearEndProcessingForm);

		YearEndProcessForm yearEndProcessForm = new YearEndProcessForm();
		model.put("yearEndProcessForm", yearEndProcessForm);

		Map<Long, CompanyForm> companyGroupMap = companyInformationLogic
				.getCompanyGroupYEP();

		model.put("companyGroupMap", companyGroupMap);

		LeaveYearEndEmployeeDetailForm leaveYearEndEmployeeDetailForm = new LeaveYearEndEmployeeDetailForm();
		model.put("leaveYearEndEmployeeDetailForm",
				leaveYearEndEmployeeDetailForm);

		return new ModelAndView("admin/endYearProcessing");
	}

	@Override
	@RequestMapping(value = "dataExport.html", method = RequestMethod.GET)
	public ModelAndView showDataExport(
			ModelMap model,
			@ModelAttribute(value = "dataExportForm") DataExportForm dataExportForm,
			HttpServletRequest request, Locale locale) {
		UserContext.setLocale(locale);
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		List<EntityMasterDTO> entityList = excelExportToolLogic.getEntityList();
		model.put("entityList", entityList);

		Map<String, String> advanceFilterCombosHashMap = generalLogic
				.getEmployeeFilterComboList(companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(
				advanceFilterCombosHashMap, jsonConfig);
		model.put("advanceFilterCombosHashMap", jsonObject.toString());
		List<EmployeeFieldDTO> employeeList = generalLogic.returnEmployeesList(
				employeeId, companyId);
		model.put("employeeList", employeeList);
		return new ModelAndView("admin/dataExport", model);
	}

	@Override
	@RequestMapping(value = "dataImport.html", method = RequestMethod.GET)
	public ModelAndView showDataImport(
			ModelMap model,
			@ModelAttribute(value = "dataImportForm") DataImportForm dataImportForm,
			HttpServletRequest request, Locale locale) {
		UserContext.setLocale(locale);
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		List<EntityMasterDTO> entityList = dataImportLogic.getEntityList();
		List<MonthMasterDTO> monthList = generalLogic.getMonthList();
		List<PayslipFrequencyDTO> payslipFrequencyList = dataImportLogic
				.getPayslipFrequencyList();
		Integer companyPart = dataImportLogic.getPartforCompany(companyId);
		model.put("companyPart", companyPart);
		model.put("entityList", entityList);
		model.put("monthList", monthList);
		model.put("payslipFrequencyList", payslipFrequencyList);
		return new ModelAndView("admin/dataImport", model);
	}

	@Override
	@RequestMapping(value = "paySlipImport.html", method = RequestMethod.GET)
	public ModelAndView showPaySlipImport() {

		return new ModelAndView("admin/paySlipImport");
	}

	@Override
	@RequestMapping(value = "/companyInformation.html", method = RequestMethod.GET)
	public ModelAndView companyInformationPage(ModelMap mod,
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		CompanyCopyForm companyCopyForm = new CompanyCopyForm();
		mod.put("companyCopyForm", companyCopyForm);

		CompanyForm companyForm = new CompanyForm();
		mod.put("companyForm", companyForm);

		CompanyListForm companyListForm = new CompanyListForm();
		mod.put("companyListForm", companyListForm);

		List<CompanyForm> groupNameList = companyInformationLogic
				.getCompanyGroup();
		mod.put("groupNameList", groupNameList);

		List<CompanyForm> countryList = companyInformationLogic
				.getCountryList();
		mod.put("countryList", countryList);

		List<EntityListViewForm> viewNameList = companyInformationLogic
				.getViewName(companyId);
		mod.put("viewNameList", viewNameList);
		List<CompanyForm> financialYearList = companyInformationLogic
				.getFinancialYearList();
		mod.put("financialYearList", financialYearList);

		List<CompanyForm> paySlipFrequencyList = companyInformationLogic
				.getPaySlipFrequencyList();
		mod.put("paySlipFrequencyList", paySlipFrequencyList);

		EntityListViewForm entityListView = new EntityListViewForm();
		mod.put("entityListView", entityListView);
		return new ModelAndView("admin/company", mod);
	}

	@Override
	@RequestMapping(value = "/logoUpload.html", method = RequestMethod.GET)
	public ModelAndView logoUploadPage(ModelMap model,
			HttpServletRequest request) {
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		LogoUploadForm logoUploadForm = new LogoUploadForm();
		model.put("logoUploadForm", logoUploadForm);

		List<LogoUploadForm> companyList = logoUploadLogic
				.getCompanyList(employeeId);
		model.put("companyList", companyList);

		return new ModelAndView("admin/logoUpload", model);
	}

	@Override
	@RequestMapping(value = "companyDocumentCentre.html", method = RequestMethod.GET)
	public ModelAndView showDocCentre(ModelMap model,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) {
		UserContext.setLocale(locale);
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		CompanyDocumentCenterForm companyDocumentCenterForm = new CompanyDocumentCenterForm();
		model.put("companyDocumentCenterForm", companyDocumentCenterForm);

		List<CompanyDocumentCenterForm> categoryList = companyDocumentCenterLogic
				.getCategoryList();
		model.put("categoryList", categoryList);

		Map<String, String> advanceFilterCombosHashMap = generalLogic
				.getEmployeeFilterComboList(companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(
				advanceFilterCombosHashMap, jsonConfig);
		model.put("advanceFilterCombosHashMap", jsonObject.toString());

		return new ModelAndView("admin/companyDocumentCentre", model);
	}

	@Override
	@RequestMapping(value = "employeePreference.html", method = RequestMethod.GET)
	public ModelAndView employeePreferencePage(ModelMap mod,
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		EmployeePreferencesForm employeePreferencesForm = new EmployeePreferencesForm();
		mod.put("employeePreferencesForm", employeePreferencesForm);

		List<EmployeePreferencesForm> defaultEmployeeStatus = employeePreferencesLogic
				.getDefaultEmpStatus(companyId);
		mod.put("defaultEmployeeStatus", defaultEmployeeStatus);

		return new ModelAndView("admin/employeePreference", mod);
	}

	@Override
	@RequestMapping(value = "hrLetters.html", method = RequestMethod.GET)
	public ModelAndView showHRLetters(Map model, HttpServletRequest request,
			HttpServletResponse response) {

		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		HRLetterForm hrLetterForm = new HRLetterForm();
		model.put("hrLetterForm", hrLetterForm);

		Map<String, String> advanceFilterCombosHashMap = generalLogic
				.getEmployeeFilterComboList(companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(
				advanceFilterCombosHashMap, jsonConfig);
		model.put("advanceFilterCombosHashMap", jsonObject.toString());
		return new ModelAndView("admin/hrLetters", model);
	}

	@Override
	@RequestMapping(value = "/companyFormDesigner.html", method = RequestMethod.GET)
	public ModelAndView companyFormDesigner(
			ModelMap model,
			@ModelAttribute(value = "companyDynamicForm") CompanyDynamicForm companyDynamicForm,
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		List<CompanyDynamicForm> tabNameList = companyDynamicFormLogic
				.getTabList(companyId);
		model.addAttribute("tabNameList", tabNameList);
		return new ModelAndView("admin/companyFormDesigner", model);
	}

	@Override
	@RequestMapping(value = "holiDayListMaster.html", method = RequestMethod.GET)
	public ModelAndView showHoliDayListMaster(ModelMap model) {
		HolidayListMasterForm holidayListMasterForm = new HolidayListMasterForm();
		model.addAttribute("holidayListMasterForm", holidayListMasterForm);

		List<Integer> yearList = holidayListMasterLogic.getYearList();
		model.addAttribute("yearList", yearList);

		List<HolidayListMasterForm> countryList = holidayListMasterLogic
				.getCountryList();
		model.addAttribute("countryList", countryList);

		return new ModelAndView("admin/holidayListMaster", model);
	}

	@Override
	@RequestMapping(value = "payslipFormDesigner.html", method = RequestMethod.GET)
	public ModelAndView showPaySlipFormDesigner(
			ModelMap model,
			HttpServletRequest request,
			HttpServletResponse response,
			@ModelAttribute(value = "paySlipDynamicForm") PaySlipDynamicForm paySlipDynamicForm) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		List<PaySlipDynamicForm> tabNameList = paySlipDynamicFormLogic
				.getTabList(companyId);
		PaySlipDynamicForm paySlipForm = paySlipDynamicFormLogic
				.getPaySlipFrequencyDetails(companyId);
		PaySlipDynamicForm effectiveForm = paySlipDynamicFormLogic
				.getEffectiveFrom(companyId);
		PaySlipDynamicForm currentPayslip = paySlipDynamicFormLogic
				.getCurrentPayslipInfo(companyId);
		model.addAttribute("paySlipForm", paySlipForm);
		model.addAttribute("effectiveForm", effectiveForm);
		model.addAttribute("tabNameList", tabNameList);
		model.addAttribute("currentPayslip", currentPayslip);

		return new ModelAndView("admin/payslipDynamicForm", model);
	}

	@Override
	@RequestMapping(value = "multilingual.html", method = RequestMethod.GET)
	public ModelAndView showMultilingualPage(
			ModelMap model,
			HttpServletRequest request,
			HttpServletResponse response,
			@ModelAttribute(value = "paySlipDynamicForm") PaySlipDynamicForm paySlipDynamicForm,
			Locale locale) {
		UserContext.setLocale(locale);
		List<EntityMasterDTO> entityList = multilingualLogic.getEntityList();
		model.put("entityList", entityList);

		List<LanguageMasterDTO> languageList = multilingualLogic
				.getLanguageList();
		model.put("languageList", languageList);

		LeaveTypeForm leaveTypeForm = new LeaveTypeForm();
		model.put("leaveTypeForm", leaveTypeForm);

		MultilingualForm multilingualForm = new MultilingualForm();
		model.put("multilingualForm", multilingualForm);

		return new ModelAndView("admin/multilingual", model);
	}

	@Override
	@RequestMapping(value = "externalLink.html", method = RequestMethod.GET)
	public ModelAndView showExternalLink(ModelMap model) {
		ExternalLinkForm externalLinkForm = new ExternalLinkForm();
		model.put("externalLinkForm", externalLinkForm);
		return new ModelAndView("admin/externalLink", model);
	}

	@Override
	@RequestMapping(value = "empCalendarDefination.html", method = RequestMethod.GET)
	public ModelAndView showEmployeeCalendarDef(ModelMap model,
			HttpServletRequest request) {

		EmployeeCalendarDefForm employeeCalendarDefForm = new EmployeeCalendarDefForm();
		model.put("employeeCalendarDefForm", employeeCalendarDefForm);
		return new ModelAndView("admin/empCalendarDefinition", model);
	}

	@Override
	@RequestMapping(value = "assignLeaveScheme.html", method = RequestMethod.GET)
	public ModelAndView assignLeaveScheme(ModelMap model,
			HttpServletRequest request) {
		AssignLeaveSchemeForm assignLeaveSchemeForm = new AssignLeaveSchemeForm();
		model.put("assignLeaveSchemeForm", assignLeaveSchemeForm);

		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		List<LeaveReviewerForm> leaveSchemeList = leaveReviewerLogic
				.getLeaveSchemeList(companyId);
		model.put("leaveSchemeList", leaveSchemeList);

		return new ModelAndView("admin/assignLeaveScheme", model);
	}

	@Override
	@RequestMapping(value = "leaveBalanceSummary", method = RequestMethod.GET)
	public ModelAndView leaveBalanceSummary(ModelMap model,
			HttpServletRequest request, Locale locale) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		LeaveBalanceSummaryForm leaveBalanceSummaryForm = new LeaveBalanceSummaryForm();
		model.put("leaveBalanceSummaryForm", leaveBalanceSummaryForm);

		AddLeaveForm addLeaveForm = new AddLeaveForm();
		model.put("addLeaveForm", addLeaveForm);

		List<EmployeeLeaveSchemeTypeDTO> yearList = leaveBalanceSummaryLogic
				.getDistinctYears(companyId);
		model.put("yearList", yearList);

		List<ComboValueDTO> sessionList = leaveBalanceSummaryLogic
				.getLeaveSessionList();
		for (ComboValueDTO comboValueDTO : sessionList) {
			if (StringUtils.isNotBlank(comboValueDTO.getLabelKey())) {
				comboValueDTO.setLabel(messageSource.getMessage(
						comboValueDTO.getLabelKey(), new Object[] {}, locale));
			}
		}
		model.put("sessionList", sessionList);
		LeavePreferenceForm leavePreferenceForm = leaveBalanceSummaryLogic
				.isEncashedVisible(companyId);
		model.put("isEncashedHidden", leavePreferenceForm.getShowEncashed());
		model.put("isFullEntitlementHidden",
				leavePreferenceForm.getShowFullEntitlement());
		model.put("leaveUnitDefined", leavePreferenceForm.getLeaveUnit());

		return new ModelAndView("admin/leaveBalanceSummary", model);
	}

	@Override
	@RequestMapping(value = "/leaveEventReminder.html", method = RequestMethod.GET)
	public ModelAndView leaveEventReminder(ModelMap model,
			HttpServletRequest request) {

		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		LeaveEventReminderForm leaveEventReminderForm = new LeaveEventReminderForm();
		leaveEventReminderForm.setReminderEventDTOs(leaveEventReminderLogic
				.getLeaveReminderEvents());
		leaveEventReminderForm.setLeaveSchemeDTOs(leaveSchemeLogic
				.getAllLeaveSchemes(companyId));
		leaveEventReminderForm.setLeaveTypeDTOs(leaveTypeLogic
				.getAllLeaveTypes(companyId));
		leaveEventReminderForm.setRecAppCodeDTOs(leaveEventReminderLogic
				.getRecepientTypes());
		leaveEventReminderForm.setMailTemplateDTOs(leaveEventReminderLogic
				.getMailTemplates(companyId));
		model.put("leaveEventReminderForm", leaveEventReminderForm);

		return new ModelAndView("admin/leaveEventReminder");
	}

	@Override
	@RequestMapping(value = "/leaveGranter.html", method = RequestMethod.GET)
	public ModelAndView leaveGranter(ModelMap model, HttpServletRequest request) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		LeavePreferenceForm leavePreferenceForm = leaveBalanceSummaryLogic
				.isEncashedVisible(companyId);
		model.put("leaveUnitDefined", leavePreferenceForm.getLeaveUnit());
		return new ModelAndView("admin/leaveGranter");
	}

	@Override
	@RequestMapping(value = "/leavePreference.html", method = RequestMethod.GET)
	public ModelAndView leavePreference(ModelMap model, Locale locale) {

		LeavePreferenceForm leavePreferenceForm = new LeavePreferenceForm();
		model.put("leavePreferenceForm", leavePreferenceForm);

		List<LeavePreferenceForm> monthList = leavePreferenceLogic
				.getMonthList();
		model.put("monthList", monthList);

		List<LeavePreferenceForm> leaveTransList = leavePreferenceLogic
				.getLeaveTransactionList();
		for (LeavePreferenceForm preferenceForm : leaveTransList) {
			if (StringUtils.isNotBlank(preferenceForm
					.getLeaveTransAppCodeValue())) {
				preferenceForm.setLeaveTransName(messageSource.getMessage(
						preferenceForm.getLeaveTransAppCodeValue(),
						new Object[] {}, locale));
			}
		}

		model.put("leaveTransList", leaveTransList);

		return new ModelAndView("admin/leavePreference");
	}

	@Override
	@RequestMapping(value = "/leaveReports.html", method = RequestMethod.GET)
	public ModelAndView leaveReports(ModelMap model) {
		LeaveReportsForm leaveReportsForm = new LeaveReportsForm();
		model.put("leaveReportsForm", leaveReportsForm);
		return new ModelAndView("admin/leaveReports");
	}

	@Override
	@RequestMapping(value = "calTempDef.html", method = RequestMethod.GET)
	public ModelAndView showCalTempDef(ModelMap mod, HttpServletRequest request) {

		CalendarDefForm calDefForm = new CalendarDefForm();
		mod.put("calDefForm", calDefForm);

		CalendarTemplateMonthForm calTempMonthForm = new CalendarTemplateMonthForm();
		mod.put("calMonthForm", calTempMonthForm);

		List<AppCodeDTO> appCodeList = calendarDefLogic.getCodeValueList();
		mod.put("appCodeList", appCodeList);

		return new ModelAndView("admin/calTempDef", mod);
	}

	@Override
	@RequestMapping(value = "/LeaveReview.html", method = RequestMethod.GET)
	public ModelAndView showPendingItems(
			ModelMap model,
			@ModelAttribute("pendingItemsForm") PendingItemsForm pendingItemsForm,
			Locale locale) {
		List<ComboValueDTO> sessionList = addLeaveLogic.getLeaveSessionList();
		for (ComboValueDTO comboValueDTO : sessionList) {
			if (StringUtils.isNotBlank(comboValueDTO.getLabelKey())) {
				comboValueDTO.setLabel(messageSource.getMessage(
						comboValueDTO.getLabelKey(), new Object[] {}, locale));
			}
		}
		model.put("sessionList", sessionList);

		PendingItemsForm pendingItemsFrm = new PendingItemsForm();
		model.put("pendingItemsFrm", pendingItemsFrm);
		List<AppCodeDTO> workflowTypeList = pendingItemsLogic
				.getWorkflowTypeList();
		model.put("workflowTypeList", workflowTypeList);
		return new ModelAndView("admin/LeaveReview");
	}

	@Override
	@RequestMapping(value = "/assignClaimTemplate.html", method = RequestMethod.GET)
	public ModelAndView assignClaimTemplate(ModelMap model) {

		AssignClaimTemplateForm assignClaimTemplateForm = new AssignClaimTemplateForm();
		model.put("assignClaimTemplateForm", assignClaimTemplateForm);

		return new ModelAndView("admin/assignClaimTemplate");
	}

	@Override
	@RequestMapping(value = "/claimPreference.html", method = RequestMethod.GET)
	public ModelAndView claimPreference(ModelMap model) {

		ClaimPreferenceForm claimPreferenceForm = new ClaimPreferenceForm();
		model.put("claimPreferenceForm", claimPreferenceForm);

		return new ModelAndView("admin/claimPreference");
	}

	@Override
	@RequestMapping(value = "/employeeClaims.html", method = RequestMethod.GET)
	public ModelAndView employeeClaims(ModelMap model,
			HttpServletRequest request) {

		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		AddClaimForm addClaimForm = addClaimLogic.getClaimTemplates(companyId,
				employeeId, true);
		ClaimApplicationItemForm claimApplicationItemForm = new ClaimApplicationItemForm();
		ClaimApplicationItemAttach claimApplicationItemAttach = new ClaimApplicationItemAttach();
		ImportEmployeeClaimForm importEmployeeClaimForm = new ImportEmployeeClaimForm();
		model.put("claimApplicationItemForm", claimApplicationItemForm);
		model.put("claimApplicationItemAttach", claimApplicationItemAttach);
		model.put("addClaimForm", addClaimForm);
		model.put("importEmployeeClaimForm", importEmployeeClaimForm);
		List<CompanyForm> currencyNameList = companyInformationLogic
				.getCurrencyName();
		model.put("currencyNameList", currencyNameList);
		return new ModelAndView("admin/employeeClaims", model);
	}

	@Override
	@RequestMapping(value = "/leaveTranscationReport.html", method = RequestMethod.GET)
	public ModelAndView leaveTranscationReport(ModelMap model,
			HttpServletRequest request) {
		LeaveReportsForm leaveReportsForm = new LeaveReportsForm();
		model.put("leaveReportsForm", leaveReportsForm);

		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		List<EmployeeFilterListForm> filterList = leaveSchemeLogic
				.getEmployeeFilterList(companyId, employeeId, true);
		model.put("filterList", filterList);

		return new ModelAndView("admin/leaveReports");
	}

	@Override
	@RequestMapping(value = "payslipRelease.html", method = RequestMethod.GET)
	public ModelAndView payslipRelease(ModelMap model,
			HttpServletRequest request, HttpServletResponse response) {
		PaySlipReleaseForm payslipReleaseForm = new PaySlipReleaseForm();

		model.put("payslipReleaseForm", payslipReleaseForm);
		List<MonthMasterDTO> monthList = generalLogic.getMonthList();
		model.put("monthList", monthList);
		return new ModelAndView("admin/payslipRelease", model);
	}

	@Override
	@RequestMapping(value = "/claimReports.html", method = RequestMethod.GET)
	public ModelAndView claimReports(ModelMap model,
			HttpServletRequest request, HttpServletResponse response) {
		ClaimReportsForm claimReportsForm = new ClaimReportsForm();
		model.put("claimReportsForm", claimReportsForm);

		return new ModelAndView("admin/claimReports");
	}

	@Override
	@RequestMapping(value = "hrisReviewer.html", method = RequestMethod.GET)
	public ModelAndView hrisReviewer(ModelMap model,
			HttpServletRequest request, HttpServletResponse response) {
		HRISReviewerForm hrisReviewerForm = new HRISReviewerForm();

		List<HRISReviewerForm> workFlowRuleList = hrisReviewerLogic
				.getWorkFlowRuleList();

		hrisReviewerForm
				.setHrisReviewerRuleId1(workFlowRuleList.get(0) == null ? 0
						: workFlowRuleList.get(0).getHrisReviewerRuleId());
		hrisReviewerForm
				.setHrisReviewerRuleId2(workFlowRuleList.get(0) == null ? 0
						: workFlowRuleList.get(1).getHrisReviewerRuleId());
		hrisReviewerForm
				.setHrisReviewerRuleId3(workFlowRuleList.get(0) == null ? 0
						: workFlowRuleList.get(2).getHrisReviewerRuleId());

		model.put("hrisReviewerForm", hrisReviewerForm);

		return new ModelAndView("admin/hrisReviewer", model);
	}

	@Override
	@RequestMapping(value = "/hrisPreference.html", method = RequestMethod.GET)
	public ModelAndView hrisPreference(ModelMap model) {

		HRISPreferenceForm hrisPreferenceForm = new HRISPreferenceForm();
		model.put("HRISPreferenceForm", hrisPreferenceForm);

		return new ModelAndView("admin/HRISPreference");
	}

	@Override
	@RequestMapping(value = "/employeeEntitlements.html", method = RequestMethod.GET)
	public ModelAndView employeeEntitlements(ModelMap model,
			HttpServletRequest request) {

		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		List<ClaimReviewerForm> claimTemplateList = claimReviewerLogic
				.getClaimTemplateList(companyId);
		model.put("claimTemplateList", claimTemplateList);
		EmployeeClaimAdjustmentForm employeeClaimAdjustmentForm = new EmployeeClaimAdjustmentForm();
		model.put("employeeClaimAdjustmentForm", employeeClaimAdjustmentForm);
		return new ModelAndView("admin/employeeEntitlements", model);
	}

	@Override
	@RequestMapping(value = "lundinTimesheetPreference.html", method = RequestMethod.GET)
	public ModelAndView lundinTimesheetPreference(ModelMap model,
			HttpServletRequest request, HttpServletResponse response) {

		LundinTimesheetPreferenceForm lundinTimesheetPreferenceForm = new LundinTimesheetPreferenceForm();
		model.put("lundinTimesheetPreferenceForm",
				lundinTimesheetPreferenceForm);

		return new ModelAndView("admin/lundinTimesheetPreference", model);
	}

	@Override
	@RequestMapping(value = "lundinTimesheetEventReminder.html", method = RequestMethod.GET)
	public ModelAndView lundinTimesheetEventReminder(ModelMap model,
			HttpServletRequest request, HttpServletResponse response) {

		Long companyIdEnc = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		LundinTimesheetEventReminderForm lundinEventReminderForm = new LundinTimesheetEventReminderForm();
		lundinEventReminderForm
				.setReminderEventDTOs(lundinTimesheetEventReminderLogic
						.getTimesheetReminderEvents());
		lundinEventReminderForm
				.setRecAppCodeDTOs(lundinTimesheetEventReminderLogic
						.getTimesheetRecepientTypes());
		// lundinEventReminderForm
		// .setMailTemplateDTOs(lundinTimesheetEventReminderLogic
		// .getMailTemplates(companyId));
		model.put("lundinEventReminderForm", lundinEventReminderForm);

		return new ModelAndView("admin/lundinTimesheetEventReminder", model);
	}

	@Override
	@RequestMapping(value = "lundinTimesheetReviewer.html", method = RequestMethod.GET)
	public ModelAndView lundinTimesheetReviewer(ModelMap model,
			HttpServletRequest request, HttpServletResponse response) {
		LundinTimesheetReviewerForm otReviewerForm = new LundinTimesheetReviewerForm();

		List<LundinTimesheetReviewerForm> workFlowRuleList = lundinTimesheetReviewerLogic
				.getWorkFlowRuleList();

		otReviewerForm
				.setLundinReviewerRuleId1(workFlowRuleList.get(0) == null ? 0
						: workFlowRuleList.get(0).getLundinReviewerRuleId());
		otReviewerForm
				.setLundinReviewerRuleId2(workFlowRuleList.get(0) == null ? 0
						: workFlowRuleList.get(1).getLundinReviewerRuleId());
		otReviewerForm
				.setLundinReviewerRuleId3(workFlowRuleList.get(0) == null ? 0
						: workFlowRuleList.get(2).getLundinReviewerRuleId());

		model.put("LundinReviewerForm", otReviewerForm);

		return new ModelAndView("admin/lundinTimesheetReviewer", model);
	}

	@Override
	@RequestMapping(value = "lionTimesheetReviewer.html", method = RequestMethod.GET)
	public ModelAndView lionTimesheetReviewer(ModelMap model,
			HttpServletRequest request, HttpServletResponse response) {
		LundinTimesheetReviewerForm otReviewerForm = new LundinTimesheetReviewerForm();

		List<LundinTimesheetReviewerForm> workFlowRuleList = lundinTimesheetReviewerLogic
				.getWorkFlowRuleList();

		otReviewerForm
				.setLundinReviewerRuleId1(workFlowRuleList.get(0) == null ? 0
						: workFlowRuleList.get(0).getLundinReviewerRuleId());
		otReviewerForm
				.setLundinReviewerRuleId2(workFlowRuleList.get(0) == null ? 0
						: workFlowRuleList.get(1).getLundinReviewerRuleId());
		otReviewerForm
				.setLundinReviewerRuleId3(workFlowRuleList.get(0) == null ? 0
						: workFlowRuleList.get(2).getLundinReviewerRuleId());

		model.put("LundinReviewerForm", otReviewerForm);

		return new ModelAndView("admin/lionTimesheetReviewer", model);
	}

	@Override
	@RequestMapping(value = "/lundinTimesheetReports.html", method = RequestMethod.GET)
	public ModelAndView lundinTimesheetReports(ModelMap model,
			HttpServletRequest request, HttpServletResponse response) {

		LundinTimesheetReportsForm lundinTimesheetReportsForm = new LundinTimesheetReportsForm();
		model.put("lundinTimesheetReportsForm", lundinTimesheetReportsForm);

		return new ModelAndView("admin/lundinTimesheetReports", model);
	}

	@Override
	@RequestMapping(value = "/lionTimesheetReports.html", method = RequestMethod.GET)
	public ModelAndView lionTimesheetReports(ModelMap model,
			HttpServletRequest request, HttpServletResponse response) {

		LionTimesheetReportsForm lionTimesheetReportsForm = new LionTimesheetReportsForm();
		model.put("lionTimesheetReportsForm", lionTimesheetReportsForm);

		return new ModelAndView("admin/lionTimesheetReportsForAdmin", model);
	}

	@Override
	@RequestMapping(value = "lundinDepartment.html", method = RequestMethod.GET)
	public ModelAndView getLundinDepartment(Map model,
			HttpServletRequest request, HttpServletResponse response) {
		LundinDepartmentDTO lundinDepartmentDto = lundinDepartmentLogic
				.getDepartmentType();
		model.put("lundinDeptForm", lundinDepartmentDto);
		return new ModelAndView("admin/lundinDepartment", model);
	}

	@Override
	@RequestMapping(value = "lundinBlock.html", method = RequestMethod.GET)
	public ModelAndView getLundinBlock(Map model, HttpServletRequest request,
			HttpServletResponse response) {
		LundinBlockDTO lundinBlockDto = new LundinBlockDTO();
		lundinBlockDto.setBlockId(10L);
		model.put("lundinBlockForm", lundinBlockDto);

		return new ModelAndView("admin/lundinBlock", model);
	}

	@Override
	@RequestMapping(value = "lundinAFE.html", method = RequestMethod.GET)
	public ModelAndView getLundinAFE(Map model, HttpServletRequest request,
			HttpServletResponse response) {
		LundinAFEDTO LundinAFEDto = new LundinAFEDTO();
		model.put("lundinAfeForm", LundinAFEDto);

		return new ModelAndView("admin/lundinAFE", model);
	}

	@Override
	@RequestMapping(value = "/creditSetup.html", method = RequestMethod.GET)
	public ModelAndView creditSetup(ModelMap model) {
		return new ModelAndView("admin/creditSetup");
	}

	@Override
	@RequestMapping(value = "/creditItems.html", method = RequestMethod.GET)
	public ModelAndView creditItems(ModelMap model) {
		DiscussionBoardForm discussionBoardForm = new DiscussionBoardForm();
		model.put("discussionBoardForm", discussionBoardForm);
		return new ModelAndView("admin/creditItems");
	}

	@Override
	@RequestMapping(value = "/applicationOfEBenefits.html", method = RequestMethod.GET)
	public ModelAndView applicationOfEBenefits(ModelMap model) {
		AddClaimForm addClaimForm = new AddClaimForm();
		ClaimApplicationItemForm claimApplicationItemForm = new ClaimApplicationItemForm();
		ClaimApplicationItemAttach claimApplicationItemAttach = new ClaimApplicationItemAttach();
		model.put("claimApplicationItemForm", claimApplicationItemForm);
		model.put("claimApplicationItemAttach", claimApplicationItemAttach);
		List<CompanyForm> currencyNameList = companyInformationLogic
				.getCurrencyName();
		model.put("currencyNameList", currencyNameList);
		model.put("addClaimForm", addClaimForm);

		return new ModelAndView("admin/applicationOfEBenefits", model);
	}

	@Override
	@RequestMapping(value = "/hrisReports.html", method = RequestMethod.GET)
	public ModelAndView hrisReports(ModelMap model) {
		HRISReportsForm hrisReportsForm = new HRISReportsForm();
		model.put("hrisReportsForm", hrisReportsForm);

		ClaimReportsForm claimReportsForm = new ClaimReportsForm();
		model.put("claimReportsForm", claimReportsForm);
		return new ModelAndView("admin/hrisReports");
	}

	@Override
	@RequestMapping(value = "/discussionBoard.html", method = RequestMethod.GET)
	public ModelAndView discussionBoard(ModelMap model) {
		DiscussionBoardForm discussionBoardForm = new DiscussionBoardForm();
		model.put("discussionBoardForm", discussionBoardForm);
		return new ModelAndView("admin/discussionBoard");
	}

	@Override
	@RequestMapping(value = "/discussionTopic.html", method = RequestMethod.GET)
	public ModelAndView discussionTopic(ModelMap model) {
		DiscussionTopicCommentForm discussionTopicReplyForm = new DiscussionTopicCommentForm();
		model.put("discussionTopicReplyForm", discussionTopicReplyForm);

		DiscussionBoardForm discussionBoardForm = new DiscussionBoardForm();
		model.put("discussionBoardForm", discussionBoardForm);
		return new ModelAndView("admin/discussionTopic", model);
	}

	@Override
	@RequestMapping(value = "/manageModule.html", method = RequestMethod.GET)
	public ModelAndView manageModule(ModelMap model) {
		ManageModuleDTO manageModuleDTO = new ManageModuleDTO();

		model.put("manageModuleDTO", manageModuleDTO);
		return new ModelAndView("admin/manageModule", model);
	}

	@Override
	@RequestMapping(value = "/lionhkTimesheetPreference.html", method = RequestMethod.GET)
	public ModelAndView lionhkTimesheetPreference(ModelMap model,
			HttpServletRequest request, HttpServletResponse response) {

		LionTimesheetPreferenceForm lionTimesheetPreferenceForm = new LionTimesheetPreferenceForm();
		model.put("lionTimesheetPreferenceForm", lionTimesheetPreferenceForm);

		return new ModelAndView("admin/lionhkTimesheetPreference", model);
	}

	@Override
	@RequestMapping(value = "/lionhkETimesheetScreen.html", method = RequestMethod.GET)
	public ModelAndView lionhkETimesheetScreen(ModelMap model,
			HttpServletRequest request, HttpServletResponse response) {

		LundinTimesheetPreferenceForm lundinTimesheetPreferenceForm = new LundinTimesheetPreferenceForm();
		model.put("lundinTimesheetPreferenceForm",
				lundinTimesheetPreferenceForm);
		List<Date> dateList = new ArrayList<Date>();
		Date date = new Date("01-Jan-2016");
		Calendar c = Calendar.getInstance();
		c.setTime(date);

		dateList.add(c.getTime());

		c.add(Calendar.DATE, 1);
		dateList.add(c.getTime());

		c.add(Calendar.DATE, 1);
		dateList.add(c.getTime());
		/*
		 * dateList.add("03-Jan-2016"); dateList.add("04-Jan-2016");
		 * dateList.add("05-Jan-2016"); dateList.add("06-Jan-2016");
		 */
		model.addAttribute("dateList", dateList);
		return new ModelAndView("admin/lionhkETimesheetScreen", model);
	}

	@Override
	@RequestMapping(value = "lionhkPendingTimesheetScreen.html", method = RequestMethod.GET)
	public ModelAndView lionhkPendingTimesheetScreen(ModelMap model,
			HttpServletRequest request, HttpServletResponse response) {

		LundinTimesheetPreferenceForm lundinTimesheetPreferenceForm = new LundinTimesheetPreferenceForm();

		return new ModelAndView("admin/lionhkPendingTimesheetScreen", model);
	}

	@Override
	@RequestMapping(value = "coherentTimesheetPreference.html", method = RequestMethod.GET)
	public ModelAndView coherentTimesheetPreference(ModelMap model,
			HttpServletRequest request, HttpServletResponse response) {

		CoherentTimesheetPreferenceForm coherentTimesheetPreferenceForm = new CoherentTimesheetPreferenceForm();
		model.put("coherentTimesheetPreferenceForm",
				coherentTimesheetPreferenceForm);

		return new ModelAndView("admin/coherentTimesheetPreference", model);
	}

	@Override
	@RequestMapping(value = "coherentEmployeeOvertimeAdmin.html", method = RequestMethod.GET)
	public ModelAndView coherentEmployeeOvertimeAdmin(ModelMap model,
			HttpServletRequest request, HttpServletResponse response) {
		CoherentOvertimeDetailForm coherentOvertimeDetailForm = new CoherentOvertimeDetailForm();
		model.put("coherentOvertimeDetailForm", coherentOvertimeDetailForm);

		ImportEmployeeOvertimeShiftForm importCOEmpOvertimeForm = new ImportEmployeeOvertimeShiftForm();
		model.put("importCOEmpOvertimeForm", importCOEmpOvertimeForm);
		return new ModelAndView("admin/coherentEmployeeOvertimeAdmin", model);
	}

	@Override
	@RequestMapping(value = "coherentEmployeeShiftAdmin.html", method = RequestMethod.GET)
	public ModelAndView coherentEmployeeShiftAdmin(ModelMap model,
			HttpServletRequest request, HttpServletResponse response) {
		CoherentShiftDetailForm coherentShiftDetailForm = new CoherentShiftDetailForm();
		model.put("coherentShiftDetailForm", coherentShiftDetailForm);
		ImportEmployeeOvertimeShiftForm importCOEmpOvertimeForm = new ImportEmployeeOvertimeShiftForm();
		model.put("importCOEmpOvertimeForm", importCOEmpOvertimeForm);
		return new ModelAndView("admin/coherentEmployeeShiftAdmin", model);
	}

	@Override
	@RequestMapping(value = "/coherentTimesheetReports.html", method = RequestMethod.GET)
	public ModelAndView coherentTimesheetReports(ModelMap model,
			HttpServletRequest request, HttpServletResponse response) {

		CoherentTimesheetReportsForm coherentTimesheetReportsForm = new CoherentTimesheetReportsForm();
		model.put("coherentTimesheetReportsForm", coherentTimesheetReportsForm);

		return new ModelAndView("admin/coherentTimesheetReportsForAdmin", model);
	}

	@Override
	@RequestMapping(value = "/employeeItemEntitlements.html", method = RequestMethod.GET)
	public ModelAndView EmployeeItemEntitlements(ModelMap model,
			HttpServletRequest request) {

		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		List<ClaimReviewerForm> claimTemplateList = claimReviewerLogic
				.getClaimTemplateList(companyId);
		model.put("claimTemplateList", claimTemplateList);
		EmployeeClaimAdjustmentForm employeeClaimAdjustmentForm = new EmployeeClaimAdjustmentForm();
		model.put("employeeClaimAdjustmentForm", employeeClaimAdjustmentForm);
		EmployeeItemEntitlementForm itemEntitlementForm = new EmployeeItemEntitlementForm();
		model.put("itemEntitlementForm", itemEntitlementForm);
		return new ModelAndView("admin/EmployeeItemEntitlements", model);
	}

	@Override
	@RequestMapping(value = "viewSsoConfiguration.html", method = RequestMethod.GET)
	public ModelAndView viewSsoConfigure(ModelMap model, HttpServletRequest request) {

		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		SsoConfigurationDTO ssoConfigurationDTO = ssoConfigurationLogic.getSsoConfigurationForComapny(companyId);
		ssoConfigurationDTO.setCompanyId(companyId);
		model.put("ssoEnable", ssoConfigurationDTO.getIsEnableSso());
		model.put("ssoconfiguration", ssoConfigurationDTO);
		return new ModelAndView("admin/ssoConfiguration", model);
	}

	@Override
	@RequestMapping(value = "saveSsoConfiguration.html", method = RequestMethod.POST)
	public ModelAndView saveSsoConfiguration(ModelMap model, HttpServletRequest request,
			@ModelAttribute("ssoconfiguration") SsoConfigurationDTO ssoConfigurationDTO) {

		ssoConfigurationLogic.saveSsoConfiguration(ssoConfigurationDTO);

		return new ModelAndView("admin/ssoConfiguration");
	}
	
	//-------------WORKDAY--------------//
	
	@Override
	@RequestMapping(value = "/hroWorkdayFtp.html", method = RequestMethod.GET)
	public ModelAndView hroWorkdayFtp(HttpServletRequest request, ModelMap model) {
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		// to fetch FTP Config data
		List<DateFormatDTO> dateFormatList = workdayFtpIntegrationLogic.getdateFormatList();
		WorkdayFtpConfigForm workdayFtpConfig = workdayFtpIntegrationLogic.getFTPConfigFormData(companyId);

		if (workdayFtpConfig == null) {

			workdayFtpConfig = new WorkdayFtpConfigForm();
			workdayFtpConfig.setFtpPort(Integer.valueOf(PayAsiaConstants.SFTP_DEFAULT_PORT));
		}
		model.put("dateFormatList", dateFormatList);
		model.put("ftpConfigForm", workdayFtpConfig);
		
		return new ModelAndView("admin/workdayFtpConfig", model);
	}

	@Override
	@RequestMapping(value = "/hroWorkdayEmployeeData.html", method = RequestMethod.GET)
	public ModelAndView hroWorkdayEmployeeData(HttpServletRequest request, ModelMap model) {
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		// to fetch WD Emp Fields data
		List<WorkdayFieldMappingDTO> workdayEmpFieldMappingList = workdayFtpIntegrationLogic
				.getWorkdayFieldMappingList(companyId, PayAsiaConstants.EMPLOYEE_ENTITY_NAME);
		model.put("empFieldMappings", workdayEmpFieldMappingList);
		return new ModelAndView("admin/workdayEmployeeData", model);
	}

	@Override
	@RequestMapping(value = "/hroWorkdayPayrollData.html", method = RequestMethod.GET)
	public ModelAndView hroWorkdayPayrollData(HttpServletRequest request, ModelMap model) {
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		// to fetch WD Payroll Fields data
		List<WorkdayFieldMappingDTO> workdayPayFieldMappingList =
				// TODO may have to change employee entity argument
				workdayFtpIntegrationLogic.getWorkdayFieldMappingList(companyId, "Payroll");
		model.put("payFieldMappings", workdayPayFieldMappingList);
		return new ModelAndView("admin/workdayPayrollData", model);
	}

	@Override
	@RequestMapping(value = "/hroWorkdayReport.html", method = RequestMethod.GET)
	public ModelAndView hroWorkdayReport(HttpServletRequest request, ModelMap model) {
		WorkdayGenerateReportForm workdayGererateReportForm =new WorkdayGenerateReportForm();
		model.put("workDayReportsForm", workdayGererateReportForm);
		return new ModelAndView("admin/hroWorkdayReport", model);
	}
}
