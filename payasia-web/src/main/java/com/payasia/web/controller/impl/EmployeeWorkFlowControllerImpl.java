package com.payasia.web.controller.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.payasia.common.form.EmployeeWorkFlowDelegateResponse;
import com.payasia.common.form.EmployeeWorkFlowForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.logic.EmployeeWorkFlowLogic;
import com.payasia.web.controller.EmployeeWorkFlowController;
import com.payasia.web.util.PayAsiaSessionAttributes;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * The Class EmployeeWorkFlowControllerImpl.
 */
/**
 * @author vivekjain
 * 
 */
@Controller
@RequestMapping(value = "/employee/employeeWorkflow")
public class EmployeeWorkFlowControllerImpl implements
		EmployeeWorkFlowController {

	/** The Constant logger. */
	private static final Logger LOGGER = Logger
			.getLogger(EmployeeWorkFlowControllerImpl.class);

	/** The employee work flow logic. */
	@Resource
	EmployeeWorkFlowLogic employeeWorkFlowLogic;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.EmployeeWorkFlowController#getEmployeeWorkFlowData
	 * (javax.servlet.http.HttpServletRequest)
	 */
	@Override
	@RequestMapping(value = "/getEmployeeWorkFlowData.html", method = RequestMethod.POST)
	public @ResponseBody
	String getEmployeeWorkFlowData(HttpServletRequest request) {
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		String employeeInfo = employeeWorkFlowLogic.getEmployeeInfo(employeeId);
		return employeeInfo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.web.controller.EmployeeWorkFlowController#
	 * getEmployeeWorkFlowDetails(java.lang.String,
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	@RequestMapping(value = "/getEmployeeWorkFlowDetails.html", method = RequestMethod.POST)
	public @ResponseBody
	String getEmployeeWorkFlowDetails(
			@RequestParam(value = "searchString", required = true) String searchString,
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		List<EmployeeWorkFlowForm> employeeWorkFlowForm = employeeWorkFlowLogic
				.getEmployeeWorkFlowDetails(companyId, searchString, employeeId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(employeeWorkFlowForm,
				jsonConfig);
		return jsonObject.toString();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.web.controller.EmployeeWorkFlowController#
	 * setEmployeeWorkFlowDetails(com.payasia.common.form.EmployeeWorkFlowForm,
	 * org.springframework.validation.BindingResult,
	 * org.springframework.ui.ModelMap, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	@RequestMapping(value = "/setEmployeeWorkFlowDetails.html", method = RequestMethod.POST)
	public @ResponseBody
	String setEmployeeWorkFlowDetails(
			@ModelAttribute("employeeWorkFlowForm") EmployeeWorkFlowForm employeeWorkFlowForm,
			HttpServletRequest request) {

		 

		employeeWorkFlowForm.setEmployeeId((Long) request.getSession()
				.getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID));
		employeeWorkFlowForm.setCompanyId((Long) request.getSession()
				.getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID));

		employeeWorkFlowLogic
				.saveEmployeeWorkFlowDelagateData(employeeWorkFlowForm);

		return null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.web.controller.EmployeeWorkFlowController#
	 * employeeWorkFlowDelegateData(javax.servlet.http.HttpServletRequest,
	 * java.lang.String, int, int, java.lang.String)
	 */
	@Override
	@RequestMapping(value = "/employeeWorkFlowDelegateData.html", method = RequestMethod.POST)
	public @ResponseBody
	String employeeWorkFlowDelegateData(HttpServletRequest request,
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			@RequestParam(value = "sord", required = false) String sortingType) {

		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		EmployeeWorkFlowDelegateResponse response = employeeWorkFlowLogic
				.viewEmployeeWorkFlowDelegateData(pageDTO, sortDTO, employeeId,
						companyId);

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
	 * @see com.payasia.web.controller.EmployeeWorkFlowController#
	 * deleteEmployeeWorkFlowDelegate(java.lang.Long)
	 */
	@Override
	@RequestMapping(value = "/deleteEmployeeWorkFlowDelegate.html", method = RequestMethod.POST)
	public @ResponseBody
	String deleteEmployeeWorkFlowDelegate(
			@RequestParam(value = "workFLowDelegateId") Long workFLowDelegateId) {

		String responseStatus = employeeWorkFlowLogic
				.deleteEmployeeWorkFlowDelegate(workFLowDelegateId);

		return responseStatus;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.payasia.web.controller.EmployeeWorkFlowController#
	 * editEmployeeWorkFlowDetails(com.payasia.common.form.EmployeeWorkFlowForm,
	 * org.springframework.validation.BindingResult,
	 * org.springframework.ui.ModelMap, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	@RequestMapping(value = "/editEmployeeWorkFlowDetails.html", method = RequestMethod.POST)
	public @ResponseBody
	String editEmployeeWorkFlowDetails(
			@ModelAttribute("employeeWorkFlowForm") EmployeeWorkFlowForm employeeWorkFlowForm,
			HttpServletRequest request) {

		 

		employeeWorkFlowForm.setEmployeeId((Long) request.getSession()
				.getAttribute(PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID));
		employeeWorkFlowForm.setCompanyId((Long) request.getSession()
				.getAttribute(PayAsiaSessionAttributes.ADMIN_COMPANY_ID));

		employeeWorkFlowLogic
				.editEmployeeWorkFlowDelagateData(employeeWorkFlowForm);

		return null;

	}

}
