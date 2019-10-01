package com.payasia.dao;

import com.payasia.dao.bean.CompanyCalendar;

public interface CompanyCalendarDAO {

	CompanyCalendar findByID(Long companyCalendarId);

	void save(CompanyCalendar calTempMaster);

	void delete(CompanyCalendar companyCalendar);

	void update(CompanyCalendar companyCalendar);

	CompanyCalendar findByDate(String eventDate, String dateFormat);

	CompanyCalendar findByCompanyCalTemlateId(Long CompanyCalTemplateId);

}
