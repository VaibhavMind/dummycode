package com.payasia.web.controller.impl;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.payasia.common.dto.CompanyModuleDTO;
import com.payasia.common.form.EmployeeFilterListForm;
import com.payasia.common.form.EmployeeHomePageForm;
import com.payasia.common.util.PasswordUtils;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.logic.EmployeeHomePageLogic;
import com.payasia.logic.MultilingualLogic;
import com.payasia.web.controller.EmployeeHomePageController;
import com.payasia.web.util.PayAsiaSessionAttributes;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

@Controller
@RequestMapping(value = "/employee/empHomePage")
public class EmployeeHomePageControllerImpl implements
		EmployeeHomePageController {
	private static final Logger LOGGER = Logger
			.getLogger(EmployeeHomePageControllerImpl.class);
	@Resource
	EmployeeHomePageLogic employeeHomePageLogic;
	@Resource
	MultilingualLogic multilingualLogic;

	/** The employee image width. */
	@Value("#{payasiaptProperties['payasia.mobile.app.apk.path']}")
	private String mobileApplicationApkPath;

	/** The employee image width. */
	@Value("#{payasiaptProperties['payasia.mobile.app.ios.path']}")
	private String mobileApplicationIOSPath;

	@Override
	public @ResponseBody @RequestMapping(value = "/getPayslipDetails.html", method = RequestMethod.POST) String getPayslipDetails(
			HttpServletRequest request, HttpServletResponse response) {

		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		EmployeeHomePageForm employeeHomePageForm = employeeHomePageLogic
				.getPayslipDetails(companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(employeeHomePageForm,
				jsonConfig);
		return jsonObject.toString();
	}

	@Override
	public @ResponseBody @RequestMapping(value = "/getLeaveDetails", method = RequestMethod.POST) String getLeaveDetails(
			HttpServletRequest request, HttpServletResponse response) {

		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		EmployeeHomePageForm employeeHomePageForm = employeeHomePageLogic
				.getLeaveDetails(companyId, employeeId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(employeeHomePageForm,
				jsonConfig);
		return jsonObject.toString();
	}

	@Override
	public @ResponseBody @RequestMapping(value = "/getClaimDetails", method = RequestMethod.POST) String getClaimDetails(
			HttpServletRequest request, HttpServletResponse response) {

		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		EmployeeHomePageForm employeeHomePageForm = employeeHomePageLogic
				.getClaimDetails(companyId, employeeId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(employeeHomePageForm,
				jsonConfig);
		return jsonObject.toString();
	}

	@Override
	public @ResponseBody @RequestMapping(value = "/getPasswordExpiryReminder.html", method = RequestMethod.POST) String getPasswordExpiryReminder(
			HttpServletRequest request, HttpServletResponse response) {
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		String expiryDays = employeeHomePageLogic.getPasswordExpiryReminder(
				employeeId, companyId);

		return expiryDays;
	}

	@Override
	@RequestMapping(value = "/getAllRecentActivityList.html", method = RequestMethod.POST)
	@ResponseBody
	public String getAllRecentActivityList(HttpServletRequest request,
			HttpServletResponse response, Locale locale) {

		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		Long languageId = multilingualLogic.getLanguageId(locale.toString());
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

		EmployeeHomePageForm employeeHomePageForm = employeeHomePageLogic
				.getAllRecentActivityList(companyId, employeeId,
						companyModuleDTO, languageId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(employeeHomePageForm,
				jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	public @ResponseBody @RequestMapping(value = "/generateEmployeeActivationCode.html", method = RequestMethod.POST) String generateEmployeeActivationCode(
			HttpServletRequest request, HttpServletResponse response) {

		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		EmployeeHomePageForm employeeHomePageForm = employeeHomePageLogic
				.generateEmployeeActivationCode(employeeId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(employeeHomePageForm,
				jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/mobile/mHROnline.apk", method = RequestMethod.GET)
	public @ResponseBody byte[] downloadMobileApplication(
			@RequestParam(value = "applicationType", required = true) String applicationType,
			HttpServletRequest request, HttpServletResponse response) {
		byte[] byteFile = null;

		String filePath = "";
		String filename = "";
		if ("apk".equals(applicationType)) {
			filePath = mobileApplicationApkPath;
			filename = "mHROnline.APK";
		} else {
			filePath = mobileApplicationIOSPath;
			filename = "mHROnline.IOS";

		}

		File defaultApkFile = new File(filePath);
		try {
			byteFile = PasswordUtils.getBytesFromFile(defaultApkFile);
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
		response.reset();
		URLConnection.guessContentTypeFromName("apk");
		response.setContentType("application/octet-stream");
		response.setContentLength(byteFile.length);

		response.setHeader("Content-Disposition", "attachment;filename="
				+ filename);

		return byteFile;

	}

	@Override
	@RequestMapping(value = "/getHomeSubmittedLundin", method = RequestMethod.GET)
	public ModelAndView getHomeSubmittedLundin(ModelMap model) {
		return new ModelAndView("/employee/homeLundinTimesheet");
	}

	@Override
	@RequestMapping(value = "/getHomePendingLundin", method = RequestMethod.GET)
	public ModelAndView getHomePendingLundin(ModelMap model) {
		return new ModelAndView("/employee/homePendingLundinTimesheet");
	}

	@Override
	@RequestMapping(value = "/getHomeSubmittedLion", method = RequestMethod.GET)
	public ModelAndView getHomeSubmittedLion(ModelMap model) {
		return new ModelAndView("/employee/homeSubmittedLionTimesheet");
	}

	@Override
	@RequestMapping(value = "/getHomePendingLion", method = RequestMethod.GET)
	public ModelAndView getHomePendingLion(ModelMap model) {
		return new ModelAndView("/employee/homePendingLionTimesheet");
	}


	@Override
	@RequestMapping(value = "/getHomeSubmittedCoherent", method = RequestMethod.GET)
	public ModelAndView getHomeSubmittedCoherent(ModelMap model) {
		return new ModelAndView("/employee/homeSubmittedCoherentTimesheet");
	}

	@Override
	@RequestMapping(value = "/getHomePendingCoherent", method = RequestMethod.GET)
	public ModelAndView getHomePendingCoherent(ModelMap model) {
		return new ModelAndView("/employee/homePendingCoherentTimesheet");
	}

	@Override
	@RequestMapping(value = "/getHomeSubmittedIngersoll", method = RequestMethod.GET)
	public ModelAndView getHomeSubmittedIngersoll(ModelMap model) {
		return new ModelAndView("/employee/homeMySubmittedOT");
	}

	@Override
	@RequestMapping(value = "/getDefaultEmailCCListByEmp.html", method = RequestMethod.POST)
	@ResponseBody
	public String getDefaultEmailCCListByEmployee(
			@RequestParam(value = "moduleName", required = false) String moduleName,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		boolean moduleEnabled = false;
		if (moduleName.equalsIgnoreCase(PayAsiaConstants.COMPANY_MODULE_CLAIM)) {
			moduleEnabled = (boolean) request.getSession().getAttribute(
					PayAsiaSessionAttributes.COMPANY_HAS_CLAIM_MODULE);
		}
		if (moduleName.equalsIgnoreCase(PayAsiaConstants.COMPANY_MODULE_LEAVE)) {
			moduleEnabled = (boolean) request.getSession().getAttribute(
					PayAsiaSessionAttributes.COMPANY_HAS_LEAVE_MODULE);
		}

		List<EmployeeFilterListForm> filterList = employeeHomePageLogic
				.getDefaultEmailCCListByEmployee(companyId, employeeId,
						moduleName, moduleEnabled);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(filterList, jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/saveDefaultEmailCCByEmp.html", method = RequestMethod.POST)
	@ResponseBody
	public void saveDefaultEmailCCByEmployee(
			@RequestParam(value = "ccEmailIds", required = false) String ccEmailIds,
			@RequestParam(value = "moduleName", required = false) String moduleName,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		boolean moduleEnabled = false;
		if (moduleName.equalsIgnoreCase(PayAsiaConstants.COMPANY_MODULE_CLAIM)) {
			moduleEnabled = (boolean) request.getSession().getAttribute(
					PayAsiaSessionAttributes.COMPANY_HAS_CLAIM_MODULE);
		}
		if (moduleName.equalsIgnoreCase(PayAsiaConstants.COMPANY_MODULE_LEAVE)) {
			moduleEnabled = (boolean) request.getSession().getAttribute(
					PayAsiaSessionAttributes.COMPANY_HAS_LEAVE_MODULE);
		}

		employeeHomePageLogic.saveDefaultEmailCCByEmployee(companyId,
				employeeId, ccEmailIds, moduleName, moduleEnabled);
	}

	@Override
	@RequestMapping(value = "/getDefaultEmailCCByEmp.html", method = RequestMethod.POST)
	@ResponseBody
	public String getDefaultEmailCCByEmp(
			@RequestParam(value = "moduleName", required = false) String moduleName,
			HttpServletRequest request, HttpServletResponse response,
			Locale locale) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		boolean moduleEnabled = false;
		if (moduleName.equalsIgnoreCase(PayAsiaConstants.COMPANY_MODULE_CLAIM)) {
			moduleEnabled = (boolean) request.getSession().getAttribute(
					PayAsiaSessionAttributes.COMPANY_HAS_CLAIM_MODULE);
		}
		if (moduleName.equalsIgnoreCase(PayAsiaConstants.COMPANY_MODULE_LEAVE)) {
			moduleEnabled = (boolean) request.getSession().getAttribute(
					PayAsiaSessionAttributes.COMPANY_HAS_LEAVE_MODULE);
		}

		String emailIds = employeeHomePageLogic.getDefaultEmailCCByEmp(
				companyId, employeeId, moduleName, moduleEnabled);
		return emailIds;
	}


	@Override
	@RequestMapping(value = "/getHomeSubmittedShiftCoherent", method = RequestMethod.GET)
	public ModelAndView getHomeSubmittedShiftCoherent(ModelMap model) {
		return new ModelAndView("/employee/homeSubmittedCoherentShift");
	}

	@Override
	@RequestMapping(value = "/getHomePendingShiftCoherent", method = RequestMethod.GET)
	public ModelAndView getHomePendingShiftCoherent(ModelMap model) {
		return new ModelAndView("/employee/homePendingCoherentShift");
	}
}
