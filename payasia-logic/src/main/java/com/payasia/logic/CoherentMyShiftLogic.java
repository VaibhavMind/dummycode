package com.payasia.logic;

import java.util.List;
import java.util.Map;

import com.payasia.common.dto.LundinEmployeeTimesheetReviewerDTO;
import com.payasia.common.dto.LundinOTBatchDTO;
import com.payasia.common.dto.TimesheetFormPdfDTO;
import com.payasia.common.form.AddClaimFormResponse;
import com.payasia.common.form.DataExportForm;
import com.payasia.common.form.LundinPendingItemsForm;
import com.payasia.common.form.OTPendingTimesheetForm;
import com.payasia.common.form.PageRequest;
import com.payasia.common.form.PendingOTTimesheetForm;
import com.payasia.common.form.PendingOTTimesheetResponseForm;
import com.payasia.common.form.SortCondition;

public interface CoherentMyShiftLogic {

	AddClaimFormResponse getPendingShift(String fromDate, String toDate,
			Long empId, PageRequest pageDTO, SortCondition sortDTO,
			String searchCondition, String searchText, Long companyId);

	AddClaimFormResponse getSubmittedShift(String fromDate, String toDate,
			Long empId, PageRequest pageDTO, SortCondition sortDTO,
			String pageContextPath, String searchCondition, String searchText,
			Long companyId);

	AddClaimFormResponse getApprovedShift(String fromDate, String toDate,
			Long empId, PageRequest pageDTO, SortCondition sortDTO,
			String pageContextPath, String searchCondition, String searchText,
			Long companyId);

	AddClaimFormResponse getRejectedShift(String fromDate, String toDate,
			Long empId, PageRequest pageDTO, SortCondition sortDTO,
			String pageContextPath, String searchCondition, String searchText,
			Long companyId);

	String getCoherentShiftJSON(long batchId, long companyId, long employeeId);

	long saveCherentShiftApplication(long batchId, long companyId,
			long employeeId);

	Map<String, String> updateCoherentShiftApplicationDetailEmployee(
			String shiftId, String totalShift, String remarks, String isShift,
			String coherentShiftType, String shiftTypePerDate);

	void submitToWorkFlow(
			List<LundinEmployeeTimesheetReviewerDTO> employeeOTReviewerDTOs,
			long timesheetId, long companyId, long employeeId);

	String shiftApplications(long shiftApplicationId, long employeeId,
			long companyId);

	boolean withdrawShiftRequest(long shiftApplicationId, long employeeId,
			Long companyId);

	boolean getCanWithdraw(long timesheetId);

	AddClaimFormResponse getAllCoherentShifts(String fromDate, String toDate,
			Long empId, PageRequest pageDTO, SortCondition sortDTO,
			String pageContextPath, String transactionType, Long companyId,
			String searchCondition, String searchText);

	PendingOTTimesheetResponseForm getPendingEmployeeShift(Long empId,
			PageRequest pageDTO, SortCondition sortDTO, String searchCondition,
			String searchText, Long companyId);

	PendingOTTimesheetResponseForm getShift(Long empId, PageRequest pageDTO,
			SortCondition sortDTO, String searchCondition, String searchText,
			List<String> StatusNameList, Long companyId);

	List<LundinOTBatchDTO> getOTBacthesByCompanyId(long companyId,
			Long employeeId);

	Long getEmployeeIdFromShiftId(long shiftApplicationId);

	LundinPendingItemsForm getPendingItemForReview(long timesheetId,
			Long employeeId, long companyId);

	PendingOTTimesheetForm acceptShift(
			PendingOTTimesheetForm pendingOTTimesheetForm, Long employeeId,
			Long companyId);

	PendingOTTimesheetForm rejectShift(
			PendingOTTimesheetForm pendingOTTimesheetForm, Long employeeId,
			Long companyId);

	PendingOTTimesheetForm forwardShift(
			PendingOTTimesheetForm pendingOtTimesheetForm, Long employeeId,
			Long companyId);

	PendingOTTimesheetResponseForm getAllShift(Long empId, PageRequest pageDTO,
			SortCondition sortDTO, String searchCondition, String searchText,
			List<String> StatusNameList, Long companyId);

	boolean saveAsDraftTimesheet(long shiftApplicationId, long employeeId,
			Long companyId);

	OTPendingTimesheetForm getTimesheetWorkflowHistory(Long otTimesheetId);

	DataExportForm generateShiftExcel(Long timesheetId);

	TimesheetFormPdfDTO generateTimesheetPrintPDF(Long companyId,
			Long employeeId, Long timesheetId,
			boolean hasCoherentTimesheetModule);

	Map<String, String> updateCoherentShiftApplicationDetailRevewer(
			String shiftDetailId, String totalShift, String remarks,
			String isShift, String coherentShiftType, String shiftTypePerDate);

}
