package com.payasia.dao;

import java.util.Date;
import java.util.List;

import com.payasia.common.dto.HolidayCalendarConditionDTO;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.EmployeeHolidayCalendar;

public interface EmployeeHolidayCalendarDAO {

	EmployeeHolidayCalendar findByID(Long employeeHolidayCalendarId);

	void save(EmployeeHolidayCalendar employeeHolidayCalendar);

	void update(EmployeeHolidayCalendar employeeHolidayCalendar);

	void delete(EmployeeHolidayCalendar employeeHolidayCalendar);

	List<EmployeeHolidayCalendar> getEmployeeHolidayCalendars(
			HolidayCalendarConditionDTO conditionDTO, PageRequest pageDTO,
			SortCondition sortDTO);

	Integer getCountForCondition(HolidayCalendarConditionDTO conditionDTO,
			Long companyId);

	EmployeeHolidayCalendar isAssignedEmpExist(Long employeeId);

	EmployeeHolidayCalendar findByEmpId(Long employeeId, Long companyId,
			int year);

	EmployeeHolidayCalendar getCalendarDetail(Long employeeId, Date startDate,
			Date endDate);
	
	EmployeeHolidayCalendar findByID(Long employeeHolidayCalendarId,Long companyId);

}
