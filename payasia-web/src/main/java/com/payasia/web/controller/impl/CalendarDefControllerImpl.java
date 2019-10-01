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
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.payasia.common.form.CalendarDefForm;
import com.payasia.common.form.CalendarDefResponse;
import com.payasia.common.form.CalendarTempDataForm;
import com.payasia.common.form.CalendarTempShortListResponse;
import com.payasia.common.form.CalendarTemplateMonthForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.common.util.FormatPreserveCryptoUtil;
import com.payasia.logic.CalendarDefLogic;
import com.payasia.web.controller.CalendarDefController;
import com.payasia.web.util.PayAsiaSessionAttributes;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

@Controller
@RequestMapping(value = "/admin/calendar")
public class CalendarDefControllerImpl implements CalendarDefController {

	@Resource
	CalendarDefLogic calendarDefLogic;

	@Autowired
	private MessageSource messageSource;

	private static final Logger LOGGER = Logger
			.getLogger(CalendarDefControllerImpl.class);

	@Override
	@RequestMapping(value = "viewCalendarDefinition.html", method = RequestMethod.POST)
	@ResponseBody public String viewCalTemplates(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			@RequestParam(value = "calTempFilter", required = true) String calTempFilter,
			@RequestParam(value = "filterText", required = true) String filterText,
			HttpServletRequest request) {

		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		CalendarDefResponse response = calendarDefLogic.getCalTemplates(
				pageDTO, sortDTO, calTempFilter, filterText, companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(response, jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);

		}
		return null;

	}

	@Override
	@RequestMapping(value = "getInitialIds.html", method = RequestMethod.POST)
	@ResponseBody public String getInitialData() {

		CalendarTempDataForm response = calendarDefLogic.getInitialIds();

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(response, jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "saveCalMonthDetail.html", method = RequestMethod.POST)
	public @ResponseBody void addCalMonthTemplate(
			@ModelAttribute("calMonthForm") CalendarTemplateMonthForm calTempMonthForm) {
		calendarDefLogic.addCalendarMonthDetail(calTempMonthForm);
	}

	@Override
	@RequestMapping(value = "getCalTemplateData.html", method = RequestMethod.POST)
	@ResponseBody public String getCalTempForEdit(
			@RequestParam(value = "calTempId", required = true) Long calTempId,
			@RequestParam(value = "year", required = true) int year,
			HttpServletRequest request) {
		/* ID DECRYPT */
		calTempId= FormatPreserveCryptoUtil.decrypt(calTempId);
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		CalendarDefForm response = calendarDefLogic.getCalTempData(calTempId,
				year, companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(response, jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@RequestMapping(value = "editCalendarTemplate.html", method = RequestMethod.POST)
	@ResponseBody public String editCalendarTemplate(
			@ModelAttribute(value = "calDefForm") CalendarDefForm calDefForm,
			HttpServletRequest request) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		String response = calendarDefLogic.editCalTemplate(calDefForm,
				companyId);

		return response;
	}

	@Override
	@RequestMapping(value = "getYearData.html", method = RequestMethod.POST)
	@ResponseBody public String getYearList(
			@RequestParam(value = "calTempId", required = true) Long calTempId) {
		List<Integer> yearList = calendarDefLogic.getYearList(calTempId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonArray = JSONArray.fromObject(yearList, jsonConfig);
		return jsonArray.toString();
	}

	@Override
	@RequestMapping(value = "getMonthDetail.html", method = RequestMethod.POST)
	@ResponseBody public String getMonthDetail(
			@RequestParam(value = "year", required = true) Integer year,
			@RequestParam(value = "calTempId", required = true) Long calTempId) {

		CalendarTemplateMonthForm response = calendarDefLogic.getMonthDetail(
				year, calTempId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(response, jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "editCalMonthDetail.html", method = RequestMethod.POST)
	public @ResponseBody void editCalMonthTemplate(
			@ModelAttribute(value = "calMonthForm") CalendarTemplateMonthForm calTempMonthForm) {
		calendarDefLogic.editCalMonthTemplate(calTempMonthForm);
	}

	@Override
	@RequestMapping(value = "delCalendarTemplate.html", method = RequestMethod.POST)
	@ResponseBody public String delete(
			@RequestParam(value = "companyCalTemplateId", required = false) Long companyCalTemplateId) {
		/* ID DECRYPT */
		companyCalTemplateId= FormatPreserveCryptoUtil.decrypt(companyCalTemplateId);
		try {
			calendarDefLogic.deleteCalTemplate(companyCalTemplateId);

		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return "error";
		}
		return "success";
	}

	@Override
	@RequestMapping(value = "getCategoryShortList.html", method = RequestMethod.POST)
	@ResponseBody public String getEntityForShortList() {

		CalendarTempDataForm response = calendarDefLogic.getEntityData();

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(response, jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "getFieldNameShortList.html", method = RequestMethod.POST)
	@ResponseBody public String getFieldsForShortList(
			@RequestParam(value = "categoryVal", required = true) Long categoryId,
			HttpServletRequest request) {

		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		CalendarTempDataForm response = calendarDefLogic.getFieldsData(
				categoryId, companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(response, jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "addShortListData.html", method = RequestMethod.POST)
	@ResponseBody public String addShortListData(
			@RequestParam(value = "shortListData") String shortListData,
			Locale locale) {

		String response = calendarDefLogic.addShortListData(shortListData);

		try {
			response = URLEncoder
					.encode(messageSource.getMessage(response, new Object[] {},
							locale), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (NoSuchMessageException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return response;
	}

	@Override
	@RequestMapping(value = "getShortListForEdit.html", method = RequestMethod.POST)
	@ResponseBody public String getShortListForEdit(
			@RequestParam(value = "calTempId", required = true) Long calTempId) {

		CalendarTempShortListResponse response = calendarDefLogic
				.getShortListForEdit(calTempId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(response, jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = " editShortListData.html", method = RequestMethod.POST)
	@ResponseBody public String editShortListData(
			@RequestParam(value = "shortListData") String shortListData,
			Locale locale) {
		String response = calendarDefLogic.editShortListData(shortListData);
		try {
			response = URLEncoder
					.encode(messageSource.getMessage(response, new Object[] {},
							locale), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (NoSuchMessageException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return response;
	}

	@Override
	@RequestMapping(value = "saveYearlyCalendar.html", method = RequestMethod.POST)
	@ResponseBody public String saveYearlyCalendar(
			@RequestParam(value = "calTempId", required = false) Long calTempId,
			@RequestParam(value = "yearlyMap", required = false) String yearlyMap) {

		String response = calendarDefLogic.saveYearlyCalendar(calTempId,
				yearlyMap);

		return response;
	}

	@Override
	@RequestMapping(value = "viewCalendarCodeValue.html", method = RequestMethod.POST)
	@ResponseBody public String viewCalendarCodeValue(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			HttpServletRequest request) {

		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		CalendarDefResponse response = calendarDefLogic.viewCalendarCodeValue(
				pageDTO, sortDTO, companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(response, jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;

	}

	@Override
	@RequestMapping(value = "viewCalendarPatternCode.html", method = RequestMethod.POST)
	@ResponseBody public String viewCalendarPatternCode(
			@RequestParam(value = "sidx", required = false) String columnName,
			@RequestParam(value = "sord", required = false) String sortingType,
			@RequestParam(value = "page", required = false) int page,
			@RequestParam(value = "rows", required = false) int rows,
			HttpServletRequest request) {

		PageRequest pageDTO = new PageRequest();
		pageDTO.setPageNumber(page);
		pageDTO.setPageSize(rows);

		SortCondition sortDTO = new SortCondition();
		sortDTO.setColumnName(columnName);
		sortDTO.setOrderType(sortingType);

		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		CalendarDefResponse response = calendarDefLogic
				.viewCalendarPatternCode(pageDTO, sortDTO, companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(response, jsonConfig);
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;

	}

	@Override
	@RequestMapping(value = "getPatternCodeList.html", method = RequestMethod.POST)
	@ResponseBody public String getPatternCodeList(HttpServletRequest request) {

		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		List<CalendarDefForm> patternCodeList = calendarDefLogic
				.getPatternCodeList(companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray
				.fromObject(patternCodeList, jsonConfig);
		return jsonObject.toString();

	}

	@Override
	@RequestMapping(value = " addCalendarCodeValue.html", method = RequestMethod.POST)
	@ResponseBody public String addCalendarCodeValue(
			@ModelAttribute("calendarDefForm") CalendarDefForm calendarDefForm,
			Locale locale, HttpServletRequest request) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		String response = calendarDefLogic.addCalendarCodeValue(
				calendarDefForm, companyId);
		return response;
	}

	@Override
	@RequestMapping(value = " addCalendarPatternValue.html", method = RequestMethod.POST)
	@ResponseBody public String addCalendarPatternValue(
			@ModelAttribute("calendarDefForm") CalendarDefForm calendarDefForm,
			Locale locale, HttpServletRequest request) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		String response = calendarDefLogic.addCalendarPatternValue(
				calendarDefForm, companyId);
		return response;
	}

	@Override
	@RequestMapping(value = "getCalCodeForEdit.html", method = RequestMethod.POST)
	@ResponseBody public String getCalCodeForEdit(
			@RequestParam(value = "calCodeId", required = true) Long calCodeId) {
		/* ID DECRYPT */
		calCodeId= FormatPreserveCryptoUtil.decrypt(calCodeId);
		CalendarDefForm response = calendarDefLogic
				.getCalCodeForEdit(calCodeId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(response, jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "/getCalendarCodeList.html", method = RequestMethod.POST)
	@ResponseBody public String getCalendarCodeList(HttpServletRequest request) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		List<CalendarDefForm> patternCodeValueList = calendarDefLogic
				.getCalendarCodeList(companyId);
		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray.fromObject(patternCodeValueList,
				jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = "getCalPatterneForEdit.html", method = RequestMethod.POST)
	@ResponseBody public String getCalPatterneForEdit(
			@RequestParam(value = "calPatternId", required = true) Long calPatternId) {
		/* ID DECRYPT */
		calPatternId= FormatPreserveCryptoUtil.decrypt(calPatternId);
		CalendarDefForm response = calendarDefLogic
				.getCalPatterneForEdit(calPatternId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(response, jsonConfig);
		return jsonObject.toString();
	}

	@Override
	@RequestMapping(value = " updateCalendarCodeValue.html", method = RequestMethod.POST)
	@ResponseBody public String updateCalendarCodeValue(
			@ModelAttribute("calendarDefForm") CalendarDefForm calendarDefForm,
			Locale locale, HttpServletRequest request) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		String response = calendarDefLogic.updateCalendarCodeValue(
				calendarDefForm, companyId);
		try {
			response = URLEncoder
					.encode(messageSource.getMessage(response, new Object[] {},
							locale), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (NoSuchMessageException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return response;
	}

	@Override
	@RequestMapping(value = " updateCalendarPatternValue.html", method = RequestMethod.POST)
	@ResponseBody public String updateCalendarPatternValue(
			@ModelAttribute("calendarDefForm") CalendarDefForm calendarDefForm,
			Locale locale, HttpServletRequest request) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);
		String response = calendarDefLogic.updateCalendarPatternValue(
				calendarDefForm, companyId);
		try {
			response = URLEncoder
					.encode(messageSource.getMessage(response, new Object[] {},
							locale), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (NoSuchMessageException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return response;
	}

	@Override
	@RequestMapping(value = "deleteCalCode.html", method = RequestMethod.POST)
	@ResponseBody public String deleteCalCode(
			@RequestParam(value = "calCodeId", required = true) Long calCodeId) {
		/* ID DECRYPT */
		calCodeId= FormatPreserveCryptoUtil.decrypt(calCodeId);
		try {
			calendarDefLogic.deleteCalCode(calCodeId);

		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return "error";
		}
		return "success";
	}

	@Override
	@RequestMapping(value = "deleteCalPattern.html", method = RequestMethod.POST)
	@ResponseBody public String deleteCalPattern(
			@RequestParam(value = "calPatternId", required = true) Long calPatternId) {
		/* ID DECRYPT */
		calPatternId= FormatPreserveCryptoUtil.decrypt(calPatternId);
		try {
			calendarDefLogic.deleteCalPattern(calPatternId);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return "error";
		}
		return "success";

	}

	@Override
	@RequestMapping(value = "addCalendarTemplate.html", method = RequestMethod.POST)
	@ResponseBody public String saveCalTemplate(
			@ModelAttribute("calDefForm") CalendarDefForm calDefForm,
			HttpServletRequest request) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		String response = calendarDefLogic.saveCalTemplate(calDefForm,
				companyId);

		return response;
	}

	@Override
	@RequestMapping(value = "getDataForCompanyCalTemplate.html", method = RequestMethod.POST)
	@ResponseBody public String getDataForCompanyCalTemplate(
			@RequestParam(value = "companyCalTemplateId", required = false) Long companyCalTemplateId) {
		/* ID DECRYPT */
		companyCalTemplateId = FormatPreserveCryptoUtil.decrypt(companyCalTemplateId);
		CalendarDefForm response = calendarDefLogic
				.getDataForCompanyCalTemplate(companyCalTemplateId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONObject jsonObject = JSONObject.fromObject(response, jsonConfig);
		//return jsonObject.toString();
		try {
			return URLEncoder.encode(jsonObject.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
		
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
		calTempId = FormatPreserveCryptoUtil.decrypt(calTempId);
		
		calendarDefLogic.saveCalEventByDate(calTempId, calCode, eventDate,
				companyId);
		return null;

	}

	@Override
	@RequestMapping(value = "/getCalTemConfigYearList.html", method = RequestMethod.POST)
	@ResponseBody public String getCalTemConfigYearList(
			@RequestParam(value = "calTempId", required = false) Long calTempId,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		Long companyId = (Long) request.getSession().getAttribute(
				PayAsiaSessionAttributes.ADMIN_COMPANY_ID);

		List<CalendarDefForm> calendarDefForm = calendarDefLogic
				.getCalTemConfigYearList(calTempId, companyId);

		JsonConfig jsonConfig = new JsonConfig();
		JSONArray jsonObject = JSONArray
				.fromObject(calendarDefForm, jsonConfig);
		return jsonObject.toString();

	}
}
