package com.payasia.web.controller;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public interface HrisMyRequestController {

	String getSubmittedRequest(String columnName, String sortingType,
			String searchCondition, String searchText, int page, int rows,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session, Locale locale);

	String getApprovedRequest(String columnName, String sortingType,
			String searchCondition, String searchText, int page, int rows,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session, Locale locale);

	String getRejectedRequest(String columnName, String sortingType,
			String searchCondition, String searchText, int page, int rows,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session, Locale locale);

	String getWithdrawnRequest(String columnName, String sortingType,
			String searchCondition, String searchText, int page, int rows,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session, Locale locale);

	String withdrawChangeRequest(Long hrisChangeRequestId,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session, Locale locale);

	String viewChangeRequest(Long hrisChangeRequestId,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session, Locale locale);

}
