package com.payasia.web.controller.impl;

/**
 * @author vivekjain
 *
 */
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.payasia.common.bean.util.UserContext;
import com.payasia.common.form.CustomAdminAccessControlResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.logic.AdminAccessControlLogic;
import com.payasia.logic.impl.PaySlipPDFLogoHeaderSectionImpl;
import com.payasia.web.controller.AdminAccessControlController;
import com.payasia.web.util.PayAsiaSessionAttributes;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * The Class AdminAccessControlControllerImpl.
 */

@Controller
@RequestMapping(value = "/admin/accessControl")
public class AdminAccessControlControllerImpl implements
		AdminAccessControlController {

	/** The Constant logger. */
	private static final Logger LOGGER = Logger
			.getLogger(PaySlipPDFLogoHeaderSectionImpl.class);

	/** The admin access control logic. */
	@Resource
	AdminAccessControlLogic adminAccessControlLogic;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.AdminAccessControlController#enableEmployee
	 * (java.lang.String[])
	 */
	@Override
	@RequestMapping(value = "/enableEmployee.html", method = RequestMethod.POST)
	public @ResponseBody void enableEmployee(
			@RequestParam(value = "employeeId", required = true) String[] employeeId) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		adminAccessControlLogic.enableEmployee(employeeId,companyId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.AdminAccessControlController#disableEmployee
	 * (java.lang.String[])
	 */
	@Override
	@RequestMapping(value = "/disableEmployee.html", method = RequestMethod.POST)
	public @ResponseBody void disableEmployee(
			@RequestParam(value = "employeeId", required = true) String[] employeeId) {
		Long companyId = Long.parseLong(UserContext.getWorkingCompanyId());
		adminAccessControlLogic.disableEmployee(employeeId,companyId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.AdminAccessControlController#searchEmployee
	 * (java.lang.String, java.lang.String, int, int, java.lang.String,
	 * java.lang.String, java.lang.String,
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, javax.servlet.http.HttpSession)
	 */
	@Override
	@RequestMapping(value = "/searchEmployee.html", method = RequestMethod.POST)
	@ResponseBody public String searchEmployee(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			@RequestParam(value = "searchCondition", required = true) String searchCondition,
			@RequestParam(value = "searchText", required = true) String searchText,
			@RequestParam(value = "employeeStatus", required = true) String employeeStatus,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {

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
		CustomAdminAccessControlResponse customAdminAccessControlResponse = adminAccessControlLogic
				.searchEmployee(searchCondition, searchText, employeeStatus,
						pageDTO, sortDTO, companyId, employeeId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(
				customAdminAccessControlResponse, jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return null;

	}
}
