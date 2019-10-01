package com.payasia.logic;

import com.payasia.common.dto.TimesheetFormPdfDTO;
import com.payasia.common.form.DataExportForm;
import com.payasia.common.form.LundinPendingItemsForm;
import com.payasia.common.form.OTPendingTimesheetForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.PendingOTTimesheetForm;
import com.payasia.common.form.PendingOTTimesheetResponseForm;
import com.payasia.common.form.SortCondition;

public interface LionPendingTimesheetLogic {

	PendingOTTimesheetResponseForm getPendingTimesheet(Long empId,
			PageRequest pageDTO, SortCondition sortDTO, String searchCondition,
			String searchText);

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

	PendingOTTimesheetResponseForm getApprovedTimesheet(Long empId,
			PageRequest pageDTO, SortCondition sortDTO, String searchCondition,
			String searchText);

	PendingOTTimesheetResponseForm getSubmittedTimesheet(Long empId,
			PageRequest pageDTO, SortCondition sortDTO, String searchCondition,
			String searchText);

	TimesheetFormPdfDTO generateTimesheetPrintPDF(Long companyId,
			Long employeeId, Long timesheetId);

	DataExportForm generateTimesheetExcel(Long timesheetId);

}
