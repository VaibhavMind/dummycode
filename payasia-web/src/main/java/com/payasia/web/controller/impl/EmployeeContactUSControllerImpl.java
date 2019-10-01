package com.payasia.web.controller.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.payasia.common.form.EmployeeContactUSForm;
import com.payasia.logic.EmployeeContactUSLogic;
import com.payasia.logic.TaxDocumentLogic;
import com.payasia.web.controller.EmployeeContactUSController;
import com.payasia.web.util.PayAsiaSessionAttributes;

import nl.captcha.Captcha;

/**
 * The Class EmployeeContactUSControllerImpl.
 */
@Controller
@RequestMapping(value = "/employee/ContactUS")
public class EmployeeContactUSControllerImpl implements
		EmployeeContactUSController {

	/** The employee contact us logic. */
	@Resource
	EmployeeContactUSLogic employeeContactUSLogic;

	/** The Constant logger. */
	private static final Logger LOGGER = Logger
			.getLogger(EmployeeDetailControllerImpl.class);

	/** The Tax document logic. */
	@Resource
	TaxDocumentLogic TaxDocumentLogic;
	/** The message source. */
	@Autowired
	private MessageSource messageSource;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.EmployeeContactUSController#getContactEmail
	 * (javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/contactEmail.html", method = RequestMethod.POST)
	@ResponseBody public String getContactEmail(HttpServletRequest request,
			HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		String contactEmailId = employeeContactUSLogic
				.getContactEmail(companyId);
		return contactEmailId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.EmployeeContactUSController#sendMail(com.payasia
	 * .common.form.EmployeeContactUSForm,
	 * javax.servlet.http.HttpServletRequest)
	 */
	@Override
	@RequestMapping(value = "/sendMail.html", method = RequestMethod.POST)
	@ResponseBody public String sendMail(
			@ModelAttribute("employeeContactUSForm") EmployeeContactUSForm employeeContactUSForm,
			HttpServletRequest request, HttpSession session, Locale locale) {
		 
		 
		Long empId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		Captcha captcha = (Captcha) session.getAttribute(Captcha.NAME);
		String answer = employeeContactUSForm.getTuring();
		String mailstatus = null;
		if (captcha.isCorrect(answer)) {
			mailstatus = employeeContactUSLogic.sendMail(employeeContactUSForm,
					empId);

		} else {
			mailstatus = "payasia.verification.code.error";
		}
		try {
			mailstatus = URLEncoder.encode(messageSource.getMessage(mailstatus,
					new Object[] {}, locale), "UTF-8");
		} catch (UnsupportedEncodingException unsupportedEncodingException) {
			LOGGER.error(unsupportedEncodingException.getMessage(),
					unsupportedEncodingException);
		} catch (NoSuchMessageException noSuchMessageException) {
			LOGGER.error(noSuchMessageException.getMessage(),
					noSuchMessageException);
		}
		return mailstatus;
	}

}
