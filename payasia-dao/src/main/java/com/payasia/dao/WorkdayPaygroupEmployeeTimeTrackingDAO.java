package com.payasia.dao;

import com.payasia.dao.bean.WorkdayPaygroupEmployeeTimeTracking;

public interface WorkdayPaygroupEmployeeTimeTrackingDAO {

	void save(WorkdayPaygroupEmployeeTimeTracking workdayPaygroupEmployeeTimeTracking);

	void update(WorkdayPaygroupEmployeeTimeTracking workdayPaygroupEmployeeTimeTracking);

	WorkdayPaygroupEmployeeTimeTracking saveReturn(WorkdayPaygroupEmployeeTimeTracking workdayPaygroupEmployeeTimeTracking);
	
	//WorkdayPaygroupEmployeeTimeTracking findEmployeeTimeTrackingByDate(long employeeId, long companyId, Timestamp startDate, Timestamp endDate);

	WorkdayPaygroupEmployeeTimeTracking findEmployeeTimeTracking(long employeeId, long companyId, String code);

	void deleteEmployeeTimeTracking(long employeeId, long companyId, String code);
	
}
