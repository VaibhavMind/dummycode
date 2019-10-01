package com.payasia.logic;

import java.util.Date;
import java.util.List;

import com.payasia.common.dto.LeaveSessionDTO;
import com.payasia.common.form.CoherentShiftDetailForm;
import com.payasia.common.form.CoherentShiftDetailFormResponse;
import com.payasia.common.form.ImportEmployeeOvertimeShiftForm;
import com.payasia.common.form.LundinPendingItemsForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.PendingOTTimesheetForm;
import com.payasia.common.form.PendingOTTimesheetResponseForm;
import com.payasia.common.form.SortCondition;

public interface CoherentEmployeeShiftAdminLogic {

	PendingOTTimesheetResponseForm getPendingTimesheet(Long empId,
			PageRequest pageDTO, SortCondition sortDTO, String searchCondition,
			String searchText, Long companyId);

	PendingOTTimesheetResponseForm getTimesheet(Long empId,
			PageRequest pageDTO, SortCondition sortDTO, String searchCondition,
			String searchText, List<String> StatusNameList, Long companyId);

	PendingOTTimesheetResponseForm getAllTimesheet(Long empId,
			PageRequest pageDTO, SortCondition sortDTO, String searchCondition,
			String searchText, List<String> StatusNameList, Long companyId);

	List<String> getHolidaysFor(Long employeeId, Date startDate, Date endDate);

	LundinPendingItemsForm getPendingItemForReview(long timesheetId,
			Long employeeId, long companyId);

	CoherentShiftDetailFormResponse viewMultipleTimesheetApps(Long companyId,
			Long loggedInEmpId, String[] timesheetIds);

	List<PendingOTTimesheetForm> reviewMultipleAppByAdmin(
			CoherentShiftDetailForm pendingItemsForm, Long employeeId,
			Long companyId, LeaveSessionDTO sessionDTO);

	PendingOTTimesheetForm forwardTimesheet(
			CoherentShiftDetailForm pendingOtTimesheetForm, Long employeeId,
			Long companyId);

	PendingOTTimesheetForm rejectTimesheet(
			CoherentShiftDetailForm pendingOTTimesheetForm, Long employeeId,
			Long companyId);

	PendingOTTimesheetForm acceptTimesheet(
			CoherentShiftDetailForm pendingOTTimesheetForm, Long employeeId,
			Long companyId);

	ImportEmployeeOvertimeShiftForm importEmployeeShift(
			ImportEmployeeOvertimeShiftForm importEmployeeClaimForm,
			Long companyId, Long employeeId);

}
