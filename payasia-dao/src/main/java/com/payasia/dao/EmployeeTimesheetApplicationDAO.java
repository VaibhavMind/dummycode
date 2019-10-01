package com.payasia.dao;

import java.sql.Timestamp;
import java.util.List;

import com.payasia.common.dto.AddClaimConditionDTO;
import com.payasia.common.dto.EmployeeShortListDTO;
import com.payasia.common.dto.LionTimesheetDetailDTO;
import com.payasia.common.dto.LundinDailyPaidTimesheetDTO;
import com.payasia.common.dto.LundinPendingTsheetConditionDTO;
import com.payasia.common.dto.LundinTimesheetStatusReportDTO;
import com.payasia.common.dto.LundinTimewritingDeptReportDTO;
import com.payasia.common.dto.LundinTsheetConditionDTO;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;
import com.payasia.dao.bean.EmployeeTimesheetApplication;
import com.payasia.dao.bean.TimesheetApplicationReviewer;
import com.payasia.dao.bean.TimesheetBatch;

public interface EmployeeTimesheetApplicationDAO {
	EmployeeTimesheetApplication findById(long id);

	void save(EmployeeTimesheetApplication otTimesheet);

	long saveAndReturn(EmployeeTimesheetApplication timesheet);

	List<EmployeeTimesheetApplication> findByCondition(
			AddClaimConditionDTO conditionDTO, PageRequest pageDTO,
			SortCondition sortDTO);

	Long getCountForCondition(AddClaimConditionDTO conditionDTO);

	public List<EmployeeTimesheetApplication> findByConditionForEmployee(
			PageRequest pageDTO, SortCondition sortDTO, Long employeeId,
			String fromDate, String toDate, Boolean visibleToEmployee,
			LundinTsheetConditionDTO conditionDTO);

	Integer getCountByConditionForEmployee(Long employeeId, String fromDate,
			String toDate, LundinTsheetConditionDTO conditionDTO);

	void update(EmployeeTimesheetApplication ingersollOTTimesheet);

	List<EmployeeTimesheetApplication> getEmployeeTimesheetReport(
			Long employeeId, Long companyId, Long batchId,
			boolean includeResignedEmployees,
			EmployeeShortListDTO employeeShortListDTO);

	EmployeeTimesheetApplication findReviewerById(long id);

	List<EmployeeTimesheetApplication> findByConditionSubmitted(
			LundinPendingTsheetConditionDTO conditionDTO, PageRequest pageDTO,
			SortCondition sortDTO);

	List<TimesheetApplicationReviewer> findByTimesheetStatus(
			List<String> timesheetStatusNames, Long companyId);

	Boolean isTimesheetSubmitted(Long batchId, Long employeeId, Long companyId);

	List<EmployeeTimesheetApplication> findSubmitTimesheetEmp(Long batchId,
			Long companyId);

	List<LundinTimewritingDeptReportDTO> LundinTimewritingReportProc(
			Long companyId, Long startBatchId, Long endBatchId, Long blockId,
			Long afeId, String employeeNumber,
			boolean isIncludeResignedEmployees, String employeeIdList);

	TimesheetBatch findCurrentBatchForDate(Timestamp date, Long companyId);

	void deleteByCondition(long blockId, long afeId, long timesheetId);

	List<LundinDailyPaidTimesheetDTO> dailyPaidTimesheetReportProc(
			Long companyId, Long batchId, String year,
			String Dynamic_Form_Record_Col_Name,
			String Dynamic_Form_Table_Record_Col_Name, String Form_ID,
			boolean isIncludeResignedEmployees, String employeeIdList);

	List<LundinTimesheetStatusReportDTO> lundinTimesheetStatusReportProc(
			Long companyId, Long fromBatchId, Long toBatchId,
			String timesheetStatus, boolean isIncludeResignedEmployees,
			String employeeIdList);

	List<EmployeeTimesheetApplication> findByTimsheetCondition(Long companyId,
			LundinPendingTsheetConditionDTO conditionDTO, PageRequest pageDTO,
			SortCondition sortDTO);

	Long getCountForTimesheetCondition(Long companyId,
			LundinPendingTsheetConditionDTO conditionDTO);

	List<EmployeeTimesheetApplication> findByTimesheetBatchId(
			long timesheetBatchId, long companyId, long employeeId);

	List<EmployeeTimesheetApplication> findLionTimsheetByCondition(
			Long companyId, LundinTsheetConditionDTO conditionDTO);

	List<EmployeeTimesheetApplication> findByCompanyAndEmployee(long companyId,
			long employeeId);

	List<LionTimesheetDetailDTO> getlionTimesheetNotFilledDetailProc(
			Long companyId, Timestamp fromDate, Timestamp Date);

	List<EmployeeTimesheetApplication> findByConditionLion(
			AddClaimConditionDTO conditionDTO, PageRequest pageDTO,
			SortCondition sortDTO);

	List<EmployeeTimesheetApplication> findByConditionForEmployeeLion(
			PageRequest pageDTO, SortCondition sortDTO, Long employeeId,
			String fromDate, String toDate, Boolean visibleToEmployee,
			LundinTsheetConditionDTO conditionDTO);

	List<EmployeeTimesheetApplication> findTimesheetDetailsByDate(
			Timestamp fromBatchdate, Timestamp toBatchDate,
			List<Long> employeeIdList, Long companyId,
			List<String> timesheetStatus);
	
	public List<EmployeeTimesheetApplication> findByConditionForEmployee(
			PageRequest pageDTO, SortCondition sortDTO, Long employeeId,
			String fromDate, String toDate, Boolean visibleToEmployee,
			LundinTsheetConditionDTO conditionDTO,Long companyId);
	
	EmployeeTimesheetApplication findById(long otTimesheetId,Long employeeId,long companyId);
	
	EmployeeTimesheetApplication findById(long otTimesheetId,Long employeeId);

	EmployeeTimesheetApplication findTimesheetByCompanyId(long otTimesheetId, Long companyId);

	List<EmployeeTimesheetApplication> findByConditionSubmitted2(
			LundinPendingTsheetConditionDTO otTimesheetConditionDTO, PageRequest pageDTO, SortCondition sortDTO,
			int startPos);
	
}
