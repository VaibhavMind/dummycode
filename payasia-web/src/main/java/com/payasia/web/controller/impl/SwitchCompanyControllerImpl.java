package com.payasia.web.controller.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.util.WebUtils;

import com.payasia.common.bean.util.UserContext;
import com.payasia.common.form.CompanyForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.form.SwitchCompanyResponse;
import com.payasia.dao.ClaimApplicationReviewerDAO;
import com.payasia.dao.CompanyDAO;
import com.payasia.dao.EmployeeClaimReviewerDAO;
import com.payasia.dao.EmployeeDAO;
import com.payasia.dao.EmployeeLeaveReviewerDAO;
import com.payasia.dao.EmployeeRoleMappingDAO;
import com.payasia.dao.LeaveApplicationReviewerDAO;
import com.payasia.dao.PrivilegeMasterDAO;
import com.payasia.dao.bean.Company;
import com.payasia.dao.bean.Employee;
import com.payasia.logic.LoginLogic;
import com.payasia.logic.SwitchCompanyLogic;
import com.payasia.web.controller.SwitchCompanyController;
import com.payasia.web.util.PayAsiaSessionAttributes;
import com.payasia.web.util.URLUtils;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * The Class SwitchCompanyControllerImpl.
 */
/**
 * @author vivekjain
 * 
 */
@Controller
@RequestMapping(value = "/admin/switchCompany")
public class SwitchCompanyControllerImpl implements SwitchCompanyController {

	public static final String LOCALE_SESSION_ATTRIBUTE_NAME = SessionLocaleResolver.class
			.getName() + ".LOCALE";

	/** The Constant logger. */
	private static final Logger LOGGER = Logger
			.getLogger(SwitchCompanyControllerImpl.class);

	/** The switch company logic. */
	@Resource
	SwitchCompanyLogic switchCompanyLogic;

	@Resource
	LoginLogic loginLogic;

	/** The employee dao. */
	@Resource
	EmployeeDAO employeeDAO;

	/** The employee role mapping dao. */
	@Resource
	EmployeeRoleMappingDAO employeeRoleMappingDAO;

	/** The privilege master dao. */
	@Resource
	PrivilegeMasterDAO privilegeMasterDAO;

	@Resource
	CompanyDAO companyDAO;

	@Resource
	URLUtils urlUtils;

	@Resource
	EmployeeLeaveReviewerDAO employeeLeaveReviewerDAO;
	@Resource
	LeaveApplicationReviewerDAO leaveApplicationReviewersDAO;
	@Resource
	EmployeeClaimReviewerDAO employeeClaimReviewerDAO;
	@Resource
	ClaimApplicationReviewerDAO claimApplicationReviewersDAO;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.SwitchCompanyController#SwitchCompany(java
	 * .lang.Long, java.lang.String, javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, javax.servlet.http.HttpSession)
	 * Check if employee is Claim Reviewer or Claim Manager of other employees
	 * ,if employee is Claim Manager ROLE_Claim_MANAGER
	 * List<EmployeeClaimReviewer> claimReviewersList = employeeClaimReviewerDAO
	 * .checkEmployeeClaimReviewer(employeeVO.getEmployeeId()); if
	 * (claimReviewersList != null) { grantedAuthorities.add(new
	 * SimpleGrantedAuthority( "ROLE_CLAIM_MANAGER")); } else { List<String>
	 * claimStatusList = new ArrayList<>();
	 * claimStatusList.add(PayAsiaConstants.CLAIM_STATUS_SUBMITTED);
	 * claimStatusList.add(PayAsiaConstants.CLAIM_STATUS_APPROVED);
	 * List<ClaimApplicationReviewer> claimAppRevsList =
	 * claimApplicationReviewersDAO
	 * .checkClaimEmployeeReviewer(employeeVO.getEmployeeId(), claimStatusList);
	 * if (claimAppRevsList != null) { grantedAuthorities.add(new
	 * SimpleGrantedAuthority( "ROLE_CLAIM_MANAGER")); } }
	 * 
	 * return grantedAuthorities; }
	 */
	@Override
	@RequestMapping(value = "/companySwitch.html", method = RequestMethod.POST)
	@ResponseBody
	public String switchCompany(
			@RequestParam(value = "companyId", required = true) Long companyId,
			@RequestParam(value = "companyCode", required = false) String companyCode,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session) throws IOException {
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		SecurityContext securityContext = SecurityContextHolder.getContext();
		Authentication authentication = securityContext.getAuthentication();

		Employee employee = employeeDAO.findById(employeeId);
		Company company = companyDAO.findById(companyId);
		List<GrantedAuthority> grantedAuthorities = getUserPrivilege(employee,
				company);

		securityContext
				.setAuthentication(new UsernamePasswordAuthenticationToken(
						authentication.getPrincipal(), authentication
								.getCredentials(), grantedAuthorities));

		companyCode = populateSession(companyId, companyCode, session, request);
		return urlUtils.getSwitchCompanyAdminURL(request, companyCode);
	}

	/**
	 * Populate session.
	 * 
	 * @param companyId
	 *            the company id
	 * @param companyCode
	 *            the company code
	 * @param session
	 *            the session
	 * @return the string
	 */
	private String populateSession(Long companyId, String companyCode,
			HttpSession session, HttpServletRequest request) {
		CompanyForm companyForm = switchCompanyLogic.getCompany(companyId);

		if (StringUtils.isBlank(companyCode)) {
			companyCode = companyForm.getCompanyCode();
		}

		WebUtils.setSessionAttribute(request, LOCALE_SESSION_ATTRIBUTE_NAME,
				new Locale(companyCode));

		String localeSession = (String) session
				.getAttribute(PayAsiaSessionAttributes.PAYASIA_LOCALE_LABEL);
		session.setAttribute(PayAsiaSessionAttributes.PAYASIA_LOCALE_LABEL,
				localeSession);

		session.setAttribute(PayAsiaSessionAttributes.COMPANY_HAS_CLAIM_MODULE,
				companyForm.isHasClaimModule());

		session.setAttribute(PayAsiaSessionAttributes.COMPANY_HAS_LEAVE_MODULE,
				companyForm.isHasLeaveModule());

		session.setAttribute(PayAsiaSessionAttributes.COMPANY_HAS_HRIS_MODULE,
				companyForm.isHasHrisModule());

		session.setAttribute(
				PayAsiaSessionAttributes.COMPANY_HAS_LUNDIN_TIMESHEET_MODULE,
				companyForm.isHasLundinTimesheetModule());
		session.setAttribute(
				PayAsiaSessionAttributes.COMPANY_HAS_LION_TIMESHEET_MODULE,
				companyForm.isHasLionTimesheetModule());
		session.setAttribute(
				PayAsiaSessionAttributes.COMPANY_HAS_COHERENT_TIMESHEET_MODULE,
				companyForm.isHasCoherentTimesheetModule());

		session.removeAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		session.setAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID,
				companyId);

		session.removeAttribute(PayAsiaSessionAttributes.ADMIN_TIMEZONE_GMT_OFFSET);
		if (StringUtils.isNotBlank(companyForm.getGmtOffset())) {
			session.setAttribute(
					PayAsiaSessionAttributes.ADMIN_TIMEZONE_GMT_OFFSET,
					companyForm.getGmtOffset());
		}

		if ((companyCode != null) && (!companyCode.equals(""))) {
			session.removeAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_CODE);
			session.setAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_CODE,
					companyCode);
		} else {
			companyCode = companyForm.getCompanyCode();
			session.removeAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_CODE);
			session.setAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_CODE,
					companyCode);
		}

		String companyName = companyForm.getCompanyName();
		session.removeAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_NAME);
		session.setAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_NAME,
				companyName);

		String dateFormat = companyForm.getDateFormat();
		session.removeAttribute(PayAsiaSessionAttributes.COMPANY_DATE_FORMAT);
		session.setAttribute(PayAsiaSessionAttributes.COMPANY_DATE_FORMAT,
				dateFormat);
		UserContext.setWorkingCompanyDateFormat(dateFormat);

		String noOfOpenTabs = switchCompanyLogic.getNumberOfOpenTabs();
		if (StringUtils.isNotBlank(noOfOpenTabs)) {
			session.setAttribute(PayAsiaSessionAttributes.NUMBER_OF_OPEN_TABS,
					noOfOpenTabs);
		}
		return companyCode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.SwitchCompanyController#SwitchCompany(java
	 * .lang.Long, java.lang.String, javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, javax.servlet.http.HttpSession)
	 */
	@Override
	@RequestMapping(value = "/switchRole.html", method = RequestMethod.POST)
	@ResponseBody
	public String switchRole(
			@RequestParam(value = "companyId", required = false) Long companyId,
			@RequestParam(value = "companyCode", required = false) String companyCode,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session) throws IOException {
		companyCode = populateSession(companyId, companyCode, session, request);
		Long employeeId = (Long) session
				.getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		loginLogic.setUserPrivilegeOnInfoSwitchRole(employeeId, companyCode,
				companyId);
		return urlUtils.getSwitchRoleEmployeeURL(request, companyCode);
	}

	/**
	 * Gets the user roles.
	 * 
	 * @param userName
	 *            the user name
	 * @param companyId
	 *            the company id
	 * @param employeeId
	 *            the employee Id
	 * @return the user roles
	 */
	private List<GrantedAuthority> getUserPrivilege(Employee employeeVO,
			Company company) {
		return loginLogic.getUserPrivilege(employeeVO, company);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.SwitchCompanyController#getSwitchCompanyList
	 * (int, int, java.lang.String, java.lang.String,
	 * javax.servlet.http.HttpServletRequest)
	 */
	@Override
	@RequestMapping(value = "/switchCompanyList.html", method = RequestMethod.POST)
	@ResponseBody
	public String getSwitchCompanyList(
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "searchCondition", required = false) String searchCondition,
			@RequestParam(value = "searchText", required = false) String searchText,
			@RequestParam(value = "includeInactiveCompany", required = false) Boolean includeInactiveCompany,
			HttpServletRequest request) {
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		SwitchCompanyResponse response = switchCompanyLogic
				.getSwitchCompanyList(pageDTO, sortDTO, employeeId,
						searchCondition, searchText, includeInactiveCompany);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(response, jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.SwitchCompanyController#getSwitchCompanyList
	 * (int, int, java.lang.String, java.lang.String,
	 * javax.servlet.http.HttpServletRequest)
	 */
	@Override
	@RequestMapping(value = "/exportCompanies.html", method = RequestMethod.POST)
	@ResponseBody
	public String getExportCompanies(
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			HttpServletRequest request) {
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		SwitchCompanyResponse response = switchCompanyLogic.getExportCompanies(
				pageDTO, sortDTO, employeeId, companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(response, jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.SwitchCompanyController#getCompanyName(javax
	 * .servlet.http.HttpServletRequest)
	 */
	@Override
	@RequestMapping(value = "/companyName.html", method = RequestMethod.POST)
	@ResponseBody
	public String getCompanyName(HttpServletRequest request) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		CompanyForm companyForm = switchCompanyLogic.getCompany(companyId);
		String companyName = companyForm.getCompanyName();
		try {
			return URLEncoder.encode(companyName, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;

	}
}