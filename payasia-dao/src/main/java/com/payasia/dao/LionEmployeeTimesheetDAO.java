package com.payasia.dao;

import java.util.List;

import com.payasia.dao.bean.LionEmployeeTimesheet;

public interface LionEmployeeTimesheetDAO {

	void save(LionEmployeeTimesheet lionEmployeeTimesheet);

	List<LionEmployeeTimesheet> findByEmployeeTimesheetApplication(
			long timesheetId);

	void update(LionEmployeeTimesheet lionEmployeeTimesheet);
}
