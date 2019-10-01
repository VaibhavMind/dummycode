package com.payasia.dao;

import java.util.List;

import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.CalendarCodeMaster;

public interface CalendarCodeMasterDAO {

	List<CalendarCodeMaster> findByConditionCompany(PageRequest pageDTO,
			SortCondition sortDTO, Long companyId);

	Long getCountForConditionCompany(Long companyId);

	void save(CalendarCodeMaster calendarCodeMaster);

	CalendarCodeMaster findById(Long calendarCodeMasterId);

	void update(CalendarCodeMaster calendarCodeMaster);

	void delete(CalendarCodeMaster calendarCodeMaster);

	CalendarCodeMaster findByCodeName(Long companyId, String calCode);

	CalendarCodeMaster findByCodeNameAndCalCodeMasterId(Long companyId,
			String calCode, Long calendarCodeMasterId);

}
