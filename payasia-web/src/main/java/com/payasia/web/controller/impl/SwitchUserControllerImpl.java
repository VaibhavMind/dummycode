package com.payasia.web.controller.impl;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.payasia.logic.LoginLogic;
import com.payasia.web.controller.SwitchUserController;
import com.payasia.web.util.PayAsiaSessionAttributes;

/**
 * The Class SwitchUserControllerImpl.
 */
/**
 * @author vivekjain
 * 
 */
@Controller
@SessionAttributes
public class SwitchUserControllerImpl implements SwitchUserController {

	/** The login logic. */
	@Resource
	LoginLogic loginLogic;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.SwitchUserController#employeePageRequest(javax
	 * .servlet.http.HttpSession, java.lang.String)
	 */
	@Override
	@RequestMapping(value = "portal/{companyCode}/adminHome.html", method = RequestMethod.GET)
	public String employeePageRequest(HttpSession session,
			@PathVariable String companyCode) {
		 
		Long employeeId = (Long) session
				.getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		loginLogic.setUserPrivilegeOnInfoSwitchRole(employeeId, companyCode,
				null);

		String sessionLocale = (String) session
				.getAttribute(PayAsiaSessionAttributes.PAYASIA_LOCALE_LABEL);
		String retURLString = "forward:/admin/home.html?locale="
				+ sessionLocale;
		return retURLString;
	}

}