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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.payasia.common.bean.util.UserContext;
import com.payasia.common.form.AssignLeaveSchemeResponse;
import com.payasia.common.form.ChangeEmployeeNameListForm;
import com.payasia.common.form.ChangeEmployeeNumberFromResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.logic.ChangeEmployeeNumberLogic;
import com.payasia.web.controller.ChangeEmployeeNumberController;
import com.payasia.web.util.PayAsiaSessionAttributes;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * The Class ChangeEmployeeNumberControllerImpl.
 */
@Controller
@RequestMapping(value = "/admin/changeNumber")
public class ChangeEmployeeNumberControllerImpl implements
		ChangeEmployeeNumberController {

	/** The Constant logger. */
	private static final Logger LOGGER = Logger
			.getLogger(ChangeEmployeeNumberControllerImpl.class);
	@Resource
	ChangeEmployeeNumberLogic changeEmployeeNumberLogic;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mind.payasia.common.controller.ChangeEmployeeNumberController#
	 * searchEmployee(java.lang.String, org.springframework.ui.Model)
	 */
	@Override
	@RequestMapping(value = "/searchEmployee.html", method = RequestMethod.POST)
	@ResponseBody public String searchEmployee(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			@RequestParam(value = "keyword", required = true) String keyword,
			@RequestParam(value = "searchBy", required = true) String searchBy,
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		ChangeEmployeeNumberFromResponse changeEmpNumresponse = changeEmployeeNumberLogic
				.getSearchResultList(companyId, keyword, searchBy, pageDTO,
						sortDTO);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(changeEmpNumresponse,
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
	 * @see com.mind.payasia.common.controller.ChangeEmployeeNumberController#
	 * changeEmployeeNumber(java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String,
	 * org.springframework.ui.Model)
	 */
	@Override
	@RequestMapping(value = "/changeEmployeeNo.html", method = RequestMethod.POST)
	@ResponseBody public String changeEmployeeNumber(
			@ModelAttribute(value = "changeEmployeeNameListForm") ChangeEmployeeNameListForm changeEmployeeNameListForm,
			@RequestParam(value = "empNumSeriesId", required = true) Long empNumSeriesId,
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		Long employeeId = Long.parseLong(UserContext.getUserId());

		String status = changeEmployeeNumberLogic.changeEmployeeNumber(
				changeEmployeeNameListForm, companyId, employeeId,
				empNumSeriesId);
		return status;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mind.payasia.common.controller.ChangeEmployeeNumberController#getAll
	 * ()
	 */
	@Override
	@RequestMapping(value = "/changeEmployeeNumber.html", method = RequestMethod.POST)
	@ResponseBody public String getAll(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			HttpServletRequest request)

	{
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		ChangeEmployeeNumberFromResponse response = changeEmployeeNumberLogic
				.getChangeEmployeeNameList(pageDTO, sortDTO, companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(response, jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return null;

	}

	@Override
	@RequestMapping(value = "/searchEmployeeList.html", method = RequestMethod.POST)
	@ResponseBody public String searchEmployeeList(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			@RequestParam(value = "empName", required = true) String empName,
			@RequestParam(value = "empNumber", required = true) String empNumber,
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);
		AssignLeaveSchemeResponse leaveSchemeResponse = changeEmployeeNumberLogic
				.searchEmployee(pageDTO, sortDTO, empName, empNumber,
						companyId, employeeId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(leaveSchemeResponse,
				jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return null;
	}
}
