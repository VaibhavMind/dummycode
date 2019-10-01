package com.payasia.web.controller;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.payasia.common.form.HrisPendingItemsForm;

public interface HrisPendingItemsController {

	String getPendingItems(String columnName, String sortingType,
			String searchCondition, String searchText, int page, int rows,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session, Locale locale);

	String reject(HrisPendingItemsForm hrisPendingItemsForm,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session, Locale locale);

	String accept(HrisPendingItemsForm hrisPendingItemsForm,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session, Locale locale);

	String forward(HrisPendingItemsForm hrisPendingItemsForm,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session, Locale locale);

	String searchEmployee(String columnName, String sortingType, int page,
			int rows, String empName, String empNumber,
			HttpServletRequest request, HttpServletResponse response);

	String reviewHrisPendingItem(Long reviewId, Locale locale,
			HttpServletRequest request, HttpServletResponse response);

}
