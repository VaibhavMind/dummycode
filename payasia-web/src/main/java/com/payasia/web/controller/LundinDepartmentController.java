package com.payasia.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.payasia.common.dto.LundinDepartmentDTO;

public interface LundinDepartmentController {

	String editLundinDepartment(LundinDepartmentDTO lundinDepartmentDTO,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session);

	String getDepartments(String columnName, String sortingType,
			String fromDate, String toDate, String searchCondition,
			String searchText, String transactionType, int page, int rows,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session);

	String getRowDepartmentData(Long departmentId, HttpServletRequest request,
			HttpServletResponse response, HttpSession session);
}
