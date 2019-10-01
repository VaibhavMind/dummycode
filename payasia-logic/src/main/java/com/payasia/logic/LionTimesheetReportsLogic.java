package com.payasia.logic;

import java.util.List;

import com.payasia.common.dto.LionTimesheetReportDTO;
import com.payasia.common.dto.LionTimesheetSummaryReportDTO;
import com.payasia.common.form.LionTimesheetReportsForm;

public interface LionTimesheetReportsLogic {

	List<LionTimesheetReportsForm> otBatchList(Long companyId, int year);

	LionTimesheetSummaryReportDTO getTimesheetBatchName(Long companyId,
			Long batchId);

	List<LionTimesheetReportsForm> lionTimesheetEmployeeList(Long employeeId,
			Long companyId);

	LionTimesheetReportDTO showTimesheetSummaryReport(Long companyId,
			Long employeeId,
			LionTimesheetReportsForm lundinTimesheetReportsForm,
			Boolean isManager, String[] dataDictionaryIds);

	List<LionTimesheetReportsForm> getEmpDynlocationFieldList(Long companyId);

	List<LionTimesheetReportsForm> lionTimesheetReviewerList(Long employeeId,
			Long companyId);

	List<LionTimesheetReportsForm> lionTimesheetEmpListForManager(
			Long employeeId, Long companyId);

	List<Object[]> getTimesheetLocationEmpList(Long companyId,
			String dateFormat, Long employeeId);

	LionTimesheetReportDTO showTimesheetSummaryReportDetails(Long companyId,
			Long employeeId,
			LionTimesheetReportsForm lionTimesheetDetailListingReportForm,
			Boolean isManager, String[] dataDictionaryIds);

}
