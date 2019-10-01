package com.payasia.dao;

import java.util.List;

import com.payasia.dao.bean.CalendarPatternDetail;

public interface CalendarPatternDetailDAO {

	CalendarPatternDetail findByID(Long calendarPatternDetailId);

	void save(CalendarPatternDetail calendarPatternDetail);

	void delete(CalendarPatternDetail calendarPatternDetail);

	void update(CalendarPatternDetail calendarPatternDetail);

	CalendarPatternDetail findByCalPatternMasterId(Long calendarPatternMasterId);

	List<CalendarPatternDetail> findListByCalPatternMasterId(
			Long calendarPatternMasterId);

}
