package com.payasia.logic;

import java.util.List;

import com.payasia.common.form.CompanyHolidayCalendarForm;
import com.payasia.common.form.EmployeeListFormPage;
import com.payasia.common.form.HolidayListResponse;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;

public interface HolidayCalendarLogic {

	String addHolidayCal(CompanyHolidayCalendarForm companyHolidayCalendarForm,
			Long companyId);

	CompanyHolidayCalendarForm getHolidayCalData(Long holidayCalId,
			Long companyId);

	String editHolidayCal(
			CompanyHolidayCalendarForm companyHolidayCalendarForm,
			Long companyId);

	void deleteHolidayCal(Long holidayCalId, Long companyId);

	CompanyHolidayCalendarForm getCompanyHolidayCalData(
			Long companyHolidayCalId, Long companyId);

	void deleteCompanyHolidayCal(Long companyHolidayCalId, Long companyId);

	List<CompanyHolidayCalendarForm> getStateList(Long countryId);

	List<CompanyHolidayCalendarForm> getCountryList();

	void unAssignCompanyHolidayCal(Long employeeHolidayCalId, Long companyId);

	EmployeeListFormPage fetchEmpsForCalendarTemplate(String searchCondition,
			String searchText, PageRequest pageDTO, SortCondition sortDTO,
			Long companyId, Long employeeId);

	String importCompanyHolidayData(
			CompanyHolidayCalendarForm companyHolidayCalendarForm,
			Long companyId, String[] selectedIds, Long companyHolidayId);

	HolidayListResponse getHolidayCalendars(Long companyId);

	String assignHolCalToEmployees(Long companyId, String employeeIds,
			Long holidayCalId);

	HolidayListResponse getEmployeeHolidayCalendars(String searchCondition,
			String searchText, PageRequest pageDTO, SortCondition sortDTO,
			Long companyId, Long employeeId);

	String addCompanyHolidayData(
			CompanyHolidayCalendarForm companyHolidayCalendarForm,
			Long companyId, Long companyHolidayCalId);

	HolidayListResponse getHolidayMasterListForImport(PageRequest pageDTO,
			SortCondition sortDTO, String countryId, String stateId,
			String year, Long companyId);

	HolidayListResponse searchHolidayCalendar(PageRequest pageDTO,
			SortCondition sortDTO, String searchCondition, String searchText,
			int year, Long companyId);

	HolidayListResponse getCalendarHolidayList(PageRequest pageDTO,
			SortCondition sortDTO, Long holidayCalId, Long companyId, int year);

	String editCompanyHolidayCal(
			CompanyHolidayCalendarForm companyHolidayCalendarForm,
			Long companyHolidayCalId, Long companyId);

}
