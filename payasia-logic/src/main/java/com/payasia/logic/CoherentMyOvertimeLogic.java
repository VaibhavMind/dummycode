package com.payasia.logic;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.payasia.common.dto.LundinEmployeeTimesheetReviewerDTO;
import com.payasia.common.dto.LundinOTBatchDTO;
import com.payasia.common.form.AddClaimFormResponse;
import com.payasia.common.form.CoherentTimesheetDTO;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.SortCondition;

public interface CoherentMyOvertimeLogic {

	AddClaimFormResponse getRejectedTimesheet(String fromDate, String toDate,
			Long empId, PageRequest pageDTO, SortCondition sortDTO,
			String pageContextPath, String searchCondition, String searchText,
			Long companyId);

	AddClaimFormResponse getWithdrawnTimesheet(String fromDate, String toDate,
			Long empId, PageRequest pageDTO, SortCondition sortDTO,
			String pageContextPath, String searchCondition, String searchText,
			Long companyId);

	boolean getCanWithdraw(long timesheetId);

	boolean withdrawTimesheetRequest(long timesheetId, long employeeId,
			Long companyId);

	List<String> getHolidaysFor(Long employeeId, Date startDate, Date endDate);

	boolean saveAsDraftTimesheet(long timesheetId, long employeeId,
			Long companyId);

	List<LundinOTBatchDTO> getOTBacthesByCompanyId(long companyId,
			Long employeeId);

	void createOTBatches(int yearOfBatch, long companyId);

	AddClaimFormResponse getAllTimesheet(String fromDate, String toDate,
			Long empId, PageRequest pageDTO, SortCondition sortDTO,
			String pageContextPath, String transactionType, Long companyId,
			String searchCondition, String searchText);

	String getTimesheetApplications(long timesheetId, long employeeId,
			long companyId);

	String submitToWorkFlow(
			List<LundinEmployeeTimesheetReviewerDTO> lundinEmployeeOTReviewerDTOs,
			long timesheetId, String remarks, long companyId, long employeeId);

	String getCoherentTimesheetJSON(long batchId, long companyId,
			long employeeId);

	Map<String, String> updateCoherentEmployeeTimesheetApplicationDetailEmployee(
			CoherentTimesheetDTO coherentTimesheetDTO);

	AddClaimFormResponse getPendingTimesheet(String fromDate, String toDate,
			Long empId, PageRequest pageDTO, SortCondition sortDTO,
			String searchCondition, String searchText, Long companyId);

	AddClaimFormResponse getSubmittedTimesheet(String fromDate, String toDate,
			Long empId, PageRequest pageDTO, SortCondition sortDTO,
			String pageContextPath, String searchCondition, String searchText,
			Long companyId);

	AddClaimFormResponse getApprovedTimesheet(String fromDate, String toDate,
			Long empId, PageRequest pageDTO, SortCondition sortDTO,
			String pageContextPath, String searchCondition, String searchText,
			Long companyId);

}
