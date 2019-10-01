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

import com.payasia.common.form.HRISPreferenceForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.logic.HRISPreferenceLogic;
import com.payasia.web.controller.HRISPreferenceController;
import com.payasia.web.util.PayAsiaSessionAttributes;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * The Class HRISPreferenceControllerImpl.
 */
@Controller
@RequestMapping(value = "/admin/hrisPreference")
public class HRISPreferenceControllerImpl implements HRISPreferenceController {
	/** The Constant logger. */
	private static final Logger LOGGER = Logger
			.getLogger(HRISPreferenceControllerImpl.class);

	@Resource
	HRISPreferenceLogic hrisPreferenceLogic;

	@Override
	@RequestMapping(value = "/saveHRISPreferences.html", method = RequestMethod.POST)
	public @ResponseBody void saveHRISPreferences(
			@ModelAttribute(value = "hrisPreferenceForm") HRISPreferenceForm hrisPreferenceForm,
			BindingResult result, ModelMap model, HttpServletRequest request) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		hrisPreferenceLogic.saveHRISPreference(hrisPreferenceForm, companyId);
	}

	@Override
	@RequestMapping(value = "/HRISPreferenceDataForEdit.html", method = RequestMethod.POST)
	public @ResponseBody
	String getHRISPreference(HttpServletRequest request,
			HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		HRISPreferenceForm hrisPreferenceForm = hrisPreferenceLogic
				.getHRISPreference(companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(hrisPreferenceForm,
				jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/searchEmployee.html", method = RequestMethod.POST)
	@ResponseBody
	public String searchEmployee(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			@RequestParam(value = "empName", required = true) String empName,
			@RequestParam(value = "empNumber", required = true) String empNumber,
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		HRISPreferenceForm hrisPreferenceForm = hrisPreferenceLogic
				.searchEmployee(pageDTO, sortDTO, employeeId, empName,
						empNumber, companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(hrisPreferenceForm,
				jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return null;
	}
}
