package com.payasia.logic;

import com.payasia.common.dto.LundinTimesheetDTO;
import com.payasia.common.dto.TimesheetFormPdfDTO;
import com.payasia.common.form.LundinPendingItemsForm;
import com.payasia.common.form.OTPendingTimesheetForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.PendingOTTimesheetForm;
import com.payasia.common.form.PendingOTTimesheetResponseForm;
import com.payasia.common.form.SortCondition;

public interface LundinPendingTimesheetLogic {
	PendingOTTimesheetResponseForm getPendingTimesheet(Long empId,
			PageRequest pageDTO, SortCondition sortDTO, String searchCondition,
			String searchText);

	PendingOTTimesheetForm forwardTimesheet(
			PendingOTTimesheetForm pendingOTTimesheetForm, Long employeeId,
			Long companyId);

	PendingOTTimesheetForm acceptTimesheet(
			PendingOTTimesheetForm pendingOTTimesheetForm, Long employeeId,
			Long companyId);

	PendingOTTimesheetForm rejectTimesheet(
			PendingOTTimesheetForm pendingOTTimesheetForm, Long employeeId,
			Long companyId);

	LundinPendingItemsForm getPendingItemForReview(long timesheetId,
			Long employeeId, long companyId);

	OTPendingTimesheetForm getDataForPendingOtReviewWorkflow(Long otTimesheetId);

	TimesheetFormPdfDTO generateTimesheetPrintPDF(Long companyId,
			Long employeeId, Long timesheetId,
			boolean hasLundinTimesheetModule,
			LundinTimesheetDTO lundinTimesheetDTO);

}
