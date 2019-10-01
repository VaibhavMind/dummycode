package com.payasia.dao;

import java.util.List;

import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.CalendarPatternMaster;

public interface CalendarPatternMasterDAO {

	void save(CalendarPatternMaster calendarPatternMaster);

	void update(CalendarPatternMaster calendarPatternMaster);

	CalendarPatternMaster findById(Long calendarPatternMasterId);

	void delete(CalendarPatternMaster calendarPatternMaster);

	List<CalendarPatternMaster> findByConditionCompany(PageRequest pageDTO,
			SortCondition sortDTO, Long companyId);

	Long getCountForConditionCompany(Long companyId);

	CalendarPatternMaster saveCalPatternMaster(
			CalendarPatternMaster calendarPatternMaster);

	CalendarPatternMaster findByPatternName(Long companyId,
			String calendarPattern);

	CalendarPatternMaster findByPatternNameAndId(Long companyId,
			String calendarPattern, Long patternMasterId);

}
