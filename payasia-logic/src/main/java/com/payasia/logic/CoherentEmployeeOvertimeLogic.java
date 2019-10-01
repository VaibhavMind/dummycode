package com.payasia.logic;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.payasia.common.dto.TimesheetFormPdfDTO;
import com.payasia.common.form.CoherentTimesheetDTO;
import com.payasia.common.form.DataExportForm;
import com.payasia.common.form.LundinPendingItemsForm;
import com.payasia.common.form.OTPendingTimesheetForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.PendingOTTimesheetForm;
import com.payasia.common.form.PendingOTTimesheetResponseForm;
import com.payasia.common.form.SortCondition;

public interface CoherentEmployeeOvertimeLogic {

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

	TimesheetFormPdfDTO generateTimesheetPrintPDF(Long companyId,
			Long employeeId, Long timesheetId, boolean hasLundinTimesheetModule);

	PendingOTTimesheetResponseForm getTimesheet(Long empId, Long companyId,
			PageRequest pageDTO, SortCondition sortDTO, String searchCondition,
			String searchText, List<String> StatusNameList);

	List<String> getHolidaysFor(Long employeeId, Date startDate, Date endDate);

	String getTimesheetApplications(long timesheetId, long companyId);

	PendingOTTimesheetResponseForm getAllTimesheet(Long empId, Long companyId,
			PageRequest pageDTO, SortCondition sortDTO, String searchCondition,
			String searchText, List<String> StatusNameList);

	Map<String, String> updateCoherentOvertimeDetailByRev(
			CoherentTimesheetDTO coherentTimesheetDTO);

	OTPendingTimesheetForm getTimesheetWorkflowHistory(Long otTimesheetId);

	PendingOTTimesheetResponseForm getPendingTimesheet(Long empId,
			Long companyId, PageRequest pageDTO, SortCondition sortDTO,
			String searchCondition, String searchText);

	DataExportForm exportOvertimeDetail(Long timesheetId);

}
