package com.payasia.web.controller;

public interface LeaveGranterController {

	String getLeaveGrantBatchEmpDetailList(String columnName,
			String sortingType, int page, int rows,
			Long leaveGrantBatchDetailId, String employeeNumber);

	String getLeaveScheme();

	String getLeaveGranterEmpList(String columnName, String sortingType,
			int page, int rows, Long leaveSchemeId, Long leaveTypeId);

	String deleteLeaveGrantBatchDetail(Long leaveGrantBatchDetailId);

	String deleteLeaveGrantBatchEmpDetail(Long leaveGrantBatchEmpDetailId);

	String getAnnualRollbackEmpList(String columnName, String sortingType,
			int page, int rows, String fromDate, String toDate);

	String getLeaveGranterLeaveTypeList(String columnName, String sortingType,
			int page, int rows, Long leaveSchemeId);

	String rollbackResignedEmployeeLeaveProc(String employeeIds);

	String getLeaveGrantBatchDetailList(String columnName, String sortingType,
			int page, int rows, String fromDate, String toDate, Long leaveType);

	String getLeaveType();

	String callLeaveGrantProc(String leaveSchemeTypeIds, Boolean isNewHires,
			String fromDate, String toDate, String employeeIdsList);

}
