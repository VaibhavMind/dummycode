package com.payasia.dao;

import com.payasia.dao.bean.WorkdayPaygroupEmployeeTimeOff;

public interface WorkdayPaygroupEmployeeTimeOffDAO {

	void save(WorkdayPaygroupEmployeeTimeOff workdayPaygroupEmployeeTimeOff);

	void update(WorkdayPaygroupEmployeeTimeOff workdayPaygroupEmployeeTimeOff);

	WorkdayPaygroupEmployeeTimeOff saveReturn(WorkdayPaygroupEmployeeTimeOff workdayPaygroupEmployeeTimeOff);
	
	// WorkdayPaygroupEmployeeTimeOff findEmployeeTimeOffByDate(long employeeId, long companyId, Timestamp date);

	WorkdayPaygroupEmployeeTimeOff findEmployeeTimeOff(long employeeId, long companyId, String code);

	void deleteEmployeeTimeOff(long employeeId, long companyId, String code);
	
}
