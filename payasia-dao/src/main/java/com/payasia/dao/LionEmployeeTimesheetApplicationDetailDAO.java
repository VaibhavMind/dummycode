package com.payasia.dao;

import java.util.List;

import com.payasia.common.dto.LundinTsheetConditionDTO;
import com.payasia.dao.bean.LionEmployeeTimesheetApplicationDetail;

public interface LionEmployeeTimesheetApplicationDetailDAO {
	void save(
			LionEmployeeTimesheetApplicationDetail lionEmployeeTimesheetApplicationDetail);

	LionEmployeeTimesheetApplicationDetail saveReturn(
			LionEmployeeTimesheetApplicationDetail lionEmployeeTimesheetApplicationDetail);

	LionEmployeeTimesheetApplicationDetail findById(Long lionEmployeeTimesheetId);

	void delete(
			LionEmployeeTimesheetApplicationDetail lionEmployeeTimesheetApplicationDetail);

	List<LionEmployeeTimesheetApplicationDetail> getLionEmployeeTimesheetApplicationDetails(
			long timesheeetId);

	void update(
			LionEmployeeTimesheetApplicationDetail lionEmployeeTimesheetApplicationDetail);

	List<LionEmployeeTimesheetApplicationDetail> findLionTimsheetDetailsByCondition(
			Long companyId, LundinTsheetConditionDTO conditionDTO);
}
