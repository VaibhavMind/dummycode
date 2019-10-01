package com.payasia.dao;

import java.sql.Timestamp;
import java.util.List;

import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.CompanyHolidayCalendarDetail;

public interface CompanyHolidayCalendarDetailDAO {

	CompanyHolidayCalendarDetail findByID(Long companyHolidayCalendarDetailId);

	void save(CompanyHolidayCalendarDetail companyHolidayCalendarDetail);

	void update(CompanyHolidayCalendarDetail companyHolidayCalendarDetail);

	void delete(CompanyHolidayCalendarDetail companyHolidayCalendarDetail);

	Integer getHolidayCountByYear(Long holidayCalId, int year);

	int getCountForCondition(Long holidayCalId, Long companyId, int year);

	List<CompanyHolidayCalendarDetail> findByCondition(Long holidayCalId,
			Long companyId, int year, PageRequest pageDTO, SortCondition sortDTO);

	CompanyHolidayCalendarDetail findByDateAndCalId(Long compaHoliCalDetailId,
			Long holidayCalId, Timestamp holidayDate);
	CompanyHolidayCalendarDetail findByID(Long companyHolidayCalendarDetailId, Long companyId);
}
