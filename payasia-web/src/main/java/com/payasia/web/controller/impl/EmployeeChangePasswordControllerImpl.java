package com.payasia.web.controller.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.payasia.common.dto.MessageDTO;
import com.payasia.common.exception.PayAsiaSystemException;
import com.payasia.common.form.PasswordPolicyPreferenceForm;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.logic.EmployeeChangePasswordLogic;
import com.payasia.logic.PasswordPolicyLogic;
import com.payasia.web.controller.EmployeeChangePasswordController;
import com.payasia.web.util.PayAsiaSessionAttributes;

import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;

/**
 * The Class EmployeeChangePasswordControllerImpl.
 */
/**
 * @author vivekjain
 * 
 */
@Controller
@RequestMapping(value = "/employee/ChangePassword")
public class EmployeeChangePasswordControllerImpl implements
		EmployeeChangePasswordController {

	/** The Constant logger. */
	private static final Logger LOGGER = Logger
			.getLogger(EmployeeChangePasswordControllerImpl.class);

	/** The employee change password logic. */
	@Resource
	EmployeeChangePasswordLogic employeeChangePasswordLogic;

	/** The message source. */
	@Autowired
	private MessageSource messageSource;

	@Resource
	PasswordPolicyLogic passwordPolicyLogic;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.EmployeeChangePasswordController#changePassword
	 * (java.lang.String, java.lang.String,
	 * javax.servlet.http.HttpServletRequest, java.util.Locale)
	 */
	@Override
	@RequestMapping(value = "/changePassword.html", method = RequestMethod.POST)
	@ResponseBody public String changePassword(
			@RequestParam(value = "oldPassword", required = true) String oldPassword,
			@RequestParam(value = "newPassword", required = true) String newPassword,
			HttpServletRequest request, Locale locale) {
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		MessageDTO msgDto = employeeChangePasswordLogic.changePassword(
				employeeId, companyId, oldPassword, newPassword);
		String status = msgDto.getKey();
		try {
			status = URLEncoder
					.encode(messageSource.getMessage(status,
							new Object[] { msgDto.getArgs() }, locale), "UTF-8");
		} catch (UnsupportedEncodingException unsupportedEncodingException) {
			LOGGER.error(unsupportedEncodingException.getMessage(),
					unsupportedEncodingException);
			throw new PayAsiaSystemException(
					unsupportedEncodingException.getMessage(),
					unsupportedEncodingException);
		} catch (NoSuchMessageException noSuchMessageException) {
			LOGGER.error(noSuchMessageException.getMessage(),
					noSuchMessageException);
			throw new PayAsiaSystemException(
					noSuchMessageException.getMessage(), noSuchMessageException);
		}
		return status;
	}

	@Override
	@RequestMapping(value = "/empChangePasswordAfterLogin.html", method = RequestMethod.POST)
	@ResponseBody public String empChangePasswordAfterLogin(
			@RequestParam(value = "oldPassword", required = true) String oldPassword,
			@RequestParam(value = "newPassword", required = true) String newPassword,
			HttpServletRequest request, Locale locale) {
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		MessageDTO msgDto = employeeChangePasswordLogic
				.changePwdForFirstTimeLogin(employeeId, companyId, oldPassword,
						newPassword, request.getRemoteAddr());
		String status = msgDto.getKey();
		try {
			if (status.equalsIgnoreCase("new.passWord.has.been.changed")) {
				status = PayAsiaConstants.PAYASIA_SUCCESS;
			} else {
				status = URLEncoder.encode(
						messageSource.getMessage(status,
								new Object[] { msgDto.getArgs() }, locale),
						"UTF-8");
			}
		} catch (UnsupportedEncodingException unsupportedEncodingException) {
			LOGGER.error(unsupportedEncodingException.getMessage(),
					unsupportedEncodingException);
			throw new PayAsiaSystemException(
					unsupportedEncodingException.getMessage(),
					unsupportedEncodingException);
		} catch (NoSuchMessageException noSuchMessageException) {
			LOGGER.error(noSuchMessageException.getMessage(),
					noSuchMessageException);
			throw new PayAsiaSystemException(
					noSuchMessageException.getMessage(), noSuchMessageException);
		}
		return status;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.EmployeeChangePasswordController#getPasswordPolicy
	 * (javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/passwordPolicy.html", method = RequestMethod.POST)
	@ResponseBody public String getPasswordPolicy(HttpServletRequest request,
			HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		PasswordPolicyPreferenceForm passwordPolicy = passwordPolicyLogic
				.getPasswordPolicy(companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(passwordPolicy, jsonConfig);
		return jsonObject.toString();
	}

}
