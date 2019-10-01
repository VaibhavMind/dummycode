package com.payasia.dao;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import com.payasia.dao.bean.WorkdayPaygroupBatch;

public interface WorkdayPaygroupBatchDAO {

	WorkdayPaygroupBatch findById(long workdayPayGroupBatchId);
	
	void save(WorkdayPaygroupBatch workdayPaygroupBatch);
	
	void update(WorkdayPaygroupBatch workdayPaygroupBatch);
	
	WorkdayPaygroupBatch saveReturn(WorkdayPaygroupBatch workdayPaygroupBatch);

	WorkdayPaygroupBatch findByPayPeriodDates(long companyId, Date payPeriodStartDate, Date payPeriodEndDate, boolean isEmployeeData);

	WorkdayPaygroupBatch findByWorkdayPaygroupBatchId(Long workdayPayGroupBatchId, Long companyId);

	List<WorkdayPaygroupBatch> findByCompanyId(Long companyId, Map<String, Object> dataRecord);

	List<WorkdayPaygroupBatch> findByCompanyId(Long companyId);
	
}
