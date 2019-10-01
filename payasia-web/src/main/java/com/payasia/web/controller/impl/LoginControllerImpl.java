package com.payasia.web.controller.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;

import com.payasia.common.bean.util.UserContext;
import com.payasia.common.dto.MessageDTO;
import com.payasia.common.dto.PasswordPolicyDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.ChangePasswordForm;
import com.payasia.common.form.CompanyForm;
import com.payasia.common.form.ForgotPasswordForm;
import com.payasia.dao.SsoConfigurationDAO;
import com.payasia.dao.bean.SsoConfiguration;
import com.payasia.logic.EmployeeChangePasswordLogic;
import com.payasia.logic.LoginLogic;
import com.payasia.web.controller.LoginController;
import com.payasia.web.security.PayAsiaAuthenticationFilter;
import com.payasia.web.util.PayAsiaSessionAttributes;
import com.payasia.web.util.URLUtils;

import nl.captcha.Captcha;

/**
 * The Class LoginControllerImpl.
 */
/**
 * @author vivekjain
 * 
 */
@Controller
@SessionAttributes
public class LoginControllerImpl extends AbstractPayAsiaBaseController implements LoginController {

	/** The Constant logger. */
	private static final Logger LOGGER = Logger.getLogger(LoginControllerImpl.class);

	/** The url utils. */
	@Resource
	URLUtils urlUtils;

	@Resource
	SsoConfigurationDAO ssoConfigurationDAO;

	/** The message source. */
	@Autowired
	private MessageSource messageSource;

	@Resource
	EmployeeChangePasswordLogic employeeChangePasswordLogic;

	/** The login logic. */
	@Resource
	LoginLogic loginLogic;
	
	@Value("#{payasiaptProperties['payasia.app.encryption']}")
	private  String encryptionValue;
	
	@Autowired
	private ServletContext servletContext;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.LoginController#loginRequest(java.lang.String,
	 * org.springframework.ui.ModelMap)
	 */
	@Override
	@RequestMapping(value = "portal/{companyCode}/login.html", method = RequestMethod.GET)
	public ModelAndView loginRequest(@PathVariable String companyCode, ModelMap model, HttpServletRequest request) {
		String shortCompanyCode = loginLogic.getShortCompanyCode(companyCode);
		String localeParam = "en" + "_US";
		Locale newLocale = urlUtils.getNewLocale(localeParam, shortCompanyCode);

		WebUtils.setSessionAttribute(request, PayAsiaAuthenticationFilter.LOCALE_SESSION_ATTRIBUTE_NAME, newLocale);

		ForgotPasswordForm forgotPasswordForm = new ForgotPasswordForm();
		model.put("forgotPasswordForm", forgotPasswordForm);

		model.put("employeeCompanyCode", companyCode);
		return new ModelAndView("login/loginForm", model);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.web.controller.LoginController#loginRequest(javax.servlet
	 * .http.HttpSession, javax.servlet.http.HttpServletResponse,
	 * javax.servlet.http.HttpServletRequest)
	 */
	@Override
	@RequestMapping(value = "portal/login.html", method = RequestMethod.GET)
	public ModelAndView loginRequest(HttpSession session, HttpServletResponse response, HttpServletRequest request)
			throws IOException {

		// Check if SSO enabled for the company then redirect to IDP for authentication
		String companyCode = urlUtils.getSubDomain(new URL(request.getRequestURL().toString()));
		SsoConfiguration ssoConfiguration = ssoConfigurationDAO.findByCompanyCodeWithGroup(companyCode);
		if (ssoConfiguration != null && ssoConfiguration.getIsEnableSso()) {
			return new ModelAndView(
					"redirect:/saml/login?idp=" + URLEncoder.encode(ssoConfiguration.getIdpIssuer(), "UTF-8"));
		}

		String urlWithCompanyCode = urlUtils.getURLWithCompanyCode(request.getRequestURL().toString(), request);

		if (urlWithCompanyCode != null) {
			response.sendRedirect(urlWithCompanyCode);
		}
		ModelMap model = new ModelMap();

		ForgotPasswordForm forgotPasswordForm = new ForgotPasswordForm();
		model.put("forgotPasswordForm", forgotPasswordForm);

		return new ModelAndView("login/loginForm", model);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.web.controller.LoginController#logoutRequest(javax.servlet
	 * .http.HttpSession, javax.servlet.http.HttpServletResponse,
	 * javax.servlet.http.HttpServletRequest)
	 */
	@Override
	@RequestMapping(value = "portal/logout.html", method = RequestMethod.GET)
	public ModelAndView logoutRequest(HttpSession session, HttpServletResponse response, HttpServletRequest request)
			throws IOException {
		String urlWithCompanyCode = urlUtils.getURLWithCompanyCode(request.getRequestURL().toString(), request);

		if (urlWithCompanyCode != null) {
			response.sendRedirect(urlWithCompanyCode);
		}

		ModelMap model = new ModelMap();

		ForgotPasswordForm forgotPasswordForm = new ForgotPasswordForm();
		model.put("forgotPasswordForm", forgotPasswordForm);

		return new ModelAndView("login/loginForm", model);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.web.controller.LoginController#employeePageRequest(javax.
	 * servlet.http.HttpSession, java.lang.String)
	 */
	@Override
	@RequestMapping(value = "portal/{companyCode}/employeeHome.html", method = RequestMethod.GET)
	public String employeePageRequest(HttpServletRequest request, HttpSession session, @PathVariable String companyCode,
			HttpServletResponse response) {
		String retURLString = null;

		String userName = SecurityContextHolder.getContext().getAuthentication().getName();
		Long employeeId = null;
		Long companyId = null;
		if ((userName != "") && (userName != null)) {
			CompanyForm companyForm = loginLogic.getCompany(userName, companyCode);
			if (companyForm == null) {

				try {
					response.sendRedirect(urlUtils.getSessionExpiryURL(request, "/portal/sessionExpired.html"));
				} catch (IOException ioe) {
					LOGGER.error(">>CompanyForm null check >>" + ioe);
				}

			}

			if (companyForm != null) {
				session.setAttribute(PayAsiaSessionAttributes.COMPANY_HAS_CLAIM_MODULE, companyForm.isHasClaimModule());

				session.setAttribute(PayAsiaSessionAttributes.COMPANY_HAS_LEAVE_MODULE, companyForm.isHasLeaveModule());

				session.setAttribute(PayAsiaSessionAttributes.COMPANY_HAS_HRIS_MODULE, companyForm.isHasHrisModule());

				session.setAttribute(PayAsiaSessionAttributes.COMPANY_HAS_MOBILE_MODULE,
						companyForm.isHasMobileModule());

				session.setAttribute(PayAsiaSessionAttributes.COMPANY_HAS_LUNDIN_TIMESHEET_MODULE,
						companyForm.isHasLundinTimesheetModule());
				session.setAttribute(PayAsiaSessionAttributes.COMPANY_HAS_LION_TIMESHEET_MODULE,
						companyForm.isHasLionTimesheetModule());
				session.setAttribute(PayAsiaSessionAttributes.COMPANY_HAS_COHERENT_TIMESHEET_MODULE,
						companyForm.isHasCoherentTimesheetModule());

				companyId = companyForm.getCompanyId();
			}
			if ((companyId != null)) {
				session.setAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID, companyId);
			}

			String companyName = companyForm.getCompanyName();
			if ((companyName != "")) {
				session.setAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_NAME, companyName);
			}

			Long employeeCompanyId = companyForm.getCompanyId();
			if ((companyId != null)) {
				session.setAttribute(PayAsiaSessionAttributes.EMPLOYEE_COMPANY_ID, employeeCompanyId);
			}

			employeeId = loginLogic.getEmployeeIdByLoginName(userName, companyCode);
			if (employeeId != null) {
				session.setAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID, employeeId);
			}

			if ((loginLogic.getNumberOfOpenTabs() != null) && (loginLogic.getNumberOfOpenTabs() != "")) {
				String noOfOpenTabs = loginLogic.getNumberOfOpenTabs();
				session.setAttribute(PayAsiaSessionAttributes.NUMBER_OF_OPEN_TABS, noOfOpenTabs);
			}

			if (!companyForm.getDateFormat().equals("")) {
				String dateFormat = companyForm.getDateFormat();
				session.setAttribute(PayAsiaSessionAttributes.COMPANY_DATE_FORMAT, dateFormat);
				UserContext.setWorkingCompanyDateFormat(dateFormat);

			}

			if (companyForm.getGmtOffset() != null && !companyForm.getGmtOffset().equals("")) {
				String gmtOffset = companyForm.getGmtOffset();
				session.setAttribute(PayAsiaSessionAttributes.ADMIN_TIMEZONE_GMT_OFFSET, gmtOffset);
			}
		}

		//check encryption is enable than encrypt IDs
		if(encryptionValue!=null && encryptionValue.equalsIgnoreCase("YES")){
			session.setAttribute("privateKey",getNumber());
		}
		
		//Check if SSO enabled then bypass the password policy checks
		SsoConfiguration ssoConfiguration = ssoConfigurationDAO.findByCompanyId(companyId);
		if (ssoConfiguration != null && ssoConfiguration.getIsEnableSso()) {
			return "forward:/employee/home.html";
		}
		
		boolean hasAnyRecordInLoginHistoryStatus = loginLogic.getEmployeeLoginHistoryStatus(userName, companyCode);
		boolean isMaxPwdAgeValidStatus = loginLogic.checkMaxPwdAgeExceeded(employeeId, companyId);
		boolean isPasswordReseted = loginLogic.checkIsPasswordReseted(employeeId);

		
		if (hasAnyRecordInLoginHistoryStatus) {

			if (isMaxPwdAgeValidStatus || isPasswordReseted) {
				retURLString = "forward:/employee/employeeChangePassword.html";
			} else {
				retURLString = "forward:/employee/home.html";
			}

		} else {
			retURLString = "forward:/employee/employeeChangePassword.html";
		}

		return retURLString;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.LoginController#forgetPassword(java.lang.String ,
	 * java.util.Locale)
	 */
	@Override
	@RequestMapping(value = "forgetPassword.html", method = RequestMethod.POST)
	@ResponseBody
	public String forgetPassword(@ModelAttribute(value = "forgotPasswordForm") ForgotPasswordForm forgotPasswordForm,
			HttpServletRequest request, HttpSession session, Locale locale) {
		String response = "";
		if (forgotPasswordForm.isDontKnowMyUsername()) {
			Captcha captcha = (Captcha) session.getAttribute(Captcha.NAME);
			String answer = forgotPasswordForm.getTuring();
			if (captcha.isCorrect(answer)) {
				response = loginLogic.sendForgotPasswdMail(forgotPasswordForm);
			} else {
				response = "payasia.invalid.captcha";
			}
		} else {
			response = loginLogic.sendForgotPasswdMail(forgotPasswordForm);
		}

		try {
			response = URLEncoder.encode(messageSource.getMessage(response, new Object[] {}, locale), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		} catch (NoSuchMessageException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return response;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.web.controller.LoginController#getLoginPageLogo(java.lang
	 * .String, javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "LoginPageLogo.html", method = RequestMethod.GET)
	public @ResponseBody void getLoginPageLogo(@RequestParam(value = "companyCode", required = true) String companyCode,
			HttpServletRequest request, HttpServletResponse response) {

		ServletOutputStream outStream = null;
		try {
			String defaultImagePath = servletContext.getRealPath("/resources/images/")
					+ "/company_logo.png";
			byte[] byteFile = loginLogic.getLoginPageCompanyLogo(companyCode, defaultImagePath);
			response.reset();
			String mimeType = URLConnection.guessContentTypeFromName("");
			response.setContentType("png");
			response.setContentLength(byteFile.length);
			String imageName = byteFile.getClass().getName();

			response.setHeader("Content-Disposition", "inline;filename=" + imageName);

			outStream = response.getOutputStream();
			outStream.write(byteFile);

		} catch (IOException ex) {
			LOGGER.error(ex.getMessage(), ex);
		} finally {
			try {
				if (outStream != null) {

					outStream.flush();
					outStream.close();
				}

			} catch (IOException ex) {
				LOGGER.error(ex.getMessage(), ex);
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.web.controller.LoginController#getCompanyLogoSize()
	 */
	@Override
	@RequestMapping(value = "CompanyLogoSize.html", method = RequestMethod.POST)
	@ResponseBody
	public String getCompanyLogoSize(@RequestParam(value = "companyCode", required = true) String companyCode,
			HttpServletRequest request, HttpServletResponse response) {

		String defaultImagePath = servletContext.getRealPath("/resources/images/") + "/company_logo.png";

		String newImageWidthheight = loginLogic.getLogoWidthHeight(companyCode, defaultImagePath);

		return newImageWidthheight;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.LoginController#isAjaxSessionValid(javax.servlet
	 * .http.HttpServletRequest, javax.servlet.http.HttpServletResponse,
	 * java.lang.String)
	 */
	@Override
	@RequestMapping(value = "checkAjaxSession.html", method = RequestMethod.POST)
	@ResponseBody
	public String isAjaxSessionValid(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "reqSessId", required = true) String reqSessId) {
		HttpSession session = request.getSession(false);
		String employeeId = String.valueOf(session.getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID));
		String sessionWindowId = String.valueOf(session.getAttribute(PayAsiaSessionAttributes.WINDOW_ID));

		String custSessId = employeeId + sessionWindowId;

		if (!custSessId.equals(reqSessId)) {
			try {
				response.sendError(HttpServletResponse.SC_FORBIDDEN);
			} catch (IOException e) {
				LOGGER.error(e);
			}
		}
		return "";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.web.controller.LoginController#setWindowId(javax.servlet.
	 * http.HttpServletRequest, javax.servlet.http.HttpServletResponse,
	 * java.lang.String)
	 */
	@Override
	@RequestMapping(value = "setTabId.html", method = RequestMethod.POST)
	@ResponseBody
	public String setWindowId(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "windowId", required = true) String windowId) {
		HttpSession session = request.getSession(false);
		String sessionWindowId = String.valueOf(session.getAttribute(PayAsiaSessionAttributes.WINDOW_ID));

		if (sessionWindowId == null) {
			sessionWindowId = "NotInitialized";
		}

		if (windowId != null && !windowId.equals(sessionWindowId)) {
			if (windowId.charAt(0) != sessionWindowId.charAt(0)) {
				session.setAttribute(PayAsiaSessionAttributes.WINDOW_ID, windowId);
			} else {
				try {
					response.sendError(HttpServletResponse.SC_FORBIDDEN);
				} catch (IOException e) {
					LOGGER.error(e);
				}
			}
		}
		return "";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.LoginController#sessionExpired(java.lang.String ,
	 * org.springframework.ui.ModelMap, javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "portal/{companyCode}/sessionExpired.html", method = RequestMethod.GET)
	public ModelAndView sessionExpired(@PathVariable String companyCode, ModelMap model, HttpServletRequest request,
			HttpServletResponse response) {
		String empCompanyCode = urlUtils.getEmployeeCompanyCode(request);

		HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}

		SecurityContextHolder.getContext().setAuthentication(null);

		if (StringUtils.isNotEmpty(empCompanyCode) && !empCompanyCode.equals(companyCode)) {
			try {
				String redirectURL = urlUtils.getSessionExpiryURL(request, "/portal/login.html?login_error=3");
				response.sendRedirect(redirectURL);
			} catch (IOException ioe) {
				LOGGER.error(ioe);
			}
		}

		ForgotPasswordForm forgotPasswordForm = new ForgotPasswordForm();
		model.put("forgotPasswordForm", forgotPasswordForm);

		model.put("employeeCompanyCode", companyCode);
		model.put("sessionExpired", "Invalid Session");
		return new ModelAndView("login/loginForm", model);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.web.controller.LoginController#getContactEmail(java.lang.
	 * String, javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "contactEmail.html", method = RequestMethod.POST)
	@ResponseBody
	public String getContactEmail(@RequestParam(value = "companyCode", required = true) String companyCode,
			HttpServletRequest request, HttpServletResponse response) {

		String contactEmail = loginLogic.getEmailPreference(companyCode);

		return contactEmail;

	}

	@Override
	@RequestMapping(value = "resetPassword/{token}", method = RequestMethod.GET)
	public ModelAndView resetpasswordRequest(@PathVariable String token, HttpServletRequest request,
			HttpServletResponse response, ModelMap model, Locale locale) {
		String subdomain = "";
		try {
			subdomain = urlUtils.getSubDomain(new URL(request.getRequestURL().toString()));
		} catch (MalformedURLException e) {
			LOGGER.error(e);
		}

		if (StringUtils.isNotBlank(subdomain)) {
			Long companyId = loginLogic.getCompanyId(subdomain);
			if (companyId != null) {
				ChangePasswordForm changePasswordForm = employeeChangePasswordLogic.getPassWordPolicyDetails(companyId);
				List<PasswordPolicyDTO> passwordPolicyDTOs = changePasswordForm.getPasswordPolicyDTO();
				List<PasswordPolicyDTO> passWordPolicyDetails = new ArrayList<>();
				Integer msgNumber = 1;
				for (PasswordPolicyDTO passwordPolicy : passwordPolicyDTOs) {
					PasswordPolicyDTO passwordPolicyDTO = new PasswordPolicyDTO();
					if (passwordPolicy.getValue() != null) {
						passwordPolicyDTO.setMessage(messageSource.getMessage(passwordPolicy.getMessage(),
								new Object[] { passwordPolicy.getValue() }, locale));
					} else {
						passwordPolicyDTO.setMessage(
								messageSource.getMessage(passwordPolicy.getMessage(), new Object[] {}, locale));
					}

					passwordPolicyDTO.setMsgSrNum(msgNumber);
					msgNumber++;

					passWordPolicyDetails.add(passwordPolicyDTO);

				}
				changePasswordForm.getPasswordPolicyDTO().clear();
				changePasswordForm.setPasswordPolicyDTO(passWordPolicyDetails);
				model.put("employeeCompanyId", companyId);
				model.put("employeeToken", token);
				model.put("changePasswordForm", changePasswordForm);
			}
		}

		return new ModelAndView("login/resetPasswordPage");
	}

	@Override
	@RequestMapping(value = "/resetForgotPassword.html", method = RequestMethod.POST)
	@ResponseBody
	public String resetForgotPassword(@RequestParam(value = "username", required = true) String username,
			@RequestParam(value = "newPassword", required = true) String newPassword,
			@RequestParam(value = "employeeToken", required = true) String employeeToken,
			@RequestParam(value = "employeeCompany", required = true) Long employeeCompany, HttpServletRequest request,
			Locale locale) {
		MessageDTO msgDto = employeeChangePasswordLogic.resetForgotPassword(username, newPassword, employeeToken,
				employeeCompany, request.getRemoteAddr());
		String status = msgDto.getKey();
		try {
			status = URLEncoder.encode(messageSource.getMessage(status, new Object[] { msgDto.getArgs() }, locale),
					"UTF-8");
		} catch (UnsupportedEncodingException unsupportedEncodingException) {
			LOGGER.error(unsupportedEncodingException.getMessage(), unsupportedEncodingException);
			throw new PayAsiaSystemException(unsupportedEncodingException.getMessage(), unsupportedEncodingException);
		} catch (NoSuchMessageException noSuchMessageException) {
			LOGGER.error(noSuchMessageException.getMessage(), noSuchMessageException);
			throw new PayAsiaSystemException(noSuchMessageException.getMessage(), noSuchMessageException);
		}
		return status;
	}

	/* Generate 16 digit number*/	
	private static String getNumber() {
		Random rand = new Random();
		int num = rand.nextInt(9000000) + 10000000;
		int num1 = 24682468;
		String finalKey = String.valueOf(num).concat(String.valueOf(num1));
		return finalKey;

	}
	
}


