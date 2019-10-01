package com.payasia.logic;

import java.util.Date;
import java.util.List;

import com.payasia.common.dto.AppCodeDTO;
import com.payasia.common.dto.LundinEmployeeTimesheetReviewerDTO;
import com.payasia.common.dto.LundinOTBatchDTO;
import com.payasia.common.dto.LundinTimesheetDTO;
import com.payasia.common.dto.LundinTimesheetDelDTO;
import com.payasia.common.dto.LundinTimesheetSaveDTO;
import com.payasia.common.form.AddClaimFormResponse;
import com.payasia.common.form.LundinMyRequestForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;

public interface LundinTimesheetRequestLogic {
	AddClaimFormResponse getPendingTimesheet(String fromDate, String toDate,
			Long empId, PageRequest pageDTO, SortCondition sortDTO,
			String searchCondition, String searchText);

	AddClaimFormResponse getSubmittedTimesheet(String fromDate, String toDate,
			Long empId, PageRequest pageDTO, SortCondition sortDTO,
			String pageContextPath, String searchCondition, String searchText);

	AddClaimFormResponse getApprovedTimesheet(String fromDate, String toDate,
			Long empId, PageRequest pageDTO, SortCondition sortDTO,
			String pageContextPath, String searchCondition, String searchText);

	AddClaimFormResponse getRejectedTimesheet(String fromDate, String toDate,
			Long empId, PageRequest pageDTO, SortCondition sortDTO,
			String pageContextPath, String searchCondition, String searchText);

	AddClaimFormResponse getWithdrawnTimesheet(String fromDate, String toDate,
			Long empId, PageRequest pageDTO, SortCondition sortDTO,
			String pageContextPath, String searchCondition, String searchText);

	String getSubmittedOnDate(Long timesheetId);

	LundinMyRequestForm getotTimesheetReviewWorkflow(Long lundinTsheetId,
			Long empId);

	void saveTimeSheet(LundinTimesheetDTO timesheet);

	long saveTimeSheetAndReturnId(LundinOTBatchDTO otbatch);

	void saveTimeSheetDetail(LundinTimesheetSaveDTO timesheetDetailDto,
			long companyId);

	long saveTimeSheetAndReturn(LundinTimesheetDTO timesheetDTO);

	void submitToWorkFlow(
			List<LundinEmployeeTimesheetReviewerDTO> LundinEmployeeTimesheetReviewerDTOs,
			long timesheetId, long companyId, long employeeId);

	LundinTimesheetSaveDTO getTimesheetResponseForm(long id);

	List<LundinEmployeeTimesheetReviewerDTO> getReviewersFor(long employeeId,
			long companyId);

	LundinTimesheetDTO findById(long id);

	LundinOTBatchDTO getOTBatch(long timesheetId);

	boolean getCanWithdraw(long timesheetId);

	boolean withdrawTimesheetRequest(long timesheetId, long employeeId,
			Long companyId);

	List<String> getHolidaysFor(Long employeeId, Date startDate, Date endDate);

	List<AppCodeDTO> getValueAndCodes();

	void deleteTimesheetRecords(
			List<LundinTimesheetDelDTO> lundinTimesheetDelDTOs);

	boolean saveAsDraftTimesheet(long timesheetId, long employeeId,
			Long companyId);

	List<String> getEmpResignedAndNewHiresDates(Long employeeId,
			Date startDate, Date endDate);

	AddClaimFormResponse getAllTimesheet(String fromDate, String toDate,
			Long empId, PageRequest pageDTO, SortCondition sortDTO,
			String pageContextPath, String transactionType, Long companyId,
			String searchCondition, String searchText);

}
