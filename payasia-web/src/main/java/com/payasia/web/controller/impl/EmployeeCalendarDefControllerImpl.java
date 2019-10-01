package com.payasia.web.controller.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.payasia.common.form.CalendarDefForm;
import com.payasia.common.form.EmployeeCalendarDefForm;
import com.payasia.common.form.EmployeeListFormPage;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.common.util.PayAsiaConstants;
import com.payasia.logic.EmployeeCalendarDefLogic;
import com.payasia.logic.EmployeeDetailLogic;
import com.payasia.logic.MultilingualLogic;
import com.payasia.web.controller.EmployeeCalendarDefController;
import com.payasia.web.util.PayAsiaSessionAttributes;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

@Controller
@RequestMapping(value = "/admin/empCalenderDef")
public class EmployeeCalendarDefControllerImpl implements
		EmployeeCalendarDefController {

	/** The Constant logger. */
	private static final Logger LOGGER = Logger
			.getLogger(EmployeeCalendarDefControllerImpl.class);
	/** The message source. */
	@Autowired
	private MessageSource messageSource;
	@Resource
	EmployeeCalendarDefLogic employeeCalendarDefLogic;

	@Resource
	MultilingualLogic multilingualLogic;

	@Resource
	EmployeeDetailLogic employeeDetailLogic;

	@Override
	@RequestMapping(value = "/assignCalendarTemplate.html", method = RequestMethod.POST)
	@ResponseBody public String assignCalendarTemplates(
			@ModelAttribute("employeeCalendarDefForm") EmployeeCalendarDefForm employeeCalendarDefForm,
			@RequestParam(value = "selectedIds", required = true) String[] selectedIds,
			HttpServletRequest request, Locale locale) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		String status = employeeCalendarDefLogic.assignCalendarTemplates(
				employeeCalendarDefForm, companyId, selectedIds);

		return status;
	}

	@Override
	@RequestMapping(value = "/fetchEmployees.html", method = RequestMethod.POST)
	@ResponseBody public String fetchEmployees(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "searchCondition", required = true) String searchCondition,
			@RequestParam(value = "searchText", required = true) String searchText,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			@RequestParam(value = "calendarTemplateId", required = true) Long calendarTemplateId,
			@RequestParam(value = "startDate", required = true) String startDate,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session, Locale locale) {

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

		 
		 

		EmployeeListFormPage employeeListFormPageRes = null;
		employeeListFormPageRes = employeeCalendarDefLogic
				.fetchEmpsForCalendarTemplate(searchCondition, searchText,
						pageDTO, sortDTO, companyId, employeeId,
						calendarTemplateId, startDate);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(employeeListFormPageRes,
				jsonConfig);

		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "/viewEmpCalendarDefinition.html", method = RequestMethod.POST)
	@ResponseBody public String viewEmpCalendarDefinition(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "searchCondition", required = true) String searchCondition,
			@RequestParam(value = "searchText", required = true) String searchText,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session, Locale locale) {

		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		Long languageId = multilingualLogic.getLanguageId(locale.getLanguage());

		EmployeeCalendarDefForm employeeCalendarDefForm = null;

		try {
			employeeCalendarDefForm = employeeCalendarDefLogic
					.getEmpCalendarTemplateList(searchCondition, searchText,
							pageDTO, sortDTO, companyId, languageId);
		} catch (UnsupportedEncodingException unsupportedEncodingException) {
			LOGGER.error(unsupportedEncodingException.getMessage(),
					unsupportedEncodingException);
		}

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(employeeCalendarDefForm,
				jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "getEmpCalTemplateData.html", method = RequestMethod.POST)
	@ResponseBody public String getEmpCalTempForEdit(
			@RequestParam(value = "empCalendarConfigId", required = true) Long empCalendarConfigId,
			@RequestParam(value = "year", required = true) int year,
			HttpServletRequest request, HttpServletResponse response) {
		/* ID DECRYPT */
		empCalendarConfigId=FormatPreserveCryptoUtil.decrypt(empCalendarConfigId);
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		CalendarDefForm responseForm = employeeCalendarDefLogic
				.getEmpCalTempData(empCalendarConfigId, year, companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(responseForm, jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "delEmpCalendarTemplate.html", method = RequestMethod.POST)
	@ResponseBody public String delete(
			@RequestParam(value = "empCalTempId", required = false) Long empCalTempId) {
		/* ID DECRYPT */
		empCalTempId= FormatPreserveCryptoUtil.decrypt(empCalTempId);
		String responseString = PayAsiaConstants.PAYASIA_SUCCESS;
		try {
			employeeCalendarDefLogic.deleteEmpCalTemplate(empCalTempId);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			responseString = PayAsiaConstants.PAYASIA_ERROR;
		}

		return responseString;
	}

	@Override
	@RequestMapping(value = "/getCalTemConfigYearList.html", method = RequestMethod.POST)
	@ResponseBody public String getCalTemConfigYearList(
			@RequestParam(value = "empCalConfigId", required = false) Long empCalConfigId,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {

		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		List<EmployeeCalendarDefForm> employeeCalendarDefForm = employeeCalendarDefLogic
				.getCalTemConfigYearList(empCalConfigId, companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(employeeCalendarDefForm,
				jsonConfig);
		return jsonObject.toString();

	}

	@Override
	@RequestMapping(value = "/getCalTempYear.html", method = RequestMethod.POST)
	@ResponseBody public String getCalTempYear(HttpServletRequest request,
			HttpServletResponse response, HttpSession session) {

		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		List<EmployeeCalendarDefForm> employeeCalendarDefForm = employeeCalendarDefLogic
				.getCalTempYear(companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(employeeCalendarDefForm,
				jsonConfig);
		return jsonObject.toString();

	}

	@Override
	@RequestMapping(value = "/fetchTemplates.html", method = RequestMethod.POST)
	@ResponseBody public String fetchEmployeeCalenderTemplates(
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session,
			@RequestParam(value = "year", required = true) Integer year) {

		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		List<CalendarDefForm> employeeCalendarDefForm = employeeCalendarDefLogic
				.getEmployeeCalendarTemplates(year, companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(employeeCalendarDefForm,
				jsonConfig);
		return jsonObject.toString();

	}

	@Override
	@RequestMapping(value = "saveCalEventByDate.html", method = RequestMethod.POST)
	@ResponseBody public String saveCalEventByDate(
			@RequestParam(value = "calTempId", required = true) Long calTempId,
			@RequestParam(value = "calCode", required = true) String calCode,
			@RequestParam(value = "eventDate", required = true) String eventDate,
			HttpServletRequest request) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		/* ID DECRYPT */
		calTempId =  FormatPreserveCryptoUtil.decrypt(calTempId);
		
		employeeCalendarDefLogic.saveCalEventByDate(calTempId, calCode,
				eventDate, companyId);
		return null;

	}

	@Override
	@RequestMapping(value = "getDataForEmpCalConfig.html", method = RequestMethod.POST)
	@ResponseBody public String getDataForEmpCalConfig(
			@RequestParam(value = "empCalConfigId", required = false) Long empCalConfigId) {
		/* ID DECRYPT */
		empCalConfigId= FormatPreserveCryptoUtil.decrypt(empCalConfigId);
		EmployeeCalendarDefForm response = employeeCalendarDefLogic
				.getDataForEmpCalConfig(empCalConfigId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(response, jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "editEmpCalConfig.html", method = RequestMethod.POST)
	@ResponseBody public String editEmpCalConfig(
			@ModelAttribute(value = "employeeCalendarDefForm") EmployeeCalendarDefForm employeeCalendarDefForm,
			HttpServletRequest request) {

		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		String response = employeeCalendarDefLogic.editEmpCalConfig(
				employeeCalendarDefForm, companyId);

		return response;
	}
}
