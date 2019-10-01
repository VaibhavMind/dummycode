/**
 * @author abhisheksachdeva
 *
 */
package com.payasia.web.controller.impl;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.payasia.common.form.EmailPreferenceForm;
import com.payasia.logic.EmailPreferenceLogic;
import com.payasia.web.controller.EmailPreferenceController;
import com.payasia.web.util.PayAsiaSessionAttributes;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * The Class EmailPreferenceControllerImpl.
 */
@Controller
@RequestMapping(value = "/admin/emailPreference")
public class EmailPreferenceControllerImpl implements EmailPreferenceController {

	/** The email preference logic. */
	@Resource
	EmailPreferenceLogic emailPreferenceLogic;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mind.payasia.common.controller.EmailPreferenceController#
	 * saveEmailPreferences(com.mind.payasia.common.form.EmailPreferenceForm,
	 * org.springframework.validation.BindingResult,
	 * org.springframework.ui.ModelMap, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	@RequestMapping(value = "/saveEmailPreferences.html", method = RequestMethod.POST)
	public void saveEmailPreference(
			@ModelAttribute(value = "emailPreferenceForm") EmailPreferenceForm emailPreferenceForm,
			BindingResult result, ModelMap model, HttpServletRequest request) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		emailPreferenceLogic
				.saveEmailPreference(emailPreferenceForm, companyId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mind.payasia.common.controller.EmailPreferenceController#
	 * getEmailPreference(int)
	 */
	@Override
	@RequestMapping(value = "/emailPreferenceDataForEdit.html", method = RequestMethod.POST)
	public @ResponseBody
	String getEmailPreference(HttpServletRequest request,
			HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		EmailPreferenceForm emailPreferenceForm = emailPreferenceLogic
				.getEmailPrefrence(companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(emailPreferenceForm,
				jsonConfig);
		return jsonObject.toString();
	}

}
