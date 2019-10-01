package com.payasia.logic;

import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface LionEmployeeTimesheetApplicationDetailLogic {

	String getTimesheetApplications(long timesheetId, long employeeId,
			long companyId);

	String getLionTimesheetJSON(long batchId, long companyId, long employeeId);

	long saveEmployeeTimesheetApplication(long batchId, long companyId,
			long employeeId);

	void submitLionEmployeeTimesheetApplicationDetail(long employeeId,
			long employeeTimesheetDetailId, String inTime, String outTime,
			String breakTime, String totalHours, String grandTotalHours,
			String excessHours, String remarks);

	Map<String, String> approveLionEmployeeTimesheetApplicationDetail(
			long employeeId, long employeeTimesheetDetailId, String inTime,
			String outTime, String breakTime, String totalHours,
			String grandTotalHours, String excessHours, String remark);

	Map<String, String> updateLionEmployeeTimesheetApplicationDetailEmployee(
			long employeeTimesheetDetailId, String inTime, String outTime,
			String breakTime, String totalHours, String grandTotalHours,
			String excessHours, String remarks);

	Map<String, String> updateLionEmployeeTimesheetApplicationDetailReviewer(
			long employeeTimesheetDetailId, String inTime, String outTime,
			String breakTime, String totalHours, String grandTotalHours,
			String excessHours, String remark);

	void submitEmpTimesheetRowByRev(long employeeId,
			long employeeTimesheetDetailId, String inTime, String outTime,
			String breakTime, String totalHours, String grandTotalHours,
			String excessHours, String remarks);

	Map<String, String> submitAndApproveEmpTimesheetAppDetailByRev(
			long employeeId, long employeeTimesheetDetailId, String inTime,
			String outTime, String breakTime, String totalHours,
			String grandTotalHours, String excessHours, String remarks);

}
