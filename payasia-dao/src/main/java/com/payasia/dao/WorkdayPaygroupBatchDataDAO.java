package com.payasia.dao;

import java.util.List;

import com.payasia.dao.bean.WorkdayPaygroupBatchData;

public interface WorkdayPaygroupBatchDataDAO {

	List<WorkdayPaygroupBatchData> findAllByPaygroupBatch(long paygroupBatchId, long companyId);

	void save(WorkdayPaygroupBatchData workdayPaygroupBatchData);

	void update(WorkdayPaygroupBatchData workdayPaygroupBatchData);

	WorkdayPaygroupBatchData saveReturn(WorkdayPaygroupBatchData workdayPaygroupBatchData);

	List<WorkdayPaygroupBatchData> findEmployeeWorkdayPaygroupBatchData(long companyId, long batchPeriod);

	WorkdayPaygroupBatchData findEmployeePayrollBatchData(Long companyId, WorkdayPaygroupBatchData workdayPaygroupBatch);

}
