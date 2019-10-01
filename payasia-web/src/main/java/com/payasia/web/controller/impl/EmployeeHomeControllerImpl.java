package com.payasia.web.controller.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.payasia.common.dto.EmployeeLeaveSchemeTypeDTO;
import com.payasia.common.dto.LanguageListDTO;
import com.payasia.common.dto.MonthMasterDTO;
import com.payasia.common.dto.PasswordPolicyDTO;
import com.payasia.common.form.AddClaimForm;
import com.payasia.common.form.AddLeaveForm;
import com.payasia.common.form.AnouncementForm;
import com.payasia.common.form.ChangePasswordForm;
import com.payasia.common.form.ClaimApplicationItemAttach;
import com.payasia.common.form.ClaimApplicationItemForm;
import com.payasia.common.form.ClaimApplicationItemWorkflowForm;
import com.payasia.common.form.ClaimReportsForm;
import com.payasia.common.form.CoherentTimesheetReportsForm;
import com.payasia.common.form.CompanyDocumentCenterForm;
import com.payasia.common.form.CompanyForm;
import com.payasia.common.form.DynamicFormTableDocumentDTO;
import com.payasia.common.form.EmpDocumentCenterForm;
import com.payasia.common.form.EmpOTAddForm;
import com.payasia.common.form.EmployeeContactUSForm;
import com.payasia.common.form.EmployeeListForm;
import com.payasia.common.form.EmployeePaySlipForm;
import com.payasia.common.form.EmployeeWorkFlowForm;
import com.payasia.common.form.ExternalLinkForm;
import com.payasia.common.form.HRLetterForm;
import com.payasia.common.form.HrisChangeRequestForm;
import com.payasia.common.form.HrisPendingItemsForm;
import com.payasia.common.form.LeaveBalanceSummaryForm;
import com.payasia.common.form.LeavePreferenceForm;
import com.payasia.common.form.LeaveReportsForm;
import com.payasia.common.form.LeaveSchemeForm;
import com.payasia.common.form.LionTimesheetReportsForm;
import com.payasia.common.form.LundinTimesheetReportsForm;
import com.payasia.common.form.PendingClaimsForm;
import com.payasia.common.form.PendingItemsForm;
import com.payasia.common.form.WorkFlowDelegateForm;
import com.payasia.dao.LanguageMasterDAO;
import com.payasia.dao.bean.LanguageMaster;
import com.payasia.logic.AddClaimLogic;
import com.payasia.logic.AddLeaveLogic;
import com.payasia.logic.CompanyDocumentCenterLogic;
import com.payasia.logic.CompanyInformationLogic;
import com.payasia.logic.EmpOTAddLogic;
import com.payasia.logic.EmployeeChangePasswordLogic;
import com.payasia.logic.EmployeeDetailLogic;
import com.payasia.logic.EmployeePaySlipLogic;
import com.payasia.logic.EmployeeWorkFlowLogic;
import com.payasia.logic.ExternalLinkLogic;
import com.payasia.logic.GeneralLogic;
import com.payasia.logic.LanguageMasterLogic;
import com.payasia.logic.LeaveBalanceSummaryLogic;
import com.payasia.logic.LundinDepartmentLogic;
import com.payasia.logic.PendingItemsLogic;
import com.payasia.logic.WorkFlowDelegateLogic;
import com.payasia.web.controller.EmployeeHomeController;
import com.payasia.web.util.PayAsiaSessionAttributes;

@Controller
@RequestMapping(value = "/employee")
public class EmployeeHomeControllerImpl implements EmployeeHomeController {

	@Resource
	EmployeePaySlipLogic employeePaySlipLogic;
	@Resource
	LundinDepartmentLogic lundinDepartmentLogic;
	@Resource
	EmployeeDetailLogic employeeDetailLogic;
	@Resource
	GeneralLogic generalLogic;
	@Resource
	LanguageMasterLogic languageMasterLogic;
	@Resource
	CompanyDocumentCenterLogic companyDocumentCenterLogic;
	@Resource
	ExternalLinkLogic externalLinkLogic;
	@Resource
	LeaveBalanceSummaryLogic leaveBalanceSummaryLogic;
	@Resource
	AddLeaveLogic addLeaveLogic;
	@Resource
	PendingItemsLogic pendingItemsLogic;
	@Resource
	AddClaimLogic addClaimLogic;
	@Resource
	EmpOTAddLogic empOTAddLogic;
	@Resource
	EmployeeWorkFlowLogic employeeWorkFlowLogic;
	@Resource
	CompanyInformationLogic companyInformationLogic;
	@Resource
	EmployeeChangePasswordLogic employeeChangePasswordLogic;
	@Resource
	WorkFlowDelegateLogic workflowDelegateLogic;

	/** The message source. */
	@Autowired
	private MessageSource messageSource;
	@Resource
	private LanguageMasterDAO languageMasterDAO;

	@Override
	@RequestMapping(value = "/home.html", method = RequestMethod.GET)
	public ModelAndView homePage(ModelMap model, HttpServletRequest request,
			Locale locale) {

		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

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

		AnouncementForm anouncementForm = new AnouncementForm();
		model.put("anouncementForm", anouncementForm);

		ExternalLinkForm externalLinkForm = externalLinkLogic
				.getExternalLink(companyId);
		if (externalLinkForm.getImage() == null) {
			externalLinkForm.setResponseString("ERROR");
		} else {
			externalLinkForm.setResponseString("SUCCESS");
		}
		model.put("externalLink", externalLinkForm);

		AddClaimForm addClaimForm = new AddClaimForm();
		model.put("addClaimForm", addClaimForm);

		PendingItemsForm pendingItemsForm = new PendingItemsForm();
		model.put("pendingItemsForm", pendingItemsForm);
		AddLeaveForm addLeaveForm = new AddLeaveForm();
		model.put("addLeaveForm", addLeaveForm);

		HrisPendingItemsForm hrisPendingItemsForm = new HrisPendingItemsForm();
		model.put("hrisPendingItemsForm", hrisPendingItemsForm);

		HrisChangeRequestForm hrisChangeRequestForm = new HrisChangeRequestForm();
		model.put("hrisChangeRequestForm", hrisChangeRequestForm);

		PendingClaimsForm pendingClaimsForm = new PendingClaimsForm();
		model.put("pendingClaimsForm", pendingClaimsForm);

		ClaimApplicationItemWorkflowForm claimApplicationItemWorkflowForm = new ClaimApplicationItemWorkflowForm();
		model.put("claimApplicationItemWorkflowForm",
				claimApplicationItemWorkflowForm);

		LeaveSchemeForm leaveScheme = addLeaveLogic.getLeaveSchemes(companyId,
				employeeId);

		model.put("leaveScheme", leaveScheme);

		List<ComboValueDTO> sessionList = addLeaveLogic.getLeaveSessionList();
		for (ComboValueDTO comboValueDTO : sessionList) {
			if (StringUtils.isNotBlank(comboValueDTO.getLabelKey())) {
				comboValueDTO.setLabel(messageSource.getMessage(
						comboValueDTO.getLabelKey(), new Object[] {}, locale));
			}
		}
		model.put("sessionList", sessionList);

		return new ModelAndView("employee/home");
	}

	@Override
	@RequestMapping(value = "/information.html", method = RequestMethod.GET)
	public ModelAndView employeeHomePage(ModelMap model) {
		EmployeeListForm employeeListForm = new EmployeeListForm();
		model.put("employeeListForm", employeeListForm);

		DynamicFormTableDocumentDTO dynamicFormDocumentDTO = new DynamicFormTableDocumentDTO();
		model.put("addEmpDynDocumentForm", dynamicFormDocumentDTO);
		return new ModelAndView("employee/information", model);

	}

	@Override
	@RequestMapping(value = "/claimList.html", method = RequestMethod.GET)
	public ModelAndView claimListPage(ModelMap model, HttpServletRequest request) {

		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		AddClaimForm addClaimForm = addClaimLogic.getClaimTemplates(companyId,
				employeeId, false);
		ClaimApplicationItemForm claimApplicationItemForm = new ClaimApplicationItemForm();
		ClaimApplicationItemAttach claimApplicationItemAttach = new ClaimApplicationItemAttach();
		model.put("claimApplicationItemForm", claimApplicationItemForm);
		model.put("claimApplicationItemAttach", claimApplicationItemAttach);
		model.put("addClaimForm", addClaimForm);
		return new ModelAndView("employee/addClaims", model);
	}

	@Override
	@RequestMapping(value = "/myClaims.html", method = RequestMethod.GET)
	public ModelAndView myClaimsPage(ModelMap model, HttpServletRequest request) {

		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		AddClaimForm addClaimForm = addClaimLogic.getClaimTemplates(companyId,
				employeeId, false);
		ClaimApplicationItemForm claimApplicationItemForm = new ClaimApplicationItemForm();
		ClaimApplicationItemAttach claimApplicationItemAttach = new ClaimApplicationItemAttach();
		model.put("claimApplicationItemForm", claimApplicationItemForm);
		model.put("claimApplicationItemAttach", claimApplicationItemAttach);
		List<CompanyForm> currencyNameList = companyInformationLogic
				.getCurrencyName();
		model.put("currencyNameList", currencyNameList);
		model.put("addClaimForm", addClaimForm);

		return new ModelAndView("employee/myClaims", model);
	}

	@Override
	@RequestMapping(value = "/empDocumentCenter.html", method = RequestMethod.GET)
	public ModelAndView documentCenterPage(ModelMap model, Locale locale) {
		UserContext.setLocale(locale);
		EmpDocumentCenterForm employeeDocumentCenterForm = new EmpDocumentCenterForm();
		model.put("employeeDocumentCenterForm", employeeDocumentCenterForm);

		CompanyDocumentCenterForm companyDocumentCenterForm = new CompanyDocumentCenterForm();
		model.put("companyDocumentCenterForm", companyDocumentCenterForm);

		List<CompanyDocumentCenterForm> categoryList = companyDocumentCenterLogic
				.getCategoryList();
		model.put("categoryList", categoryList);

		return new ModelAndView("employee/empDocumentCenter", model);
	}

	@Override
	@RequestMapping(value = "/addLeaves.html", method = RequestMethod.GET)
	public ModelAndView showEmpaddLeave(ModelMap model,
			@ModelAttribute(value = "addLeaveForm") AddLeaveForm addLeaveForm,
			HttpServletRequest request, Locale locale) {

		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		LeaveSchemeForm leaveScheme = addLeaveLogic.getLeaveSchemes(companyId,
				employeeId);
		List<ComboValueDTO> sessionList = addLeaveLogic.getLeaveSessionList();
		for (ComboValueDTO comboValueDTO : sessionList) {
			if (StringUtils.isNotBlank(comboValueDTO.getLabelKey())) {
				comboValueDTO.setLabel(messageSource.getMessage(
						comboValueDTO.getLabelKey(), new Object[] {}, locale));
			}
		}
		model.put("sessionList", sessionList);
		LeavePreferenceForm leavePreferenceForm = leaveBalanceSummaryLogic
				.isEncashedVisible(companyId);
		model.put("leaveUnitDefined", leavePreferenceForm.getLeaveUnit());
		model.put("leaveScheme", leaveScheme);
		return new ModelAndView("employee/addLeaves", model);
	}

	@Override
	@RequestMapping(value = "/pendingItems.html", method = RequestMethod.GET)
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

		List<AppCodeDTO> workflowTypeList = pendingItemsLogic
				.getWorkflowTypeList();
		model.put("workflowTypeList", workflowTypeList);
		return new ModelAndView("employee/pendingItems");
	}

	@Override
	@RequestMapping(value = "/pendingClaims.html", method = RequestMethod.GET)
	public ModelAndView showPendingClaims(ModelMap model) {

		AddClaimForm addClaimForm = new AddClaimForm();
		model.put("addClaimForm", addClaimForm);
		PendingClaimsForm pendingClaimsForm = new PendingClaimsForm();
		model.put("pendingClaimsForm", pendingClaimsForm);

		ClaimApplicationItemWorkflowForm claimApplicationItemWorkflowForm = new ClaimApplicationItemWorkflowForm();
		model.put("claimApplicationItemWorkflowForm",
				claimApplicationItemWorkflowForm);
		return new ModelAndView("employee/pendingClaims");
	}

	@Override
	@RequestMapping(value = "/workFlowDelegate.html", method = RequestMethod.GET)
	public ModelAndView showWorkFlowDelegate(
			ModelMap model,
			@ModelAttribute("employeeWorkflowForm") EmployeeWorkFlowForm employeeWorkFlowForm,
			HttpServletRequest request, Locale locale) {

		WorkFlowDelegateForm workFlowDelegateForm = new WorkFlowDelegateForm();
		UserContext.setLocale(locale);
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

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

		String employeeName = employeeDetailLogic.getEmployeeName(employeeId);
		List<WorkFlowDelegateForm> workflowTypeList = workflowDelegateLogic
				.getWorkflowTypeList(companyModuleDTO);
		model.addAttribute("workflowTypeList", workflowTypeList);
		workFlowDelegateForm.setEmployeeName(employeeName);
		workFlowDelegateForm.setUserId(employeeId);
		model.addAttribute("workFlowDelegateForm", workFlowDelegateForm);

		model.addAttribute("userName", employeeName);

		return new ModelAndView("employee/workFlowDelegate", model);
	}

	@Override
	@RequestMapping(value = "/modifyClaims.html", method = RequestMethod.GET)
	public ModelAndView modifyClaimsPage() {

		return new ModelAndView("employee/modifyClaims");
	}

	@Override
	@RequestMapping(value = "/withdrawClaims.html", method = RequestMethod.GET)
	public ModelAndView withdrawClaimsPage() {

		return new ModelAndView("employee/withdrawClaims");
	}

	@Override
	@RequestMapping(value = "/changePassword.html", method = RequestMethod.GET)
	public ModelAndView changePasswordPage(ModelMap model,
			HttpServletRequest request, Locale locale) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		ChangePasswordForm changePasswordForm = employeeChangePasswordLogic
				.getPassWordPolicyDetails(companyId);
		List<PasswordPolicyDTO> passwordPolicyDTOs = changePasswordForm
				.getPasswordPolicyDTO();
		List<PasswordPolicyDTO> passWordPolicyDetails = new ArrayList<>();
		Integer msgNumber = 1;
		for (PasswordPolicyDTO passwordPolicy : passwordPolicyDTOs) {
			PasswordPolicyDTO passwordPolicyDTO = new PasswordPolicyDTO();
			if (passwordPolicy.getValue() != null) {
				passwordPolicyDTO.setMessage(messageSource.getMessage(
						passwordPolicy.getMessage(),
						new Object[] { passwordPolicy.getValue() }, locale));
			} else {
				passwordPolicyDTO.setMessage(messageSource.getMessage(
						passwordPolicy.getMessage(), new Object[] {}, locale));
			}

			passwordPolicyDTO.setMsgSrNum(msgNumber);
			msgNumber++;

			passWordPolicyDetails.add(passwordPolicyDTO);

		}
		changePasswordForm.getPasswordPolicyDTO().clear();
		changePasswordForm.setPasswordPolicyDTO(passWordPolicyDetails);
		model.put("changePasswordForm", changePasswordForm);
		return new ModelAndView("employee/changePassword");
	}

	@Override
	@RequestMapping(value = "/paySlip.html", method = RequestMethod.GET)
	public ModelAndView paySlipPage(
			ModelMap model,
			@ModelAttribute(value = "employeePaySlipForm") EmployeePaySlipForm employeePaySlip,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) {
		UserContext.setLocale(locale);
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		ExternalLinkForm externalLinkForm = externalLinkLogic
				.getExternalLink(companyId);

		if (externalLinkForm.getImage() == null) {
			externalLinkForm.setResponseString("ERROR");
		} else {
			externalLinkForm.setResponseString("SUCCESS");
		}

		model.put("externalLink", externalLinkForm);

		EmployeePaySlipForm employeePaySlipForm = employeePaySlipLogic
				.getPaySlipFrequencyDetails(companyId);
		model.addAttribute(employeePaySlipForm);
		List<MonthMasterDTO> monthList = generalLogic.getMonthList();
		model.put("monthList", monthList);

		List<Integer> yearList = employeePaySlipLogic.getYearList(employeeId);
		model.put("yearList", yearList);

		return new ModelAndView("employee/paySlip", model);
	}

	@Override
	@RequestMapping(value = "/myBalances.html", method = RequestMethod.GET)
	public ModelAndView myBalancesPage() {

		return new ModelAndView("employee/myBalances");
	}

	@Override
	@RequestMapping(value = "/switchUser.html", method = RequestMethod.GET)
	public ModelAndView switchUserPage() {

		return new ModelAndView("employee/switchUser");
	}

	@Override
	@RequestMapping(value = "/modifyLeaves.html", method = RequestMethod.GET)
	public ModelAndView swithDrawLeaves() {

		return new ModelAndView("employee/modifyLeaves");
	}

	@Override
	@RequestMapping(value = "/withDrawLeaves.html", method = RequestMethod.GET)
	public ModelAndView withDrawLeaves() {

		return new ModelAndView("employee/withDrawLeaves");
	}

	@Override
	@RequestMapping(value = "/oTAdd.html", method = RequestMethod.GET)
	public ModelAndView oTAddPage(ModelMap model, HttpServletRequest request,
			HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		EmpOTAddForm empOTAddForm = new EmpOTAddForm();
		model.put("empOTAddForm", empOTAddForm);

		List<EmpOTAddForm> otTemplateList = empOTAddLogic
				.getOTTemplateList(companyId);
		model.put("otTemplateList", otTemplateList);

		List<EmpOTAddForm> otDayTypeList = empOTAddLogic.getOTDayTypeList();
		model.put("otDayTypeList", otDayTypeList);
		return new ModelAndView("employee/oTAdd", model);
	}

	@Override
	@RequestMapping(value = "/oTModify.html", method = RequestMethod.GET)
	public ModelAndView oTModifyPage() {

		return new ModelAndView("employee/oTModify");
	}

	@Override
	@RequestMapping(value = "/oTWithdraw.html", method = RequestMethod.GET)
	public ModelAndView oTWithdrawPage() {

		return new ModelAndView("employee/oTWithdraw");
	}

	@Override
	@RequestMapping(value = "/myRequest.html", method = RequestMethod.GET)
	public ModelAndView showMyRequest() {

		return new ModelAndView("employee/myRequest");
	}

	@Override
	@RequestMapping(value = "/contactUs.html", method = RequestMethod.GET)
	public ModelAndView showContactUs(ModelMap model) {

		EmployeeContactUSForm employeeContactUSForm = new EmployeeContactUSForm();
		model.put("employeeContactUSForm", employeeContactUSForm);
		return new ModelAndView("employee/contactUs");
	}

	@Override
	@RequestMapping(value = "/employeeChangePassword.html", method = RequestMethod.GET)
	public ModelAndView employeeChangePassword(ModelMap model,
			HttpServletRequest request, Locale locale) {

		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		ChangePasswordForm changePasswordForm = employeeChangePasswordLogic
				.getPassWordPolicyDetails(companyId);
		List<PasswordPolicyDTO> passwordPolicyDTOs = changePasswordForm
				.getPasswordPolicyDTO();
		List<PasswordPolicyDTO> passWordPolicyDetails = new ArrayList<>();
		Integer msgNumber = 1;
		for (PasswordPolicyDTO passwordPolicy : passwordPolicyDTOs) {
			PasswordPolicyDTO passwordPolicyDTO = new PasswordPolicyDTO();
			if (passwordPolicy.getValue() != null) {
				passwordPolicyDTO.setMessage(messageSource.getMessage(
						passwordPolicy.getMessage(),
						new Object[] { passwordPolicy.getValue() }, locale));
			} else {
				passwordPolicyDTO.setMessage(messageSource.getMessage(
						passwordPolicy.getMessage(), new Object[] {}, locale));
			}

			passwordPolicyDTO.setMsgSrNum(msgNumber);
			msgNumber++;

			passWordPolicyDetails.add(passwordPolicyDTO);

		}
		changePasswordForm.getPasswordPolicyDTO().clear();
		changePasswordForm.setPasswordPolicyDTO(passWordPolicyDetails);
		model.put("changePasswordForm", changePasswordForm);

		List<LanguageListDTO> languageList = languageMasterLogic.getLanguages();
		model.put("languageList", languageList);

		return new ModelAndView("employee/employeeChangePassword");
	}

	@Override
	@RequestMapping(value = "/employeeLeaveBalanceSummary.html", method = RequestMethod.GET)
	public ModelAndView employeeLeaveBalanceSummary(ModelMap model,
			HttpServletRequest request, Locale locale) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		LeaveBalanceSummaryForm leaveBalanceSummaryForm = new LeaveBalanceSummaryForm();
		model.put("leaveBalanceSummaryForm", leaveBalanceSummaryForm);

		List<EmployeeLeaveSchemeTypeDTO> yearList = leaveBalanceSummaryLogic
				.getDistinctYears(companyId);
		model.put("yearList", yearList);

		AddLeaveForm addLeaveForm = new AddLeaveForm();
		model.put("addLeaveForm", addLeaveForm);
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		LeaveSchemeForm leaveScheme = addLeaveLogic.getLeaveSchemes(companyId,
				employeeId);
		List<ComboValueDTO> sessionList = leaveBalanceSummaryLogic
				.getLeaveSessionList();
		for (ComboValueDTO comboValueDTO : sessionList) {
			if (StringUtils.isNotBlank(comboValueDTO.getLabelKey())) {
				comboValueDTO.setLabel(messageSource.getMessage(
						comboValueDTO.getLabelKey(), new Object[] {}, locale));
			}
		}
		model.put("sessionList", sessionList);
		model.put("leaveScheme", leaveScheme);

		LeavePreferenceForm leavePreferenceForm = leaveBalanceSummaryLogic
				.isEncashedVisible(companyId);
		model.put("isEncashedHidden", leavePreferenceForm.getShowEncashed());
		model.put("isFullEntitlementHidden",
				leavePreferenceForm.getShowFullEntitlement());
		model.put("leaveUnitDefined", leavePreferenceForm.getLeaveUnit());

		return new ModelAndView("employee/employeeLeaveBalanceSummary");
	}

	@Override
	@RequestMapping(value = "/leaveReports.html", method = RequestMethod.GET)
	public ModelAndView leaveReports(ModelMap model) {
		LeaveReportsForm leaveReportsForm = new LeaveReportsForm();
		model.put("leaveReportsForm", leaveReportsForm);
		return new ModelAndView("employee/leaveReportsForManager");
	}

	@Override
	@RequestMapping(value = "/hrisMyRequest.html", method = RequestMethod.GET)
	public ModelAndView hrisMyRequest(ModelMap model,
			HttpServletRequest request, Locale locale) {

		HrisChangeRequestForm hrisChangeRequestForm = new HrisChangeRequestForm();
		model.put("hrisChangeRequestForm", hrisChangeRequestForm);
		return new ModelAndView("employee/hrisMyrequest");
	}

	@Override
	@RequestMapping(value = "/hrisPendingItems.html", method = RequestMethod.GET)
	public ModelAndView hrisPendingItems(ModelMap model,
			HttpServletRequest request, Locale locale) {

		HrisPendingItemsForm hrisPendingItemsForm = new HrisPendingItemsForm();
		model.put("hrisPendingItemsForm", hrisPendingItemsForm);
		return new ModelAndView("employee/hrisPendingItems");
	}

	@Override
	@RequestMapping(value = "/employeHrLetters.html", method = RequestMethod.GET)
	public ModelAndView employeHrLetters(Map model, HttpServletRequest request,
			HttpServletResponse response) {

		HRLetterForm hrLetterForm = new HRLetterForm();
		model.put("hrLetterForm", hrLetterForm);
		return new ModelAndView("employee/employeeHrLetters", model);
	}

	@Override
	@RequestMapping(value = "/employeeClaimSummary.html", method = RequestMethod.GET)
	public ModelAndView employeeClaimSummary(Map model,
			HttpServletRequest request, HttpServletResponse response) {

		return new ModelAndView("employee/employeeClaimSummary", model);
	}

	@Override
	@RequestMapping(value = "/lundinTimesheet.html", method = RequestMethod.GET)
	public ModelAndView getLundinTimesheet(Map model,
			HttpServletRequest request, HttpServletResponse response) {
		AddClaimForm addClaimForm = new AddClaimForm();
		ClaimApplicationItemForm claimApplicationItemForm = new ClaimApplicationItemForm();
		ClaimApplicationItemAttach claimApplicationItemAttach = new ClaimApplicationItemAttach();
		model.put("claimApplicationItemForm", claimApplicationItemForm);
		model.put("claimApplicationItemAttach", claimApplicationItemAttach);
		List<CompanyForm> currencyNameList = companyInformationLogic
				.getCurrencyName();
		model.put("currencyNameList", currencyNameList);
		model.put("addClaimForm", addClaimForm);

		return new ModelAndView("employee/lundinTimesheet", model);
	}

	@Override
	@RequestMapping(value = "/lionhkTimesheet.html", method = RequestMethod.GET)
	public ModelAndView lionhkMyTimesheet(ModelMap model,
			HttpServletRequest request, HttpServletResponse response) {

		AddClaimForm addClaimForm = new AddClaimForm();
		ClaimApplicationItemForm claimApplicationItemForm = new ClaimApplicationItemForm();
		ClaimApplicationItemAttach claimApplicationItemAttach = new ClaimApplicationItemAttach();
		model.put("claimApplicationItemForm", claimApplicationItemForm);
		model.put("claimApplicationItemAttach", claimApplicationItemAttach);
		List<CompanyForm> currencyNameList = companyInformationLogic
				.getCurrencyName();
		model.put("currencyNameList", currencyNameList);
		model.put("addClaimForm", addClaimForm);

		return new ModelAndView("employee/lionhkTimesheet", model);
	}

	@Override
	@RequestMapping(value = "/lionhkPendingTimesheet.html", method = RequestMethod.GET)
	public ModelAndView lionhkPendingTimesheet(ModelMap model,
			HttpServletRequest request, HttpServletResponse response) {

		return new ModelAndView("employee/lionhkPendingTimesheet", model);
	}

	@Override
	@RequestMapping(value = "/lundinPendingTimesheet.html", method = RequestMethod.GET)
	public ModelAndView getLundinPendingTimesheet(Map model,
			HttpServletRequest request, HttpServletResponse response) {

		return new ModelAndView("employee/lundinPendingTimesheet", model);
	}

	@Override
	@RequestMapping(value = "/lionTimesheetReports.html", method = RequestMethod.GET)
	public ModelAndView lionTimesheetReports(ModelMap model,
			HttpServletRequest request, HttpServletResponse response) {

		LionTimesheetReportsForm lionTimesheetReportsForm = new LionTimesheetReportsForm();
		model.put("lionTimesheetReportsForm", lionTimesheetReportsForm);

		return new ModelAndView("employee/lionTimesheetReportsForManager",
				model);
	}

	@Override
	@RequestMapping(value = "/coherentOvertimeTimesheet.html", method = RequestMethod.GET)
	public ModelAndView coherentOvertimeTimesheet(ModelMap model,
			HttpServletRequest request, HttpServletResponse response) {

		AddClaimForm addClaimForm = new AddClaimForm();
		ClaimApplicationItemForm claimApplicationItemForm = new ClaimApplicationItemForm();
		ClaimApplicationItemAttach claimApplicationItemAttach = new ClaimApplicationItemAttach();
		model.put("claimApplicationItemForm", claimApplicationItemForm);
		model.put("claimApplicationItemAttach", claimApplicationItemAttach);
		List<CompanyForm> currencyNameList = companyInformationLogic
				.getCurrencyName();
		model.put("currencyNameList", currencyNameList);
		model.put("addClaimForm", addClaimForm);

		return new ModelAndView("employee/coherentMyOvertime", model);
	}
	
	@Override
	@RequestMapping(value = "/payasiaOvertimeTimesheet.html", method = RequestMethod.GET)
	public ModelAndView payasiaOvertimeTimesheet(ModelMap model,
			HttpServletRequest request, HttpServletResponse response) {

		AddClaimForm addClaimForm = new AddClaimForm();
		ClaimApplicationItemForm claimApplicationItemForm = new ClaimApplicationItemForm();
		ClaimApplicationItemAttach claimApplicationItemAttach = new ClaimApplicationItemAttach();
		model.put("claimApplicationItemForm", claimApplicationItemForm);
		model.put("claimApplicationItemAttach", claimApplicationItemAttach);
		List<CompanyForm> currencyNameList = companyInformationLogic
				.getCurrencyName();
		model.put("currencyNameList", currencyNameList);
		model.put("addClaimForm", addClaimForm);

		return new ModelAndView("employee/coherentMyOvertime", model);
	}

	@Override
	@RequestMapping(value = "/coherentEmployeeOvertime.html", method = RequestMethod.GET)
	public ModelAndView coherentEmployeeOvertime(ModelMap model,
			HttpServletRequest request, HttpServletResponse response) {

		return new ModelAndView("employee/coherentEmployeeOvertime", model);
	}
	
	@Override
	@RequestMapping(value = "/payasiaEmployeeOvertime.html", method = RequestMethod.GET)
	public ModelAndView payasiaEmployeeOvertime(ModelMap model,
			HttpServletRequest request, HttpServletResponse response) {

		return new ModelAndView("employee/coherentEmployeeOvertime", model);
	}

	@Override
	@RequestMapping(value = "/myShift.html", method = RequestMethod.GET)
	public ModelAndView myShift(Map model, HttpServletRequest request,
			HttpServletResponse response) {
		AddClaimForm addClaimForm = new AddClaimForm();
		ClaimApplicationItemForm claimApplicationItemForm = new ClaimApplicationItemForm();
		ClaimApplicationItemAttach claimApplicationItemAttach = new ClaimApplicationItemAttach();
		model.put("claimApplicationItemForm", claimApplicationItemForm);
		model.put("claimApplicationItemAttach", claimApplicationItemAttach);
		List<CompanyForm> currencyNameList = companyInformationLogic
				.getCurrencyName();
		model.put("currencyNameList", currencyNameList);
		model.put("addClaimForm", addClaimForm);

		return new ModelAndView("employee/myShift", model);
	}

	@Override
	@RequestMapping(value = "/coherentEmployeeShift.html", method = RequestMethod.GET)
	public ModelAndView coherentEmployeeShift(Map model,
			HttpServletRequest request, HttpServletResponse response) {

		return new ModelAndView("employee/coherentEmployeeShiftTimesheet",model);
	}

	@Override
	@RequestMapping(value = "/coherentTimesheetReports.html", method = RequestMethod.GET)
	public ModelAndView coherentTimesheetReports(ModelMap model,
			HttpServletRequest request, HttpServletResponse response) {

		CoherentTimesheetReportsForm coherentTimesheetReportsForm = new CoherentTimesheetReportsForm();
		model.put("coherentTimesheetReportsForm", coherentTimesheetReportsForm);

		return new ModelAndView("employee/coherentTimesheetReportsForManager",
				model);
	}

	@Override
	@RequestMapping(value = "/lundinTimesheetReports.html", method = RequestMethod.GET)
	public ModelAndView lundinTimesheetReports(ModelMap model,
			HttpServletRequest request, HttpServletResponse response) {

		LundinTimesheetReportsForm lundinTimesheetReportsForm = new LundinTimesheetReportsForm();
		model.put("lundinTimesheetReportsForm", lundinTimesheetReportsForm);

		return new ModelAndView("employee/lundinTimesheetManagerReports", model);
	}
	
	@Override
	@RequestMapping(value = "/employeeManagerClaimReports.html", method = RequestMethod.GET)
	public ModelAndView managerClaimReports(ModelMap model) {
		ClaimReportsForm claimReportsForm = new ClaimReportsForm();
		model.put("claimReportsForm", claimReportsForm);
		return new ModelAndView("employee/employeeManagerClaimReports");
	}
	
}
