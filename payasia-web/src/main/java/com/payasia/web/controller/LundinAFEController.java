package com.payasia.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.payasia.common.dto.LundinAFEDTO;

public interface LundinAFEController {

	String getAfeData(String columnName, String sortingType, String fromDate,
			String toDate, String searchCondition, String searchText,
			String transactionType, int page, int rows,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session);

	String addLundinAFE(LundinAFEDTO lundinAFEDTO, HttpServletRequest request,
			HttpServletResponse response, HttpSession session);

	String editLundinAFE(LundinAFEDTO lundinAFEDTO, HttpServletRequest request,
			HttpServletResponse response, HttpSession session);

	String deleteLundinAFE(long afeId, HttpServletRequest request,
			HttpServletResponse response, HttpSession session);

	String getActiveBlockList(HttpServletRequest request,
			HttpServletResponse response, HttpSession session);

	String getRowAfeData(Long afeId, HttpServletRequest request,
			HttpServletResponse response, HttpSession session);
}
