package com.payasia.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.payasia.common.dto.LundinTimesheetSaveDTO;

public interface LundinTimesheetController {
	String getPendingOTTimesheet(String columnName, String sortingType,
			String fromDate, String toDate, String searchCondition,
			String OTTimesheet, int page, int rows, HttpServletRequest request,
			HttpServletResponse response, HttpSession session);

	String getSubmittedOTTimesheet(String columnName, String sortingType,
			String fromDate, String toDate, String searchCondition,
			String searchText, int page, int rows, HttpServletRequest request,
			HttpServletResponse response, HttpSession session);

	String getApprovedOTTimesheet(String columnName, String sortingType,
			String fromDate, String toDate, String searchCondition,
			String searchText, int page, int rows, HttpServletRequest request,
			HttpServletResponse response, HttpSession session);

	String getRejectedOTTimesheet(String columnName, String sortingType,
			String fromDate, String toDate, String searchCondition,
			String searchText, int page, int rows, HttpServletRequest request,
			HttpServletResponse response, HttpSession session);

	String getWithdrawnOTTimesheet(String columnName, String sortingType,
			String fromDate, String toDate, String searchCondition,
			String searchText, int page, int rows, HttpServletRequest request,
			HttpServletResponse response, HttpSession session);

	String getDataForTimesheetReview(Long otTimesheetId,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session);

	String getBatches(HttpServletRequest request, HttpServletResponse response);

	String withdrawRequest(long timesheetId, HttpServletRequest request,
			HttpServletResponse response);

	String generateAndGetBatches(HttpServletRequest request,
			HttpServletResponse response);

	String submitToWorkflow(long timesheetId, HttpServletRequest request,
			HttpServletResponse response);

	String getReviewers(HttpServletRequest request, HttpServletResponse response);

	String editTimesheet(LundinTimesheetSaveDTO lundinTimesheetSaveDTO,
			HttpServletRequest request, HttpServletResponse response);

	String getTimesheetForBatch(long batchId, String columnName,
			String sortingType, Integer page, Integer rows,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session);

	String openTimesheetForEditing(long timesheetId,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session);

	String getValueAndCodes(HttpServletRequest request,
			HttpServletResponse response);

	String openTimesheet(long timesheetId, HttpServletRequest request,
			HttpServletResponse response, HttpSession session);

	String saveAsDraftWorkflow(long timesheetId, HttpServletRequest request,
			HttpServletResponse response);

	String getAllOTTimesheet(String columnName, String sortingType,
			String searchCondition, String searchText, String transactionType,
			String fromDate, String toDate, int page, int rows,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session);

}
