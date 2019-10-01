package com.payasia.dao;

import java.sql.Timestamp;
import java.util.List;

import com.payasia.dao.bean.TimesheetBatch;

public interface TimesheetBatchDAO {
	TimesheetBatch findById(long id);

	List<TimesheetBatch> getOTBacthesByStatus(Long companyId, Long employeeId,
			List<String> otStatusList);

	List<TimesheetBatch> getOTBacthesByCompanyId(Long companyId);

	TimesheetBatch findOtBatchExist(Long companyId);

	List<TimesheetBatch> getOTBatchesByYear(Long companyId, int year);

	void save(TimesheetBatch lundinTimesheetBatch);

	long saveOTBatchAndReturnId(TimesheetBatch lundinTimesheetBatch);

	List<TimesheetBatch> getLionHKOTBacthes(Long companyId, Long employeeId,
			List<String> otStatusList);

	TimesheetBatch findLatestBatchLion(Long companyId);

	TimesheetBatch getBatchByCurrentDate(Long companyId, Timestamp date);

	List<TimesheetBatch> getCurrentAndPreviousBatchByCompany(Long companyId,
			Timestamp date);

	TimesheetBatch findBatchByDate(Timestamp endDate, Long companyId);

	TimesheetBatch findCurrentBatchForDate(Timestamp date, Long companyId);

	TimesheetBatch getBatchByStartDate(Long companyId, Timestamp date);
	
	TimesheetBatch findById(long id,Long companyId,Long employeeId);

}
