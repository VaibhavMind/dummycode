package com.payasia.dao;

import java.util.List;

import com.payasia.common.dto.HolidayCalendarConditionDTO;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.CompanyHolidayCalendar;

public interface CompanyHolidayCalendarDAO {

	CompanyHolidayCalendar findByID(Long companyHolidayCalendarId);

	void save(CompanyHolidayCalendar companyHolidayCalendar);

	void update(CompanyHolidayCalendar companyHolidayCalendar);

	void delete(CompanyHolidayCalendar companyHolidayCalendar);

	List<CompanyHolidayCalendar> findByCondition(
			HolidayCalendarConditionDTO conditionDTO, Long companyId,
			PageRequest pageDTO, SortCondition sortDTO);

	Integer getCountForCondition(HolidayCalendarConditionDTO conditionDTO,
			Long companyId);

	CompanyHolidayCalendar checkDuplicateCalendar(Long companyId,
			String holidayCalName, Long holidayCalId);
	
	CompanyHolidayCalendar findByID(Long companyHolidayCalendarId,Long companyId);

}
