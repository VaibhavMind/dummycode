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
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.payasia.common.form.CompanyHolidayCalendarForm;
import com.payasia.common.form.EmployeeListFormPage;
import com.payasia.common.form.HolidayListResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.logic.HolidayCalendarLogic;
import com.payasia.web.controller.HolidayCalendarController;
import com.payasia.web.util.PayAsiaSessionAttributes;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * The Class HolidayListControllerImpl.
 */
@Controller
@RequestMapping(value = "/admin/companyHolidayCal")
public class HolidayCalendarControllerImpl implements HolidayCalendarController {

	/** The Constant logger. */
	private static final Logger LOGGER = Logger
			.getLogger(HolidayCalendarControllerImpl.class);

	/** The holiday list master logic. */
	@Resource
	HolidayCalendarLogic holidayListLogic;

	@Autowired
	private MessageSource messageSource;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mind.payasia.common.controller.HolidayListController#addHoliday(com
	 * .mind.payasia.common.form.HolidayListForm,
	 * org.springframework.validation.BindingResult,
	 * org.springframework.ui.ModelMap, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	@RequestMapping(value = "/searchHolidayCalendar.html", method = RequestMethod.POST)
	@ResponseBody public String searchHolidayCalendar(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			@RequestParam(value = "searchCondition", required = false) String searchCondition,
			@RequestParam(value = "searchText", required = false) String searchText,
			@RequestParam(value = "year", required = false) int year,
			HttpServletRequest request) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		HolidayListResponse response = holidayListLogic.searchHolidayCalendar(
				pageDTO, sortDTO, searchCondition, searchText, year, companyId);

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
	@RequestMapping(value = "/addHolidayCal.html", method = RequestMethod.POST)
	@ResponseBody public String addHolidayCal(
			@ModelAttribute("companyHolidayCalendarForm") CompanyHolidayCalendarForm companyHolidayCalendarForm,
			HttpServletRequest request) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		String holidayStatus = holidayListLogic.addHolidayCal(
				companyHolidayCalendarForm, companyId);
		return holidayStatus;

	}

	@Override
	@RequestMapping(value = "/getHolidayCalData.html", method = RequestMethod.POST)
	@ResponseBody public String getHolidayCalData(
			@RequestParam(value = "holidayCalId", required = true) Long holidayCalId,
			HttpServletRequest request) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		/* ID DYCRYPT
		 * */
		holidayCalId = FormatPreserveCryptoUtil.decrypt(holidayCalId);
		CompanyHolidayCalendarForm holidayListForm = holidayListLogic
				.getHolidayCalData(holidayCalId, companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(holidayListForm,
				jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return null;

	}

	@Override
	@RequestMapping(value = "/editHolidayCal.html", method = RequestMethod.POST)
	@ResponseBody public String editHolidayCal(
			@ModelAttribute("companyHolidayCalendarForm") CompanyHolidayCalendarForm companyHolidayCalendarForm,
			BindingResult result, ModelMap model, HttpServletRequest request) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		
		/*ID DYCRYPT
		 * */
		companyHolidayCalendarForm.setHolidayCalId(FormatPreserveCryptoUtil.decrypt(companyHolidayCalendarForm.getHolidayCalId()));
		String holidayStatus = holidayListLogic.editHolidayCal(
				companyHolidayCalendarForm, companyId);

		return holidayStatus;
	}

	@Override
	@RequestMapping(value = "/deleteHolidayCal.html", method = RequestMethod.POST)
	@ResponseBody public String deleteHolidayCal(
			@RequestParam(value = "holidayCalId", required = true) Long holidayCalId,
			HttpServletRequest request) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		/* ID DYCRYPT
		 * */
		holidayCalId = FormatPreserveCryptoUtil.decrypt(holidayCalId);
		try {
			holidayListLogic.deleteHolidayCal(holidayCalId, companyId);
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			return "false";
		}
		return "true";

	}

	@Override
	@RequestMapping(value = "/getCalendarHolidayList.html", method = RequestMethod.POST)
	@ResponseBody public String getCalendarHolidayList(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			@RequestParam(value = "holidayCalId", required = false) Long holidayCalId,
			@RequestParam(value = "year", required = false) int year,
			HttpServletRequest request) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		
		/* ID DYCRYPT
		 * */
		holidayCalId = FormatPreserveCryptoUtil.decrypt(holidayCalId);
		
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		HolidayListResponse response = holidayListLogic.getCalendarHolidayList(
				pageDTO, sortDTO, holidayCalId, companyId, year);

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
	@RequestMapping(value = "/getCompanyHolidayCalData.html", method = RequestMethod.POST)
	@ResponseBody public String getCompanyHolidayCalData(
			@RequestParam(value = "companyHolidayCalId", required = true) Long companyHolidayCalId,
			HttpServletRequest request) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		
		/*
		 * ID DYCRYPT
		 * */
		companyHolidayCalId = FormatPreserveCryptoUtil.decrypt(companyHolidayCalId);
		CompanyHolidayCalendarForm holidayListForm = holidayListLogic
				.getCompanyHolidayCalData(companyHolidayCalId, companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(holidayListForm,
				jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return null;

	}

	@Override
	@RequestMapping(value = "/editCompanyHolidayCal.html", method = RequestMethod.POST)
	@ResponseBody public String editCompanyHolidayCal(
			@ModelAttribute("companyHolidayCalendarForm") CompanyHolidayCalendarForm companyHolidayCalendarForm,
			@RequestParam(value = "companyHolidayCalId", required = true) Long companyHolidayCalId,
			BindingResult result, ModelMap model, HttpServletRequest request) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		/*
		 * ID DYCRYPT
		 * */
		companyHolidayCalendarForm.setCompanyHolidayCalDetailId(FormatPreserveCryptoUtil.decrypt(companyHolidayCalendarForm.getCompanyHolidayCalDetailId()));
		companyHolidayCalendarForm.setCompanyHolidayCalId(FormatPreserveCryptoUtil.decrypt(companyHolidayCalendarForm.getCompanyHolidayCalId()));
		companyHolidayCalId = FormatPreserveCryptoUtil.decrypt(companyHolidayCalId);
		String holidayStatus = holidayListLogic.editCompanyHolidayCal(
				companyHolidayCalendarForm, companyHolidayCalId, companyId);

		return holidayStatus;
	}

	@Override
	@RequestMapping(value = "/deleteCompanyHolidayCal.html", method = RequestMethod.POST)
	@ResponseBody public String deleteCompanyHolidayCal(
			@RequestParam(value = "companyHolidayCalDetailId", required = true) Long companyHolidayCalDetailId,
			HttpServletRequest request) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		/*
		 * ID DYCRYPT
		 * */
		companyHolidayCalDetailId = FormatPreserveCryptoUtil.decrypt(companyHolidayCalDetailId);
		try {
			holidayListLogic.deleteCompanyHolidayCal(companyHolidayCalDetailId,
					companyId);
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			return "false";
		}
		return "true";

	}

	@Override
	@RequestMapping(value = "/addCompanyHolidayDetail.html", method = RequestMethod.POST)
	@ResponseBody public String addCompanyHolidayData(
			@ModelAttribute("companyHolidayCalendarForm") CompanyHolidayCalendarForm companyHolidayCalendarForm,
			@RequestParam(value = "companyHolidayCalId", required = true) Long companyHolidayCalId,
			HttpServletRequest request) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		/*
		 * ID DYCRYPT
		 * */
		companyHolidayCalendarForm.setCompanyHolidayCalId(FormatPreserveCryptoUtil.decrypt(companyHolidayCalendarForm.getCompanyHolidayCalId()));
		companyHolidayCalId = FormatPreserveCryptoUtil.decrypt(companyHolidayCalId);
		String holidayStatus = holidayListLogic.addCompanyHolidayData(
				companyHolidayCalendarForm, companyId, companyHolidayCalId);
		return holidayStatus;

	}

	@Override
	@RequestMapping(value = "/getStateList.html", method = RequestMethod.POST)
	@ResponseBody public String getStateList(
			@RequestParam(value = "countryId", required = true) Long countryId) {
		List<CompanyHolidayCalendarForm> stateList = holidayListLogic
				.getStateList(countryId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(stateList, jsonConfig);
		return jsonObject.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.payasia.web.controller.HolidayListMasterController#getCountryList()
	 */
	@Override
	@RequestMapping(value = "/getCountryList.html", method = RequestMethod.POST)
	@ResponseBody public String getCountryList() {
		List<CompanyHolidayCalendarForm> countryList = holidayListLogic
				.getCountryList();
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(countryList, jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/getHolidayMasterListForImport.html", method = RequestMethod.POST)
	@ResponseBody public String getHolidayMasterListForImport(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			@RequestParam(value = "countryId", required = false) String countryId,
			@RequestParam(value = "stateId", required = false) String stateId,
			@RequestParam(value = "year", required = false) String year,
			HttpServletRequest request) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		HolidayListResponse response = holidayListLogic
				.getHolidayMasterListForImport(pageDTO, sortDTO, countryId,
						stateId, year, companyId);

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
	@RequestMapping(value = "/importCompanyHolidayData.html", method = RequestMethod.POST)
	@ResponseBody public String importCompanyHolidayData(
			@ModelAttribute("companyHolidayCalendarForm") CompanyHolidayCalendarForm companyHolidayCalendarForm,
			@RequestParam(value = "selectedIds", required = true) String[] selectedIds,
			@RequestParam(value = "companyHolidayCalId", required = true) Long companyHolidayCalId,
			HttpServletRequest request, Locale locale) {
		
		/*ID DECRYPT*/
		companyHolidayCalId = FormatPreserveCryptoUtil.decrypt(companyHolidayCalId);
		
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		String status = holidayListLogic.importCompanyHolidayData(
				companyHolidayCalendarForm, companyId, selectedIds,
				companyHolidayCalId);

		return status;
	}

	@Override
	@RequestMapping(value = "/unAssignCompanyHolidayCal.html", method = RequestMethod.POST)
	@ResponseBody public String unAssignCompanyHolidayCal(
			@RequestParam(value = "employeeHolidayCalId", required = true) Long employeeHolidayCalId,
			HttpServletRequest request) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		/*
		 * ID DYCRYPT
		 * */
		employeeHolidayCalId = FormatPreserveCryptoUtil.decrypt(employeeHolidayCalId);
		try {
			holidayListLogic.unAssignCompanyHolidayCal(employeeHolidayCalId,
					companyId);
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage(), ex);
			return "false";
		}
		return "true";

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
		employeeListFormPageRes = holidayListLogic
				.fetchEmpsForCalendarTemplate(searchCondition, searchText,
						pageDTO, sortDTO, companyId, employeeId);

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
	@RequestMapping(value = "/getHolidayCalendars.html", method = RequestMethod.POST)
	@ResponseBody public String getHolidayCalendars(HttpServletRequest request) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		HolidayListResponse response = holidayListLogic
				.getHolidayCalendars(companyId);

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
	@RequestMapping(value = "/assignHolCalToEmployees.html", method = RequestMethod.POST)
	@ResponseBody public String assignHolCalToEmployees(
			@RequestParam(value = "employeeIds", required = true) String employeeIds,
			@RequestParam(value = "holidayCalId", required = true) Long holidayCalId,
			HttpServletRequest request, HttpServletResponse response) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		String status = holidayListLogic.assignHolCalToEmployees(companyId,
				employeeIds, holidayCalId);
		return status;
	}

	@Override
	@RequestMapping(value = "/getEmployeeHolidayCalendars.html", method = RequestMethod.POST)
	@ResponseBody public String getEmployeeHolidayCalendars(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "searchCondition", required = false) String searchCondition,
			@RequestParam(value = "searchText", required = false) String searchText,
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
		Long employeeId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.EMPLOYEE_EMPLOYEE_ID);

		 
		 

		HolidayListResponse holidayListResponse = null;
		holidayListResponse = holidayListLogic.getEmployeeHolidayCalendars(
				searchCondition, searchText, pageDTO, sortDTO, companyId,
				employeeId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(holidayListResponse,
				jsonConfig);

		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error(ex.getMessage(), ex);
		}
		return null;
	}

}