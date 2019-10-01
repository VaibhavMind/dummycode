/**
 * @author abhisheksachdeva
 *
 */
package com.payasia.web.controller.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.payasia.common.form.PageRequest;
import com.payasia.common.form.PasswordPolicyPreferenceForm;
import com.payasia.common.form.PasswordSecurityQuestionForm;
import com.payasia.common.form.PasswordSecurityQuestionResponse;
import com.payasia.common.form.SortCondition;
import com.payasia.logic.PasswordPolicyPreferenceLogic;
import com.payasia.web.controller.PasswordPolicyPreferenceController;
import com.payasia.web.util.PayAsiaSessionAttributes;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * The Class PasswordPolicyPreferenceControllerImpl.
 */
/**
 * @author vivekjain
 * 
 */
@Controller
@RequestMapping(value = "/admin/passwordPolicyPreference")
public class PasswordPolicyPreferenceControllerImpl implements
		PasswordPolicyPreferenceController {

	/** The Constant logger. */
	private static final Logger LOGGER = Logger
			.getLogger(PasswordPolicyPreferenceControllerImpl.class);

	/** The password policy preference logic. */
	@Resource
	PasswordPolicyPreferenceLogic passwordPolicyPreferenceLogic;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mind.payasia.common.controller.PasswordPolicyPreferenceController
	 * #addSecurityQuestionList(java.lang.String, int)
	 */
	@Override
	@RequestMapping(value = "/addSecurityQuestion.html", method = RequestMethod.POST)
	public void addSecurityQuestion(
			@RequestParam(value = "question", required = true) String question,
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		passwordPolicyPreferenceLogic.addSecurityQuestion(question, companyId);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mind.payasia.common.controller.PasswordPolicyPreferenceController
	 * #deleteSecurityQuestionList(int)
	 */
	@Override
	@RequestMapping(value = "/deleteSecurityQuestion.html", method = RequestMethod.POST)
	public void deleteSecurityQuestion(
			@RequestParam(value = "questionId", required = true) Long pwdSecurityQuestionId,
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		passwordPolicyPreferenceLogic.deleteSecurityQuestion(pwdSecurityQuestionId,companyId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mind.payasia.common.controller.PasswordPolicyPreferenceController
	 * #getSecurityQuestionDetails(int)
	 */
	@Override
	@RequestMapping(value = "/getSecurityQuestionDetails.html", method = RequestMethod.POST)
	@ResponseBody public String getSecurityQuestionDetails(
			@RequestParam(value = "questionId", required = true) Long pwdSecurityQuestionId,
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		PasswordSecurityQuestionForm passwordSecurityQuestionForm = passwordPolicyPreferenceLogic
				.getSecurityQuestion(companyId, pwdSecurityQuestionId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(
				passwordSecurityQuestionForm, jsonConfig);
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
	 * @see com.payasia.web.controller.PasswordPolicyPreferenceController#
	 * updateSecurityQuestion(java.lang.String, java.lang.Long,
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/updateSecurityQuestion.html", method = RequestMethod.POST)
	public void updateSecurityQuestion(
			@RequestParam(value = "question", required = true) String question,
			@RequestParam(value = "questionId", required = true) Long questionId,
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		passwordPolicyPreferenceLogic.updateSecurityQuestion(question,
				questionId, companyId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mind.payasia.common.controller.PasswordPolicyPreferenceController
	 * #getSecurityQuestionList()
	 */
	@Override
	@RequestMapping(value = "/getSecurityQuestionList.html", method = RequestMethod.POST)
	@ResponseBody public String getSecurityQuestionList(
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		PasswordSecurityQuestionResponse passpSecQuespResponse = passwordPolicyPreferenceLogic
				.getSecurityQuestionList(companyId, pageDTO, sortDTO);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(passpSecQuespResponse,
				jsonConfig);
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
	 * com.mind.payasia.common.controller.PasswordPolicyPreferenceController
	 * #getPasswordPolicy(int)
	 */
	@Override
	@RequestMapping(value = "/getPasswordPolicy.html", method = RequestMethod.POST)
	@ResponseBody public String getPasswordPolicy(HttpServletRequest request,
			HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		PasswordPolicyPreferenceForm passwordPolicyPreferenceForm = passwordPolicyPreferenceLogic
				.getPasswordPolicy(companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(
				passwordPolicyPreferenceForm, jsonConfig);
		return jsonObject.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mind.payasia.common.controller.PasswordPolicyPreferenceController
	 * #savePasswordPolicy
	 * (com.mind.payasia.common.form.PasswordPolicyPreferenceForm,
	 * org.springframework.validation.BindingResult,
	 * org.springframework.ui.ModelMap, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	@RequestMapping(value = "/savePasswordPolicy.html", method = RequestMethod.POST)
	@ResponseBody public String savePasswordPolicy(
			@ModelAttribute(value = "passwordPolicyPreferenceForm") PasswordPolicyPreferenceForm passwordPolicyPreferenceForm,
			BindingResult result, ModelMap model, HttpServletRequest request) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		passwordPolicyPreferenceLogic.savePasswordPolicy(
				passwordPolicyPreferenceForm, companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(
				passwordPolicyPreferenceForm, jsonConfig);
		return jsonObject.toString();

	}

}
