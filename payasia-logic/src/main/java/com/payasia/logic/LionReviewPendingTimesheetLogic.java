package com.payasia.logic;

import java.util.Map;

import com.payasia.common.dto.TimesheetFormPdfDTO;
import com.payasia.common.form.LundinPendingItemsForm;
import com.payasia.common.form.OTPendingTimesheetForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.PendingOTTimesheetForm;
import com.payasia.common.form.PendingOTTimesheetResponseForm;
import com.payasia.common.form.SortCondition;
import com.payasia.common.form.WorkFlowDelegateResponse;

public interface LionReviewPendingTimesheetLogic {

	PendingOTTimesheetResponseForm getPendingTimesheet(Long empId,
			PageRequest pageDTO, SortCondition sortDTO, String searchCondition,
			String searchText, String pageContextPath, long companyId);

	LundinPendingItemsForm getPendingItemForReview(long timesheetId,
			Long employeeId, long companyId);

	PendingOTTimesheetForm forwardTimesheet(
			PendingOTTimesheetForm pendingOtTimesheetForm, Long employeeId,
			Long companyId);

	PendingOTTimesheetForm acceptTimesheet(
			PendingOTTimesheetForm pendingOTTimesheetForm, Long employeeId,
			Long companyId);

	PendingOTTimesheetForm rejectTimesheet(
			PendingOTTimesheetForm pendingOTTimesheetForm, Long employeeId,
			Long companyId);

	OTPendingTimesheetForm getDataForPendingOtReviewWorkflow(Long otTimesheetId);

	TimesheetFormPdfDTO generateTimesheetPrintPDF(Long companyId,
			Long employeeId, Long timesheetId, boolean hasLundinTimesheetModule);

	Map<String, String> approveLionEmployeeTimesheetApplicationDetail(
			long employeeId, long employeeTimesheetDetailId, String inTime,
			String outTime, String breakTime, String totalHours, String remarks);

	Map<String, String> updateLionEmployeeTimesheetApplicationDetail(
			long employeeTimesheetDetailId, String inTime, String outTime,
			String breakTime, String totalHours, String remarks);

	PendingOTTimesheetResponseForm getSubmittedTimesheet(Long empId,
			PageRequest pageDTO, SortCondition sortDTO, String searchCondition,
			String searchText, String pageContextPath, long companyId);

	PendingOTTimesheetResponseForm getApprovedTimesheet(Long empId,
			PageRequest pageDTO, SortCondition sortDTO, String searchCondition,
			String searchText, String pageContextPath, long companyId);

	WorkFlowDelegateResponse searchEmployee(PageRequest pageDTO,
			SortCondition sortDTO, String empName, String empNumber,
			Long companyId, Long employeeId);

}
