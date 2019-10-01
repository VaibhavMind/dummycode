package com.payasia.dao;

import com.payasia.dao.bean.EmployeeCalendar;

public interface EmployeeCalendarDAO {

	void update(EmployeeCalendar employeeCalendar);

	void delete(EmployeeCalendar employeeCalendar);

	void save(EmployeeCalendar employeeCalendar);

	EmployeeCalendar findByID(Long employeeCalendarId);

	EmployeeCalendar findByDate(String eventDate, String dateFormat);

	EmployeeCalendar findByEmployeeCalConfigId(Long empCalConfigId);

}
