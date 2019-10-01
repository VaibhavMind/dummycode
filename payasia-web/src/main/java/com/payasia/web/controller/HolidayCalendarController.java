/**
 * @author abhisheksachdeva
 *
 */
package com.payasia.web.controller;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;

import com.payasia.common.form.CompanyHolidayCalendarForm;

/**
 * The Interface HolidayListController.
 */
public interface HolidayCalendarController {

	String addHolidayCal(CompanyHolidayCalendarForm companyHolidayCalendarForm,
			HttpServletRequest request);

	String getHolidayCalData(Long holidayCalId, HttpServletRequest request);

	String editHolidayCal(
			CompanyHolidayCalendarForm companyHolidayCalendarForm,
			BindingResult result, ModelMap model, HttpServletRequest request);

	String deleteHolidayCal(Long holidayCalId, HttpServletRequest request);

	String getCompanyHolidayCalData(Long companyHolidayCalId,
			HttpServletRequest request);

	String deleteCompanyHolidayCal(Long companyHolidayCalId,
			HttpServletRequest request);

	String getStateList(Long countryId);

	String getCountryList();

	String unAssignCompanyHolidayCal(Long employeeHolidayCalId,
			HttpServletRequest request);

	String fetchEmployees(String columnName, String sortingType,
			String searchCondition, String searchText, int page, int rows,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session, Locale locale);

	String importCompanyHolidayData(
			CompanyHolidayCalendarForm companyHolidayCalendarForm,
			String[] selectedIds, Long companyHolidayId,
			HttpServletRequest request, Locale locale);

	String addCompanyHolidayData(
			CompanyHolidayCalendarForm companyHolidayCalendarForm,
			Long companyHolidayCalId, HttpServletRequest request);

	String getHolidayCalendars(HttpServletRequest request);

	String assignHolCalToEmployees(String employeeIds, Long holidayCalId,
			HttpServletRequest request, HttpServletResponse response);

	String getEmployeeHolidayCalendars(String columnName, String sortingType,
			String searchCondition, String searchText, int page, int rows,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session, Locale locale);

	String getHolidayMasterListForImport(String columnName, String sortingType,
			int page, int rows, String countryId, String stateId, String year,
			HttpServletRequest request);

	String searchHolidayCalendar(String columnName, String sortingType,
			int page, int rows, String searchCondition, String searchText,
			int year, HttpServletRequest request);

	String getCalendarHolidayList(String columnName, String sortingType,
			int page, int rows, Long holidayCalId, int year,
			HttpServletRequest request);

	String editCompanyHolidayCal(
			CompanyHolidayCalendarForm companyHolidayCalendarForm,
			Long companyHolidayCalId, BindingResult result, ModelMap model,
			HttpServletRequest request);

}