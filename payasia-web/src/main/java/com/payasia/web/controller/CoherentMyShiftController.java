package com.payasia.web.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public interface CoherentMyShiftController {

	String getPendingMysheet(String columnName, String sortingType,
			String fromDate, String toDate, String searchCondition,
			String searchText, int page, int rows, HttpServletRequest request,
			HttpServletResponse response, HttpSession session);

	String getSubmittedOTTimesheet(String columnName, String sortingType,
			String fromDate, String toDate, String searchCondition,
			String searchText, int page, int rows, HttpServletRequest request,
			HttpServletResponse response, HttpSession session);

	String getApprovedMyshift(String columnName, String sortingType,
			String fromDate, String toDate, String searchCondition,
			String searchText, int page, int rows, HttpServletRequest request,
			HttpServletResponse response, HttpSession session);

	String getRejectedShift(String columnName, String sortingType,
			String fromDate, String toDate, String searchCondition,
			String searchText, int page, int rows, HttpServletRequest request,
			HttpServletResponse response, HttpSession session);

	String getBatches(HttpServletRequest request, HttpServletResponse response);

	String getShiftForBatch(long batchId, String columnName,
			String sortingType, Integer page, Integer rows,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session);

	String submitShiftToWorkflow(long shiftApplicationId,
			HttpServletRequest request, HttpServletResponse response);

	String openSubmittedShift(long shiftApplicationId,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session);

	String withdrawRequest(long shiftApplicationId, HttpServletRequest request,
			HttpServletResponse response);

	String getAllShift(String columnName, String sortingType,
			String searchCondition, String searchText, String transactionType,
			String fromDate, String toDate, int page, int rows,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session);

	byte[] printShiftDetail(Long ShiftApplicationId,
			HttpServletResponse response, HttpServletRequest request);

	String generateAndGetBatches(HttpServletRequest request,
			HttpServletResponse response);

	String saveAsDraftWorkflow(long shiftApplicationId,
			HttpServletRequest request, HttpServletResponse response);

	void exportShiftDetail(Long timesheetId, HttpServletResponse response,
			HttpServletRequest request) throws IOException;

	String updateShiftForBatch(long shiftId, String totalShift, String remarks,
			String isShift, String coherentShiftType, String shiftTypePerDate,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session);

}
