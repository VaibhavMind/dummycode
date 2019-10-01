package com.payasia.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.payasia.common.dto.LundinBlockDTO;

public interface LundinBlockController {

	String addLundinBlock(LundinBlockDTO lundinBlockDTO,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session);

	String editLundinBlock(LundinBlockDTO lundinBlockDTO,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session);

	String deleteLundinBlock(long blockId, HttpServletRequest request,
			HttpServletResponse response, HttpSession session);

	String getBlocks(String columnName, String sortingType, String fromDate,
			String toDate, String searchCondition, String searchText,
			String transactionType, int page, int rows,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session);

	String getBlocksAndAfes(HttpServletRequest request,
			HttpServletResponse response, HttpSession session);

	String getRowBlockData(Long blockId, HttpServletRequest request,
			HttpServletResponse response, HttpSession session);
}
