package com.payasia.web.controller;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.payasia.common.form.EmployeeCalendarDefForm;

public interface EmployeeCalendarDefController {

	String fetchEmployeeCalenderTemplates(HttpServletRequest request,
			HttpServletResponse response, HttpSession session, Integer Year);

	String delete(Long empCalTempId);

	String getCalTempYear(HttpServletRequest request,
			HttpServletResponse response, HttpSession session);

	String getCalTemConfigYearList(Long empCalConfigId,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session);

	String getEmpCalTempForEdit(Long empCalendarConfigId, int year,
			HttpServletRequest request, HttpServletResponse response);

	String saveCalEventByDate(Long calTempId, String calCode, String eventDate,
			HttpServletRequest request);

	String getDataForEmpCalConfig(Long empCalConfigId);

	String editEmpCalConfig(EmployeeCalendarDefForm employeeCalendarDefForm,
			HttpServletRequest request);

	String assignCalendarTemplates(
			EmployeeCalendarDefForm employeeCalendarDefForm,
			String[] selectedIds, HttpServletRequest request, Locale locale);

	String viewEmpCalendarDefinition(String columnName, String sortingType,
			String searchCondition, String searchText, int page, int rows,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session, Locale locale);

	String fetchEmployees(String columnName, String sortingType,
			String searchCondition, String searchText, int page, int rows,
			Long calendarTemplateId, String startDate,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session, Locale locale);

}
