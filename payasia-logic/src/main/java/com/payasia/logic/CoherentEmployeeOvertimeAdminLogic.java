package com.payasia.logic;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.payasia.common.dto.LeaveSessionDTO;
import com.payasia.common.dto.TimesheetFormPdfDTO;
import com.payasia.common.form.CoherentOvertimeDetailForm;
import com.payasia.common.form.CoherentOvertimeDetailFormResponse;
import com.payasia.common.form.CoherentTimesheetDTO;
import com.payasia.common.form.ImportEmployeeOvertimeShiftForm;
import com.payasia.common.form.LundinPendingItemsForm;
import com.payasia.common.form.OTPendingTimesheetForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.PendingOTTimesheetForm;
import com.payasia.common.form.PendingOTTimesheetResponseForm;
import com.payasia.common.form.SortCondition;

public interface CoherentEmployeeOvertimeAdminLogic {

	PendingOTTimesheetForm forwardTimesheet(
			CoherentOvertimeDetailForm pendingOTTimesheetForm, Long employeeId,
			Long companyId);

	PendingOTTimesheetForm rejectTimesheet(
			CoherentOvertimeDetailForm pendingOTTimesheetForm, Long employeeId,
			Long companyId);

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

	CoherentOvertimeDetailFormResponse viewMultipleTimesheetApps(
			Long companyId, Long loggedInEmpId, String[] leaveApplicationRevIds);

	CoherentOvertimeDetailForm showEmpWorkflowHistory(Long companyId,
			Long timesheetId);

	List<PendingOTTimesheetForm> reviewMultipleAppByAdmin(
			CoherentOvertimeDetailForm pendingItemsForm, Long employeeId,
			Long companyId, LeaveSessionDTO sessionDTO);

	PendingOTTimesheetForm acceptTimesheet(
			CoherentOvertimeDetailForm pendingOTTimesheetForm, Long employeeId,
			Long companyId);

	LundinPendingItemsForm getPendingItemForReview(long timesheetId,
			Long employeeId, long companyId);

	PendingOTTimesheetResponseForm getPendingTimesheet(Long empId,
			Long companyId, PageRequest pageDTO, SortCondition sortDTO,
			String searchCondition, String searchText);

	ImportEmployeeOvertimeShiftForm importEmployeeOvertime(
			ImportEmployeeOvertimeShiftForm importEmployeeClaimForm,
			Long companyId, Long employeeId);

}
