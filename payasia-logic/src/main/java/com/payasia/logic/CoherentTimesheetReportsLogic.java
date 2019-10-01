package com.payasia.logic;

import java.util.List;

import com.payasia.common.dto.CoherentTimesheetReportDTO;
import com.payasia.common.form.CoherentTimesheetReportsForm;

public interface CoherentTimesheetReportsLogic {

	List<CoherentTimesheetReportsForm> otBatchList(Long companyId, int year);

	List<CoherentTimesheetReportsForm> coherentEmployeeList(Long employeeId,
			Long companyId);

	CoherentTimesheetReportDTO genEmpOvertimeDetailRepExcelFile(Long companyId,
			Long employeeId,
			CoherentTimesheetReportsForm coherentTimesheetReportsForm,
			Boolean isManager, String[] dataDictionaryIds);

	List<CoherentTimesheetReportsForm> getEmpDynCostCentreFieldList(
			Long companyId);

	List<CoherentTimesheetReportsForm> coherentTimesheetReviewerList(
			Long employeeId, Long companyId);

	List<CoherentTimesheetReportsForm> coherentTimesheetEmpListForManager(
			Long employeeId, Long companyId);

	List<Object[]> getEmpDynFieldsValueList(Long companyId, String dateFormat,
			Long employeeId);

	CoherentTimesheetReportDTO getOTBatchName(Long companyId, Long fromBatchId,
			Long toBatchId);

	CoherentTimesheetReportDTO genEmpShiftDetailRepExcelFile(Long companyId,
			Long employeeId,
			CoherentTimesheetReportsForm coherentTimesheetReportsForm,
			Boolean isManager, String[] dataDictionaryIds);

}
