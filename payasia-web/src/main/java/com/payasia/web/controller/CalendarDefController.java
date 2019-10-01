package com.payasia.web.controller;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.payasia.common.form.CalendarDefForm;
import com.payasia.common.form.CalendarTemplateMonthForm;

/**
 * The Interface CalendarDefController.
 */
public interface CalendarDefController {

	/**
	 * Purpose: To View saved calendar templates in jQgrid.
	 * 
	 * @param columnName
	 *            the column name
	 * @param sortingType
	 *            the sorting type
	 * @param page
	 *            the page
	 * @param rows
	 *            the rows
	 * @param calTempFilter
	 *            the cal temp filter
	 * @param filterText
	 *            the filter text
	 * @param request
	 *            the request
	 * @return the string
	 */
	String viewCalTemplates(String columnName, String sortingType, int page,
			int rows, String calTempFilter, String filterText,
			HttpServletRequest request);

	/**
	 * Gets the initial data.
	 * 
	 * @return the initial data
	 */
	String getInitialData();

	/**
	 * Adds the cal month template.
	 * 
	 * @param calTempMonthForm
	 *            the cal temp month form
	 */
	void addCalMonthTemplate(CalendarTemplateMonthForm calTempMonthForm);

	/**
	 * Edits the calendar template.
	 * 
	 * @param calDefForm
	 *            the cal def form
	 * @param request
	 *            the request
	 * @return the string
	 */
	String editCalendarTemplate(CalendarDefForm calDefForm,
			HttpServletRequest request);

	/**
	 * Gets the year list.
	 * 
	 * @param calTempId
	 *            the cal temp id
	 * @return the year list
	 */
	String getYearList(Long calTempId);

	/**
	 * Gets the month detail.
	 * 
	 * @param year
	 *            the year
	 * @param calTempId
	 *            the cal temp id
	 * @return the month detail
	 */
	String getMonthDetail(Integer year, Long calTempId);

	/**
	 * Edits the cal month template.
	 * 
	 * @param calTempMonthForm
	 *            the cal temp month form
	 */
	void editCalMonthTemplate(CalendarTemplateMonthForm calTempMonthForm);

	/**
	 * Delete.
	 * 
	 * @param calTempId
	 *            the cal temp id
	 * @return the string
	 */
	String delete(Long calTempId);

	/**
	 * Gets the entity for short list.
	 * 
	 * @return the entity for short list
	 */
	String getEntityForShortList();

	/**
	 * Gets the fields for short list.
	 * 
	 * @param categoryId
	 *            the category id
	 * @param request
	 *            the request
	 * @return the fields for short list
	 */
	String getFieldsForShortList(Long categoryId, HttpServletRequest request);

	/**
	 * Adds the short list data.
	 * 
	 * @param shortListData
	 *            the short list data
	 * @param locale
	 *            the locale
	 * @return the string
	 */
	String addShortListData(String shortListData, Locale locale);

	/**
	 * Gets the short list for edit.
	 * 
	 * @param calTempId
	 *            the cal temp id
	 * @return the short list for edit
	 */
	String getShortListForEdit(Long calTempId);

	/**
	 * Edits the short list data.
	 * 
	 * @param shortListData
	 *            the short list data
	 * @param locale
	 *            the locale
	 * @return the string
	 */
	String editShortListData(String shortListData, Locale locale);

	/**
	 * Upload file.
	 * 
	 * @param calendarDefForm
	 *            the calendar def form
	 * @param result
	 *            the result
	 * @param model
	 *            the model
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @param session
	 *            the session
	 * @param locale
	 *            the locale
	 * @return the string
	 */

	String saveYearlyCalendar(Long calTempId, String yearlyMap);

	String viewCalendarPatternCode(String columnName, String sortingType,
			int page, int rows, HttpServletRequest request);

	String viewCalendarCodeValue(String columnName, String sortingType,
			int page, int rows, HttpServletRequest request);

	String addCalendarCodeValue(CalendarDefForm calendarDefForm, Locale locale,
			HttpServletRequest request);

	String addCalendarPatternValue(CalendarDefForm calendarDefForm,
			Locale locale, HttpServletRequest request);

	String updateCalendarCodeValue(CalendarDefForm calendarDefForm,
			Locale locale, HttpServletRequest request);

	String updateCalendarPatternValue(CalendarDefForm calendarDefForm,
			Locale locale, HttpServletRequest request);

	String getCalCodeForEdit(Long calCodeId);

	String getCalPatterneForEdit(Long calPatternId);

	String deleteCalCode(Long calCodeId);

	String deleteCalPattern(Long calPatternId);

	String getCalendarCodeList(HttpServletRequest request);

	String saveCalTemplate(CalendarDefForm calDefForm,
			HttpServletRequest request);

	String getPatternCodeList(HttpServletRequest request);

	String getDataForCompanyCalTemplate(Long companyCalTemplateId);

	String saveCalEventByDate(Long calTempId, String calCode, String eventDate,
			HttpServletRequest request);

	String getCalTempForEdit(Long calTempId, int year,
			HttpServletRequest request);

	String getCalTemConfigYearList(Long calTempId, HttpServletRequest request,
			HttpServletResponse response, HttpSession session);

}