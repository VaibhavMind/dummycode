package com.payasia.dao;

import java.util.List;

import com.payasia.dao.bean.YearEndProcessSchedule;

public interface YearEndProcessScheduleDAO {

	void update(YearEndProcessSchedule yearEndProcessSchedule);

	void save(YearEndProcessSchedule yearEndProcessSchedule);

	void delete(YearEndProcessSchedule yearEndProcessSchedule);

	YearEndProcessSchedule findByID(long yearEndProcessScheduleId);

	List<YearEndProcessSchedule> findAll();

	YearEndProcessSchedule findByCompanyId(Long companyId);

	void callProcessYearEndRollOverProc(Long companyId, Integer year);

	void callyearEndLeaveActivateProc(Long companyId, Integer year);

}
